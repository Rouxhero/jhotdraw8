# jhotdraw8

 Le Van Canh dit Ban Léo

- Projet : JhotDrawn8 : https://github.com/wrandelshofer/jhotdraw8

En:

I don't have make the pull-request because my change are minimal and for a major part, it's was already made by the owner in the same time

Fr:

Je n'ai pas à faire la pull-request car mes modifications sont minimes et pour la majeure partie, elles ont déjà été faites par le propriétaire dans le même temps

-----

# english
# Step of modification attempts

As part of this part of the project, I forked the project to put the changes made.
I am not put in the push the following attempts:

- Attempt to compile the project using ANT

- Creation of unit tests to increase coverage

## Compiling using ANT

In the root of the repository we have a build-jhotdraw.xml file with ANT syntax, so I tried to compile the project using ANT.
Unfortunately the project uses the JavaFx dependency, which is no longer added as a base in JDk > 8 . So I tried to download the binary files
of Jdk11 and JavaFx, then using gradlew, recompiling JDk with JavaFx in it, but even with the help of tutorials from Oracle, my machine running with WSL ubuntu, this ended in failure.

## Creating unit tests

There is a unit test file posted on this repository, but unfortunately the tests turned out to be complicated because for its classes it uses classes defined by itself and without documentation it is very complicated to understand the operation of these classes, which often use very specific types and even complicate to use (*ex event listener lists, which are created with other types, etc*) we therefore end up with very heavy unit tests, sometimes simply to test a constructor . Also the surfire plugin doesn't seem to support mocks, so testing abstracts is impossible.
So I tried to fix bugs/problems in the code

## Correction

So I ended up trying to correct duplicate code, which is sometimes very cumbersome. So I moved functions that were repeated in classes that extended from the same untreated class, so we lose some line of redundant code, but again the code being very specific, with custom objects, it is difficult to extract method.

-----

# Français

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

