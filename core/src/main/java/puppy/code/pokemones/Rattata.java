package puppy.code.pokemones;

import puppy.code.movimientos.MovimientoRecto;

public class Rattata extends Pokemon {
    private boolean yaSeAsusto;
    private float radioDeteccion;

    public Rattata(float startX, float startY) {
        super(startX, startY, 80f, 80f, 180f, 25, "pokemones/rattata.png");
        this.yaSeAsusto = false;
        this.radioDeteccion = 220f; // Distancia en píxeles a la que detecta al entrenador
    }

    @Override
    protected void comportamientoEspecial(float delta) {
        setVelocidad(getVelocidad() + (20f * delta));

        if (!this.yaSeAsusto) {
            float centroRattataX = getX() + (getWidth() / 2f);
            float centroRattataY = getY() + (getHeight() / 2f);

            float dx = centroRattataX - getObjetivoX();
            float dy = centroRattataY - getObjetivoY();
            float distancia = (float) Math.sqrt(dx * dx + dy * dy);

            // Si el entrenador se acerca dentro del radio, se asusta y cambia su Strategy en tiempo real
            if (distancia <= this.radioDeteccion) {
                setEstrategiaMovimiento(new MovimientoRecto());
                setVelocidad(getVelocidad() * 1.75f);
                setTiempoAlerta(0.6f); // Muestra el signo "!" encima durante 0.6 segundos
                this.yaSeAsusto = true;
            }
        }
    }

    // Getters y Setters (GM-5)
    public boolean isYaSeAsusto() { return yaSeAsusto; }
    public void setYaSeAsusto(boolean yaSeAsusto) { this.yaSeAsusto = yaSeAsusto; }
    public float getRadioDeteccion() { return radioDeteccion; }
    public void setRadioDeteccion(float radioDeteccion) { this.radioDeteccion = radioDeteccion; }
}