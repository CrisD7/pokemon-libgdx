package puppy.code.spawns;

import puppy.code.movimientos.EstrategiaMovimiento;
import puppy.code.movimientos.MovimientoRecto;
import puppy.code.pokemones.Caterpie;
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
        this.pokemonBase = pokemonBase;
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
        this.multiplicadorVelocidad = multiplicador;
        return this;
    }

    public PokemonBuilder conEstrategia(EstrategiaMovimiento estrategia) {
        this.estrategia = estrategia;
        return this;
    }

    public Pokemon build() {
        // Si no se especificó ninguno, por defecto instancia un Caterpie
        if (this.pokemonBase == null) {
            this.pokemonBase = new Caterpie(this.x, this.y);
        } else {
            this.pokemonBase.setX(this.x);
            this.pokemonBase.setY(this.y);
        }

        this.pokemonBase.setEsShiny(this.esShiny);
        this.pokemonBase.setVelocidad(this.pokemonBase.getVelocidad() * this.multiplicadorVelocidad);
        this.pokemonBase.setEstrategiaMovimiento(this.estrategia);

        return this.pokemonBase;
    }

    // Getters y Setters estrictos para cumplir GM-5
    public Pokemon getPokemonBase() { return pokemonBase; }
    public void setPokemonBase(Pokemon pokemonBase) { this.pokemonBase = pokemonBase; }
    public float getX() { return x; }
    public void setX(float x) { this.x = x; }
    public float getY() { return y; }
    public void setY(float y) { this.y = y; }
    public boolean isEsShiny() { return esShiny; }
    public void setEsShiny(boolean esShiny) { this.esShiny = esShiny; }
    public float getMultiplicadorVelocidad() { return multiplicadorVelocidad; }
    public void setMultiplicadorVelocidad(float multiplicadorVelocidad) { this.multiplicadorVelocidad = multiplicadorVelocidad; }
    public EstrategiaMovimiento getEstrategia() { return estrategia; }
    public void setEstrategia(EstrategiaMovimiento estrategia) { this.estrategia = estrategia; }
}