# Post-contenido — Unidad 2: Patrones Creacionales

## Descripción
Repositorio del post-contenido de la Unidad 2 de Patrones de Diseño
de Software. Un único proyecto Maven (`exportador-reportes/`) que
resuelve la exportación de reportes académicos en múltiples formatos
(Parte 1) y se extiende con configuración compleja y evaluación de
Singleton (Parte 2).

## Cómo ejecutar
```bash
cd exportador-reportes
mvn compile
mvn exec:java
```

## Decisiones de diseño

### Decisión 1 — Factory Method vs. Abstract Factory (Parte 1)
**Patrón elegido:** Abstract Factory.

**Justificación:** el problema exige dos productos distintos —el
cuerpo del reporte y el encabezado/pie de página— que deben
mantenerse coherentes entre sí dentro de un mismo formato (el
enunciado prohíbe explícitamente combinar un cuerpo Excel con un
encabezado PDF). Respondiendo las tres preguntas diagnósticas:

1. El sistema no crea un único producto que varía por formato, sino
   dos productos relacionados que deben permanecer consistentes
   dentro del mismo formato.
2. Al agregar el futuro formato CSV, no basta con una implementación
   nueva: hay que agregar la familia completa (`CsvReportBody` y
   `CsvHeaderFooter`).
3. El riesgo real del problema no es instanciar la clase equivocada,
   sino mezclar piezas de familias distintas y dejar el documento
   final inconsistente.

Las tres respuestas apuntan a una familia de productos relacionados,
por lo que se descartó **Factory Method**: ese patrón resuelve bien
la variación de un único producto por formato, pero no expresa ni
garantiza la coherencia entre dos productos que deben viajar juntos
dentro de la misma familia — habría que coordinar esa coherencia por
fuera del patrón, con convenciones que el compilador no verifica.

### Decisión 2 — Mecanismo de extensibilidad de formatos (Parte 1)
**Opción elegida:** registro dinámico con `Map<String, Supplier<ReportFormatFactory>>`
(`ReportFactoryRegistry`).

**Justificación:** la alternativa evidente era un `switch`/`if-else`
sobre el string de formato, pero esa opción obliga a modificar ese
mismo método cada vez que se agregue un formato nuevo (el CSV
planeado a futuro), violando el Principio Abierto/Cerrado (OCP). El
registro dinámico permite agregar un formato nuevo llamando a
`register("csv", CsvReportFactory::new)` sin tocar ninguna línea de
código existente en `ReportFactoryRegistry`, `ReportExportService` ni
`Main`.

## Herramientas utilizadas
- Java 17, Apache Maven, VS Code, Git, GitHub
