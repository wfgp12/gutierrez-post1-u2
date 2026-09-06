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

### Decisión 3 — Builder vs. constructor telescópico vs. setters (Parte 2)
**Patrón elegido:** Builder (`ExportConfig.Builder`).

**Justificación:** `ExportConfig` tiene 1 parámetro obligatorio
(`format`) y 8 opcionales. Se descartaron las otras dos opciones:

- **Constructor con los 9 parámetros:** varios son del mismo tipo
  (`String`, `boolean`), por lo que el cliente puede invertir el
  orden de los argumentos sin que el compilador lo detecte.
- **Clase mutable con setters sueltos:** el objeto puede quedar a
  medio configurar, y no existe un punto único donde validar que la
  combinación de valores sea consistente (por ejemplo, pedir
  `compress=true` sin especificar `outputPath`).

El Builder resuelve ambos problemas: expone métodos encadenables
autodescriptivos (sin depender del orden) y centraliza la validación
de consistencia en `build()`, antes de que exista un objeto a medio
construir — `new ExportConfig.Builder("pdf").compress(true).build()`
lanza `IllegalStateException` en vez de producir un objeto inválido.

### Decisión 4 — ¿ReportFactoryRegistry necesita ser Singleton? (Parte 2)
**Conclusión:** NO conviene Singleton.

**Justificación**, con base en los criterios objetivos del material
teórico:

- **Identidad de objeto:** nada en el proyecto necesita pasar el
  registro como objeto (no se inyecta por constructor, no se
  sustituye por un mock, no implementa una interfaz); se usa
  invocando métodos estáticos directamente.
- **Inicialización costosa:** el bloque `static` solo llena un `Map`
  con tres entradas — trabajo trivial en el classloading, sin
  lectura de archivos ni conexiones que justifiquen una
  inicialización perezosa.
- **Fuente única de verdad:** el campo `static final Map` ya
  garantiza un único registro compartido en toda la JVM, sin
  necesitar la semántica de "instancia" que aporta Singleton.
- **Escenarios futuros razonables:** no existe ningún escenario
  concreto (ni siquiera el CSV planeado) que requiera más de un
  registro independiente. Si ese escenario apareciera (por ejemplo,
  un registro distinto por institución en una plataforma
  multi-tenant), Singleton dejaría de ser apropiado por la razón
  opuesta: se necesitarían múltiples instancias, no una sola.

Convertir `ReportFactoryRegistry` en un Singleton clásico (constructor
privado con guardas + `getInstance()` + eventual sincronización)
agregaría ceremonia sin resolver ningún problema real que el `Map`
estático no resuelva ya.

## Herramientas utilizadas
- Java 17, Apache Maven, VS Code, Git, GitHub

## Conclusiones
Este post-contenido mostró que elegir un patrón creacional no es un
paso mecánico: las mismas preguntas diagnósticas (¿un producto o una
familia?, ¿qué pasa al extender?, ¿dónde está el riesgo real?)
llevaron a Abstract Factory en la Parte 1 y, aplicadas a un problema
distinto, habrían llevado a Factory Method. La Parte 2 reforzó que
"parece un buen candidato para X" no basta como justificación: Builder
se eligió porque resolvía un problema concreto (validar consistencia
antes de construir), y Singleton se descartó explícitamente para
`ReportFactoryRegistry` porque ningún criterio objetivo lo respaldaba,
pese a ser la "costumbre" para clases de registro centralizado. El
aprendizaje central es que el valor de un patrón está en el problema
que resuelve, no en aplicarlo por hábito.
