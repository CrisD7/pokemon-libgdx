package puppy.code.movimientos;

import puppy.code.pokemones.Pokemon;

public class MovimientoEstatico implements EstrategiaMovimiento {
    @Override
    public void mover(Pokemon pokemon, float delta) {
        // Permanece fijo en su coordenada del suelo mirando hacia abajo
        pokemon.setDireccionActual(Pokemon.DIR_ABAJO);
    }
}