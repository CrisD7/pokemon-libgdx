package puppy.code.pokemones;

public class Caterpie extends Pokemon {
    public Caterpie(float startX, float startY) {
        super(startX, startY, 80f, 80f, 60f, 10, "pokemones/caterpie.png");
    }

    @Override
    protected void comportamientoEspecial(float delta) {
        if (isEsShiny() && getPuntosPorCaptura() == 10) {
            setPuntosPorCaptura(20);
        }
    }
}