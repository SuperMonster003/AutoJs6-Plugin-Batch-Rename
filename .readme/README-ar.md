<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <picture>
      <source srcset="https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/app/src/main/res/mipmap-night/ic_launcher.png?raw=true" media="(prefers-color-scheme: dark)" />
      <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="autojs6-plugin-batch-rename-ic-launcher" border="0" width="128" />
    </picture>
  </p>

  <p>إعادة تسمية الملفات والمجلدات دفعة واحدة باستخدام البحث والاستبدال والتعبيرات النمطية والعدادات ومتغيرات التاريخ</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-Batch-Rename?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-Batch-Rename?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-Batch-Rename?color=534BAE&label=License"/></a>
  </p>
</div>

******

### اللغات (Languages)

******

يدعم README.md الحالي اللغات التالية:

- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-zh-Hant-TW.md)
- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-en.md)
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-fr.md)
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-es.md)
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-ja.md)
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-ru.md)
- العربية [ar] # الحالية

******

### مقدمة

******

تضيف إضافة Batch Rename إلى AutoJs6 إعادة التسمية الدفعية على غرار [PowerRename](https://learn.microsoft.com/windows/powertoys/powerrename) من PowerToys. فهي تعيد تسمية عدد كبير من الملفات والمجلدات دفعة واحدة باستخدام البحث والاستبدال والتعبيرات النمطية والعدادات ومتغيرات التاريخ والسلاسل العشوائية, وتعرض معاينة كاملة قبل أي تغيير, وتتيح التراجع بعده.

الإضافة حزمة APK تثبت بشكل مستقل ولا تملك أيقونة في المشغل. لها نقطتا دخول: "إعادة التسمية الدفعية" في شريط التحديد المتعدد وقائمة المجلد ومربع حوار إعادة التسمية في مدير الملفات, و`files.batchRename` في السكربتات. تحسب الإضافة الأسماء الجديدة فقط; أما إعادة التسمية الفعلية وفحص التعارضات وسجل التراجع فيتولاها AutoJs6, لذلك لا تحتاج الإضافة إلى أي إذن تخزين.

******

### أبرز الميزات

******

- البحث والاستبدال: نص عادي أو تعبيرات JavaScript النمطية, كل التطابقات أو الأول فقط, مع مراعاة حالة الأحرف أو دونها, على الاسم أو الامتداد أو كليهما.
- المتغيرات: العدادات (`${start=1;increment=1;padding=3}`) ومتغيرات التاريخ من وقت التعديل أو الوقت الحالي (`$YYYY` و `$MM` و `$DD` وغيرها) والسلاسل العشوائية, بالكتابة نفسها في PowerRename.
- تنظيف الأسماء: تحويل حالة الأحرف وتسوية Unicode وتحويل الأحرف كاملة العرض إلى نصف العرض واستبدال الأحرف غير المسموح بها وإزالة المسافات من الطرفين ومتابعة ترقيم الأسماء الموجودة.
- معاينة قبل التنفيذ: الاسم القديم والجديد جنبا إلى جنب, وإلغاء التحديد يستبعد العنصر, ويمكن عرض العناصر التي تتغير فقط; وإذا كان الاسم الجديد مستخدما يمكن الرفض أو التخطي أو إضافة رقم أو استبدال العنصر الموجود.
- التراجع: يحتفظ AutoJs6 بآخر 20 دفعة من إعادة التسمية, ويمكن التراجع عنها من مدير الملفات ومن السكربتات, ويتحقق قبل التراجع من أن الملف لم يتغير مرة أخرى.
- تتبع الشاشة لون سمة AutoJs6 والوضع الداكن واللغة, ويمكنها تذكر آخر الخيارات ونصوص البحث والاستبدال الأخيرة.

******

### الاستخدام

******

1. حدث AutoJs6 إلى البناء 5431 (6.8.0) أو أحدث.
2. ثبت Batch Rename من مركز الإضافات في AutoJs6, أو نزل ملف APK من [Releases](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/releases), ثم امنحه الإذن وفعله في مركز الإضافات.
3. في مدير الملفات اضغط مطولا لتحديد العناصر ثم اضغط "إعادة التسمية الدفعية", أو استدع `files.batchRename` في سكربت.

> توزع الإضافة كحزمة APK عامة واحدة لنظام Android 7.0 (API 24) وما بعده, ولا تضيف أيقونة إلى المشغل.

******

### في مدير الملفات

******

بعد تثبيت المكون الإضافي وتفعيله يوفر مدير ملفات AutoJs6 ثلاثة مداخل:

- التحديد المتعدد: حدد ملفات أو مجلدات ثم اضغط "إعادة تسمية دفعية" في شريط التحديد (ضمن "أكثر" عند توفر عدة إجراءات للمكونات الإضافية).
- قائمة المجلد: اختر "إعادة تسمية العناصر الداخلية دفعة واحدة" من قائمة المجلد لإعادة تسمية محتوياته.
- مربع حوار إعادة التسمية: عند إعادة تسمية ملف أو مجلد واحد افتح قائمة "أكثر" في الزاوية العلوية من مربع الحوار واختر "إعادة تسمية دفعية...".
- اكتب ما تريد البحث عنه واستبداله في شاشة المكون الإضافي فتتحدث المعاينة فورا; ألغ تحديد العناصر لاستبعادها ثم اضغط "إعادة التسمية", وبعدها "التراجع عن آخر دفعة" لاستعادة الأسماء السابقة.

******

### متغيرات الاستبدال

******

يمكن أن يحتوي نص الاستبدال على المتغيرات التالية:

| المتغير | المعنى |
|---|---|
| `${}` `${start=X;increment=X;padding=X}` | عداد بقيمة بداية وخطوة وحشو بالأصفار, ولا يتقدم إلا للعناصر التي يعاد تسميتها |
| `$YYYY` `$YY` `$MM` `$MMM` `$DD` `$DDDD` `$hh` `$mm` `$ss` `$fff` | السنة والشهر واليوم والساعة والدقيقة والثانية والمللي ثانية لوقت التعديل أو للوقت الحالي |
| `${rstringalnum=N}` `${rstringalpha=N}` `${rstringdigit=N}` `${ruuidv4}` | سلاسل عشوائية أبجدية رقمية أو أبجدية أو رقمية ومعرفات UUID |
| `$1` `$<name>` `$&` `$$` | مجموعات الالتقاط والتطابق الكامل للتعبير النمطي, كما في `String.prototype.replace` في JavaScript |

******

### واجهة برمجة النصوص

******

تستخدم السكربتات المحرك نفسه عبر `files.batchRename` في AutoJs6; راجع [وثائق AutoJs6](https://docs.autojs6.com/#/files?id=m-batchrename) للمرجع الكامل:

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

- `files.batchRename.plan(paths, options)` يحسب دون إعادة تسمية ويعيد الاسم الجديد والحالة لكل عنصر; اجعل `items[i].excluded` يساوي `true` ثم مرر الخطة إلى `apply`.
- `files.batchRename(paths, options)` ينفذ كل شيء في خطوة واحدة; `files.batchRename.undo(result)` يتراجع عن الدفعة و `files.batchRename.history()` يسرد الدفعات الأخيرة (بما فيها دفعات مدير الملفات).
- تعمل التعبيرات النمطية مثل `RegExp` في السكربتات ويمكن تمرير كائن `RegExp` مباشرة. تتوفر الأشكال غير المتزامنة `planAsync` / `applyAsync` / `undoAsync`, وعند الفشل يلقى `BatchRenameError` يحمل `code`.

******

### التوافق

******

- يتطلب AutoJs6 6.8.0 (الإصدار 5431) أو أحدث و Android 7.0 (API 24) أو أحدث.
- وحدات التخزين FAT32 / exFAT مثل بطاقات SD ومحركات USB لا تميز حالة الأحرف ولا تسمح بالأحرف `: * ? " < > |` والشرطة المائلة العكسية وما شابهها; تعلم الأسماء الجديدة التي تحتويها على أنها غير صالحة, ويستبدلها خيار "استبدال الأحرف غير المسموح بها" تلقائيا.
- تضم الدفعة الواحدة 2000 عنصر كحد أقصى, وتصل السكربتات إلى 32 مستوى كحد أقصى تحت المجلدات المحددة.

******

### الأسئلة الشائعة

******

#### لماذا لا توجد أيقونة للمكون الإضافي على الشاشة الرئيسية?

ليس للمكون الإضافي شاشة مستقلة تفتح منها. يكتشفه AutoJs6 بعد التثبيت, ويجري كل العمل في مدير ملفات AutoJs6 أو في السكربتات.

#### هل يحتاج المكون الإضافي إلى إذن التخزين?

لا. يحسب المكون الإضافي الأسماء الجديدة من أسماء الملفات فقط, بينما يقرأ AutoJs6 المجلدات وينفذ إعادة التسمية بالأذونات التي يملكها بالفعل.

#### ما الفرق عن PowerRename?

تستخدم متغيرات التاريخ وقت التعديل (وقت الإنشاء غير موثوق في Android), وأضيفت تسوية Unicode وتحويل الأحرف كاملة العرض إلى نصف العرض واستبدال الأحرف غير المسموح بها ودفعات يمكن التراجع عنها. متغيرات EXIF للصور مخطط لها في 1.1.0.

#### هل تكتب التعبيرات النمطية كما في JavaScript?

نعم. يستخدم المكون الإضافي محرك التعبيرات النمطية Rhino نفسه الذي تستخدمه سكربتات AutoJs6, ويمكن استخدام `$1` و `$<name>` و `$&` في نص الاستبدال. يوقف أي تطابق يستغرق أكثر من ثانية واحدة حتى لا تتجمد الشاشة.

#### لماذا لا يزداد العداد?

فعل خيار "العداد" أولا (`enumerate: true` في السكربتات), وإلا يبقى `${}` كما هو. يعد العداد العناصر التي تتغير أسماؤها فقط, ولا تأخذ العناصر غير المتغيرة أو المستبعدة رقما.

#### لماذا لا تظهر إعادة التسمية الدفعية في مربع حوار إعادة التسمية?

توجد في قائمة "أكثر" في الزاوية العلوية من مربع الحوار. إذا لم تعرض القائمة سوى دليل, فالمكون الإضافي غير مثبت أو غير مفعل في مركز المكونات الإضافية أو يحتاج AutoJs6 إلى تحديث; اتبع الدليل لتصبح متاحة.

******

### الأذونات والأمان

******

تقلل الإضافة صلاحياتها إلى الحد الأدنى:

- أقل صلاحية: يعلن البيان عن إذن إضافة AutoJs6 فقط (`org.autojs.permission.PLUGIN`), دون أذونات الشبكة أو التخزين.
- التنفيذ على المضيف: تعيد الإضافة خطة إعادة التسمية فقط; ويعيد AutoJs6 التحقق من كل عنصر قبل إعادة التسمية, ولا يكتب فوق الملفات الموجودة إلا إذا اختيرت سياسة الكتابة فوقها صراحة.
- نقاط دخول محمية: تتطلب الخدمات وشاشة إعادة التسمية إذن الإضافة, لذلك لا تستطيع التطبيقات الأخرى استدعاءها مباشرة.

******

### واجهة المكون الإضافي

******

المعلومات التالية موجهة لمطوري مضيف AutoJs6 والمكونات الإضافية; يستخدم المضيف هذه المعرفات لاكتشاف المكون والتفاوض على القدرات:

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

### خارطة الطريق

******

تتابع الخطط وقرارات التصميم والتقدم في شكل قائمة تحقق في ROADMAP.md. العناصر غير المحددة خطط وليست ميزات في الإصدار الحالي.

- [عرض ROADMAP.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/ROADMAP.md)

******

### سجل الإصدارات

******

#### v1.0.0

_2026/10/15_

- `تلميح` يتطلب AutoJs6 6.8.0 (5431) أو أحدث
- `ميزة` إعادة تسمية دفعية للملفات والمجلدات في مدير ملفات AutoJs6 (المداخل: شريط التحديد المتعدد/قائمة المجلد "إعادة تسمية العناصر الداخلية دفعة واحدة"/قائمة "أكثر" في مربع حوار إعادة التسمية)
- `ميزة` البحث والاستبدال بنص عادي أو بتعبيرات JavaScript النمطية, مع كل التطابقات ومراعاة حالة الأحرف وتغيير الاسم أو الامتداد فقط
- `ميزة` العدادات ومتغيرات التاريخ من وقت التعديل أو الوقت الحالي والسلاسل العشوائية و UUID
- `ميزة` تحويل حالة الأحرف وتسوية Unicode وتحويل الأحرف كاملة العرض إلى نصف العرض واستبدال الأحرف غير المسموح بها وإزالة المسافات من الطرفين ومتابعة ترقيم الأسماء الموجودة
- `ميزة` معاينة الأسماء الجديدة قبل إعادة التسمية واستبعاد العناصر فرديا أو عرض العناصر التي تتغير فقط, مع إمكانية الرفض أو التخطي أو إضافة رقم أو استبدال العنصر الموجود عندما يكون الاسم الجديد مستخدما
- `ميزة` التراجع عن دفعات إعادة التسمية الأخيرة (يحتفظ AutoJs6 بآخر 20 دفعة يتشاركها مدير الملفات والسكربتات)
- `ميزة` حساب الأسماء الجديدة لواجهة السكربتات files.batchRename في AutoJs6
- `ميزة` تتبع الشاشة لون سمة AutoJs6 والوضع الداكن واللغة, ويمكنها تذكر آخر الخيارات ونصوص البحث والاستبدال الأخيرة
- `تبعية` إرفاق Jetpack Compose BOM 2026.09.00
- `تبعية` إرفاق AndroidX Activity Compose 1.12.2
- `تبعية` إرفاق AndroidX Core KTX 1.15.0
- `تبعية` إرفاق Kotlin Coroutines 1.11.0
- `تبعية` إرفاق Desugar JDK Libs 2.1.5
- `تبعية` إرفاق Rhino (النسخة المصغرة من AutoJs6, للتعبيرات النمطية)

##### لمزيد من سجل الإصدارات

* [CHANGELOG.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/app/src/main/assets/doc/CHANGELOG-ar.md)

******

### البناء

******

هذا القسم موجه للمطورين الراغبين في بناء المكون من المصدر.

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest
.\gradlew.bat :app:appendDigestToReleasedFiles
py .python\generate_markdown.py --check
py .python\generate_icon_studio.py --check
py .python\check_repository.py
```

******

### الترخيص

******

يوزع المشروع بموجب [Mozilla Public License 2.0](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/LICENSE). يتبع تصميم الميزات التوثيق العام لأداة PowerToys PowerRename ولا يتضمن شيئا من شيفرتها أو مواردها.

******

### روابط

******

- مشروع AutoJs6: https://github.com/SuperMonster003/AutoJs6
- توثيق PowerToys PowerRename (مرجع الميزات): https://learn.microsoft.com/windows/powertoys/powerrename
