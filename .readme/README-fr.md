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

> Ceci est un aperçu de développement: AutoJs6 reconnaît le plugin, mais l'écran de renommage, les entrées du gestionnaire de fichiers et l'API de script ne sont pas encore livrés. Les sections ci-dessous décrivent les objectifs de la version 1.0.0; voir la feuille de route pour l'avancement.

******

### Points Forts

******

- Rechercher et remplacer: texte brut ou expressions régulières, toutes les occurrences ou seulement la première, avec ou sans respect de la casse, appliqué au nom, à l'extension ou au nom complet.
- Variables: compteurs (`${start=1;increment=1;padding=3}`), variables de date issues de la date de modification (`$YYYY`, `$MM`, `$DD`, etc.) et chaînes aléatoires, avec la même syntaxe que PowerRename.
- Nettoyage du texte: changement de casse, normalisation Unicode, conversion pleine chasse vers demi-chasse, suppression des caractères interdits, suppression des espaces et points en début et en fin, et numérotation qui poursuit une séquence existante.
- Aperçu d'abord: chaque élément affiche son ancien et son nouveau nom, des éléments peuvent être exclus, et les conflits et noms invalides sont signalés; un conflit peut rejeter le lot, ignorer l'élément, ajouter un numéro ou écraser.
- Annulation: AutoJs6 conserve les 20 derniers lots de renommage; le gestionnaire de fichiers comme les scripts peuvent les annuler, après avoir vérifié que les fichiers n'ont pas été modifiés depuis.

******

### Utilisation

******

1. Mettez à jour AutoJs6 vers la build 5352 (6.8.0) ou ultérieure.
2. Installez Batch Rename depuis le centre de plugins d'AutoJs6, ou téléchargez l'APK depuis [Releases](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename/releases), puis autorisez-le et activez-le dans le centre de plugins.
3. Dans le gestionnaire de fichiers, appuyez longuement pour sélectionner des éléments puis touchez "Renommer par lot", ou appelez `files.batchRename` dans un script.

> Le plugin est distribué sous forme d'un seul APK universel pour Android 7.0 (API 24) et versions ultérieures. Il n'ajoute pas d'icône au lanceur.

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

Les scripts utilisent le même moteur via `files.batchRename` (ébauche de conception 1.0.0; la documentation publiée fait foi):

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

- `files.batchRename.plan(paths, options)` calcule sans renommer et renvoie un résultat par élément; modifiez `items[i].excluded` puis transmettez le plan à `apply`.
- `files.batchRename(paths, options)` renomme en une étape et renvoie l'identifiant du lot avec un résultat par élément; `files.batchRename.undo(result)` annule ce lot.
- Les expressions régulières se comportent exactement comme `RegExp` dans les scripts, et les objets `RegExp` peuvent être passés directement. `planAsync`, `applyAsync` et `undoAsync` renvoient des promesses.

******

### FAQ

******

#### Pourquoi n'y a-t-il pas d'icône dans le lanceur?

Le plugin n'a pas d'entrée autonome. AutoJs6 le découvre après l'installation, et tout se passe dans le gestionnaire de fichiers d'AutoJs6 ou dans les scripts.

#### Le plugin a-t-il besoin de l'autorisation de stockage?

Non. Le plugin calcule seulement les nouveaux noms à partir des noms de fichiers; AutoJs6 parcourt les dossiers et renomme les fichiers avec les accès dont il dispose déjà.

#### En quoi diffère-t-il de PowerRename?

Les variables de date utilisent la date de modification, car la date de création n'est pas fiable sur Android. Il ajoute la normalisation Unicode, la conversion pleine chasse, le nettoyage des caractères interdits et un historique de lots annulables. Les variables EXIF des photos sont prévues pour la version 1.1.0.

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
minimum host build: 5352
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

_2026/10/15_

- `Note` Aperçu de développement: AutoJs6 reconnaît le plugin, mais l'écran de renommage, les entrées du gestionnaire de fichiers et l'API de script files.batchRename ne sont pas encore livrés
- `Note` Nécessite AutoJs6 6.8.0 (5352) ou ultérieur (le numéro de build minimal exact sera renseigné une fois les changements côté hôte intégrés)
- `Fonctionnalité` Squelette du plugin: activation Wake, service d'identité du centre de plugins, service de capacité pour les scripts et service d'actions du gestionnaire de fichiers (catalogue d'actions vide pour l'instant)
- `Fonctionnalité` README, instructions du centre de plugins et journal des modifications en 10 langues, générés à partir de sources JSON
- `Dépendance` Ajout de Jetpack Compose BOM 2026.09.00
- `Dépendance` Ajout de AndroidX Activity Compose 1.12.2
- `Dépendance` Ajout de AndroidX Core KTX 1.15.0
- `Dépendance` Ajout de Kotlin Coroutines 1.11.0

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
