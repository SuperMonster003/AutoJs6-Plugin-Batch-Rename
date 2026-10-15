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

> Esta es una versión preliminar de desarrollo: AutoJs6 reconoce el plugin, pero la pantalla de cambio de nombre, las entradas del administrador de archivos y la API de script aún no están disponibles. Las secciones siguientes describen los objetivos de la versión 1.0.0; consulte la hoja de ruta para ver el progreso.

******

### Funciones Destacadas

******

- Buscar y reemplazar: texto sin formato o expresiones regulares, todas las coincidencias o solo la primera, con o sin distinción entre mayúsculas y minúsculas, aplicado al nombre, a la extensión o al nombre completo.
- Variables: contadores (`${start=1;increment=1;padding=3}`), variables de fecha basadas en la fecha de modificación (`$YYYY`, `$MM`, `$DD` y otras) y cadenas aleatorias, con la misma sintaxis que PowerRename.
- Limpieza de texto: conversión de mayúsculas y minúsculas, normalización Unicode, conversión de ancho completo a medio ancho, eliminación de caracteres no válidos, recorte de espacios y puntos iniciales y finales, y numeración que continúa una secuencia existente.
- Primero la vista previa: cada elemento muestra su nombre anterior y el nuevo, se pueden excluir elementos y se marcan los conflictos y los nombres no válidos; ante un conflicto se puede rechazar el lote, omitir el elemento, añadir un número o sobrescribir.
- Deshacer: AutoJs6 conserva los últimos 20 lotes de cambios de nombre; tanto el administrador de archivos como los scripts pueden deshacerlos, después de comprobar que los archivos no han vuelto a cambiar.

******

### Uso

******

1. Actualice AutoJs6 a la compilación 5431 (6.8.0) o posterior.
2. Instale Batch Rename desde el centro de plugins de AutoJs6, o descargue el APK desde [Releases](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/releases), y luego autorícelo y actívelo en el centro de plugins.
3. En el administrador de archivos, mantenga pulsado para seleccionar elementos y toque "Cambiar nombre por lotes", o llame a `files.batchRename` en un script.

> El plugin se distribuye como un único APK universal para Android 7.0 (API 24) y posteriores. No añade ningún icono al lanzador.

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

Los scripts usan el mismo motor mediante `files.batchRename` (borrador de diseño de 1.0.0; prevalece la documentación publicada):

```javascript
let plan = files.batchRename.plan("/sdcard/DCIM/Camera", {
    search: /^IMG_(\d+)/,
    replace: "$YYYY-$MM-$DD_${padding=3}_$1",
    enumerate: true,
});
plan.items.forEach(it => console.log(it.name, "->", it.newName, it.status));

let result = files.batchRename.apply(plan);
files.batchRename.undo(result);
```

- `files.batchRename.plan(paths, options)` calcula sin cambiar nombres y devuelve un resultado por elemento; modifique `items[i].excluded` y pase el plan a `apply`.
- `files.batchRename(paths, options)` cambia los nombres en un solo paso y devuelve el identificador del lote con un resultado por elemento; `files.batchRename.undo(result)` deshace ese lote.
- Las expresiones regulares se comportan exactamente como `RegExp` en los scripts, y se pueden pasar objetos `RegExp` directamente. `planAsync`, `applyAsync` y `undoAsync` devuelven promesas.

******

### Preguntas Frecuentes

******

#### Por qué no hay icono en el lanzador?

El plugin no tiene una entrada propia. AutoJs6 lo detecta tras la instalación y todo se hace en el administrador de archivos de AutoJs6 o en los scripts.

#### El plugin necesita permiso de almacenamiento?

No. El plugin solo calcula los nombres nuevos a partir de los nombres de archivo; AutoJs6 recorre las carpetas y cambia los nombres con el acceso que ya tiene.

#### En qué se diferencia de PowerRename?

Las variables de fecha usan la fecha de modificación, porque la fecha de creación no es fiable en Android. Añade normalización Unicode, conversión de ancho completo, limpieza de caracteres no válidos e historial de lotes que se pueden deshacer. Las variables EXIF de fotos están previstas para la versión 1.1.0.

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

- `Aviso` Versión preliminar de desarrollo: AutoJs6 reconoce el plugin, pero la pantalla de cambio de nombre, las entradas del administrador de archivos y la API de script files.batchRename aún no están disponibles
- `Aviso` Requiere AutoJs6 6.8.0 (5431) o posterior (el número mínimo exacto de compilación se completará cuando se integren los cambios del lado del host)
- `Función` Esqueleto del plugin: activación Wake, servicio de identidad del centro de plugins, servicio de capacidad para scripts y servicio de acciones del administrador de archivos (catálogo de acciones vacío por ahora)
- `Función` README, instrucciones del centro de plugins y registro de cambios en 10 idiomas, generados a partir de fuentes JSON
- `Dependencia` Se agrega Jetpack Compose BOM 2026.09.00
- `Dependencia` Se agrega AndroidX Activity Compose 1.12.2
- `Dependencia` Se agrega AndroidX Core KTX 1.15.0
- `Dependencia` Se agrega Kotlin Coroutines 1.11.0

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
