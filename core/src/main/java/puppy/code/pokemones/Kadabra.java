package puppy.code.pokemones;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import puppy.code.movimientos.MovimientoArriba;

public class Kadabra extends Pokemon {
    public static final int ESTADO_SOMBRA = 0;
    public static final int ESTADO_ZOOM = 1;
    public static final int ESTADO_ACTIVO = 2;

    private static Texture hojaSombra;
    private static TextureRegion[] framesSombra;

    private int estadoActual;
    private boolean posicionFijada;
    private float timerFase;
    private int frameSombraActual;
    private float escalaZoom;

    public Kadabra(float startX, float startY) {
        // Tamaño aumentado a 115x115 y velocidad 320f para vencer el scroll del mapa hacia arriba
        super(startX, startY, 115f, 115f, 320f, 50, "pokemones/kadabra.png");
        this.estadoActual = ESTADO_SOMBRA;
        this.posicionFijada = false;
        this.timerFase = 0f;
        this.frameSombraActual = 0;
        this.escalaZoom = 0f;

        setColisionActiva(false);
        cargarSpritesSombra();
    }

    private static void cargarSpritesSombra() {
        if (hojaSombra == null) {
            hojaSombra = new Texture(Gdx.files.internal("pokemones/sombra_tp.png"));
            hojaSombra.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

            int anchoFrame = hojaSombra.getWidth() / 6;
            int altoFrame = hojaSombra.getHeight();
            TextureRegion[][] temp = TextureRegion.split(hojaSombra, anchoFrame, altoFrame);
            
            framesSombra = new TextureRegion[6];
            for (int i = 0; i < 6; i++) {
                framesSombra[i] = temp[0][i];
            }
        }
    }

    @Override
    protected void comportamientoEspecial(float delta) {
        if (!this.posicionFijada && (getObjetivoX() != 0 || getObjetivoY() != 0)) {
            setX(getObjetivoX() - (getWidth() / 2f));
            setY(getObjetivoY() - (getHeight() / 2f));
            this.posicionFijada = true;
        }

        if (this.estadoActual == ESTADO_SOMBRA) {
            this.timerFase += delta;
            if (this.timerFase >= 0.14f) {
                this.timerFase = 0f;
                this.frameSombraActual++;
                if (this.frameSombraActual >= 6) {
                    this.frameSombraActual = 5;
                    this.estadoActual = ESTADO_ZOOM;
                }
            }
        } else if (this.estadoActual == ESTADO_ZOOM) {
            this.escalaZoom += delta * 5.0f;
            if (this.escalaZoom >= 1.0f) {
                this.escalaZoom = 1.0f;
                this.estadoActual = ESTADO_ACTIVO;
                
                // Se materializa, activa su hitbox y escapa corriendo hacia ARRIBA (GM-7)
                setColisionActiva(true);
                setEstrategiaMovimiento(new MovimientoArriba());
            }
        }
    }

    private void dibujarPortalSombra(SpriteBatch batch, int frameIndex) {
        if (framesSombra != null) {
            // Portal un 40% más ancho que Kadabra y centrado en su base
            float anchoPortal = getWidth() * 1.4f;
            float altoPortal = getHeight() * 0.7f;
            float portalX = getX() - ((anchoPortal - getWidth()) / 2f);
            float portalY = getY() - (altoPortal * 0.15f);
            batch.draw(framesSombra[frameIndex], portalX, portalY, anchoPortal, altoPortal);
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (this.estadoActual == ESTADO_SOMBRA) {
            dibujarPortalSombra(batch, this.frameSombraActual);
        } else if (this.estadoActual == ESTADO_ZOOM) {
            dibujarPortalSombra(batch, 5);
            
            float wActual = getWidth() * this.escalaZoom;
            float hActual = getHeight() * this.escalaZoom;
            float drawX = getX() + (getWidth() - wActual) / 2f;
            float drawY = getY() + (getHeight() - hActual) / 2f;

            batch.setColor(1f, 0.7f, 1f, 1f);
            batch.draw(getFramesDireccion()[getDireccionActual()][getFrameActual()], drawX, drawY, wActual, hActual);
            batch.setColor(Color.WHITE);
        } else {
            super.draw(batch);
        }
    }

    // Getters y Setters estrictos (GM-5)
    public static Texture getHojaSombra() { return hojaSombra; }
    public static void setHojaSombra(Texture hojaSombra) { Kadabra.hojaSombra = hojaSombra; }
    public static TextureRegion[] getFramesSombra() { return framesSombra; }
    public static void setFramesSombra(TextureRegion[] framesSombra) { Kadabra.framesSombra = framesSombra; }
    public int getEstadoActual() { return estadoActual; }
    public void setEstadoActual(int estadoActual) { this.estadoActual = estadoActual; }
    public boolean isPosicionFijada() { return posicionFijada; }
    public void setPosicionFijada(boolean posicionFijada) { this.posicionFijada = posicionFijada; }
    public float getTimerFase() { return timerFase; }
    public void setTimerFase(float timerFase) { this.timerFase = timerFase; }
    public int getFrameSombraActual() { return frameSombraActual; }
    public void setFrameSombraActual(int frameSombraActual) { this.frameSombraActual = frameSombraActual; }
    public float getEscalaZoom() { return escalaZoom; }
    public void setEscalaZoom(float escalaZoom) { this.escalaZoom = escalaZoom; }
}