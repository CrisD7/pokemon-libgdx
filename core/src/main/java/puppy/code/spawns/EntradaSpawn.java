package puppy.code.spawns;

import java.util.function.BiFunction;
import puppy.code.pokemones.Pokemon;

public class EntradaSpawn {
    private int pesoProbabilidad;
    private BiFunction<Float, Float, Pokemon> creadorBase;
    private puppy.code.movimientos.EstrategiaMovimiento estrategiaInicial;

    public EntradaSpawn(int pesoProbabilidad, BiFunction<Float, Float, Pokemon> creadorBase, 
                        puppy.code.movimientos.EstrategiaMovimiento estrategiaInicial) {
        this.pesoProbabilidad = pesoProbabilidad;
        this.creadorBase = creadorBase;
        this.estrategiaInicial = estrategiaInicial;
    }

    public Pokemon crearInstanciaBase(float x, float y) {
        return this.creadorBase.apply(x, y);
    }

    // Getters y Setters estrictos (GM-5)
    public int getPesoProbabilidad() { return pesoProbabilidad; }
    public void setPesoProbabilidad(int pesoProbabilidad) { this.pesoProbabilidad = pesoProbabilidad; }
    public BiFunction<Float, Float, Pokemon> getCreadorBase() { return creadorBase; }
    public void setCreadorBase(BiFunction<Float, Float, Pokemon> creadorBase) { this.creadorBase = creadorBase; }
    public puppy.code.movimientos.EstrategiaMovimiento getEstrategiaInicial() { return estrategiaInicial; }
    public void setEstrategiaInicial(puppy.code.movimientos.EstrategiaMovimiento estrategiaInicial) { this.estrategiaInicial = estrategiaInicial; }
}