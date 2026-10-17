package puppy.code.spawns;

import puppy.code.movimientos.EstrategiaMovimiento;
import puppy.code.movimientos.MovimientoRecto;
import puppy.code.pokemones.Caterpie;
import puppy.code.pokemones.Pokemon;
import puppy.code.pokemones.Rattata;

public class PokemonBuilder {
    private String especie;
    private float x;
    private float y;
    private boolean esShiny;
    private float multiplicadorVelocidad;
    private EstrategiaMovimiento estrategia;

    public PokemonBuilder() {
        this.especie = "CATERPIE";
        this.x = 0f;
        this.y = 0f;
        this.esShiny = false;
        this.multiplicadorVelocidad = 1.0f;
        this.estrategia = new MovimientoRecto();
    }

    public PokemonBuilder conEspecie(String especie) {
        this.especie = especie;
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
        Pokemon nuevoPokemon;
        if ("RATTATA".equalsIgnoreCase(this.especie)) {
            nuevoPokemon = new Rattata(this.x, this.y);
        } else {
            nuevoPokemon = new Caterpie(this.x, this.y);
        }

        nuevoPokemon.setEsShiny(this.esShiny);
        nuevoPokemon.setVelocidad(nuevoPokemon.getVelocidad() * this.multiplicadorVelocidad);
        nuevoPokemon.setEstrategiaMovimiento(this.estrategia);

        return nuevoPokemon;
    }

    // Getters y Setters para cumplir GM-5
    public String getEspecie() { return especie; }
    public void setEspecie(String especie) { this.especie = especie; }
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