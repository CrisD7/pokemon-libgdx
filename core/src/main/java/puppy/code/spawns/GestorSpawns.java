package puppy.code.spawns;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import puppy.code.movimientos.MovimientoRecto;
import puppy.code.movimientos.MovimientoZigZag;
import puppy.code.pokemones.Pokemon;

public class GestorSpawns {
    // Instancia única estática y privada (GM-6)
    private static GestorSpawns instancia;

    private float timerSpawn;
    private float intervaloSpawn;

    // Constructor privado para impedir "new GestorSpawns()" desde afuera
    private GestorSpawns() {
        this.timerSpawn = 0f;
        this.intervaloSpawn = 1.8f; // Aparece un Pokémon cada 1.8 segundos
    }

    public static GestorSpawns getInstancia() {
        if (instancia == null) {
            instancia = new GestorSpawns();
        }
        return instancia;
    }

    public Pokemon intentarGenerarPokemon(float delta) {
        this.timerSpawn += delta;
        if (this.timerSpawn >= this.intervaloSpawn) {
            this.timerSpawn = 0f;
            return construirPokemonAleatorio();
        }
        return null;
    }

    private Pokemon construirPokemonAleatorio() {
        PokemonBuilder builder = new PokemonBuilder();
        float posX = MathUtils.random(40f, Gdx.graphics.getWidth() - 120f);
        float posY = Gdx.graphics.getHeight() + 20f;

        // Probabilidad por rareza: 65% Caterpie (Común), 35% Rattata (Poco común)
        int tiradaRareza = MathUtils.random(1, 100);
        if (tiradaRareza <= 65) {
            builder.conEspecie("CATERPIE")
                   .conEstrategia(new MovimientoRecto());
        } else {
            builder.conEspecie("RATTATA")
                   .conEstrategia(new MovimientoZigZag());
        }

        // 10% de probabilidad de que aparezca Shiny y más rápido
        boolean shiny = MathUtils.random(1, 100) <= 10;
        float multVel = shiny ? 1.4f : 1.0f;

        return builder.enPosicion(posX, posY)
                      .conShiny(shiny)
                      .conMultiplicadorVelocidad(multVel)
                      .build();
    }

    public float getTimerSpawn() { return timerSpawn; }
    public void setTimerSpawn(float timerSpawn) { this.timerSpawn = timerSpawn; }
    public float getIntervaloSpawn() { return intervaloSpawn; }
    public void setIntervaloSpawn(float intervaloSpawn) { this.intervaloSpawn = intervaloSpawn; }
}