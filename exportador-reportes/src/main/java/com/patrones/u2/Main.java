package com.patrones.u2;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // La consola de Windows no usa UTF-8 por defecto: sin esto, tildes
        // y guiones largos de los reportes se ven como caracteres invalidos.
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        List<GradeRecord> records = List.of(
                new GradeRecord("20231001", "Ana Torres", "IS-301", 4.2),
                new GradeRecord("20231002", "Luis Rey", "IS-301", 3.8),
                new GradeRecord("20231003", "Marta Diaz", "IS-301", 4.7)
        );

        ReportExportService exportService = new ReportExportService();

        System.out.println("=== Exportacion PDF ===");
        System.out.println(exportService.export("pdf", records, "UDES"));

        System.out.println("=== Exportacion Excel ===");
        System.out.println(exportService.export("excel", records, "UDES"));

        System.out.println("=== Exportacion HTML ===");
        System.out.println(exportService.export("html", records, "UDES"));
    }
}
