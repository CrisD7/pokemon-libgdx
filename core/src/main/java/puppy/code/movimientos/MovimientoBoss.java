package puppy.code.movimientos;

import com.badlogic.gdx.Gdx;
import puppy.code.pokemones.Pokemon;

public class MovimientoBoss implements EstrategiaMovimiento {
    private float tiempoAtaque = 0f;
    private boolean atacando = false;
    private boolean regresando = false;
    private int direccionX = 1;
    private float yBase = -1f;

    @Override
    public void mover(Pokemon pokemon, float delta) {
        if (yBase == -1f) {
            yBase = Gdx.graphics.getHeight() - pokemon.getHeight() - 20f;
        }

        if (!atacando && !regresando) {
            pokemon.setY(yBase);
            pokemon.setX(pokemon.getX() + 150f * direccionX * delta);

            if (pokemon.getX() <= 0) {
                pokemon.setX(0);
                direccionX = 1;
            } else if (pokemon.getX() >= Gdx.graphics.getWidth() - pokemon.getWidth()) {
                pokemon.setX(Gdx.graphics.getWidth() - pokemon.getWidth());
                direccionX = -1;
            }

            tiempoAtaque += delta;
            if (tiempoAtaque >= 4.0f) {
                atacando = true;
                tiempoAtaque = 0f;
            }
        } else if (atacando) {
            pokemon.setY(pokemon.getY() - 500f * delta);
            if (pokemon.getY() <= pokemon.getObjetivoY()) {
                atacando = false;
                regresando = true;
            }
        } else if (regresando) {
            pokemon.setY(pokemon.getY() + 300f * delta);
            if (pokemon.getY() >= yBase) {
                pokemon.setY(yBase);
                regresando = false;
            }
        }
    }
}