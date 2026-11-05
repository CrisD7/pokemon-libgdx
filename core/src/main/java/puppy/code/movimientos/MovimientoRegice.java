package puppy.code.movimientos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import puppy.code.pokemones.Pokemon;

public class MovimientoRegice implements EstrategiaMovimiento {
    private float timerAccion = 0f;
    private int estado = 0; 
    private int direccionX = 1;
    private float yBase = -1f;

    private boolean debeLanzarRayo = false;
    private boolean debeGenerarZona = false;

    @Override
    public void mover(Pokemon pokemon, float delta) {
        if (yBase == -1f) {
            yBase = Gdx.graphics.getHeight() - pokemon.getHeight() - 20f;
        }

        float cooldownDinamico = 0.8f + 2.5f * ((float) pokemon.getResistenciaCapturas() / 20f);

        switch (estado) {
            case 0:
                pokemon.setY(yBase);
                pokemon.setX(pokemon.getX() + 180f * direccionX * delta);

                if (pokemon.getX() <= 0) {
                    pokemon.setX(0);
                    direccionX = 1;
                } else if (pokemon.getX() >= Gdx.graphics.getWidth() - pokemon.getWidth()) {
                    pokemon.setX(Gdx.graphics.getWidth() - pokemon.getWidth());
                    direccionX = -1;
                }

                timerAccion += delta;
                if (timerAccion >= cooldownDinamico) {
                    timerAccion = 0f;
                    elegirSiguienteAtaque();
                }
                break;
            case 1:
                pokemon.setY(pokemon.getY() - 700f * delta);
                if (pokemon.getY() <= pokemon.getObjetivoY()) {
                    estado = 2;
                }
                break;
            case 2:
                pokemon.setY(pokemon.getY() + 350f * delta);
                if (pokemon.getY() >= yBase) {
                    pokemon.setY(yBase);
                    estado = 0;
                }
                break;
            case 3:
                timerAccion += delta;
                if (timerAccion >= 0.5f) {
                    timerAccion = 0f;
                    estado = 0;
                }
                break;
            case 4:
                timerAccion += delta;
                if (timerAccion >= 0.5f) {
                    timerAccion = 0f;
                    estado = 0;
                }
                break;
        }
    }

    private void elegirSiguienteAtaque() {
        int ataque = MathUtils.random(1, 100);
        if (ataque <= 20) {
            estado = 1; // 20% de probabilidad de Embestida
        } else if (ataque <= 75) {
            estado = 3; // 55% de probabilidad de Rayo de Hielo
            debeLanzarRayo = true;
        } else {
            estado = 4; // 25% de probabilidad de Zona de Hielo
            debeGenerarZona = true;
        }
    }

    public boolean isDebeLanzarRayo() { return debeLanzarRayo; }
    public void consumirRayo() { this.debeLanzarRayo = false; }
    public boolean isDebeGenerarZona() { return debeGenerarZona; }
    public void consumirZona() { this.debeGenerarZona = false; }
}