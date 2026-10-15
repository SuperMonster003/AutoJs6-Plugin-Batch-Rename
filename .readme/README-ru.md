<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <picture>
      <source srcset="https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/app/src/main/res/mipmap-night/ic_launcher.png?raw=true" media="(prefers-color-scheme: dark)" />
      <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="autojs6-plugin-batch-rename-ic-launcher" border="0" width="128" />
    </picture>
  </p>

  <p>Пакетное переименование файлов и папок с поиском и заменой, регулярными выражениями, счетчиками и переменными даты</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-Batch-Rename?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-Batch-Rename?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-Batch-Rename?color=534BAE&label=License"/></a>
  </p>
</div>

******

### Языки (Languages)

******

Текущий README.md поддерживает следующие языки:

- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-zh-Hant-TW.md)
- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-en.md)
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-fr.md)
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-es.md)
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-ja.md)
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-ko.md)
- Русский [ru] # текущий
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-ar.md)

******

### Введение

******

Плагин Batch Rename добавляет в AutoJs6 пакетное переименование по образцу [PowerRename](https://learn.microsoft.com/windows/powertoys/powerrename) из PowerToys. Он переименовывает сразу много файлов и папок с помощью поиска и замены, регулярных выражений, счетчиков, переменных даты и случайных строк, показывает полный предварительный просмотр до изменений и позволяет затем отменить их.

Плагин устанавливается отдельным APK и не имеет значка в лаунчере. У него две точки входа: "Пакетное переименование" на панели множественного выбора, в меню папки и в диалоге переименования файлового менеджера, а также `files.batchRename` в скриптах. Плагин только вычисляет новые имена; переименование, проверку конфликтов и журнал отмены выполняет AutoJs6, поэтому плагину не нужно разрешение на доступ к хранилищу.

> Это предварительная сборка для разработки: AutoJs6 распознает плагин, но экран переименования, пункты файлового менеджера и скриптовый API еще не готовы. Ниже описаны цели версии 1.0.0; ход работ см. в дорожной карте.

******

### Основные Возможности

******

- Поиск и замена: обычный текст или регулярные выражения, все совпадения или только первое, с учетом регистра или без, для имени файла, расширения или полного имени.
- Переменные: счетчики (`${start=1;increment=1;padding=3}`), переменные даты по времени изменения (`$YYYY`, `$MM`, `$DD` и другие) и случайные строки в том же синтаксисе, что и в PowerRename.
- Очистка текста: смена регистра, нормализация Unicode, преобразование полноширинных символов в обычные, удаление недопустимых символов, обрезка пробелов и точек по краям и нумерация с продолжением существующей последовательности.
- Сначала просмотр: для каждого элемента показаны старое и новое имя, отдельные элементы можно исключить, а конфликты и недопустимые имена выделяются; при конфликте можно отклонить пакет, пропустить элемент, добавить номер или перезаписать.
- Отмена: AutoJs6 хранит последние 20 пакетов переименования; файловый менеджер и скрипты могут отменить их после проверки, что файлы не изменялись повторно.

******

### Использование

******

1. Обновите AutoJs6 до сборки 5352 (6.8.0) или новее.
2. Установите Batch Rename из центра плагинов AutoJs6 или скачайте APK со страницы [Releases](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/releases), затем разрешите и включите его в центре плагинов.
3. В файловом менеджере выделите элементы долгим нажатием и выберите "Пакетное переименование" или вызовите `files.batchRename` в скрипте.

> Плагин поставляется одним универсальным APK для Android 7.0 (API 24) и новее. Он не добавляет значок в лаунчер.

******

### Переменные замены

******

Текст замены может содержать следующие переменные:

| Переменная | Значение |
|---|---|
| `${}` `${start=X;increment=X;padding=X}` | Счетчик с начальным значением, шагом и дополнением нулями; увеличивается только для переименованных элементов |
| `$YYYY` `$YY` `$MM` `$MMM` `$DD` `$DDDD` `$hh` `$mm` `$ss` `$fff` | Год, месяц, день, час, минута, секунда и миллисекунда времени изменения или текущего времени |
| `${rstringalnum=N}` `${rstringalpha=N}` `${rstringdigit=N}` `${ruuidv4}` | Случайные буквенно-цифровые, буквенные или цифровые строки и UUID |
| `$1` `$<name>` `$&` `$$` | Группы захвата и полное совпадение регулярного выражения, как в `String.prototype.replace` в JavaScript |

******

### API Скриптов

******

Скрипты используют тот же механизм через `files.batchRename` (проект дизайна 1.0.0; приоритет имеет опубликованная документация):

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

- `files.batchRename.plan(paths, options)` вычисляет результат без переименования и возвращает его для каждого элемента; измените `items[i].excluded` и передайте план в `apply`.
- `files.batchRename(paths, options)` переименовывает за один шаг и возвращает идентификатор пакета с результатом для каждого элемента; `files.batchRename.undo(result)` отменяет этот пакет.
- Регулярные выражения ведут себя точно так же, как `RegExp` в скриптах, и объекты `RegExp` можно передавать напрямую. `planAsync`, `applyAsync` и `undoAsync` возвращают промисы.

******

### Частые Вопросы

******

#### Почему в лаунчере нет значка?

У плагина нет собственной точки входа. AutoJs6 обнаруживает его после установки, а вся работа выполняется в файловом менеджере AutoJs6 или в скриптах.

#### Нужно ли плагину разрешение на доступ к хранилищу?

Нет. Плагин только вычисляет новые имена по именам файлов; чтение папок и переименование выполняет AutoJs6 с уже имеющимся у него доступом.

#### Чем он отличается от PowerRename?

Переменные даты используют время изменения, так как время создания на Android ненадежно. Добавлены нормализация Unicode, преобразование полноширинных символов, удаление недопустимых символов и журнал пакетов с возможностью отмены. Переменные EXIF для фотографий запланированы на версию 1.1.0.

******

### Разрешения И Безопасность

******

Плагин сводит свои разрешения к минимуму:

- Минимальные привилегии: манифест объявляет только разрешение плагина AutoJs6 (`org.autojs.permission.PLUGIN`) без разрешений на сеть и хранилище.
- Выполнение на стороне хоста: плагин возвращает только план переименования; AutoJs6 повторно проверяет каждый элемент перед переименованием и не перезаписывает существующие файлы, если политика перезаписи не выбрана явно.
- Защищенные точки входа: службы и экран переименования требуют разрешения плагина, поэтому другие приложения не могут вызвать их напрямую.

******

### Интерфейс Плагина

******

Следующая информация предназначена разработчикам хоста AutoJs6 и плагинов; хост использует эти идентификаторы для обнаружения плагина и согласования возможностей:

```text
application id: io.github.supermonster003.autojs6.plugin.batch.rename
plugin id: batch-rename
engine: batch-rename
variant: default
info service: org.autojs.plugin.INFO (category batch-rename)
capability service: org.autojs.plugin.BATCH_RENAME (category batch-rename)
file manager service: org.autojs.plugin.EXPLORER_ACTION
wake action: org.autojs.plugin.action.WAKE
minimum host build: 5352
native library: libandroidx.graphics.path.so (Jetpack Compose, all ABIs)
```

******

### Дорожная Карта

******

Планы, проектные решения и ход работ ведутся в виде списка с отметками в ROADMAP.md. Неотмеченные пункты являются планами, а не возможностями текущей версии.

- [Открыть ROADMAP.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/ROADMAP.md)

******

### История Выпусков

******

#### v1.0.0

_2026/10/15_

- `Подсказка` Предварительная сборка для разработки: AutoJs6 распознает плагин, но экран переименования, пункты файлового менеджера и скриптовый API files.batchRename еще не готовы
- `Подсказка` Требуется AutoJs6 6.8.0 (5352) или новее (точный минимальный номер сборки будет указан после внесения изменений на стороне хоста)
- `Функция` Каркас плагина: активация через Wake, служба идентификации для центра плагинов, служба возможностей для скриптов и служба действий файлового менеджера (каталог действий пока пуст)
- `Функция` README, описание для центра плагинов и журнал изменений на 10 языках, генерируемые из JSON-исходников
- `Зависимость` Добавлен Jetpack Compose BOM 2026.09.00
- `Зависимость` Добавлен AndroidX Activity Compose 1.12.2
- `Зависимость` Добавлен AndroidX Core KTX 1.15.0
- `Зависимость` Добавлен Kotlin Coroutines 1.11.0

##### Больше истории выпусков

* [CHANGELOG.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/app/src/main/assets/doc/CHANGELOG-ru.md)

******

### Сборка

******

Этот раздел предназначен разработчикам, желающим собрать плагин из исходников.

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest
.\gradlew.bat :app:appendDigestToReleasedFiles
py .python\generate_markdown.py --check
py .python\generate_icon_studio.py --check
py .python\check_repository.py
```

******

### Лицензия

******

Проект распространяется по лицензии [Mozilla Public License 2.0](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/LICENSE). Функции спроектированы по общедоступной документации PowerToys PowerRename и не содержат ее кода или ресурсов.

******

### Ссылки

******

- Проект AutoJs6: https://github.com/SuperMonster003/AutoJs6
- Документация PowerToys PowerRename (функциональный ориентир): https://learn.microsoft.com/windows/powertoys/powerrename
