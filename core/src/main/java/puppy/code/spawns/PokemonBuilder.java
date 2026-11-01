package puppy.code.spawns;

import puppy.code.movimientos.EstrategiaMovimiento;
import puppy.code.movimientos.MovimientoRecto;
import java.util.Objects;
import puppy.code.pokemones.Pokemon;

public class PokemonBuilder {
    private Pokemon pokemonBase;
    private float x;
    private float y;
    private boolean esShiny;
    private float multiplicadorVelocidad;
    private EstrategiaMovimiento estrategia;

    public PokemonBuilder() {
        this.pokemonBase = null;
        this.x = 0f;
        this.y = 0f;
        this.esShiny = false;
        this.multiplicadorVelocidad = 1.0f;
        this.estrategia = new MovimientoRecto();
    }

    // Recibe cualquier subclase de Pokemon por polimorfismo (Cumple OCP: no requiere if-else)
    public PokemonBuilder conPokemonBase(Pokemon pokemonBase) {
        this.pokemonBase = Objects.requireNonNull(pokemonBase, "pokemonBase");
        return this;
    }

    public PokemonBuilder enPosicion(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public PokemonBuilder conShiny(boolean esShiny) {
        this.esShiny = esShiny;
        return this;
    }

    public PokemonBuilder conMultiplicadorVelocidad(float multiplicador) {
        setMultiplicadorVelocidad(multiplicador);
        return this;
    }

    public PokemonBuilder conEstrategia(EstrategiaMovimiento estrategia) {
        this.estrategia = Objects.requireNonNull(estrategia, "estrategia");
        return this;
    }

    public Pokemon build() {
        if (this.pokemonBase == null) {
            throw new IllegalStateException("Se requiere una instancia base no consumida");
        }
        this.pokemonBase.setX(this.x);
        this.pokemonBase.setY(this.y);

        this.pokemonBase.setEsShiny(this.esShiny);
        this.pokemonBase.setVelocidad(this.pokemonBase.getVelocidad() * this.multiplicadorVelocidad);
        this.pokemonBase.setEstrategiaMovimiento(this.estrategia);

        Pokemon resultado = this.pokemonBase;
        this.pokemonBase = null; // Un build consume la base; el siguiente exige otra.
        return resultado;
    }

    // Getters y Setters estrictos para cumplir GM-5
    public Pokemon getPokemonBase() { return pokemonBase; }
    public void setPokemonBase(Pokemon pokemonBase) { this.pokemonBase = Objects.requireNonNull(pokemonBase, "pokemonBase"); }
    public float getX() { return x; }
    public void setX(float x) { this.x = x; }
    public float getY() { return y; }
    public void setY(float y) { this.y = y; }
    public boolean isEsShiny() { return esShiny; }
    public void setEsShiny(boolean esShiny) { this.esShiny = esShiny; }
    public float getMultiplicadorVelocidad() { return multiplicadorVelocidad; }
    public void setMultiplicadorVelocidad(float multiplicadorVelocidad) {
        if (!Float.isFinite(multiplicadorVelocidad) || multiplicadorVelocidad <= 0f) {
            throw new IllegalArgumentException("El multiplicador debe ser finito y positivo");
        }
        this.multiplicadorVelocidad = multiplicadorVelocidad;
    }
    public EstrategiaMovimiento getEstrategia() { return estrategia; }
    public void setEstrategia(EstrategiaMovimiento estrategia) { this.estrategia = Objects.requireNonNull(estrategia, "estrategia"); }
}