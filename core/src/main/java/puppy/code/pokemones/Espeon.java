package puppy.code.pokemones;

import puppy.code.EmisorConfusion;
import puppy.code.ProyectilConfusion;

public class Espeon extends Pokemon implements EmisorConfusion {
    private static final float INTERVALO_DISPARO = 1f;
    private float tiempoDisparo = INTERVALO_DISPARO;
    private ProyectilConfusion proyectilPendiente;

    public Espeon(float startX, float startY) {
        super(startX, startY, 100f, 100f, 100f, 35, "pokemones/espeon.png");
    }

    @Override
    protected void comportamientoEspecial(float delta) {
        if (isEsShiny() && getPuntosPorCaptura() == 35) {
            setPuntosPorCaptura(70);
        }
        // El cooldown transcurre también cuando el jugador sale de la línea de visión.
        tiempoDisparo = Math.min(INTERVALO_DISPARO, tiempoDisparo + delta);
        float centroX = getX() + getWidth() / 2f;
        float distanciaY = getY() - getObjetivoY();
        if (isColisionActiva() && distanciaY > 0f && distanciaY <= 500f
                && Math.abs(getObjetivoX() - centroX) <= 55f
                && tiempoDisparo >= INTERVALO_DISPARO && proyectilPendiente == null) {
            proyectilPendiente = new ProyectilConfusion(centroX, getY());
            tiempoDisparo = 0f;
        }
    }

    @Override
    public ProyectilConfusion consumirProyectil() {
        ProyectilConfusion proyectil = proyectilPendiente;
        proyectilPendiente = null;
        return proyectil;
    }

    public float getTiempoDisparo() { return tiempoDisparo; }
}
