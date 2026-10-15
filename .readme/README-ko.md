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

> 현재는 개발 미리보기입니다: AutoJs6이 플러그인을 인식하지만, 이름 변경 화면, 파일 관리자 진입점, 스크립트 API는 아직 제공되지 않습니다. 아래 내용은 1.0.0의 설계 목표이며, 진행 상황은 로드맵을 참고하세요.

******

### 기능 하이라이트

******

- 찾기 및 바꾸기: 일반 텍스트 또는 정규식, 모든 일치 항목 또는 첫 번째만, 대소문자 구분 여부를 선택하고 파일 이름, 확장자 또는 전체 이름에 적용합니다.
- 변수: 카운터 (`${start=1;increment=1;padding=3}`), 수정 시간 기반 날짜 변수 (`$YYYY`, `$MM`, `$DD` 등), 임의 문자열을 PowerRename과 같은 문법으로 사용합니다.
- 텍스트 정리: 대소문자 변환, 유니코드 정규화, 전각을 반각으로 변환, 사용할 수 없는 문자 제거, 앞뒤 공백과 마침표 제거, 기존 번호에 이어서 번호 매기기를 지원합니다.
- 먼저 미리 보기: 항목마다 이전 이름과 새 이름을 보여 주고, 개별 항목을 제외할 수 있으며, 충돌과 잘못된 이름을 표시합니다. 충돌 시 전체 거부, 건너뛰기, 자동 번호 붙이기, 덮어쓰기 중에서 고를 수 있습니다.
- 실행 취소: AutoJs6은 최근 20개의 일괄 이름 변경 기록을 보관하며, 파일이 다시 변경되지 않았는지 확인한 뒤 파일 관리자와 스크립트 모두에서 실행 취소할 수 있습니다.

******

### 사용 방법

******

1. AutoJs6을 빌드 5352 (6.8.0) 이상으로 업데이트합니다.
2. AutoJs6 플러그인 센터에서 Batch Rename을 설치하거나 [Releases](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/releases)에서 APK를 내려받아 설치한 뒤, 플러그인 센터에서 승인하고 활성화합니다.
3. 파일 관리자에서 길게 눌러 항목을 선택하고 "일괄 이름 변경"을 누르거나, 스크립트에서 `files.batchRename`을 호출합니다.

> 플러그인은 Android 7.0 (API 24) 이상을 지원하는 단일 범용 APK로 배포되며, 런처에 아이콘을 추가하지 않습니다.

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

스크립트에서는 `files.batchRename`으로 같은 엔진을 사용합니다 (1.0.0 설계 초안이며, 배포된 문서가 우선합니다):

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

- `files.batchRename.plan(paths, options)`은 이름을 바꾸지 않고 계산하여 항목별 결과를 반환합니다. `items[i].excluded`를 바꾼 뒤 계획을 `apply`에 넘길 수 있습니다.
- `files.batchRename(paths, options)`은 한 번에 이름을 바꾸고 배치 ID와 항목별 결과를 반환합니다. `files.batchRename.undo(result)`는 해당 배치를 실행 취소합니다.
- 정규식은 스크립트의 `RegExp`와 똑같이 동작하며, `RegExp` 객체를 그대로 넘길 수 있습니다. `planAsync`, `applyAsync`, `undoAsync`는 Promise를 반환합니다.

******

### 자주 묻는 질문

******

#### 런처에 아이콘이 없는 이유는 무엇인가요?

플러그인에는 독립된 진입점이 없습니다. 설치 후 AutoJs6이 자동으로 찾아내며, 모든 작업은 AutoJs6 파일 관리자나 스크립트에서 이루어집니다.

#### 플러그인에 저장소 권한이 필요한가요?

아니요. 플러그인은 파일 이름으로 새 이름만 계산하고, 폴더 읽기와 실제 이름 변경은 AutoJs6이 이미 가진 권한으로 처리합니다.

#### PowerRename과 무엇이 다른가요?

Android에서는 생성 시간을 신뢰할 수 없어 날짜 변수는 수정 시간을 사용합니다. 또한 유니코드 정규화, 전각 변환, 사용할 수 없는 문자 제거, 실행 취소 가능한 배치 기록을 추가했습니다. 사진 EXIF 변수는 1.1.0에서 제공할 예정입니다.

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
minimum host build: 5352
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

- `힌트` 개발 미리보기: AutoJs6이 플러그인을 인식하지만, 이름 변경 화면, 파일 관리자 진입점, files.batchRename 스크립트 API는 아직 제공되지 않습니다
- `힌트` AutoJs6 6.8.0 (5352) 이상이 필요합니다 (정확한 최소 빌드 번호는 호스트 쪽 변경이 반영된 뒤 채워집니다)
- `기능` 플러그인 골격: Wake 활성화, 플러그인 센터 식별 서비스, 스크립트 기능 서비스, 파일 관리자 동작 서비스 (동작 목록은 현재 비어 있음)
- `기능` JSON 원본에서 생성되는 10개 언어의 README, 플러그인 센터 설명, 변경 로그
- `의존성` Jetpack Compose BOM 2026.09.00 추가
- `의존성` AndroidX Activity Compose 1.12.2 추가
- `의존성` AndroidX Core KTX 1.15.0 추가
- `의존성` Kotlin Coroutines 1.11.0 추가

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
