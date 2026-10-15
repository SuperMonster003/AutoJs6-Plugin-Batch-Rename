El plugin Batch Rename añade el cambio de nombre por lotes a AutoJs6, inspirado en [PowerRename](https://learn.microsoft.com/windows/powertoys/powerrename) de PowerToys. Cambia el nombre de muchos archivos y carpetas a la vez con buscar y reemplazar, expresiones regulares, contadores, variables de fecha y cadenas aleatorias, muestra una vista previa completa antes de cambiar nada y permite deshacer después.

> Esta es una versión preliminar de desarrollo: AutoJs6 reconoce el plugin, pero la pantalla de cambio de nombre, las entradas del administrador de archivos y la API de script aún no están disponibles. Las secciones siguientes describen los objetivos de la versión 1.0.0; consulte la hoja de ruta para ver el progreso.

### Funciones Destacadas

- Buscar y reemplazar: texto sin formato o expresiones regulares, todas las coincidencias o solo la primera, con o sin distinción entre mayúsculas y minúsculas, aplicado al nombre, a la extensión o al nombre completo.
- Variables: contadores (`${start=1;increment=1;padding=3}`), variables de fecha basadas en la fecha de modificación (`$YYYY`, `$MM`, `$DD` y otras) y cadenas aleatorias, con la misma sintaxis que PowerRename.
- Limpieza de texto: conversión de mayúsculas y minúsculas, normalización Unicode, conversión de ancho completo a medio ancho, eliminación de caracteres no válidos, recorte de espacios y puntos iniciales y finales, y numeración que continúa una secuencia existente.
- Primero la vista previa: cada elemento muestra su nombre anterior y el nuevo, se pueden excluir elementos y se marcan los conflictos y los nombres no válidos; ante un conflicto se puede rechazar el lote, omitir el elemento, añadir un número o sobrescribir.
- Deshacer: AutoJs6 conserva los últimos 20 lotes de cambios de nombre; tanto el administrador de archivos como los scripts pueden deshacerlos, después de comprobar que los archivos no han vuelto a cambiar.

Consulte la [página del proyecto](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename) para ver el progreso y el diseño.
