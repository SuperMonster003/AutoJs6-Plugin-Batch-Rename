******

### Historique Des Versions

******

# v1.0.0

###### 2026/10/16

* `Note` Nécessite AutoJs6 6.8.0 (5438) ou ultérieur
* `Fonctionnalité` Renommage par lot de fichiers et de dossiers dans le gestionnaire de fichiers d'AutoJs6 (entrées: barre de sélection multiple/menu d'un dossier "Renommer par lot les éléments du dossier"/menu "Plus" de la boîte de dialogue de renommage)
* `Fonctionnalité` Rechercher et remplacer en texte brut ou par expressions régulières JavaScript, toutes les occurrences, respect de la casse et renommage du nom ou de l'extension seulement
* `Fonctionnalité` Compteurs, variables de date tirées de la date de modification ou de l'heure actuelle, chaînes aléatoires et UUID
* `Fonctionnalité` Changement de casse, normalisation Unicode, conversion de la pleine chasse en demi-chasse, remplacement des caractères interdits, suppression des espaces aux extrémités et numérotation qui prolonge les noms existants
* `Fonctionnalité` Aperçu des nouveaux noms avant de renommer, exclusion par élément ou affichage des seuls éléments qui changent, et choix de refuser, ignorer, ajouter un numéro ou remplacer l'élément existant lorsqu'un nouveau nom est pris
* `Fonctionnalité` Annulation des lots de renommage récents (AutoJs6 conserve les 20 derniers, partagés par le gestionnaire de fichiers et les scripts)
* `Fonctionnalité` Calcule les nouveaux noms pour l'API de scripts files.batchRename d'AutoJs6
* `Fonctionnalité` L'écran suit la couleur du thème, le mode sombre et la langue d'AutoJs6, et peut mémoriser les dernières options ainsi que les textes de recherche et de remplacement récents
* `Dépendance` Ajout de Jetpack Compose BOM 2026.09.00
* `Dépendance` Ajout de AndroidX Activity Compose 1.12.2
* `Dépendance` Ajout de AndroidX Core KTX 1.15.0
* `Dépendance` Ajout de Kotlin Coroutines 1.11.0
* `Dépendance` Ajout de Desugar JDK Libs 2.1.5
* `Dépendance` Ajout de Rhino (version allégée d'AutoJs6, pour les expressions régulières)
