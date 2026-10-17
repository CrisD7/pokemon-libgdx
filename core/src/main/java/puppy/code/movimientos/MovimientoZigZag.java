package puppy.code.movimientos;

import com.badlogic.gdx.Gdx;
import puppy.code.pokemones.Pokemon;

public class MovimientoZigZag implements EstrategiaMovimiento {
    private int direccionX = 1; // 1 derecha, -1 izquierda

    @Override
    public void mover(Pokemon pokemon, float delta) {
        float nuevaY = pokemon.getY() - (pokemon.getVelocidad() * 0.5f * delta);
        float nuevaX = pokemon.getX() + (pokemon.getVelocidad() * direccionX * delta);

        if (nuevaX < 0) {
            nuevaX = 0;
            direccionX = 1;
        } else if (nuevaX + pokemon.getWidth() > Gdx.graphics.getWidth()) {
            nuevaX = Gdx.graphics.getWidth() - pokemon.getWidth();
            direccionX = -1;
        }

        pokemon.setX(nuevaX);
        pokemon.setY(nuevaY);
        
        // Cambia el sprite según hacia dónde esté rebotando
        if (direccionX == 1) {
            pokemon.setDireccionActual(Pokemon.DIR_DERECHA);
        } else {
            pokemon.setDireccionActual(Pokemon.DIR_IZQUIERDA);
        }
    }

    public int getDireccionX() { return direccionX; }
    public void setDireccionX(int direccionX) { this.direccionX = direccionX; }
}