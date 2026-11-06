package puppy.code.movimientos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import puppy.code.pokemones.Pokemon;

public class MovimientoMewtwo implements EstrategiaMovimiento {
	private int estado = 0; 
    private float timerEstado = 0f;
    
    private float cooldownTeleport = 6.0f; 
    private float cooldownRayo = 3.0f;
    private float cooldownEmbestida = 4.0f; 
    
    private float objetivoX; 
    
    private boolean debeLanzarRayo = false;
    private boolean debeGenerarZonaTP = false;
    
    private float marcaTpX, marcaTpY;
    private float direccionX = 1f;

    @Override
    public void mover(Pokemon pokemon, float delta) { 
        timerEstado += delta;
        cooldownTeleport -= delta;
        cooldownRayo -= delta;
        cooldownEmbestida -= delta;
        
        switch (estado) {
            case 0: 
                pokemon.setY(Math.min(pokemon.getY() + 400f * delta, Gdx.graphics.getHeight() - pokemon.getHeight() - 20f));

                float velocidadLateral = 200f; 
                pokemon.setX(pokemon.getX() + (velocidadLateral * direccionX * delta));

                if (pokemon.getX() <= 0) {
                    pokemon.setX(0);
                    direccionX = 1f;
                } else if (pokemon.getX() >= Gdx.graphics.getWidth() - pokemon.getWidth()) {
                    pokemon.setX(Gdx.graphics.getWidth() - pokemon.getWidth());
                    direccionX = -1f;
                }
                
                if (cooldownTeleport <= 0) {
                    prepararTeletransporte(pokemon);
                } else if (cooldownEmbestida <= 0 && detectarJugadorDeFrente(pokemon)) {
                    estado = 1; 
                    timerEstado = 0f;
                } else if (cooldownRayo <= 0) {
                    estado = 3;
                    debeLanzarRayo = true;
                    timerEstado = 0f;
                    cooldownRayo = 4.0f;
                }
                break;
                
            case 1: 
                if (timerEstado >= 0.5f) {
                    estado = 2; 
                    timerEstado = 0f;
                }
                break;
                
            case 2: 
            	pokemon.setY(pokemon.getY() - 1100f * delta);
                if (pokemon.getY() < -pokemon.getHeight()) {
                    pokemon.setY(Gdx.graphics.getHeight() + 50f);
                    estado = 0;
                    timerEstado = 0f;
                    cooldownEmbestida = 3.5f;
                }
                break;
                
            case 3: 
                if (timerEstado > 0.5f) { estado = 0; timerEstado = 0f; }
                break;
                
            case 4: 
                if (timerEstado >= 1.2f) {
                    pokemon.setX(marcaTpX);
                    pokemon.setY(marcaTpY);
                    debeGenerarZonaTP = true; 
                    estado = 5;
                    timerEstado = 0f;
                }
                break;
                
            case 5: 
                if (timerEstado >= 0.5f) {
                    pokemon.setY(Gdx.graphics.getHeight() + 50f);
                    estado = 0;
                    cooldownTeleport = MathUtils.random(6.0f, 9.0f);
                }
                break;
        }
    }
    
    private void prepararTeletransporte(Pokemon pokemon) {
        estado = 4;
        timerEstado = 0f;
        marcaTpX = MathUtils.random(20f, Gdx.graphics.getWidth() - pokemon.getWidth() - 20f);
        marcaTpY = MathUtils.random(50f, Gdx.graphics.getHeight() / 2f);
    }
    
    private boolean detectarJugadorDeFrente(Pokemon pokemon) {
        float centroMewtwo = pokemon.getX() + (pokemon.getWidth() / 2f);
        return Math.abs(centroMewtwo - objetivoX) < 100f; 
    }
    
    public void actualizarObjetivo(float x, float y) {
        this.objetivoX = x;
    }
    
    public int getEstado() { return estado; }
    public boolean isMarcandoTP() { return estado == 4; }
    public float getMarcaTpX() { return marcaTpX; }
    public float getMarcaTpY() { return marcaTpY; }
    
    public boolean isDebeLanzarRayo() { return debeLanzarRayo; }
    public void consumirRayo() { debeLanzarRayo = false; }
    
    public boolean isDebeGenerarZonaTP() { return debeGenerarZonaTP; }
    public void consumirZonaTP() { debeGenerarZonaTP = false; }
}