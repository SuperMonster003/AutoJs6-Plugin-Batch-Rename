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

> هذا إصدار معاينة للتطوير: يتعرف AutoJs6 على الإضافة, لكن شاشة إعادة التسمية ونقاط الدخول في مدير الملفات وواجهة برمجة السكربتات لم تسلم بعد. تصف الأقسام التالية أهداف الإصدار 1.0.0; راجع خارطة الطريق لمعرفة التقدم.

******

### أبرز الميزات

******

- البحث والاستبدال: نص عادي أو تعبيرات نمطية, كل التطابقات أو الأول فقط, مع مراعاة حالة الأحرف أو دونها, ويطبق على اسم الملف أو الامتداد أو الاسم الكامل.
- المتغيرات: عدادات (`${start=1;increment=1;padding=3}`) ومتغيرات تاريخ من وقت التعديل (`$YYYY` و`$MM` و`$DD` وغيرها) وسلاسل عشوائية, بنفس صيغة PowerRename.
- تنظيف النص: تحويل حالة الأحرف, وتوحيد Unicode, وتحويل الأحرف كاملة العرض إلى نصف العرض, وإزالة الأحرف غير المسموح بها, وحذف المسافات والنقاط في البداية والنهاية, ومتابعة الترقيم من تسلسل موجود.
- المعاينة أولا: يعرض كل عنصر اسمه القديم والجديد, ويمكن استبعاد عناصر بعينها, وتميز التعارضات والأسماء غير الصالحة; وعند التعارض يمكن رفض الدفعة أو تخطي العنصر أو إضافة رقم أو الكتابة فوق الملف.
- التراجع: يحتفظ AutoJs6 بآخر 20 دفعة من عمليات إعادة التسمية, ويمكن لمدير الملفات والسكربتات التراجع عنها بعد التحقق من أن الملفات لم تتغير مجددا.

******

### الاستخدام

******

1. حدث AutoJs6 إلى البناء 5352 (6.8.0) أو أحدث.
2. ثبت Batch Rename من مركز الإضافات في AutoJs6, أو نزل ملف APK من [Releases](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/releases), ثم امنحه الإذن وفعله في مركز الإضافات.
3. في مدير الملفات اضغط مطولا لتحديد العناصر ثم اضغط "إعادة التسمية الدفعية", أو استدع `files.batchRename` في سكربت.

> توزع الإضافة كحزمة APK عامة واحدة لنظام Android 7.0 (API 24) وما بعده, ولا تضيف أيقونة إلى المشغل.

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

تستخدم السكربتات المحرك نفسه عبر `files.batchRename` (مسودة تصميم للإصدار 1.0.0; المرجع هو التوثيق المنشور):

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

- تحسب `files.batchRename.plan(paths, options)` النتيجة دون إعادة تسمية وتعيد نتيجة لكل عنصر; عدل `items[i].excluded` ثم مرر الخطة إلى `apply`.
- تعيد `files.batchRename(paths, options)` التسمية في خطوة واحدة وتعيد معرف الدفعة مع نتيجة لكل عنصر; وتتراجع `files.batchRename.undo(result)` عن تلك الدفعة.
- تعمل التعبيرات النمطية تماما مثل `RegExp` في السكربتات, ويمكن تمرير كائنات `RegExp` مباشرة. وتعيد `planAsync` و`applyAsync` و`undoAsync` وعودا (Promise).

******

### الأسئلة الشائعة

******

#### لماذا لا توجد أيقونة في المشغل?

لا تملك الإضافة نقطة دخول مستقلة. يكتشفها AutoJs6 بعد التثبيت, ويتم كل شيء في مدير ملفات AutoJs6 أو في السكربتات.

#### هل تحتاج الإضافة إلى إذن التخزين?

لا. تحسب الإضافة الأسماء الجديدة من أسماء الملفات فقط; ويقرأ AutoJs6 المجلدات ويعيد تسمية الملفات بالصلاحيات التي يملكها أصلا.

#### ما الفرق بينها وبين PowerRename?

تستخدم متغيرات التاريخ وقت التعديل لأن وقت الإنشاء غير موثوق على Android. كما تضيف توحيد Unicode وتحويل الأحرف كاملة العرض وإزالة الأحرف غير المسموح بها وسجل دفعات قابلا للتراجع. ومن المخطط توفير متغيرات EXIF للصور في الإصدار 1.1.0.

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
minimum host build: 5352
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

- `تلميح` معاينة تطوير: يتعرف AutoJs6 على الإضافة, بينما لم تسلم شاشة إعادة التسمية ونقاط الدخول في مدير الملفات وواجهة برمجة السكربتات files.batchRename بعد
- `تلميح` يتطلب AutoJs6 6.8.0 (5352) أو أحدث (يستكمل رقم البناء الأدنى الدقيق بعد اعتماد تغييرات جانب المضيف)
- `ميزة` هيكل الإضافة: التفعيل عبر Wake وخدمة هوية مركز الإضافات وخدمة القدرات للسكربتات وخدمة إجراءات مدير الملفات (قائمة الإجراءات فارغة حاليا)
- `ميزة` README وتعليمات مركز الإضافات وسجل التغييرات بعشر لغات, مولدة من مصادر JSON
- `تبعية` إرفاق Jetpack Compose BOM 2026.09.00
- `تبعية` إرفاق AndroidX Activity Compose 1.12.2
- `تبعية` إرفاق AndroidX Core KTX 1.15.0
- `تبعية` إرفاق Kotlin Coroutines 1.11.0

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
