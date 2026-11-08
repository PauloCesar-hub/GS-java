package br.com.fiap.rotaseca.exception;

import br.com.fiap.rotaseca.util.Relogio;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

/**
 * Camada de tratamento de excecoes: converte cada excecao em uma resposta HTTP
 * padronizada (ErroResponse), sem expor stack traces ao cliente.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> naoEncontrado(RecursoNaoEncontradoException e, HttpServletRequest req) {
        return montar(HttpStatus.NOT_FOUND, e.getMessage(), req, List.of());
    }

    @ExceptionHandler(DadosInvalidosException.class)
    public ResponseEntity<ErroResponse> dadosInvalidos(DadosInvalidosException e, HttpServletRequest req) {
        return montar(HttpStatus.BAD_REQUEST, "Os dados enviados sao invalidos", req, e.getErros());
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroResponse> regraNegocio(RegraNegocioException e, HttpServletRequest req) {
        return montar(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage(), req, List.of());
    }

    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<ErroResponse> conflito(ConflitoException e, HttpServletRequest req) {
        return montar(HttpStatus.CONFLICT, e.getMessage(), req, List.of());
    }

    @ExceptionHandler(PersistenciaException.class)
    public ResponseEntity<ErroResponse> persistencia(PersistenciaException e, HttpServletRequest req) {
        LOG.error("Erro de banco de dados em {}", req.getRequestURI(), e);
        return montar(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao acessar o banco de dados", req, causasBanco(e));
    }

    /**
     * Primeira linha de cada causa (ex.: "ORA-00942: table or view does not exist") para facilitar o
     * diagnostico no deploy. Mensagens do driver nao contem senha nem dados dos registros.
     */
    static List<String> causasBanco(Throwable e) {
        List<String> causas = new java.util.ArrayList<>();
        for (Throwable t = e.getCause(); t != null && causas.size() < 4; t = t.getCause()) {
            String msg = t.getMessage();
            if (msg != null && !msg.isBlank()) {
                String linha = msg.lines().findFirst().orElse("").trim();
                if (!causas.contains(linha)) {
                    causas.add(linha);
                }
            }
            if (t.getCause() == t) {
                break;
            }
        }
        return causas;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponse> jsonInvalido(HttpMessageNotReadableException e, HttpServletRequest req) {
        return montar(HttpStatus.BAD_REQUEST,
                "Corpo da requisicao ausente ou JSON mal formatado (confira tipos, datas e valores de enum)",
                req, List.of());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResponse> tipoInvalido(MethodArgumentTypeMismatchException e, HttpServletRequest req) {
        return montar(HttpStatus.BAD_REQUEST,
                "Parametro '" + e.getName() + "' com valor invalido: " + e.getValue(), req, List.of());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErroResponse> parametroAusente(MissingServletRequestParameterException e,
                                                         HttpServletRequest req) {
        return montar(HttpStatus.BAD_REQUEST, "Parametro obrigatorio ausente: " + e.getParameterName(), req,
                List.of());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErroResponse> metodoNaoSuportado(HttpRequestMethodNotSupportedException e,
                                                           HttpServletRequest req) {
        return montar(HttpStatus.METHOD_NOT_ALLOWED, "Metodo " + e.getMethod() + " nao suportado neste endereco",
                req, List.of());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErroResponse> rotaInexistente(NoResourceFoundException e, HttpServletRequest req) {
        return montar(HttpStatus.NOT_FOUND, "Endereco nao encontrado", req, List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> inesperado(Exception e, HttpServletRequest req) {
        LOG.error("Erro inesperado em {}", req.getRequestURI(), e);
        return montar(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno inesperado", req, List.of());
    }

    private ResponseEntity<ErroResponse> montar(HttpStatus status, String mensagem, HttpServletRequest req,
                                                List<String> detalhes) {
        ErroResponse corpo = new ErroResponse(Relogio.agora(), status.value(), status.getReasonPhrase(), mensagem,
                req.getRequestURI(), detalhes);
        return ResponseEntity.status(status).body(corpo);
    }
}
