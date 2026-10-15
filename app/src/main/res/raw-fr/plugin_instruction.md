Le plugin Batch Rename ajoute le renommage par lot à AutoJs6, sur le modèle de [PowerRename](https://learn.microsoft.com/windows/powertoys/powerrename) de PowerToys. Il renomme en une fois de nombreux fichiers et dossiers avec rechercher-remplacer, expressions régulières, compteurs, variables de date et chaînes aléatoires, affiche un aperçu complet avant toute modification et permet d'annuler ensuite.

> Ceci est un aperçu de développement: AutoJs6 reconnaît le plugin, mais l'écran de renommage, les entrées du gestionnaire de fichiers et l'API de script ne sont pas encore livrés. Les sections ci-dessous décrivent les objectifs de la version 1.0.0; voir la feuille de route pour l'avancement.

### Points Forts

- Rechercher et remplacer: texte brut ou expressions régulières, toutes les occurrences ou seulement la première, avec ou sans respect de la casse, appliqué au nom, à l'extension ou au nom complet.
- Variables: compteurs (`${start=1;increment=1;padding=3}`), variables de date issues de la date de modification (`$YYYY`, `$MM`, `$DD`, etc.) et chaînes aléatoires, avec la même syntaxe que PowerRename.
- Nettoyage du texte: changement de casse, normalisation Unicode, conversion pleine chasse vers demi-chasse, suppression des caractères interdits, suppression des espaces et points en début et en fin, et numérotation qui poursuit une séquence existante.
- Aperçu d'abord: chaque élément affiche son ancien et son nouveau nom, des éléments peuvent être exclus, et les conflits et noms invalides sont signalés; un conflit peut rejeter le lot, ignorer l'élément, ajouter un numéro ou écraser.
- Annulation: AutoJs6 conserve les 20 derniers lots de renommage; le gestionnaire de fichiers comme les scripts peuvent les annuler, après avoir vérifié que les fichiers n'ont pas été modifiés depuis.

Consultez la [page du projet](https://github.com/SuperMonster003/AutoJs6-Plugin-Batch-Rename) pour l'avancement et la conception.
