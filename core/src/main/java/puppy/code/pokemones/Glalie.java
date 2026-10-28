package puppy.code.pokemones;

public class Glalie extends Pokemon {
    private int impactosRecibidos;

    public Glalie(float x, float y) {
        super(x, y, 120f, 120f, 140f, 50, "pokemones/Glalie.png");
        this.impactosRecibidos = 0;
    }

    @Override
    public boolean recibirImpactoPokeball() {
        this.impactosRecibidos++;
        if (this.impactosRecibidos >= 2) {
            return super.recibirImpactoPokeball();
        }
        return false;
    }

    @Override
    protected void comportamientoEspecial(float delta) {
    }
}