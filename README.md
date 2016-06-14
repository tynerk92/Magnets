# Magnets 
Magnets (tentative name) is a block-pushing puzzle game similar to Sokoban and the boulder-pushing puzzles in the caves of Pokemon, but we'll be introducing magnetic elements within to add an extra layer of depth. This generates new mechanics and possibilities unexplored in other block-pushing puzzles, making this a very interesting concept. 

This project is a NUS Orbital project in the works by students @nginyc and @ckjr, mentored by @melvinzhang.

# Demo

![Demo](https://raw.githubusercontent.com/nginyc/Magnets/f0952aea2614ed0553c44502efdf3006105d15b6/android/assets/Others/demo.gif)

# Compiling and Running on Desktop
We are using Android Studio + libgdx to develop the game, which uses Gradle. Follow the instructions for [Gradle on the Commandline](https://github.com/libgdx/libgdx/wiki/Gradle-on-the-Commandline), or try the following:

1. Download and install Java Development Kit (JDK) of version `1.6` or higher ([link](http://www.oracle.com/technetwork/java/javase/downloads)) on your system. To check your java version, run the command `java -version` on the command line. Ensure that your `JAVA_HOME` environment variable is set to point to your JDK directory by running `set JAVA_HOME`.
2. Download and install android SDK (or alternatively, the whole Android Studio) ([link](https://developer.android.com/studio)) and set the `ANDROID_HOME` environment variable to point to your android SDK directory by running `set ANDROID_HOME=C:/Path/To/Your/Android/Sdk`.
3. Download and install `git` on your system.
4. Clone our Github project on your system by running `git clone https://github.com/nginyc/Magnets`.
5. Navigate to the root directory of our project (run `cd Magnets`) and run `gradlew desktop:run`.

# Deploying on Desktop
1. Navigate to the root directory of our project (run `cd Magnets`) and run `gradlew desktop:dist`.
2. The deployed, independent `.jar` file is in the `desktop/build/libs` folder.
