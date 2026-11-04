package puppy.code.pokemones;

public class Espeon extends Pokemon {
    public Espeon(float startX, float startY) {
        super(startX, startY, 80f, 80f, 100f, 10, "pokemones/espeon.png");
    }

    @Override
    protected void comportamientoEspecial(float delta) {
        if (isEsShiny() && getPuntosPorCaptura() == 10) {
            setPuntosPorCaptura(20);
        }
    }
}