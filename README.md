# Pokewalk

Pokewalk es un juego arcade 2D de supervivencia hecho en Java con libGDX. Esquiva Pokémon salvajes para conservar tus vidas y lanza Poké Balls para capturarlos y sumar puntos.

## Requisitos previos

- **Oracle JDK 11 (LTS)** para compilar y ejecutar el juego según las bases del proyecto GM.
- **Eclipse IDE for Java Developers con Buildship**, si vas a trabajar desde Eclipse. Si ya tienes Eclipse, comprueba que aparezca la categoría **Gradle** en **File → Import…**. Si falta, instala **Buildship Gradle Integration** desde **Help → Eclipse Marketplace…**. [Documentación de Buildship](https://github.com/eclipse-buildship/buildship/blob/master/docs/user/Overview.md)
- **Git**, si vas a clonar el repositorio.
- **Conexión a Internet en la primera ejecución**, para que el Gradle Wrapper descargue Gradle **8.10.1** y resuelva las dependencias. El Wrapper viene incluido en el repositorio.
- **Una sesión gráfica de escritorio y controladores con soporte para OpenGL**, necesarios para abrir la ventana del juego con LWJGL3.

Comprueba que tu terminal encuentra el JDK 11 con `java -version` y `javac -version`. Ambos comandos deben indicar la versión 11 para seguir esta guía. Si muestran otra versión, configura `JAVA_HOME` con la carpeta del JDK 11 y pon su directorio `bin` al principio de `PATH`. Abre una terminal nueva para comprobarlo.

## Obtener el proyecto

```bash
git clone https://github.com/CrisD7/pokemon-libgdx.git
cd pokemon-libgdx
```

Si ya tienes una copia local, usa la carpeta que contiene `settings.gradle`, `gradlew.bat` y `assets/`.

## Ejecutar desde Eclipse IDE

### 1. Configurar Java

1. En Windows o Linux, abre **Window → Preferences**. En macOS, abre los ajustes desde el menú **Eclipse**.
2. Ve a **Java → Installed JREs**. Si el JDK 11 no está en la lista, selecciona **Add… → Standard VM → Next**.
3. En **JRE home**, selecciona la carpeta del JDK 11, la que contiene `bin`. Pulsa **Finish** y **Apply**. En **Java → Installed JREs → Execution Environments**, asocia el JDK al entorno **JavaSE-11**.
4. Ve a **Gradle → Advanced Options** y configura **Java home** con esa misma carpeta. Guarda con **Apply and Close**. Esta opción selecciona el Java que usa Buildship para Gradle. Eclipse puede conservar su propio entorno de Java para arrancar.

### 2. Importar el proyecto

1. Elige **File → Import… → Gradle → Existing Gradle Project** y pulsa **Next**.
2. En **Project root directory**, selecciona la raíz del repositorio, donde está `settings.gradle`. Pulsa **Next**.
3. Usa **Gradle Wrapper** como distribución. Si personalizas la configuración durante la importación, verifica también que **Java home** apunte al JDK 11.
4. Pulsa **Finish** y espera a que Buildship sincronice el proyecto. En **Package Explorer** deberían aparecer los módulos `core` y `lwjgl3`, normalmente como **Pokewalk-core** y **Pokewalk-lwjgl3**.

### 3. Abrir el juego con Run As

1. En **Package Explorer**, abre **Pokewalk-lwjgl3 → src/main/java → puppy.code.lwjgl3**.
2. Haz clic derecho sobre **Lwjgl3Launcher.java** y selecciona **Run As → Java Application**.
3. Cuando aparezca la ventana **Pokewalk**, pulsa **Enter** para empezar.

### Problemas frecuentes

| Síntoma | Qué revisar |
| --- | --- |
| No aparece **Existing Gradle Project** | Comprueba que Buildship esté instalado y reinicia Eclipse si lo acabas de instalar. |
| Gradle falla al importar por la versión de Java | Comprueba **Gradle → Advanced Options → Java home** y vuelve a sincronizar con **Gradle → Refresh Gradle Project**. |
| Falla la descarga de dependencias | Comprueba la conexión y que el modo **Offline** esté desactivado durante la primera sincronización. |
| No aparece **Java Application** | Comprueba que hiciste clic derecho en `Lwjgl3Launcher.java` y que terminó la sincronización de Gradle. |
| El lanzador no encuentra un sprite o sonido | Ve a **Run → Run Configurations… → Java Application → Lwjgl3Launcher**. En **Arguments → Working directory**, selecciona **Other** y elige la carpeta `assets/` junto a `settings.gradle`. Pulsa **Apply → Run**. |
| El lanzador usa otra versión de Java | En la misma configuración, abre **JRE** y selecciona el JDK 11 registrado. |
| LWJGL3 pide el argumento de arranque en macOS | En la misma configuración, añade `-XstartOnFirstThread` en **Arguments → VM arguments**. |

## Ejecutar desde la terminal

Desde la raíz del repositorio:

**Windows (PowerShell)**

```powershell
.\gradlew.bat :lwjgl3:run
```

**Linux o macOS**

```bash
./gradlew :lwjgl3:run
```

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
