package puppy.code.spawns;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;

import puppy.code.movimientos.MovimientoEstatico;
import puppy.code.movimientos.MovimientoRecto;
import puppy.code.movimientos.MovimientoZigZag;
import puppy.code.pokemones.Caterpie;
import puppy.code.pokemones.Kadabra;
import puppy.code.pokemones.Pokemon;
import puppy.code.pokemones.Rattata;
import puppy.code.pokemones.Snorlax;
import puppy.code.pokemones.Snorunt;

public class GestorSpawns {
    // Instancia única estática y privada (GM-6)
    private static GestorSpawns instancia;

    private float timerSpawn;
    private float intervaloSpawn;
    private ArrayList<EntradaSpawn> catalogoSpawns;
    private int pesoTotal;

    private GestorSpawns() {
        this.timerSpawn = 0f;
        this.intervaloSpawn = 1.8f;
        this.catalogoSpawns = new ArrayList<>();
        this.pesoTotal = 0;

        inicializarCatalogoPorDefecto();
    }

    public static GestorSpawns getInstancia() {
        if (instancia == null) {
            instancia = new GestorSpawns();
        }
        return instancia;
    }

    // Permite agregar nuevas especies en tiempo de ejecución sin tocar la lógica de sorteo (Cumple OCP)
    public void registrarSpawn(EntradaSpawn nuevaEntrada) {
        this.catalogoSpawns.add(nuevaEntrada);
        this.pesoTotal += nuevaEntrada.getPesoProbabilidad();
    }

    private void inicializarCatalogoPorDefecto() {
        // 45% Caterpie, 25% Rattata, 15% Kadabra, 15% Snorlax, 20% Snorunt
        registrarSpawn(new EntradaSpawn(45, (x, y) -> new Caterpie(x, y), () -> new MovimientoRecto()));
        registrarSpawn(new EntradaSpawn(25, (x, y) -> new Rattata(x, y), () -> new MovimientoZigZag()));
        registrarSpawn(new EntradaSpawn(15, (x, y) -> new Kadabra(x, y), () -> new MovimientoEstatico()));
        registrarSpawn(new EntradaSpawn(15, (x, y) -> new Snorlax(x, y), () -> new MovimientoRecto()));
        registrarSpawn(new EntradaSpawn(20, (x, y) -> new Snorunt(x, y), () -> new MovimientoRecto()));
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
        float posX = MathUtils.random(40f, Gdx.graphics.getWidth() - 120f);
        float posY = Gdx.graphics.getHeight() + 20f;

        // Selección ponderada dinámica sin condicionales por especie
        int tirada = MathUtils.random(1, this.pesoTotal);
        int acumulado = 0;
        EntradaSpawn seleccionada = this.catalogoSpawns.get(0);

        for (EntradaSpawn entrada : this.catalogoSpawns) {
            acumulado += entrada.getPesoProbabilidad();
            if (tirada <= acumulado) {
                seleccionada = entrada;
                break;
            }
        }

        boolean shiny = MathUtils.random(1, 100) <= 10;
        float multVel = shiny ? 1.4f : 1.0f;

        PokemonBuilder builder = new PokemonBuilder();
        return builder.conPokemonBase(seleccionada.crearInstanciaBase(posX, posY))
                      .enPosicion(posX, posY)
                      .conEstrategia(seleccionada.crearEstrategiaInicial())
                      .conShiny(shiny)
                      .conMultiplicadorVelocidad(multVel)
                      .build();
    }
    
    public void reiniciar() {
        this.timerSpawn = 0f;
        this.intervaloSpawn = 1.8f;
    }

    // Reduce el intervalo de aparición a medida que sube el puntaje (hasta un mínimo de 0.65 seg)
    public void actualizarDificultadSegunPuntaje(int puntajeActual) {
        float nuevoIntervalo = 1.8f - (puntajeActual / 150) * 0.15f;
        if (nuevoIntervalo < 0.65f) {
            nuevoIntervalo = 0.65f;
        }
        this.intervaloSpawn = nuevoIntervalo;
    }

    // Getters y Setters estrictos (GM-5)
    public float getTimerSpawn() { return timerSpawn; }
    public void setTimerSpawn(float timerSpawn) { this.timerSpawn = timerSpawn; }
    public float getIntervaloSpawn() { return intervaloSpawn; }
    public void setIntervaloSpawn(float intervaloSpawn) { this.intervaloSpawn = intervaloSpawn; }
    public ArrayList<EntradaSpawn> getCatalogoSpawns() { return catalogoSpawns; }
    public void setCatalogoSpawns(ArrayList<EntradaSpawn> catalogoSpawns) { this.catalogoSpawns = catalogoSpawns; }
    public int getPesoTotal() { return pesoTotal; }
    public void setPesoTotal(int pesoTotal) { this.pesoTotal = pesoTotal; }
}