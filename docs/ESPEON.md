# Revisión de Espeon (4ad1e25 y revert 8fde666)

Problemas confirmados en el commit original:

- `ProyectilConfusion` se referencia desde dos clases, pero ni su código ni recursos se incluyeron en Git. Un archivo local sin versionar explicaría que funcionara en otra máquina; el historial no permite confirmar el estado de ese computador.
- Los tres archivos modificados contienen bytes incompatibles con UTF-8, mientras `core/build.gradle` exige UTF-8. Esto también impide compilar desde un checkout limpio.
- La pantalla dibujaba los proyectiles dos veces y dependía directamente de Espeon para posicionarlo y recoger disparos.
- Solo se invertían izquierda/derecha, quedando arriba/abajo sin confusión.
- El puntaje base pasó de 10 a 35 pero el bonus shiny aún comprobaba 10: nunca se aplicaba.

La recuperación incorpora el proyectil completo, con una textura generada sin assets externos, colisiones mediante `Colisionable` y liberación/recarga de la textura al cerrar/reabrir partidas. Los archivos son UTF-8.

Espeon conserva el spawn aleatorio del catálogo y su Strategy de movimiento. Dispara hacia abajo si el centro del entrenador está debajo, a un máximo de 500 unidades y alineado a ±35 unidades; el intervalo mínimo es un segundo. La interfaz opcional `EmisorConfusion` permite recoger el disparo sin acoplar la pantalla a una especie. La captura suspende los ataques mediante el Template Method existente.

Un impacto invierte WASD y flechas durante cuatro segundos de juego. Se conserva la prioridad de la acción de detenerse (ahora asignada a arriba durante confusión), los límites de pantalla y el congelamiento. Los impactos repetidos renuevan el efecto; no quitan vidas. Espacio sigue lanzando Pokéballs. El HUD muestra `CONFUSION`. La pausa y la animación de muerte suspenden proyectiles y efecto. Espeon vale 35 puntos, o 70 shiny.

Verificación:

```sh
bash gradlew build
bash gradlew lwjgl3:smokeTest
```

`smokeTest` necesita un contexto gráfico (pantalla o Xvfb). Comprueba controles, expiración, congelamiento, cooldown, captura, shiny, colisiones, pausa y recarga gráfica, además de las regresiones previas. Se actualizaron sus totales de spawn a 135/645: la incorporación previa de Espeon ya había añadido pesos 15/45 sin actualizar esas expectativas.
