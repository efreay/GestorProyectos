package com.gestorproyectos;

import com.formdev.flatlaf.FlatLightLaf;
import com.gestorproyectos.views.DashboardFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }
        new DashboardFrame();  // <-- se cambia aquí
    }
}
