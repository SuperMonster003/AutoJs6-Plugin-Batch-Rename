<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <picture>
      <source srcset="https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/app/src/main/res/mipmap-night/ic_launcher.png?raw=true" media="(prefers-color-scheme: dark)" />
      <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="autojs6-plugin-batch-rename-ic-launcher" border="0" width="128" />
    </picture>
  </p>

  <p>찾기 및 바꾸기, 정규식, 카운터, 날짜 변수로 파일과 폴더 이름을 일괄 변경</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-Batch-Rename?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-Batch-Rename?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-Batch-Rename?color=534BAE&label=License"/></a>
  </p>
</div>

******

### 언어 (Languages)

******

현재 README.md 는 다음 언어를 지원합니다:

- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-zh-Hant-TW.md)
- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-en.md)
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-fr.md)
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-es.md)
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-ja.md)
- 한국어 [ko] # 현재
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-ru.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-ar.md)

******

### 소개

******

Batch Rename 플러그인은 PowerToys의 [PowerRename](https://learn.microsoft.com/windows/powertoys/powerrename)을 참고한 일괄 이름 변경 기능을 AutoJs6에 추가합니다. 찾기 및 바꾸기, 정규식, 카운터, 날짜 변수와 임의 문자열로 여러 파일과 폴더의 이름을 한 번에 바꾸고, 변경 전에 모든 결과를 미리 보며, 변경 후에는 실행 취소할 수 있습니다.

플러그인은 별도로 설치하는 APK이며 런처 아이콘이 없습니다. 진입점은 두 가지입니다: 파일 관리자의 다중 선택 도구 모음, 폴더 메뉴, 이름 변경 대화상자에 있는 "일괄 이름 변경"과 스크립트의 `files.batchRename`입니다. 플러그인은 새 이름만 계산하고, 실제 이름 변경, 충돌 검사, 실행 취소 기록은 AutoJs6이 처리하므로 플러그인에는 저장소 권한이 필요하지 않습니다.

******

### 기능 하이라이트

******

- 찾기와 바꾸기: 일반 텍스트 또는 JavaScript 정규식, 모든 일치 또는 첫 번째 일치만, 대소문자 구분 여부, 이름, 확장자 또는 둘 다에 적용.
- 변수: 카운터 (`${start=1;increment=1;padding=3}`), 수정 시간이나 현재 시간의 날짜 변수 (`$YYYY`, `$MM`, `$DD` 등)와 임의 문자열. 쓰는 법은 PowerRename 과 같습니다.
- 이름 정리: 대소문자 변환, Unicode 정규화, 전각을 반각으로 변환, 허용되지 않는 문자 바꾸기, 앞뒤 공백 제거, 기존 이름의 번호 이어 붙이기.
- 실행 전 미리 보기: 원래 이름과 새 이름을 항목별로 보여 주며, 체크를 해제해 제외하거나 바뀌는 항목만 볼 수 있습니다. 새 이름이 이미 쓰이고 있으면 바꾸지 않기, 건너뛰기, 번호 붙이기, 기존 항목 바꾸기 중에서 고릅니다.
- 실행 취소: AutoJs6 가 최근 20 개의 이름 바꾸기 묶음을 저장하며, 파일 관리자와 스크립트 모두에서 취소할 수 있습니다. 취소 전에 파일이 다시 수정되었는지 확인합니다.
- 화면은 AutoJs6 의 테마 색, 다크 모드, 언어를 따르며, 마지막 옵션과 최근에 쓴 찾기/바꾸기 텍스트를 기억할 수 있습니다.

******

### 사용 방법

******

1. AutoJs6을 빌드 5431 (6.8.0) 이상으로 업데이트합니다.
2. AutoJs6 플러그인 센터에서 Batch Rename을 설치하거나 [Releases](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/releases)에서 APK를 내려받아 설치한 뒤, 플러그인 센터에서 승인하고 활성화합니다.
3. 파일 관리자에서 길게 눌러 항목을 선택하고 "일괄 이름 변경"을 누르거나, 스크립트에서 `files.batchRename`을 호출합니다.

> 플러그인은 Android 7.0 (API 24) 이상을 지원하는 단일 범용 APK로 배포되며, 런처에 아이콘을 추가하지 않습니다.

******

### 파일 관리자에서 사용하기

******

플러그인을 설치하고 사용하도록 설정하면 AutoJs6 파일 관리자에 세 가지 입구가 생깁니다:

- 여러 항목 선택: 파일이나 폴더를 선택한 뒤 선택 막대의 "일괄 이름 바꾸기"를 누릅니다 (플러그인 동작이 여러 개면 "더" 안에 있습니다).
- 폴더 메뉴: 폴더 메뉴에서 "내부 항목 일괄 이름 바꾸기"를 고르면 그 폴더 안의 항목 이름을 바꿉니다.
- 이름 바꾸기 대화 상자: 파일이나 폴더 하나의 이름을 바꿀 때 대화 상자 오른쪽 위의 "더" 메뉴에서 "일괄 이름 바꾸기..."를 고릅니다.
- 플러그인 화면에서 찾을 내용과 바꿀 내용을 입력하면 미리 보기가 바로 바뀝니다. 체크를 해제해 제외하고 "이름 바꾸기"로 실행하며, 그 뒤 "마지막 일괄 작업 실행 취소"로 원래 이름을 되돌릴 수 있습니다.

******

### 바꾸기 변수

******

바꿀 텍스트에는 다음 변수를 사용할 수 있습니다:

| 변수 | 의미 |
|---|---|
| `${}` `${start=X;increment=X;padding=X}` | 시작 값, 증가 값, 0 채우기 자릿수를 지정하는 카운터로, 실제로 이름이 바뀌는 항목에서만 증가합니다 |
| `$YYYY` `$YY` `$MM` `$MMM` `$DD` `$DDDD` `$hh` `$mm` `$ss` `$fff` | 수정 시간 (또는 현재 시간)의 연, 월, 일, 시, 분, 초, 밀리초 |
| `${rstringalnum=N}` `${rstringalpha=N}` `${rstringdigit=N}` `${ruuidv4}` | 임의의 영숫자, 영문자, 숫자 문자열과 UUID |
| `$1` `$<name>` `$&` `$$` | 정규식 캡처 그룹과 전체 일치 항목으로, JavaScript `String.prototype.replace`와 같은 문법입니다 |

******

### 스크립트 API

******

스크립트에서는 AutoJs6 의 `files.batchRename` 으로 같은 엔진을 사용합니다. 자세한 내용은 [AutoJs6 문서](https://docs.autojs6.com/#/files?id=m-batchrename)를 참고하세요:

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

- `files.batchRename.plan(paths, options)` 은 이름을 바꾸지 않고 계산만 하여 각 항목의 새 이름과 상태를 돌려줍니다. `items[i].excluded` 를 `true` 로 바꾼 뒤 `apply` 에 넘깁니다.
- `files.batchRename(paths, options)` 은 한 번에 처리합니다. `files.batchRename.undo(result)` 는 그 묶음을 취소하고, `files.batchRename.history()` 는 최근 묶음 (파일 관리자의 묶음 포함)을 나열합니다.
- 정규식은 스크립트의 `RegExp` 와 같으며 `RegExp` 객체를 그대로 넘길 수 있습니다. 비동기 형태인 `planAsync` / `applyAsync` / `undoAsync` 도 있으며, 실패하면 `code` 가 있는 `BatchRenameError` 를 던집니다.

******

### 호환성

******

- AutoJs6 6.8.0 (빌드 5431) 이상과 Android 7.0 (API 24) 이상이 필요합니다.
- SD 카드나 USB 메모리 같은 FAT32 / exFAT 저장소는 대소문자를 구분하지 않고 `: * ? " < > |` 와 역슬래시 같은 문자를 허용하지 않습니다. 이런 문자가 들어간 새 이름은 사용할 수 없다고 표시되며, "허용되지 않는 문자 바꾸기"를 켜면 자동으로 바꿉니다.
- 한 묶음은 최대 2000 개 항목입니다. 스크립트에서 지정한 폴더 아래로는 최대 32 단계까지입니다.

******

### 자주 묻는 질문

******

#### 홈 화면에 플러그인 아이콘이 없는 이유는 무엇인가요?

플러그인에는 따로 여는 화면이 없습니다. 설치 후 AutoJs6 가 자동으로 찾아내며, 모든 작업은 AutoJs6 의 파일 관리자나 스크립트에서 합니다.

#### 플러그인에 저장소 권한이 필요한가요?

필요 없습니다. 플러그인은 파일 이름으로 새 이름을 계산할 뿐이며, 폴더 읽기와 실제 이름 바꾸기는 AutoJs6 가 이미 가진 권한으로 처리합니다.

#### PowerRename 과 무엇이 다른가요?

날짜 변수는 수정 시간을 사용합니다 (Android 에서는 생성 시간을 믿을 수 없습니다). 또한 Unicode 정규화, 전각을 반각으로 변환, 허용되지 않는 문자 바꾸기, 취소할 수 있는 묶음 기록을 추가했습니다. 사진 EXIF 변수는 1.1.0 에서 제공할 예정입니다.

#### 정규식은 JavaScript 와 똑같이 쓰나요?

네. 플러그인은 AutoJs6 스크립트와 같은 Rhino 정규식 엔진을 사용하며, 바꿀 텍스트에 `$1`, `$<name>`, `$&` 를 쓸 수 있습니다. 한 번의 일치가 1 초를 넘으면 화면이 멈추지 않도록 중단합니다.

#### 카운터가 늘어나지 않는 이유는 무엇인가요?

먼저 "카운터" 옵션을 켜야 합니다 (스크립트에서는 `enumerate: true`). 끄면 `${}` 가 그대로 남습니다. 카운터는 이름이 바뀌는 항목만 세며, 이름이 그대로이거나 제외된 항목은 번호를 차지하지 않습니다.

#### 이름 바꾸기 대화 상자에 일괄 이름 바꾸기가 보이지 않는 이유는 무엇인가요?

입구는 대화 상자 오른쪽 위의 "더" 메뉴에 있습니다. 메뉴에 안내 항목만 있다면 플러그인이 설치되지 않았거나, 플러그인 센터에서 사용하도록 설정되지 않았거나, AutoJs6 를 업데이트해야 합니다. 안내를 따르면 사용할 수 있습니다.

******

### 권한과 보안

******

플러그인은 권한 범위를 최소화합니다:

- 최소 권한: 매니페스트는 AutoJs6 플러그인 권한 (`org.autojs.permission.PLUGIN`)만 선언하며, 네트워크나 저장소 권한을 요청하지 않습니다.
- 호스트 실행: 플러그인은 이름 변경 계획만 반환하고, AutoJs6이 이름을 바꾸기 전에 모든 항목을 다시 검증합니다. 덮어쓰기 정책을 명시적으로 선택하지 않으면 기존 파일을 덮어쓰지 않습니다.
- 보호된 진입점: 서비스와 이름 변경 화면에는 플러그인 권한이 필요하므로 다른 앱이 직접 호출할 수 없습니다.

******

### 플러그인 인터페이스

******

다음 정보는 AutoJs6 호스트와 플러그인 개발자를 위한 것으로, 호스트는 이 식별자들로 플러그인을 발견하고 기능을 협상합니다:

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

### 개발 로드맵

******

계획, 설계 결정, 진행 상황은 ROADMAP.md의 체크리스트로 관리합니다. 체크되지 않은 항목은 계획이며 현재 버전의 기능이 아닙니다.

- [ROADMAP.md 보기](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/ROADMAP.md)

******

### 릴리스 기록

******

#### v1.0.0

_2026/10/15_

- `힌트` AutoJs6 6.8.0 (5431) 이상이 필요합니다
- `기능` AutoJs6 파일 관리자에서 파일과 폴더 이름을 일괄로 바꾸기 (입구: 여러 항목 선택 도구 모음/폴더 메뉴 "내부 항목 일괄 이름 바꾸기"/이름 바꾸기 대화 상자의 "더" 메뉴)
- `기능` 찾기와 바꾸기: 일반 텍스트 또는 JavaScript 정규식, 모든 일치, 대소문자 구분, 이름이나 확장자만 바꾸기
- `기능` 카운터, 수정 시간이나 현재 시간의 날짜 변수, 임의 문자열과 UUID
- `기능` 대소문자 변환, Unicode 정규화, 전각을 반각으로 변환, 허용되지 않는 문자 바꾸기, 앞뒤 공백 제거, 기존 이름의 번호 이어 붙이기
- `기능` 바꾸기 전에 새 이름을 미리 보고, 항목별로 제외하거나 바뀌는 항목만 볼 수 있으며, 새 이름이 이미 쓰이고 있으면 바꾸지 않기, 건너뛰기, 번호 붙이기, 기존 항목 바꾸기 중에서 고를 수 있습니다
- `기능` 최근 이름 바꾸기 묶음 실행 취소 (AutoJs6 가 최근 20 개를 저장하며 파일 관리자와 스크립트가 함께 사용)
- `기능` AutoJs6 의 files.batchRename 스크립트 API 를 위해 새 이름 계산
- `기능` 화면은 AutoJs6 의 테마 색, 다크 모드, 언어를 따르며, 마지막 옵션과 최근에 쓴 찾기/바꾸기 텍스트를 기억할 수 있습니다
- `의존성` Jetpack Compose BOM 2026.09.00 추가
- `의존성` AndroidX Activity Compose 1.12.2 추가
- `의존성` AndroidX Core KTX 1.15.0 추가
- `의존성` Kotlin Coroutines 1.11.0 추가
- `의존성` Desugar JDK Libs 2.1.5 추가
- `의존성` Rhino (AutoJs6 경량판, 정규식용) 추가

##### 더 많은 릴리스 기록은 다음을 참고

* [CHANGELOG.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/app/src/main/assets/doc/CHANGELOG-ko.md)

******

### 빌드

******

이 절은 소스에서 플러그인을 빌드하려는 개발자를 위한 것입니다.

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest
.\gradlew.bat :app:appendDigestToReleasedFiles
py .python\generate_markdown.py --check
py .python\generate_icon_studio.py --check
py .python\check_repository.py
```

******

### 라이선스

******

이 프로젝트는 [Mozilla Public License 2.0](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/LICENSE)으로 배포됩니다. 기능 설계는 PowerToys PowerRename의 공개 문서를 따르며 그 코드나 리소스는 포함하지 않습니다.

******

### 관련 링크

******

- AutoJs6 프로젝트: https://github.com/SuperMonster003/AutoJs6
- PowerToys PowerRename 문서 (기능 참고): https://learn.microsoft.com/windows/powertoys/powerrename
