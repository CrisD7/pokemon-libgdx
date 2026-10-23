package puppy.code.pokemones;

public class Snorlax extends Pokemon {

    public Snorlax(float startX, float startY) {
        // Tamaño grande (145x145), velocidad lenta (35f) y 100 puntos de recompensa
        super(startX, startY, 145f, 145f, 35f, 100, "pokemones/snorlax.png");
        // Requiere 3 golpes de Pokéball para ser capturado
        setResistenciaCapturas(3);
    }

    @Override
    protected void comportamientoEspecial(float delta) {
        if (isEsShiny() && getPuntosPorCaptura() == 100) {
            setPuntosPorCaptura(200);
        }
    }
}