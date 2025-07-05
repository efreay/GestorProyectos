package com.gestorproyectos.persistencia;

import com.gestorproyectos.models.Proyecto;

import java.io.*;

public class GestorPersistencia {

    private static final String ARCHIVO = "proyecto.dat";

    public static void guardarProyecto(Proyecto proyecto) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO))) {
            oos.writeObject(proyecto);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Proyecto cargarProyecto() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARCHIVO))) {
            return (Proyecto) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }
}
