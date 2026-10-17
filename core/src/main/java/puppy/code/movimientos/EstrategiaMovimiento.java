package puppy.code.movimientos;

import puppy.code.pokemones.Pokemon;

public interface EstrategiaMovimiento {
    void mover(Pokemon pokemon, float delta);
}