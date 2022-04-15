# jhotdraw8

 Le Van Canh dit Ban Léo

- Projet : JhotDrawn8 : https://github.com/wrandelshofer/jhotdraw8

# Etape des tentatives de modification

Dans le cadre de cette partie du projet, j'ai fork le projet afin de mettre les modifications effectuées.
Je n'es pas mis dans le push les tentatives suivantes :

- Tentative de compiler le projet a l'aide de ANT

- Creation de tests unitaires pour augmenter le coverage

## Compilation a l'aide de ANT

Dans la racine du depots nous avons un fichier build-jhotdraw.xml avec la syntaxe ANT, j'ai donc essayé de compiler le projet a l'aide de ANT.
Malheureusemet le projet utilise la dependence JavaFx, qui n'est plus ajouté de base dans JDk > 8 . J'ai donc essayer de telecharger les fichiers binaire
de Jdk11 et de JavaFx, puis a l'aide de gradlew, recompiler JDk avec JavaFx dedans, mais meme avec l'aide des tutoriels de chez oracle, ma machine tournant avec WSL ubuntu, cela c'est soldé par un echec.

## Creation de tests unitaires

Il y'a un fichier de test unitaire de mis en ligne sur ce depot, mais malheureusement les testes se sont revelé compliqué car pour ses classes il utilise des classes definie par lui même et sans documentation il est très compliqué de comprendre le fonctionement de ces classes, qui utilise souvent des types très specifique et même compliquer a utiliser (*ex des liste devenement listener,qui sont cree avec d'autre type, etc*) on arrive donc a des tests unitaire très lourd, parfois simplement pour tester un constructeur. De plus le plugin surfire ne semple pas supporter les mocks, donc les test des abstracts sont imposible.
J'ai donc tenté de corriger des bug/problemene du code

## Correction 

J'ai donc fini par tenter de corriger des doublons de code, qui sont parfois très lourd. J'ai donc deplacé des fonctions qui etait repeté dans des classes qui extendais de la meme classe atraite, on perd donv quelque ligne de code redondant, mais encore une fois le code etant très specifique , avec des objets sur messure, il est dificile d'extraire les methode.

