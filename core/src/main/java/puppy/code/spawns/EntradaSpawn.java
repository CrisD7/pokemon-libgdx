package puppy.code.spawns;

import java.util.function.BiFunction;
import java.util.function.Supplier;
import puppy.code.movimientos.EstrategiaMovimiento;
import puppy.code.pokemones.Pokemon;

public class EntradaSpawn {
    private int pesoProbabilidad;
    private BiFunction<Float, Float, Pokemon> creadorBase;
    private Supplier<EstrategiaMovimiento> creadorEstrategia;

    public EntradaSpawn(int pesoProbabilidad, BiFunction<Float, Float, Pokemon> creadorBase, 
                        Supplier<EstrategiaMovimiento> creadorEstrategia) {
        this.pesoProbabilidad = pesoProbabilidad;
        this.creadorBase = creadorBase;
        this.creadorEstrategia = creadorEstrategia;
    }

    public Pokemon crearInstanciaBase(float x, float y) {
        return this.creadorBase.apply(x, y);
    }

    public EstrategiaMovimiento crearEstrategiaInicial() {
        return this.creadorEstrategia.get();
    }

    // Getters y Setters estrictos (GM-5)
    public int getPesoProbabilidad() { return pesoProbabilidad; }
    public void setPesoProbabilidad(int pesoProbabilidad) { this.pesoProbabilidad = pesoProbabilidad; }
    public BiFunction<Float, Float, Pokemon> getCreadorBase() { return creadorBase; }
    public void setCreadorBase(BiFunction<Float, Float, Pokemon> creadorBase) { this.creadorBase = creadorBase; }
    public Supplier<EstrategiaMovimiento> getCreadorEstrategia() { return creadorEstrategia; }
    public void setCreadorEstrategia(Supplier<EstrategiaMovimiento> creadorEstrategia) { this.creadorEstrategia = creadorEstrategia; }
}