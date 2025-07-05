package com.gestorproyectos.utils;

import java.time.LocalDate;

public class Validador {
    public static boolean esTextoValido(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }

    public static boolean esDuracionValida(int horas) {
        return horas > 0;
    }

    public static boolean fechasValidas(LocalDate inicio, LocalDate fin) {
        return inicio != null && fin != null && !inicio.isAfter(fin);
    }
}
