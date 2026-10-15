<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <picture>
      <source srcset="https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/app/src/main/res/mipmap-night/ic_launcher.png?raw=true" media="(prefers-color-scheme: dark)" />
      <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="autojs6-plugin-batch-rename-ic-launcher" border="0" width="128" />
    </picture>
  </p>

  <p>Cambia el nombre de archivos y carpetas por lotes con buscar y reemplazar, expresiones regulares, contadores y variables de fecha</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-Batch-Rename?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-Batch-Rename?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-Batch-Rename?color=534BAE&label=License"/></a>
  </p>
</div>

******

### Idiomas (Languages)

******

El README.md actual admite los siguientes idiomas:

- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-zh-Hant-TW.md)
- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-en.md)
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-fr.md)
- Español [es] # actual
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-ja.md)
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-ru.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-ar.md)

******

### Introducción

******

El plugin Batch Rename añade el cambio de nombre por lotes a AutoJs6, inspirado en [PowerRename](https://learn.microsoft.com/windows/powertoys/powerrename) de PowerToys. Cambia el nombre de muchos archivos y carpetas a la vez con buscar y reemplazar, expresiones regulares, contadores, variables de fecha y cadenas aleatorias, muestra una vista previa completa antes de cambiar nada y permite deshacer después.

El plugin es un APK que se instala por separado y no tiene icono en el lanzador. Ofrece dos puntos de entrada: "Cambiar nombre por lotes" en la barra de selección múltiple, el menú de carpetas y el diálogo de cambio de nombre del administrador de archivos, y `files.batchRename` en los scripts. El plugin solo calcula los nombres nuevos; AutoJs6 realiza los cambios de nombre, las comprobaciones de conflictos y el historial para deshacer, por lo que el plugin no necesita permiso de almacenamiento.

******

### Funciones Destacadas

******

- Buscar y reemplazar: texto sin formato o expresiones regulares de JavaScript, todas las coincidencias o solo la primera, con o sin distinción de mayúsculas, aplicado al nombre, a la extensión o a ambos.
- Variables: contadores (`${start=1;increment=1;padding=3}`), variables de fecha a partir de la hora de modificación o de la hora actual (`$YYYY`, `$MM`, `$DD` y más) y cadenas aleatorias, con la misma sintaxis que PowerRename.
- Limpieza de nombres: cambio de mayúsculas y minúsculas, normalización Unicode, paso de ancho completo a medio ancho, reemplazo de caracteres no permitidos, recorte de espacios en los extremos y numeración que continúa los nombres existentes.
- Vista previa antes de renombrar: nombres antiguos y nuevos lado a lado, desmarque elementos para excluirlos o muestre solo los que cambian; si un nombre nuevo ya existe, puede rechazar, omitir, añadir un número o reemplazar el elemento existente.
- Deshacer: AutoJs6 guarda los últimos 20 lotes de renombrado, que se pueden deshacer desde el gestor de archivos y desde scripts; antes de deshacer se comprueba si un archivo volvió a cambiar.
- La pantalla sigue el color del tema, el modo oscuro y el idioma de AutoJs6, y puede recordar las últimas opciones y los textos de búsqueda y reemplazo recientes.

******

### Uso

******

1. Actualice AutoJs6 a la compilación 5431 (6.8.0) o posterior.
2. Instale Batch Rename desde el centro de plugins de AutoJs6, o descargue el APK desde [Releases](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/releases), y luego autorícelo y actívelo en el centro de plugins.
3. En el administrador de archivos, mantenga pulsado para seleccionar elementos y toque "Cambiar nombre por lotes", o llame a `files.batchRename` en un script.

> El plugin se distribuye como un único APK universal para Android 7.0 (API 24) y posteriores. No añade ningún icono al lanzador.

******

### En el gestor de archivos

******

Con el plugin instalado y activado, el gestor de archivos de AutoJs6 ofrece tres entradas:

- Selección múltiple: seleccione archivos o carpetas y toque "Renombrar por lotes" en la barra de selección (dentro de "Más" si hay varias acciones de plugins).
- Menú de carpeta: elija "Renombrar por lotes los elementos del interior" en el menú de una carpeta para renombrar su contenido.
- Diálogo de renombrar: al renombrar un solo archivo o carpeta, abra el menú "Más" en la esquina superior derecha del diálogo y elija "Renombrar por lotes...".
- Escriba qué buscar y reemplazar en la pantalla del plugin y la vista previa se actualiza al instante; desmarque elementos para excluirlos, toque "Renombrar" y luego "Deshacer el último lote" para recuperar los nombres anteriores.

******

### Variables de reemplazo

******

El texto de reemplazo puede contener las siguientes variables:

| Variable | Significado |
|---|---|
| `${}` `${start=X;increment=X;padding=X}` | Contador con valor inicial, incremento y relleno con ceros; solo avanza para los elementos que cambian de nombre |
| `$YYYY` `$YY` `$MM` `$MMM` `$DD` `$DDDD` `$hh` `$mm` `$ss` `$fff` | Año, mes, día, hora, minuto, segundo y milisegundo de la fecha de modificación, o de la hora actual |
| `${rstringalnum=N}` `${rstringalpha=N}` `${rstringdigit=N}` `${ruuidv4}` | Cadenas aleatorias alfanuméricas, de letras o de dígitos y UUID |
| `$1` `$<name>` `$&` `$$` | Grupos de captura y coincidencia completa, como en `String.prototype.replace` de JavaScript |

******

### API De Script

******

Los scripts usan el mismo motor mediante `files.batchRename` de AutoJs6; consulte la [documentación de AutoJs6](https://docs.autojs6.com/#/files?id=m-batchrename) para la referencia completa:

```javascript
let plan = files.batchRename.plan("/sdcard/DCIM/Camera", {
    search: /^IMG_(\d+)/,
    replace: "$YYYY-$MM-$DD_${padding=3}_$1",
    enumerate: true,
    includeFolders: false,
});
plan.items.forEach(it => console.log(it.name, "->", it.newName, it.status));

if (!plan.rejected) {
    let result = files.batchRename.apply(plan);
    files.batchRename.undo(result);
}
```

- `files.batchRename.plan(paths, options)` calcula sin renombrar y devuelve el nombre nuevo y el estado de cada elemento; ponga `items[i].excluded` a `true` y pase el plan a `apply`.
- `files.batchRename(paths, options)` lo hace todo en un paso; `files.batchRename.undo(result)` deshace el lote y `files.batchRename.history()` enumera los lotes recientes (incluidos los del gestor de archivos).
- Las expresiones regulares se comportan como `RegExp` en los scripts y se puede pasar un objeto `RegExp` directamente. `planAsync` / `applyAsync` / `undoAsync` son las formas asíncronas; los fallos lanzan un `BatchRenameError` con un `code`.

******

### Compatibilidad

******

- Requiere AutoJs6 6.8.0 (compilación 5431) o posterior y Android 7.0 (API 24) o posterior.
- Los almacenamientos FAT32 / exFAT, como tarjetas SD y memorias USB, no distinguen mayúsculas y no admiten `: * ? " < > |`, la barra invertida y caracteres similares; los nombres nuevos que los contienen se marcan como no utilizables y "Reemplazar los caracteres no permitidos" los reemplaza automáticamente.
- Un lote admite como máximo 2000 elementos; los scripts llegan como máximo a 32 niveles por debajo de las carpetas indicadas.

******

### Preguntas Frecuentes

******

#### Por qué no hay icono del plugin en la pantalla de inicio?

El plugin no tiene una pantalla propia para abrir. AutoJs6 lo detecta tras la instalación y todo se hace en el gestor de archivos de AutoJs6 o en scripts.

#### Necesita el plugin permiso de almacenamiento?

No. El plugin solo calcula nombres nuevos a partir de los nombres de archivo; AutoJs6 lista las carpetas y realiza los cambios de nombre con los permisos que ya tiene.

#### En qué se diferencia de PowerRename?

Las variables de fecha usan la hora de modificación (la hora de creación no es fiable en Android) y se añaden normalización Unicode, conversión de ancho completo a medio ancho, reemplazo de caracteres no permitidos y lotes que se pueden deshacer. Las variables EXIF de fotos están previstas para la 1.1.0.

#### Las expresiones regulares se escriben como en JavaScript?

Sí. El plugin usa el mismo motor de expresiones regulares Rhino que los scripts de AutoJs6, y el reemplazo admite `$1`, `$<name>` y `$&`. Una coincidencia que tarda más de 1 segundo se detiene para que la pantalla no se bloquee.

#### Por qué no aumenta el contador?

Active primero la opción "Contador" (`enumerate: true` en scripts); de lo contrario `${}` se queda tal cual. El contador solo cuenta los elementos cuyo nombre cambia; los elementos sin cambios o excluidos no ocupan número.

#### Por qué no aparece el renombrado por lotes en el diálogo de renombrar?

Está en el menú "Más" de la esquina superior derecha del diálogo. Si el menú solo muestra una guía, el plugin no está instalado, no está activado en el centro de plugins o hay que actualizar AutoJs6; siga la guía y estará disponible.

******

### Permisos Y Seguridad

******

El plugin reduce al mínimo sus permisos:

- Mínimo privilegio: el manifiesto solo declara el permiso de plugin de AutoJs6 (`org.autojs.permission.PLUGIN`), sin permisos de red ni de almacenamiento.
- Ejecución en el host: el plugin solo devuelve un plan de cambios de nombre; AutoJs6 vuelve a validar cada elemento antes de cambiar el nombre y nunca sobrescribe archivos existentes salvo que se elija explícitamente la política de sobrescritura.
- Puntos de entrada protegidos: los servicios y la pantalla de cambio de nombre exigen el permiso de plugin, por lo que otras aplicaciones no pueden llamarlos.

******

### Interfaz Del Plugin

******

La siguiente información está dirigida a los desarrolladores del host AutoJs6 y de plugins; el host usa estos identificadores para descubrir el plugin y negociar capacidades:

```text
application id: io.github.supermonster003.autojs6.plugin.batch.rename
plugin id: batch-rename
engine: batch-rename
variant: default
info service: org.autojs.plugin.INFO (category batch-rename)
capability service: org.autojs.plugin.BATCH_RENAME (category batch-rename)
file manager service: org.autojs.plugin.EXPLORER_ACTION
wake action: org.autojs.plugin.action.WAKE
minimum host build: 5431
native library: libandroidx.graphics.path.so (Jetpack Compose, all ABIs)
```

******

### Hoja De Ruta

******

Los planes, las decisiones de diseño y el progreso se registran como lista de verificación en ROADMAP.md. Los elementos sin marcar son planes, no funciones de la versión actual.

- [Ver ROADMAP.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/ROADMAP.md)

******

### Historial De Versiones

******

#### v1.0.0

_2026/10/15_

- `Aviso` Requiere AutoJs6 6.8.0 (5431) o posterior
- `Función` Renombrado por lotes de archivos y carpetas en el gestor de archivos de AutoJs6 (entradas: barra de selección múltiple/menú de carpeta "Renombrar por lotes los elementos del interior"/menú "Más" del diálogo de renombrar)
- `Función` Buscar y reemplazar con texto sin formato o expresiones regulares de JavaScript, todas las coincidencias, distinción de mayúsculas y cambio solo del nombre o de la extensión
- `Función` Contadores, variables de fecha a partir de la hora de modificación o de la hora actual, cadenas aleatorias y UUID
- `Función` Cambio de mayúsculas y minúsculas, normalización Unicode, paso de ancho completo a medio ancho, reemplazo de caracteres no permitidos, recorte de espacios en los extremos y numeración que continúa los nombres existentes
- `Función` Vista previa de los nombres nuevos antes de renombrar, exclusión por elemento o solo los elementos que cambian, y opción de rechazar, omitir, añadir un número o reemplazar el elemento existente cuando un nombre nuevo ya existe
- `Función` Deshacer los lotes de renombrado recientes (AutoJs6 guarda los últimos 20, compartidos por el gestor de archivos y los scripts)
- `Función` Calcula los nombres nuevos para la API de scripts files.batchRename de AutoJs6
- `Función` La pantalla sigue el color del tema, el modo oscuro y el idioma de AutoJs6, y puede recordar las últimas opciones y los textos de búsqueda y reemplazo recientes
- `Dependencia` Se agrega Jetpack Compose BOM 2026.09.00
- `Dependencia` Se agrega AndroidX Activity Compose 1.12.2
- `Dependencia` Se agrega AndroidX Core KTX 1.15.0
- `Dependencia` Se agrega Kotlin Coroutines 1.11.0
- `Dependencia` Se agrega Desugar JDK Libs 2.1.5
- `Dependencia` Se agrega Rhino (versión reducida de AutoJs6, para expresiones regulares)

##### Para más historial de versiones

* [CHANGELOG.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/app/src/main/assets/doc/CHANGELOG-es.md)

******

### Compilación

******

Esta sección está dirigida a desarrolladores que quieran compilar el plugin desde el código fuente.

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest
.\gradlew.bat :app:appendDigestToReleasedFiles
py .python\generate_markdown.py --check
py .python\generate_icon_studio.py --check
py .python\check_repository.py
```

******

### Licencia

******

El proyecto se distribuye bajo la [Mozilla Public License 2.0](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/LICENSE). El diseño de las funciones sigue la documentación pública de PowerToys PowerRename y no contiene su código ni sus recursos.

******

### Enlaces

******

- Proyecto AutoJs6: https://github.com/SuperMonster003/AutoJs6
- Documentación de PowerToys PowerRename (referencia de funciones): https://learn.microsoft.com/windows/powertoys/powerrename
