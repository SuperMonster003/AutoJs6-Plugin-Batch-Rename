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

******

### Основные Возможности

******

- Поиск и замена: обычный текст или регулярные выражения JavaScript, все совпадения или только первое, с учетом регистра или без, для имени, расширения или того и другого.
- Переменные: счетчики (`${start=1;increment=1;padding=3}`), переменные даты по времени изменения или текущему времени (`$YYYY`, `$MM`, `$DD` и другие) и случайные строки, как в PowerRename.
- Очистка имен: смена регистра, нормализация Unicode, перевод полноширинных символов в обычные, замена недопустимых символов, удаление пробелов по краям и продолжение нумерации существующих имен.
- Предпросмотр перед переименованием: старые и новые имена рядом, снятие отметки исключает элемент, можно показать только изменяемые элементы; если новое имя занято, можно отказаться, пропустить, добавить номер или заменить существующий элемент.
- Отмена: AutoJs6 хранит последние 20 пакетов переименования, их можно отменить из файлового менеджера и из скриптов; перед отменой проверяется, не изменился ли файл снова.
- Экран следует цвету темы, темному режиму и языку AutoJs6 и может запоминать последние параметры и недавние тексты поиска и замены.

******

### Использование

******

1. Обновите AutoJs6 до сборки 5438 (6.8.0) или новее.
2. Установите Batch Rename из центра плагинов AutoJs6 или скачайте APK со страницы [Releases](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/releases), затем разрешите и включите его в центре плагинов.
3. В файловом менеджере выделите элементы долгим нажатием и выберите "Пакетное переименование" или вызовите `files.batchRename` в скрипте.

> Плагин поставляется одним универсальным APK для Android 7.0 (API 24) и новее. Он не добавляет значок в лаунчер.

******

### В файловом менеджере

******

После установки и включения плагина в файловом менеджере AutoJs6 появляются три точки входа:

- Множественный выбор: выберите файлы или папки и нажмите "Пакетное переименование" на панели выбора (в меню "Больше", если доступно несколько действий плагинов).
- Меню папки: выберите "Пакетно переименовать содержимое" в меню папки, чтобы переименовать ее содержимое.
- Диалог переименования: при переименовании одного файла или папки откройте меню "Больше" в правом верхнем углу диалога и выберите "Пакетное переименование...".
- На экране плагина введите текст поиска и замены, и предпросмотр сразу обновится; снимите отметки, чтобы исключить элементы, нажмите "Переименовать", а затем "Отменить последний пакет", чтобы вернуть прежние имена.

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

Скрипты используют тот же механизм через `files.batchRename` в AutoJs6; полное описание см. в [документации AutoJs6](https://docs.autojs6.com/#/files?id=m-batchrename):

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

- `files.batchRename.plan(paths, options)` только вычисляет, ничего не переименовывая, и возвращает новое имя и состояние каждого элемента; установите `items[i].excluded` в `true` и передайте план в `apply`.
- `files.batchRename(paths, options)` делает все за один шаг; `files.batchRename.undo(result)` отменяет пакет, а `files.batchRename.history()` перечисляет последние пакеты (включая пакеты файлового менеджера).
- Регулярные выражения работают так же, как `RegExp` в скриптах, объект `RegExp` можно передать напрямую. Есть асинхронные формы `planAsync` / `applyAsync` / `undoAsync`; при ошибке выбрасывается `BatchRenameError` с полем `code`.

******

### Совместимость

******

- Требуется AutoJs6 6.8.0 (сборка 5438) или новее и Android 7.0 (API 24) или новее.
- Хранилища FAT32 / exFAT, например SD-карты и USB-накопители, не различают регистр и не допускают `: * ? " < > |`, обратную косую черту и подобные символы; новые имена с ними помечаются как непригодные, а параметр "Заменять недопустимые символы" заменяет их автоматически.
- В одном пакете не более 2000 элементов; скрипт обрабатывает не более 32 уровней ниже указанных папок.

******

### Частые Вопросы

******

#### Почему на главном экране нет значка плагина?

У плагина нет собственного экрана для запуска. AutoJs6 находит его после установки, а вся работа идет в файловом менеджере AutoJs6 или в скриптах.

#### Нужно ли плагину разрешение на доступ к хранилищу?

Нет. Плагин лишь вычисляет новые имена по именам файлов; чтение папок и само переименование выполняет AutoJs6 с уже имеющимися у него разрешениями.

#### Чем он отличается от PowerRename?

Переменные даты используют время изменения (время создания на Android ненадежно); добавлены нормализация Unicode, перевод полноширинных символов в обычные, замена недопустимых символов и отменяемые пакеты. Переменные EXIF для фотографий планируются в 1.1.0.

#### Регулярные выражения пишутся так же, как в JavaScript?

Да. Плагин использует тот же движок регулярных выражений Rhino, что и скрипты AutoJs6, а в тексте замены доступны `$1`, `$<name>` и `$&`. Отдельное сопоставление дольше 1 секунды прерывается, чтобы экран не зависал.

#### Почему счетчик не увеличивается?

Сначала включите параметр "Счетчик" (`enumerate: true` в скриптах), иначе `${}` останется как есть. Счетчик учитывает только элементы, имена которых меняются; неизмененные и исключенные элементы номер не занимают.

#### Почему в диалоге переименования нет пакетного переименования?

Оно находится в меню "Больше" в правом верхнем углу диалога. Если в меню только подсказка, плагин не установлен, не включен в центре плагинов или нужно обновить AutoJs6; после выполнения подсказки функция станет доступна.

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
minimum host build: 5438
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

- `Подсказка` Требуется AutoJs6 6.8.0 (5438) или новее
- `Функция` Пакетное переименование файлов и папок в файловом менеджере AutoJs6 (точки входа: панель множественного выбора/меню папки "Пакетно переименовать содержимое"/меню "Больше" диалога переименования)
- `Функция` Поиск и замена: обычный текст или регулярные выражения JavaScript, все совпадения, учет регистра, переименование только имени или расширения
- `Функция` Счетчики, переменные даты по времени изменения или текущему времени, случайные строки и UUID
- `Функция` Смена регистра, нормализация Unicode, перевод полноширинных символов в обычные, замена недопустимых символов, удаление пробелов по краям и продолжение нумерации существующих имен
- `Функция` Предпросмотр новых имен перед переименованием, исключение отдельных элементов или показ только изменяемых, а если новое имя занято, выбор: отказаться, пропустить, добавить номер или заменить существующий элемент
- `Функция` Отмена последних пакетов переименования (AutoJs6 хранит последние 20, общие для файлового менеджера и скриптов)
- `Функция` Вычисление новых имен для API скриптов files.batchRename в AutoJs6
- `Функция` Экран следует цвету темы, темному режиму и языку AutoJs6 и может запоминать последние параметры и недавние тексты поиска и замены
- `Зависимость` Добавлен Jetpack Compose BOM 2026.09.00
- `Зависимость` Добавлен AndroidX Activity Compose 1.12.2
- `Зависимость` Добавлен AndroidX Core KTX 1.15.0
- `Зависимость` Добавлен Kotlin Coroutines 1.11.0
- `Зависимость` Добавлен Desugar JDK Libs 2.1.5
- `Зависимость` Добавлен Rhino (облегченная сборка AutoJs6, для регулярных выражений)

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
