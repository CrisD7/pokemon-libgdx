package puppy.code.pokemones;

import puppy.code.ProyectilConfusion;

public class Espeon extends Pokemon {
	
	private float tiempoDisparo;
    private final float intervaloDisparo = 1f;

    private ProyectilConfusion proyectilPendiente;
    
    public Espeon(float startX, float startY) {
        super(startX, startY, 100f, 100f, 100f, 35, "pokemones/espeon.png");
        
        this.tiempoDisparo = intervaloDisparo;
        this.proyectilPendiente = null;
    }

    @Override
    protected void comportamientoEspecial(float delta) {
        // Bonus si es shiny
        if (isEsShiny() && getPuntosPorCaptura() == 10) {
            setPuntosPorCaptura(20);
        }

        // Centro horizontal de Espeon
        float centroEspeonX = getX() + getWidth() / 2f;

        // Comprobar si el entrenador está debajo
        boolean jugadorDebajo = getObjetivoY() < getY();

        // Comprobar si está alineado horizontalmente
        boolean jugadorAlineado =
                Math.abs(getObjetivoX() - centroEspeonX) <= 35f;

        // Comprobar si está dentro del rango de visión
        boolean dentroDelRango =
                getY() - getObjetivoY() <= 500f;

        boolean puedeVerJugador =
                jugadorDebajo && jugadorAlineado && dentroDelRango;

        // Disparar solamente si ve al entrenador
        if (puedeVerJugador) {
            tiempoDisparo += delta;

            if (tiempoDisparo >= intervaloDisparo) {
                tiempoDisparo -= intervaloDisparo;

                proyectilPendiente = new ProyectilConfusion(
                        centroEspeonX,
                        getY()
                );
            }
        }
    }
    
    public ProyectilConfusion consumirProyectil() {
        ProyectilConfusion proyectil = proyectilPendiente;
        proyectilPendiente = null;
        return proyectil;
    }
}