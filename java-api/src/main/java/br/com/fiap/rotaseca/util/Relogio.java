package br.com.fiap.rotaseca.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

/** Centraliza o fuso horario de Sao Paulo (o servidor no Render roda em UTC). */
public final class Relogio {

    public static final ZoneId FUSO = ZoneId.of("America/Sao_Paulo");

    private Relogio() {
    }

    public static LocalDateTime agora() {
        return LocalDateTime.now(FUSO).withNano(0);
    }

    public static LocalDate hoje() {
        return LocalDate.now(FUSO);
    }
}
