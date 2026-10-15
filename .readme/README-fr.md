<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <picture>
      <source srcset="https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/app/src/main/res/mipmap-night/ic_launcher.png?raw=true" media="(prefers-color-scheme: dark)" />
      <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="autojs6-plugin-batch-rename-ic-launcher" border="0" width="128" />
    </picture>
  </p>

  <p>Renomme des fichiers et des dossiers par lot avec rechercher-remplacer, expressions régulières, compteurs et variables de date</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-Batch-Rename?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-Batch-Rename?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-Batch-Rename?color=534BAE&label=License"/></a>
  </p>
</div>

******

### Langues (Languages)

******

Le README.md actuel prend en charge les langues suivantes:

- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-zh-Hant-TW.md)
- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-en.md)
- Français [fr] # actuel
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-es.md)
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-ja.md)
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-ru.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/.readme/README-ar.md)

******

### Introduction

******

Le plugin Batch Rename ajoute le renommage par lot à AutoJs6, sur le modèle de [PowerRename](https://learn.microsoft.com/windows/powertoys/powerrename) de PowerToys. Il renomme en une fois de nombreux fichiers et dossiers avec rechercher-remplacer, expressions régulières, compteurs, variables de date et chaînes aléatoires, affiche un aperçu complet avant toute modification et permet d'annuler ensuite.

Le plugin est un APK installé séparément, sans icône de lanceur. Il offre deux points d'entrée: "Renommer par lot" dans la barre de sélection multiple, le menu des dossiers et la boîte de dialogue de renommage du gestionnaire de fichiers, et `files.batchRename` dans les scripts. Le plugin calcule seulement les nouveaux noms; AutoJs6 effectue les renommages, les contrôles de conflits et l'historique d'annulation, le plugin n'a donc besoin d'aucune autorisation de stockage.

******

### Points Forts

******

- Rechercher et remplacer: texte brut ou expressions régulières JavaScript, toutes les occurrences ou seulement la première, avec ou sans respect de la casse, appliqué au nom, à l'extension ou aux deux.
- Variables: compteurs (`${start=1;increment=1;padding=3}`), variables de date tirées de la date de modification ou de l'heure actuelle (`$YYYY`, `$MM`, `$DD`, etc.) et chaînes aléatoires, avec la même syntaxe que PowerRename.
- Nettoyage des noms: changement de casse, normalisation Unicode, conversion de la pleine chasse en demi-chasse, remplacement des caractères interdits, suppression des espaces aux extrémités et numérotation qui prolonge les noms existants.
- Aperçu avant de renommer: anciens et nouveaux noms côte à côte, décochez des éléments pour les exclure ou n'affichez que ceux qui changent; si un nouveau nom est pris, refuser, ignorer, ajouter un numéro ou remplacer l'élément existant.
- Annulation: AutoJs6 conserve les 20 derniers lots de renommage, annulables depuis le gestionnaire de fichiers comme depuis les scripts; l'annulation vérifie d'abord qu'un fichier n'a pas été modifié entre-temps.
- L'écran suit la couleur du thème, le mode sombre et la langue d'AutoJs6, et peut mémoriser les dernières options ainsi que les textes de recherche et de remplacement récents.

******

### Utilisation

******

1. Mettez à jour AutoJs6 vers la build 5438 (6.8.0) ou ultérieure.
2. Installez Batch Rename depuis le centre de plugins d'AutoJs6, ou téléchargez l'APK depuis [Releases](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/releases), puis autorisez-le et activez-le dans le centre de plugins.
3. Dans le gestionnaire de fichiers, appuyez longuement pour sélectionner des éléments puis touchez "Renommer par lot", ou appelez `files.batchRename` dans un script.

> Le plugin est distribué sous forme d'un seul APK universel pour Android 7.0 (API 24) et versions ultérieures. Il n'ajoute pas d'icône au lanceur.

******

### Dans le gestionnaire de fichiers

******

Une fois le plugin installé et activé, le gestionnaire de fichiers d'AutoJs6 propose trois entrées:

- Sélection multiple: sélectionnez des fichiers ou des dossiers et appuyez sur "Renommer par lot" dans la barre de sélection (sous "Plus" si plusieurs actions de plugins sont disponibles).
- Menu d'un dossier: choisissez "Renommer par lot les éléments du dossier" dans le menu d'un dossier pour renommer son contenu.
- Boîte de dialogue de renommage: lorsque vous renommez un seul fichier ou dossier, ouvrez le menu "Plus" en haut à droite de la boîte de dialogue et choisissez "Renommer par lot...".
- Saisissez la recherche et le remplacement sur l'écran du plugin, l'aperçu se met à jour aussitôt; décochez des éléments pour les exclure, appuyez sur "Renommer", puis sur "Annuler le dernier lot" pour rétablir les anciens noms.

******

### Variables de remplacement

******

Le texte de remplacement peut contenir les variables suivantes:

| Variable | Signification |
|---|---|
| `${}` `${start=X;increment=X;padding=X}` | Compteur avec valeur de départ, pas et remplissage par des zéros; il n'avance que pour les éléments renommés |
| `$YYYY` `$YY` `$MM` `$MMM` `$DD` `$DDDD` `$hh` `$mm` `$ss` `$fff` | Année, mois, jour, heure, minute, seconde et milliseconde de la date de modification, ou de l'heure actuelle |
| `${rstringalnum=N}` `${rstringalpha=N}` `${rstringdigit=N}` `${ruuidv4}` | Chaînes aléatoires alphanumériques, alphabétiques ou numériques et UUID |
| `$1` `$<name>` `$&` `$$` | Groupes de capture et correspondance complète, comme dans `String.prototype.replace` de JavaScript |

******

### API De Script

******

Les scripts utilisent le même moteur via `files.batchRename` d'AutoJs6; consultez la [documentation d'AutoJs6](https://docs.autojs6.com/#/files?id=m-batchrename) pour la référence complète:

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

- `files.batchRename.plan(paths, options)` calcule sans renommer et renvoie le nouveau nom et l'état de chaque élément; mettez `items[i].excluded` à `true` puis passez le plan à `apply`.
- `files.batchRename(paths, options)` fait tout en une étape; `files.batchRename.undo(result)` annule le lot et `files.batchRename.history()` liste les lots récents (y compris ceux du gestionnaire de fichiers).
- Les expressions régulières se comportent comme `RegExp` dans les scripts, et un objet `RegExp` peut être passé directement. `planAsync` / `applyAsync` / `undoAsync` sont les formes asynchrones; les échecs lèvent une `BatchRenameError` avec un `code`.

******

### Compatibilité

******

- Nécessite AutoJs6 6.8.0 (build 5438) ou ultérieur et Android 7.0 (API 24) ou ultérieur.
- Les stockages FAT32 / exFAT, comme les cartes SD et les clés USB, ignorent la casse et n'acceptent pas `: * ? " < > |`, la barre oblique inverse et les caractères semblables; les nouveaux noms qui en contiennent sont signalés comme inutilisables, et "Remplacer les caractères interdits" les remplace automatiquement.
- Un lot contient au plus 2000 éléments; les scripts descendent au plus de 32 niveaux sous les dossiers indiqués.

******

### FAQ

******

#### Pourquoi n'y a-t-il pas d'icône du plugin sur l'écran d'accueil?

Le plugin n'a pas d'écran propre à lancer. AutoJs6 le détecte après l'installation et tout se fait dans le gestionnaire de fichiers d'AutoJs6 ou dans les scripts.

#### Le plugin a-t-il besoin de l'autorisation de stockage?

Non. Le plugin calcule seulement de nouveaux noms à partir des noms de fichiers; AutoJs6 lit les dossiers et effectue les renommages avec les autorisations qu'il possède déjà.

#### En quoi diffère-t-il de PowerRename?

Les variables de date utilisent la date de modification (la date de création n'est pas fiable sur Android), et s'y ajoutent la normalisation Unicode, la conversion de la pleine chasse en demi-chasse, le remplacement des caractères interdits et des lots annulables. Les variables EXIF des photos sont prévues pour la 1.1.0.

#### Les expressions régulières s'écrivent-elles comme en JavaScript?

Oui. Le plugin utilise le même moteur d'expressions régulières Rhino que les scripts AutoJs6, et le remplacement accepte `$1`, `$<name>` et `$&`. Une correspondance qui dure plus d'une seconde est interrompue pour que l'écran ne se fige pas.

#### Pourquoi le compteur n'augmente-t-il pas?

Activez d'abord l'option "Compteur" (`enumerate: true` dans les scripts), sinon `${}` reste tel quel. Le compteur ne compte que les éléments dont le nom change; les éléments inchangés ou exclus ne prennent pas de numéro.

#### Pourquoi le renommage par lot n'apparaît-il pas dans la boîte de dialogue de renommage?

Il se trouve dans le menu "Plus" en haut à droite de la boîte de dialogue. Si le menu n'affiche qu'un guide, le plugin n'est pas installé, n'est pas activé dans le centre des plugins ou AutoJs6 doit être mis à jour; suivez le guide et il deviendra disponible.

******

### Permissions Et Sécurité

******

Le plugin limite au maximum ses autorisations:

- Moindre privilège: le manifeste déclare uniquement l'autorisation de plugin AutoJs6 (`org.autojs.permission.PLUGIN`), sans autorisation réseau ni stockage.
- Exécution par l'hôte: le plugin renvoie seulement un plan de renommage; AutoJs6 revalide chaque élément avant de renommer et n'écrase jamais de fichier existant, sauf si la stratégie d'écrasement est choisie explicitement.
- Points d'entrée protégés: les services et l'écran de renommage exigent l'autorisation de plugin, les autres applications ne peuvent donc pas les appeler.

******

### Interface Du Plugin

******

Les informations suivantes s'adressent aux développeurs de l'hôte AutoJs6 et de plugins; l'hôte utilise ces identifiants pour découvrir le plugin et négocier les capacités:

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

### Feuille De Route

******

Les plans, décisions de conception et l'avancement sont suivis sous forme de liste à cocher dans ROADMAP.md. Les éléments non cochés sont des intentions, pas des fonctionnalités de la version actuelle.

- [Voir ROADMAP.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/ROADMAP.md)

******

### Historique Des Versions

******

#### v1.0.0

_2026/10/16_

- `Note` Nécessite AutoJs6 6.8.0 (5438) ou ultérieur
- `Fonctionnalité` Renommage par lot de fichiers et de dossiers dans le gestionnaire de fichiers d'AutoJs6 (entrées: barre de sélection multiple/menu d'un dossier "Renommer par lot les éléments du dossier"/menu "Plus" de la boîte de dialogue de renommage)
- `Fonctionnalité` Rechercher et remplacer en texte brut ou par expressions régulières JavaScript, toutes les occurrences, respect de la casse et renommage du nom ou de l'extension seulement
- `Fonctionnalité` Compteurs, variables de date tirées de la date de modification ou de l'heure actuelle, chaînes aléatoires et UUID
- `Fonctionnalité` Changement de casse, normalisation Unicode, conversion de la pleine chasse en demi-chasse, remplacement des caractères interdits, suppression des espaces aux extrémités et numérotation qui prolonge les noms existants
- `Fonctionnalité` Aperçu des nouveaux noms avant de renommer, exclusion par élément ou affichage des seuls éléments qui changent, et choix de refuser, ignorer, ajouter un numéro ou remplacer l'élément existant lorsqu'un nouveau nom est pris
- `Fonctionnalité` Annulation des lots de renommage récents (AutoJs6 conserve les 20 derniers, partagés par le gestionnaire de fichiers et les scripts)
- `Fonctionnalité` Calcule les nouveaux noms pour l'API de scripts files.batchRename d'AutoJs6
- `Fonctionnalité` L'écran suit la couleur du thème, le mode sombre et la langue d'AutoJs6, et peut mémoriser les dernières options ainsi que les textes de recherche et de remplacement récents
- `Dépendance` Ajout de Jetpack Compose BOM 2026.09.00
- `Dépendance` Ajout de AndroidX Activity Compose 1.12.2
- `Dépendance` Ajout de AndroidX Core KTX 1.15.0
- `Dépendance` Ajout de Kotlin Coroutines 1.11.0
- `Dépendance` Ajout de Desugar JDK Libs 2.1.5
- `Dépendance` Ajout de Rhino (version allégée d'AutoJs6, pour les expressions régulières)

##### Pour plus d'historique des versions

* [CHANGELOG.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/app/src/main/assets/doc/CHANGELOG-fr.md)

******

### Compilation

******

Cette section s'adresse aux développeurs souhaitant compiler le plugin depuis les sources.

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest
.\gradlew.bat :app:appendDigestToReleasedFiles
py .python\generate_markdown.py --check
py .python\generate_icon_studio.py --check
py .python\check_repository.py
```

******

### Licence

******

Le projet est distribué sous [Mozilla Public License 2.0](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/blob/master/LICENSE). La conception des fonctionnalités suit la documentation publique de PowerToys PowerRename et ne contient ni son code ni ses ressources.

******

### Liens

******

- Projet AutoJs6: https://github.com/SuperMonster003/AutoJs6
- Documentation de PowerToys PowerRename (référence fonctionnelle): https://learn.microsoft.com/windows/powertoys/powerrename
