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
import puppy.code.pokemones.Glalie;
import puppy.code.pokemones.Espeon;

public class GestorSpawns {
    // Instancia única estática y privada (GM-6)
    private static GestorSpawns instancia;

    private float timerSpawn;
    private float intervaloSpawn;
    private ArrayList<EntradaSpawn> catalogoSpawns;

    private GestorSpawns() {
        this.timerSpawn = 0f;
        this.intervaloSpawn = 1.8f;
        this.catalogoSpawns = new ArrayList<>();

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
        ArrayList<EntradaSpawn> nuevo = getCatalogoSpawns();
        nuevo.add(java.util.Objects.requireNonNull(nuevaEntrada, "nuevaEntrada").copia());
        validarTotales(nuevo);
        this.catalogoSpawns = nuevo;
    }

    private void inicializarCatalogoPorDefecto() {
        // Pesos relativos: normal total 120, nevado total 600. Conservan la mezcla anterior.
        registrarSpawn(new EntradaSpawn(45, 135, (x, y) -> new Caterpie(x, y), () -> new MovimientoRecto()));
        registrarSpawn(new EntradaSpawn(25, 75, (x, y) -> new Rattata(x, y), () -> new MovimientoZigZag()));
        registrarSpawn(new EntradaSpawn(15, 45, (x, y) -> new Kadabra(x, y), () -> new MovimientoEstatico()));
        registrarSpawn(new EntradaSpawn(15, 45, (x, y) -> new Snorlax(x, y), () -> new MovimientoRecto()));
        registrarSpawn(new EntradaSpawn(20, 210, (x, y) -> new Snorunt(x, y), () -> new MovimientoRecto()));
        registrarSpawn(new EntradaSpawn(0, 90, (x, y) -> new Glalie(x, y), () -> new MovimientoRecto()));
        registrarSpawn(new EntradaSpawn(15, 45, (x, y) -> new Espeon(x, y), () -> new MovimientoRecto()));
    }

    public Pokemon intentarGenerarPokemon(float delta, boolean esNevado) {
        this.timerSpawn += delta;
        if (this.timerSpawn >= this.intervaloSpawn) {
            this.timerSpawn = 0f;
            return construirPokemonAleatorio(esNevado);
        }
        return null;
    }

    private Pokemon construirPokemonAleatorio(boolean esNevado) {
        float posX = MathUtils.random(40f, Gdx.graphics.getWidth() - 120f);
        float posY = Gdx.graphics.getHeight() + 20f;

        int total = getPesoTotal(esNevado);
        if (total == 0) return null;
        EntradaSpawn seleccionada = seleccionarEntrada(esNevado, MathUtils.random(1, total));

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
    public void setTimerSpawn(float timerSpawn) { if (!Float.isFinite(timerSpawn) || timerSpawn < 0f) throw new IllegalArgumentException("Timer inválido");
        this.timerSpawn = timerSpawn; }
    public float getIntervaloSpawn() { return intervaloSpawn; }
    public void setIntervaloSpawn(float intervaloSpawn) { if (!Float.isFinite(intervaloSpawn) || intervaloSpawn <= 0f) throw new IllegalArgumentException("Intervalo inválido");
        this.intervaloSpawn = intervaloSpawn; }
    /** Copias defensivas de lista Y entradas: editar un getter no cambia el sorteo. */
    public ArrayList<EntradaSpawn> getCatalogoSpawns() {
        ArrayList<EntradaSpawn> copia = new ArrayList<>();
        for (EntradaSpawn entrada : catalogoSpawns) copia.add(entrada.copia());
        return copia;
    }
    public void setCatalogoSpawns(ArrayList<EntradaSpawn> catalogo) {
        java.util.Objects.requireNonNull(catalogo, "catalogo");
        ArrayList<EntradaSpawn> copia = new ArrayList<>();
        for (EntradaSpawn entrada : catalogo) copia.add(java.util.Objects.requireNonNull(entrada).copia());
        validarTotales(copia);
        catalogoSpawns = copia;
    }
    private static void validarTotales(ArrayList<EntradaSpawn> catalogo) {
        long normal = 0, nevado = 0;
        for (EntradaSpawn entrada : catalogo) {
            normal += entrada.getPesoProbabilidad();
            nevado += entrada.getPesoNevado();
        }
        if (normal > Integer.MAX_VALUE || nevado > Integer.MAX_VALUE)
            throw new IllegalArgumentException("La suma de pesos excede el rango entero");
    }
    public int getPesoTotal() { return getPesoTotal(false); }
    public int getPesoTotal(boolean esNevado) {
        int total = 0;
        for (EntradaSpawn entrada : catalogoSpawns) total += entrada.pesoPara(esNevado);
        return total;
    }
    /** El total es derivado; no se puede desincronizar del catálogo. */
    public void setPesoTotal(int peso) {
        if (peso != getPesoTotal()) throw new IllegalArgumentException("El total depende del catálogo");
    }
    /** Selección determinista para una tirada en [1,total], sin crear entidades ni recursos. */
    public EntradaSpawn seleccionarEntrada(boolean esNevado, int tirada) {
        if (tirada < 1 || tirada > getPesoTotal(esNevado))
            throw new IllegalArgumentException("Tirada fuera del peso total");
        int acumulado = 0;
        for (EntradaSpawn entrada : catalogoSpawns) {
            acumulado += entrada.pesoPara(esNevado);
            if (tirada <= acumulado) return entrada.copia();
        }
        throw new IllegalStateException("Catálogo incoherente");
    }
}
