# Pokewalk

Un videojuego 2D de supervivencia arcade inspirado en el universo Pokémon, desarrollado en Java utilizando el framework LibGDX. 

En Pokewalk, asumes el rol de un entrenador que debe esquivar Pokémon salvajes que aparecen dinámicamente en el mapa. Pierdes vidas si colisionas con ellos, pero puedes capturarlos lanzando Pokeballs para sumar puntos.

## Requisitos Previos

Para poder compilar y ejecutar este proyecto, necesitas tener instalado:
* Java Development Kit (JDK): Versión 11 o superior.
* Git: Para clonar el repositorio.

## Cómo ejecutar el juego

### Opción 1: Desde la Terminal (Recomendado)
El proyecto utiliza Gradle Wrapper, por lo que no necesitas tener Gradle instalado en tu sistema.

1. Clona este repositorio en tu máquina local:
   git clone https://github.com/CrisD7/pokemon-libgdx.git

2. Navega hasta el directorio del proyecto:
   cd pokemon-libgdx

3. Ejecuta el juego usando el wrapper de Gradle:
   - En Windows:
     gradlew.bat lwjgl3:run
   - En Linux / macOS:
     ./gradlew lwjgl3:run

### Opción 2: Desde Eclipse IDE
1. Abre Eclipse y ve a File > Import...
2. Selecciona Gradle > Existing Gradle Project y dale a Next.
3. Busca la carpeta donde clonaste el repositorio y finaliza la importación.
4. En el panel izquierdo (Package Explorer), despliega el proyecto Pokewalk-lwjgl3.
5. Navega a src/main/java/puppy/code/lwjgl3/ y busca la clase Lwjgl3Launcher.java.
6. Haz clic derecho sobre ella y selecciona Run As > Java Application.

## Controles

* W: Caminar hacia adelante (Arriba)
* A: Caminar hacia la izquierda
* S: Caminar hacia la derecha (Abajo)
* D: Detener movimiento
* Espacio: Lanzar Pokeball

## Tecnologías utilizadas

* Lenguaje: Java
* Motor gráfico: LibGDX (backend LWJGL3)
* Gestor de dependencias: Gradle
* Patrones de diseño aplicados: Singleton, Strategy, Template Method y Builder.

A [libGDX](https://libgdx.com/) project generated with [gdx-liftoff](https://github.com/libgdx/gdx-liftoff).

This project was generated with a template including simple application launchers and an `ApplicationAdapter` extension that draws libGDX logo.

## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3; was called 'desktop' in older docs.

## Gradle

This project uses [Gradle](https://gradle.org/) to manage dependencies.
The Gradle wrapper was included, so you can run Gradle tasks using `gradlew.bat` or `./gradlew` commands.
Useful Gradle tasks and flags:

- `--continue`: when using this flag, errors will not stop the tasks from running.
- `--daemon`: thanks to this flag, Gradle daemon will be used to run chosen tasks.
- `--offline`: when using this flag, cached dependency archives will be used.
- `--refresh-dependencies`: this flag forces validation of all dependencies. Useful for snapshot versions.
- `build`: builds sources and archives of every project.
- `cleanEclipse`: removes Eclipse project data.
- `cleanIdea`: removes IntelliJ project data.
- `clean`: removes `build` folders, which store compiled classes and built archives.
- `eclipse`: generates Eclipse project data.
- `idea`: generates IntelliJ project data.
- `lwjgl3:jar`: builds application's runnable jar, which can be found at `lwjgl3/build/libs`.
- `lwjgl3:run`: starts the application.
- `test`: runs unit tests (if any).

Note that most tasks that are not specific to a single project can be run with `name:` prefix, where the `name` should be replaced with the ID of a specific project.
For example, `core:clean` removes `build` folder only from the `core` project.
