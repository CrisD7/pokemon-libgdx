package puppy.code.movimientos;

import puppy.code.pokemones.Pokemon;

public class MovimientoRecto implements EstrategiaMovimiento {
    @Override
    public void mover(Pokemon pokemon, float delta) {
        float nuevaY = pokemon.getY() - (pokemon.getVelocidad() * delta);
        pokemon.setY(nuevaY);
        pokemon.setDireccionActual(Pokemon.DIR_ABAJO);
    }
}