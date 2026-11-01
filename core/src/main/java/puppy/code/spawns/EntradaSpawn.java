package puppy.code.spawns;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import puppy.code.movimientos.EstrategiaMovimiento;
import puppy.code.pokemones.Pokemon;

/** Pesos relativos por bioma y fábricas polimórficas; no depende del orden del catálogo. */
public class EntradaSpawn {
    private int pesoProbabilidad;
    private int pesoNevado;
    private BiFunction<Float, Float, Pokemon> creadorBase;
    private Supplier<EstrategiaMovimiento> creadorEstrategia;

    public EntradaSpawn(int peso, BiFunction<Float, Float, Pokemon> base,
                        Supplier<EstrategiaMovimiento> estrategia) {
        this(peso, peso, base, estrategia);
    }

    public EntradaSpawn(int peso, int nevado, BiFunction<Float, Float, Pokemon> base,
                        Supplier<EstrategiaMovimiento> estrategia) {
        setPesoProbabilidad(peso);
        setPesoNevado(nevado);
        setCreadorBase(base);
        setCreadorEstrategia(estrategia);
    }

    public EntradaSpawn copia() {
        return new EntradaSpawn(pesoProbabilidad, pesoNevado, creadorBase, creadorEstrategia);
    }

    public int pesoPara(boolean esNevado) { return esNevado ? pesoNevado : pesoProbabilidad; }
    public Pokemon crearInstanciaBase(float x, float y) {
        return Objects.requireNonNull(creadorBase.apply(x, y), "La fábrica no puede devolver null");
    }
    public EstrategiaMovimiento crearEstrategiaInicial() {
        return Objects.requireNonNull(creadorEstrategia.get(), "La estrategia no puede ser null");
    }

    public int getPesoProbabilidad() { return pesoProbabilidad; }
    public void setPesoProbabilidad(int peso) {
        if (peso < 0) throw new IllegalArgumentException("Peso negativo");
        pesoProbabilidad = peso;
    }
    public int getPesoNevado() { return pesoNevado; }
    public void setPesoNevado(int peso) {
        if (peso < 0) throw new IllegalArgumentException("Peso nevado negativo");
        pesoNevado = peso;
    }
    public BiFunction<Float, Float, Pokemon> getCreadorBase() { return creadorBase; }
    public void setCreadorBase(BiFunction<Float, Float, Pokemon> creador) {
        creadorBase = Objects.requireNonNull(creador, "creadorBase");
    }
    public Supplier<EstrategiaMovimiento> getCreadorEstrategia() { return creadorEstrategia; }
    public void setCreadorEstrategia(Supplier<EstrategiaMovimiento> creador) {
        creadorEstrategia = Objects.requireNonNull(creador, "creadorEstrategia");
    }
}
