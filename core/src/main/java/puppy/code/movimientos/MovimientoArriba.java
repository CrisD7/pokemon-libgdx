package puppy.code.movimientos;

import puppy.code.pokemones.Pokemon;

public class MovimientoArriba implements EstrategiaMovimiento {
    @Override
    public void mover(Pokemon pokemon, float delta) {
        // Suma en Y para correr hacia arriba contra el movimiento del escenario
        float nuevaY = pokemon.getY() + (pokemon.getVelocidad() * delta);
        pokemon.setY(nuevaY);
        pokemon.setDireccionActual(Pokemon.DIR_ARRIBA);
    }
}