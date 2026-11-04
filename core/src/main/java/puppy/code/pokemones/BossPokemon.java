package puppy.code.pokemones;

public class BossPokemon extends Pokemon {

    public BossPokemon(float x, float y, String rutaSprite) {
        super(x, y, 128f, 128f, 200f, 500, rutaSprite);
        setResistenciaCapturas(20);
    }

    @Override
    protected void comportamientoEspecial(float delta) {
    }
}