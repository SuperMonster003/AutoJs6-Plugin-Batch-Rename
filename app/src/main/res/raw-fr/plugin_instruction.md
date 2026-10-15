Le plugin Batch Rename ajoute le renommage par lot à AutoJs6, sur le modèle de [PowerRename](https://learn.microsoft.com/windows/powertoys/powerrename) de PowerToys. Il renomme en une fois de nombreux fichiers et dossiers avec rechercher-remplacer, expressions régulières, compteurs, variables de date et chaînes aléatoires, affiche un aperçu complet avant toute modification et permet d'annuler ensuite.

### Points Forts

- Rechercher et remplacer: texte brut ou expressions régulières JavaScript, toutes les occurrences ou seulement la première, avec ou sans respect de la casse, appliqué au nom, à l'extension ou aux deux.
- Variables: compteurs (`${start=1;increment=1;padding=3}`), variables de date tirées de la date de modification ou de l'heure actuelle (`$YYYY`, `$MM`, `$DD`, etc.) et chaînes aléatoires, avec la même syntaxe que PowerRename.
- Nettoyage des noms: changement de casse, normalisation Unicode, conversion de la pleine chasse en demi-chasse, remplacement des caractères interdits, suppression des espaces aux extrémités et numérotation qui prolonge les noms existants.
- Aperçu avant de renommer: anciens et nouveaux noms côte à côte, décochez des éléments pour les exclure ou n'affichez que ceux qui changent; si un nouveau nom est pris, refuser, ignorer, ajouter un numéro ou remplacer l'élément existant.
- Annulation: AutoJs6 conserve les 20 derniers lots de renommage, annulables depuis le gestionnaire de fichiers comme depuis les scripts; l'annulation vérifie d'abord qu'un fichier n'a pas été modifié entre-temps.
- L'écran suit la couleur du thème, le mode sombre et la langue d'AutoJs6, et peut mémoriser les dernières options ainsi que les textes de recherche et de remplacement récents.

### Dans le gestionnaire de fichiers

Une fois le plugin installé et activé, le gestionnaire de fichiers d'AutoJs6 propose trois entrées:

- Sélection multiple: sélectionnez des fichiers ou des dossiers et appuyez sur "Renommer par lot" dans la barre de sélection (sous "Plus" si plusieurs actions de plugins sont disponibles).
- Menu d'un dossier: choisissez "Renommer par lot les éléments du dossier" dans le menu d'un dossier pour renommer son contenu.
- Boîte de dialogue de renommage: lorsque vous renommez un seul fichier ou dossier, ouvrez le menu "Plus" en haut à droite de la boîte de dialogue et choisissez "Renommer par lot...".
- Saisissez la recherche et le remplacement sur l'écran du plugin, l'aperçu se met à jour aussitôt; décochez des éléments pour les exclure, appuyez sur "Renommer", puis sur "Annuler le dernier lot" pour rétablir les anciens noms.

Consultez la [documentation d'AutoJs6](https://docs.autojs6.com/#/files?id=m-batchrename) pour l'API des scripts et la [page du projet](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename) pour les notes de développement.
