package puppy.code.movimientos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import puppy.code.pokemones.Pokemon;

public class MovimientoRayquaza implements EstrategiaMovimiento {
    private int estado = 0; 
    private float timerEstado = 0f;
    
    private float cooldownCometa = 5.0f;
    private float cooldownPulso = 3.0f;
    private float cooldownTrueno = 8.0f;
    private float cooldownEnfado = 15.0f;
    
    private float objetivoX, objetivoY;
    private float direccionX = 1f; 
    
    private boolean debeGenerarCometas = false;
    private boolean debeLanzarPulso = false;
    private boolean debeGenerarTrueno = false;
    private boolean debeLanzarFuria = false;

    private int truenosLanzados = 0;
    private float timerSiguienteTrueno = 0f;
    private float timerDisparoFuria = 0f;

    @Override
    public void mover(Pokemon pokemon, float delta) { 
        float topeY = Gdx.graphics.getHeight() - pokemon.getHeight() - 20f;
        if (pokemon.getY() < topeY) {
            pokemon.setY(Math.min(pokemon.getY() + 400f * delta, topeY));
        }

        timerEstado += delta;
        if (estado == 0) {
            cooldownCometa -= delta;
            cooldownPulso -= delta;
            cooldownTrueno -= delta;
            cooldownEnfado -= delta;
        }
        
        switch (estado) {
            case 0: 
                pokemon.setX(pokemon.getX() + (150f * direccionX * delta));
                if (pokemon.getX() <= 0) { pokemon.setX(0); direccionX = 1f; }
                else if (pokemon.getX() >= Gdx.graphics.getWidth() - pokemon.getWidth()) {
                    pokemon.setX(Gdx.graphics.getWidth() - pokemon.getWidth()); direccionX = -1f;
                }
                
                if (cooldownEnfado <= 0) {
                    estado = 4; timerEstado = 0f; timerDisparoFuria = 0f;
                } else if (cooldownTrueno <= 0) {
                    estado = 3; timerEstado = 0f; truenosLanzados = 0; timerSiguienteTrueno = 0f;
                } else if (cooldownCometa <= 0) {
                    estado = 1; timerEstado = 0f; debeGenerarCometas = true; cooldownCometa = MathUtils.random(6f, 9f);
                } else if (cooldownPulso <= 0) {
                    estado = 2; timerEstado = 0f; debeLanzarPulso = true; cooldownPulso = MathUtils.random(3f, 5f);
                }
                break;
                
            case 1: 
                if (timerEstado >= 0.8f) estado = 0;
                break;
                
            case 2: 
                if (timerEstado >= 0.5f) estado = 0;
                break;
                
            case 3: 
                timerSiguienteTrueno += delta;
                if (truenosLanzados < 4) {
                    if (timerSiguienteTrueno >= 0.6f) { 
                        debeGenerarTrueno = true;
                        truenosLanzados++;
                        timerSiguienteTrueno = 0f;
                    }
                } else if (timerEstado >= 3.0f) {
                    estado = 0; cooldownTrueno = MathUtils.random(8f, 12f);
                }
                break;
                
            case 4:
                pokemon.setX(pokemon.getX() + (350f * direccionX * delta)); 
                if (pokemon.getX() <= 0) { pokemon.setX(0); direccionX = 1f; }
                else if (pokemon.getX() >= Gdx.graphics.getWidth() - pokemon.getWidth()) {
                    pokemon.setX(Gdx.graphics.getWidth() - pokemon.getWidth()); direccionX = -1f;
                }
                
                timerDisparoFuria += delta;
                if (timerDisparoFuria >= 0.7f) { 
                    debeLanzarFuria = true;
                    timerDisparoFuria = 0f;
                }
                
                if (timerEstado >= 4.0f) { 
                    estado = 5; 
                    timerEstado = 0f;
                }
                break;
                
            case 5: 
                if (timerEstado >= 2.5f) {
                    estado = 0;
                    cooldownEnfado = 18.0f; 
                }
                break;
        }
    }
    
    public void actualizarObjetivo(float x, float y) { this.objetivoX = x; this.objetivoY = y; }
    public int getEstado() { return estado; }
    public boolean isDebeGenerarCometas() { return debeGenerarCometas; }
    public void consumirCometas() { debeGenerarCometas = false; }
    public boolean isDebeLanzarPulso() { return debeLanzarPulso; }
    public void consumirPulso() { debeLanzarPulso = false; }
    public boolean isDebeGenerarTrueno() { return debeGenerarTrueno; }
    public void consumirTrueno() { debeGenerarTrueno = false; }
    public boolean isDebeLanzarFuria() { return debeLanzarFuria; }
    public void consumirFuria() { debeLanzarFuria = false; }
    public float getObjetivoX() { return objetivoX; }
    public float getObjetivoY() { return objetivoY; }
}