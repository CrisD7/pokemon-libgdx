package puppy.code.pokemones;

public class Rattata extends Pokemon {
    public Rattata(float startX, float startY) {
        super(startX, startY, 80f, 80f, 140f, 25, "pokemones/rattata.png");
    }

    @Override
    protected void comportamientoEspecial(float delta) {
        setVelocidad(getVelocidad() + (10f * delta));
    }
}