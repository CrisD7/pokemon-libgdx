# Pokewalk

Pokewalk es un juego arcade 2D de supervivencia hecho en Java con libGDX. Esquiva Pokémon salvajes para conservar tus vidas y lanza Poké Balls para capturarlos y sumar puntos.

## Requisitos previos

- **Oracle JDK 11 (LTS)** para compilar y ejecutar el juego según las bases del proyecto GM.
- **Eclipse IDE for Java Developers con Buildship**. Si ya tienes Eclipse, comprueba que aparezca la categoría **Gradle** en **File → Import…**. Si falta, instala **Buildship Gradle Integration** desde **Help → Eclipse Marketplace…**. [Documentación de Buildship](https://github.com/eclipse-buildship/buildship/blob/master/docs/user/Overview.md)
- **Conexión a Internet en la primera importación**, para que el Gradle Wrapper descargue Gradle **8.10.1** y resuelva las dependencias. El Wrapper viene incluido en el repositorio.
- **Una sesión gráfica de escritorio y controladores con soporte para OpenGL**, necesarios para abrir la ventana del juego con LWJGL3.

## Ejecutar desde Eclipse IDE en Windows

1. **Descarga y extrae el ZIP.** En el [repositorio de GitHub](https://github.com/CrisD7/pokemon-libgdx), selecciona **Code → Download ZIP**. En el Explorador de archivos de Windows, haz clic derecho sobre `pokemon-libgdx-main.zip` y selecciona **Extraer todo**. Abre la carpeta extraída hasta ver juntos `settings.gradle`, `gradlew.bat` y la carpeta `assets`. Esa es la raíz del proyecto.
2. **Abre un workspace nuevo.** En Eclipse, ve a **File → Switch Workspace → Other…**. Escribe una ruta nueva, por ejemplo `C:\Users\TuUsuario\workspace-pokewalk-evaluacion` (reemplaza `TuUsuario` por tu usuario de Windows), y pulsa **Launch**. Usa una carpeta distinta de la que extrajiste.
3. **Importa el proyecto.** Ve a **File → Import… → Gradle → Existing Gradle Project**. Pulsa **Next** hasta llegar a **Project root directory**, selecciona la raíz del paso 1 y pulsa **Next**.
4. **Configura Gradle.** Marca **Override workspace settings**, elige **Gradle Wrapper** y deja **Offline Mode** desactivado. En **Advanced Options → Java home**, selecciona la carpeta de instalación de **Oracle JDK 11**, la que contiene `bin`. Pulsa **Finish** y espera a que aparezcan **Pokewalk-core** y **Pokewalk-lwjgl3** como proyectos separados en **Package Explorer**.
5. **Abre el juego.** Ve a **Window → Show View → Other… → Gradle → Gradle Tasks**. Pulsa **Refresh Tasks** en la barra de esa vista. Despliega **Pokewalk-lwjgl3 → application** y haz doble clic en **run**. Cuando se abra la ventana **Pokewalk**, pulsa **Enter** para empezar.

La tarea `run` configura automáticamente la carpeta `assets/`. Puedes revisar el código del juego en **Pokewalk-core → src/main/java**. Si aparece una cruz roja después de importar, abre **Problems** y revisa el primer error de sincronización antes de ejecutar la tarea.

## Controles

| Acción | Teclas |
| --- | --- |
| Moverse arriba, izquierda, abajo o derecha | **W**, **A**, **S**, **D** o las flechas |
| Lanzar una Poké Ball | **Espacio** |
| Pausar o reanudar la partida | **P** o **Esc** |

## Estructura del proyecto

- `core/` contiene la lógica del juego, las pantallas y los Pokémon.
- `lwjgl3/` contiene el lanzador de escritorio y su configuración de Gradle.
- `assets/` contiene los recursos gráficos y de audio.

El proyecto utiliza Java, libGDX 1.12.1, LWJGL3 y Gradle. Se inició con [gdx-liftoff](https://github.com/libgdx/gdx-liftoff).
