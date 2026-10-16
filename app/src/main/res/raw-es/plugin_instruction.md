El plugin Batch Rename añade el cambio de nombre por lotes a AutoJs6, inspirado en [PowerRename](https://learn.microsoft.com/windows/powertoys/powerrename) de PowerToys. Cambia el nombre de muchos archivos y carpetas a la vez con buscar y reemplazar, expresiones regulares, contadores, variables de fecha, cadenas aleatorias, metadatos de fotos y pinyin para caracteres chinos, muestra una vista previa completa antes de cambiar nada y permite deshacer después.

### Funciones Destacadas

- Buscar y reemplazar: texto sin formato o expresiones regulares de JavaScript, todas las coincidencias o solo la primera, con o sin distinción de mayúsculas, aplicado al nombre, a la extensión o a ambos.
- Variables: contadores (`${start=1;increment=1;padding=3}`), variables de fecha a partir de la hora de modificación o de la hora actual (`$YYYY`, `$MM`, `$DD` y más) y cadenas aleatorias, con la misma sintaxis que PowerRename.
- Pinyin: "Caracteres chinos a pinyin" en "Limpieza del nombre" convierte los caracteres chinos del nuevo nombre en pinyin, y la variable `${pinyin}` inserta el pinyin del nombre original; los tonos, el separador de sílabas, las mayúsculas y solo las iniciales son opcionales, y los caracteres con varias lecturas se resuelven por palabras.
- Metadatos de fotos: con "Variables de metadatos" activado en "Ámbito y formato", se puede nombrar fotos por fecha de captura, cámara, objetivo, apertura, velocidad de obturación, ISO, GPS y más, 25 valores en total (`$DATE_TAKEN_YYYY`, `$CAMERA_MODEL`, etc.); las filas sin metadatos se marcan.
- Preajustes con nombre: el botón "Preajustes" de la barra superior guarda las opciones actuales como preajuste y lo carga con un toque; los preajustes se pueden renombrar, eliminar, importar y exportar con archivos o el portapapeles, y también usarse desde scripts.
- Limpieza de nombres: cambio de mayúsculas y minúsculas, normalización Unicode, paso de ancho completo a medio ancho, reemplazo de caracteres no permitidos, recorte de espacios en los extremos y numeración que continúa los nombres existentes.
- Vista previa antes de renombrar: nombres antiguos y nuevos lado a lado, desmarque elementos para excluirlos o muestre solo los que cambian; si un nombre nuevo ya existe, puede rechazar, omitir, añadir un número o reemplazar el elemento existente.
- Deshacer: AutoJs6 guarda los últimos 20 lotes de renombrado de los scripts y de cada plugin (o 50 o 100), que se pueden deshacer desde el gestor de archivos y desde scripts, o ver, deshacer y eliminar en la página "Historial de cambios de nombre" de AutoJs6; antes de deshacer se comprueba si un archivo volvió a cambiar.
- La pantalla sigue el color del tema, el modo oscuro y el idioma de AutoJs6, y puede recordar las últimas opciones y los textos de búsqueda y reemplazo recientes.

### En el gestor de archivos

Con el plugin instalado y activado, el gestor de archivos de AutoJs6 ofrece tres entradas:

- Selección múltiple: seleccione archivos o carpetas y toque "Renombrar por lotes" en la barra de selección (dentro de "Más" si hay varias acciones de plugins).
- Menú de carpeta: elija "Renombrar por lotes los elementos del interior" en el menú de una carpeta para renombrar su contenido.
- Diálogo de renombrar: al renombrar un solo archivo o carpeta, abra el menú "Más" en la esquina superior derecha del diálogo y elija "Renombrar por lotes...". "Historial de cambios de nombre", al final del mismo menú, muestra los lotes anteriores y permite deshacerlos.
- Escriba qué buscar y reemplazar en la pantalla del plugin y la vista previa se actualiza al instante; desmarque elementos para excluirlos, toque "Renombrar" y luego "Deshacer el último lote" para recuperar los nombres anteriores. "Historial de cambios de nombre", al final del menú "Más opciones" de la barra superior, abre la página del historial de AutoJs6.

Consulte la [documentación de AutoJs6](https://docs.autojs6.com/#/files?id=m-batchrename) para la API de scripts y la [página del proyecto](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename) para las notas de desarrollo.
