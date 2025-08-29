package com.gestorproyectos.utils;

import com.gestorproyectos.models.Analista;
import com.gestorproyectos.models.Proyecto;
import com.gestorproyectos.services.MySQLService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.sql.SQLException;
import java.time.LocalDate;

public class ExcelImporter {

    private final MySQLService mysql;

    public ExcelImporter() {
        this.mysql = new MySQLService();
    }

    public void importarDesdeArchivo(String rutaArchivo) {
        try (FileInputStream fis = new FileInputStream(rutaArchivo);
             Workbook workbook = new XSSFWorkbook(fis)) {

            importarAnalistas(workbook.getSheet("Analistas"));
            importarProyectos(workbook.getSheet("Proyectos"));
            // Aquí podrías agregar: importarTareas(...), importarSprints(...)

            Mensajes.mostrarInfo("Importación finalizada correctamente.");

        } catch (Exception e) {
            e.printStackTrace();
            Mensajes.mostrarError("Error al importar archivo Excel.");
        }
    }

    private void importarAnalistas(Sheet sheet) throws SQLException {
        if (sheet == null) return;
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            int id = (int) row.getCell(0).getNumericCellValue();
            String nombre = row.getCell(1).getStringCellValue();
            String rol = row.getCell(2).getStringCellValue();
            String especialidad = row.getCell(3).getStringCellValue();

            Analista analista = new Analista(id, nombre, rol, especialidad);
            try {
                mysql.insertarAnalista(analista);
            } catch (SQLException ex) {
                System.out.println("Ya existe el analista ID " + id + ", omitiendo...");
            }
        }
    }

    private void importarProyectos(Sheet sheet) throws SQLException {
        if (sheet == null) return;
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            String id = row.getCell(0).getStringCellValue();
            String nombre = row.getCell(1).getStringCellValue();
            String analistaAsignado = row.getCell(2).getStringCellValue();
            int idAnalista = (int) row.getCell(3).getNumericCellValue();

            LocalDate fechaInicio = null, fechaFinEstimada = null, fechaFinReal = null;

            Cell cInicio = row.getCell(4);
            if (cInicio != null && cInicio.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cInicio)) {
                fechaInicio = cInicio.getLocalDateTimeCellValue().toLocalDate();
            }

            Cell cFinEst = row.getCell(5);
            if (cFinEst != null && cFinEst.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cFinEst)) {
                fechaFinEstimada = cFinEst.getLocalDateTimeCellValue().toLocalDate();
            }

            Cell cFinReal = row.getCell(6);
            if (cFinReal != null && cFinReal.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cFinReal)) {
                fechaFinReal = cFinReal.getLocalDateTimeCellValue().toLocalDate();
            }

            String estado = row.getCell(7).getStringCellValue();
            int porcentaje = (int) row.getCell(8).getNumericCellValue();
            String prioridad = row.getCell(9).getStringCellValue();
            String epica = row.getCell(10).getStringCellValue();
            String area = row.getCell(11).getStringCellValue();
            int horasEstimadas = (int) row.getCell(12).getNumericCellValue();
            int horasReales = (int) row.getCell(13).getNumericCellValue();
            String sprintAsignado = row.getCell(14).getStringCellValue();

            Proyecto proyecto = new Proyecto(id, nombre, analistaAsignado, idAnalista,
                    fechaInicio, fechaFinEstimada, fechaFinReal, estado, porcentaje,
                    prioridad, epica, area, horasEstimadas, horasReales, sprintAsignado);

            try {
                mysql.insertarProyecto(proyecto);
            } catch (SQLException ex) {
                System.out.println("Ya existe el proyecto ID " + id + ", omitiendo...");
            }
        }
    }
}
