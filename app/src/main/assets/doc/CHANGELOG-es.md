******

### Historial De Versiones

******

# v1.1.0

###### 2026/10/16

* `Aviso` Requiere AutoJs6 6.8.0 (5455) o posterior
* `Función` Pinyin: "Caracteres chinos a pinyin" en "Limpieza del nombre" y la variable `${pinyin}`, con tonos, separador de sílabas, mayúsculas y solo iniciales opcionales; los caracteres con varias lecturas se resuelven por palabras
* `Función` Variables de metadatos de fotos: nombrar fotos por fecha de captura, cámara, objetivo, apertura, velocidad de obturación, ISO, GPS y más, 25 valores en total; las filas sin metadatos se marcan (active "Variables de metadatos" en "Ámbito y formato")
* `Función` Preajustes con nombre: guardar las opciones actuales como preajuste y cargarlo con un toque, renombrar, eliminar, importar y exportar preajustes con archivos o el portapapeles (acceso: botón "Preajustes" de la barra superior)
* `Función` Historial de cambios de nombre: ver, buscar, deshacer o eliminar en AutoJs6 los lotes del gestor de archivos y de los scripts, y fijar cuántos lotes se guardan (acceso: "Más opciones" de la barra superior > "Historial de cambios de nombre")
* `Función` Pinyin, metadatos de fotos, preajustes con nombre y cálculo de nombres sin archivos para la API de scripts files.batchRename de AutoJs6
* `Mejora` Nuevo icono del plugin
* `Mejora` "Borrar el historial" del menú "Más opciones" de la barra superior pasa a llamarse "Borrar usados recientemente" para no confundirlo con "Historial de cambios de nombre"

# v1.0.0

###### 2026/10/16

* `Aviso` Requiere AutoJs6 6.8.0 (5438) o posterior
* `Función` Renombrado por lotes de archivos y carpetas en el gestor de archivos de AutoJs6 (entradas: barra de selección múltiple/menú de carpeta "Renombrar por lotes los elementos del interior"/menú "Más" del diálogo de renombrar)
* `Función` Buscar y reemplazar con texto sin formato o expresiones regulares de JavaScript, todas las coincidencias, distinción de mayúsculas y cambio solo del nombre o de la extensión
* `Función` Contadores, variables de fecha a partir de la hora de modificación o de la hora actual, cadenas aleatorias y UUID
* `Función` Cambio de mayúsculas y minúsculas, normalización Unicode, paso de ancho completo a medio ancho, reemplazo de caracteres no permitidos, recorte de espacios en los extremos y numeración que continúa los nombres existentes
* `Función` Vista previa de los nombres nuevos antes de renombrar, exclusión por elemento o solo los elementos que cambian, y opción de rechazar, omitir, añadir un número o reemplazar el elemento existente cuando un nombre nuevo ya existe
* `Función` Deshacer los lotes de renombrado recientes (AutoJs6 guarda los últimos 20, compartidos por el gestor de archivos y los scripts)
* `Función` Calcula los nombres nuevos para la API de scripts files.batchRename de AutoJs6
* `Función` La pantalla sigue el color del tema, el modo oscuro y el idioma de AutoJs6, y puede recordar las últimas opciones y los textos de búsqueda y reemplazo recientes
* `Dependencia` Se agrega Jetpack Compose BOM 2026.09.00
* `Dependencia` Se agrega AndroidX Activity Compose 1.12.2
* `Dependencia` Se agrega AndroidX Core KTX 1.15.0
* `Dependencia` Se agrega Kotlin Coroutines 1.11.0
* `Dependencia` Se agrega Desugar JDK Libs 2.1.5
* `Dependencia` Se agrega Rhino (versión reducida de AutoJs6, para expresiones regulares)
