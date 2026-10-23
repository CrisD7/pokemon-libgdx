package puppy.code.movimientos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import puppy.code.pokemones.Pokemon;

public class MovimientoZigZag implements EstrategiaMovimiento {
    private int direccionX; // 1 derecha, -1 izquierda

    public MovimientoZigZag() {
        // Elige aleatoriamente si parte a la derecha (1) o a la izquierda (-1)
        this.direccionX = MathUtils.randomBoolean() ? 1 : -1;
    }

    @Override
    public void mover(Pokemon pokemon, float delta) {
        float nuevaY = pokemon.getY() - (pokemon.getVelocidad() * 0.5f * delta);
        float nuevaX = pokemon.getX() + (pokemon.getVelocidad() * this.direccionX * delta);

        if (nuevaX < 0) {
            nuevaX = 0;
            this.direccionX = 1;
        } else if (nuevaX + pokemon.getWidth() > Gdx.graphics.getWidth()) {
            nuevaX = Gdx.graphics.getWidth() - pokemon.getWidth();
            this.direccionX = -1;
        }

        pokemon.setX(nuevaX);
        pokemon.setY(nuevaY);
        
        if (this.direccionX == 1) {
            pokemon.setDireccionActual(Pokemon.DIR_DERECHA);
        } else {
            pokemon.setDireccionActual(Pokemon.DIR_IZQUIERDA);
        }
    }

    // Getters y Setters (GM-5)
    public int getDireccionX() { return direccionX; }
    public void setDireccionX(int direccionX) { this.direccionX = direccionX; }
}