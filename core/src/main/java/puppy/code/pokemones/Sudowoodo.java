package puppy.code.pokemones;

public class Sudowoodo extends Pokemon {
    public Sudowoodo(float x, float y) {
        super(x, y, 88f, 88f, 0f, 20, "pokemones/sudowoodo.png", 3, 0.55f);
    }

    @Override
    protected void comportamientoEspecial(float delta) {
        if (isEsShiny() && getPuntosPorCaptura() == 20) setPuntosPorCaptura(40);
    }
}
