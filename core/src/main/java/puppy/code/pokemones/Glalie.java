package puppy.code.pokemones;

public class Glalie extends Pokemon {

    public Glalie(float x, float y) {
        super(x, y, 120f, 120f, 140f, 50, "pokemones/Glalie.png");
        setResistenciaCapturas(2);
    }

    @Override
    protected void comportamientoEspecial(float delta) {
    }
}