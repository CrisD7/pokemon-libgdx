package puppy.code.pokemones;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import puppy.code.movimientos.EstrategiaMovimiento;

public abstract class Pokemon {
    public static final int DIR_ABAJO = 0;
    public static final int DIR_ARRIBA = 1;
    public static final int DIR_IZQUIERDA = 2;
    public static final int DIR_DERECHA = 3;

    // Textura compartida en memoria para el signo "!" de alerta
    private static Texture texturaExclamacion;

    private float x;
    private float y;
    private float width;
    private float height;
    private float velocidad;
    private int puntosPorCaptura;
    private boolean esShiny;
    
    // Referencia de posición del jugador y temporizador de alerta "!"
    private float objetivoX;
    private float objetivoY;
    private float tiempoAlerta;

    private Texture hojaSprites;
    private TextureRegion[][] framesDireccion;
    private int direccionActual;
    private float animTimer;
    private int frameActual;

    private Rectangle hitbox;
    private EstrategiaMovimiento estrategiaMovimiento;
    private boolean colisionActiva;

    public Pokemon(float x, float y, float width, float height, float velocidad, int puntosPorCaptura, String rutaSprite) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.velocidad = velocidad;
        this.puntosPorCaptura = puntosPorCaptura;
        this.esShiny = false;
        this.objetivoX = 0f;
        this.objetivoY = 0f;
        this.tiempoAlerta = 0f;
        this.direccionActual = DIR_ABAJO;
        this.animTimer = 0f;
        this.frameActual = 0;
        this.hitbox = new Rectangle(x, y, width, height);
        this.colisionActiva = true;
        
        cargarSpritesheet(rutaSprite);
        asegurarTexturaExclamacion();
    }

    // Genera un ícono pixel art de "!" automáticamente sin requerir archivo PNG externo
    private static void asegurarTexturaExclamacion() {
        if (texturaExclamacion == null) {
            Pixmap pix = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
            // Borde negro del globo
            pix.setColor(Color.BLACK);
            pix.fillRectangle(2, 1, 12, 14);
            // Fondo blanco
            pix.setColor(Color.WHITE);
            pix.fillRectangle(3, 2, 10, 12);
            // Signo de exclamación rojo (!)
            pix.setColor(Color.RED);
            pix.fillRectangle(7, 3, 2, 6);  // Barra superior
            pix.fillRectangle(7, 11, 2, 2); // Punto inferior
            
            texturaExclamacion = new Texture(pix);
            texturaExclamacion.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
            pix.dispose();
        }
    }

    private void cargarSpritesheet(String rutaInterna) {
        this.hojaSprites = new Texture(Gdx.files.internal(rutaInterna));
        this.hojaSprites.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

        int anchoFrame = this.hojaSprites.getWidth() / 2;
        int altoFrame = this.hojaSprites.getHeight() / 4;
        TextureRegion[][] grilla = TextureRegion.split(this.hojaSprites, anchoFrame, altoFrame);

        this.framesDireccion = new TextureRegion[4][2];
        this.framesDireccion[DIR_ARRIBA][0]    = grilla[0][0];
        this.framesDireccion[DIR_ARRIBA][1]    = grilla[1][0];
        this.framesDireccion[DIR_ABAJO][0]     = grilla[2][0];
        this.framesDireccion[DIR_ABAJO][1]     = grilla[3][0];
        this.framesDireccion[DIR_IZQUIERDA][0] = grilla[0][1];
        this.framesDireccion[DIR_IZQUIERDA][1] = grilla[1][1];
        this.framesDireccion[DIR_DERECHA][0]   = grilla[2][1];
        this.framesDireccion[DIR_DERECHA][1]   = grilla[3][1];
    }

    // PATRÓN TEMPLATE METHOD (GM-8)
    public final void actualizar(float delta, float velocidadFondo) {
        bajarConEscenario(delta, velocidadFondo);
        ejecutarEstrategiaMovimiento(delta);
        comportamientoEspecial(delta);
        actualizarAnimacion(delta);
        actualizarHitbox();
    }

    private void bajarConEscenario(float delta, float velocidadFondo) {
        this.y -= velocidadFondo * delta;
    }

    private void ejecutarEstrategiaMovimiento(float delta) {
        if (this.estrategiaMovimiento != null) {
            this.estrategiaMovimiento.mover(this, delta);
        }
    }

    protected abstract void comportamientoEspecial(float delta);

    private void actualizarAnimacion(float delta) {
        this.animTimer += delta;
        if (this.animTimer >= 0.2f) {
            this.animTimer = 0f;
            this.frameActual = (this.frameActual == 0) ? 1 : 0;
        }
        if (this.tiempoAlerta > 0f) {
            this.tiempoAlerta -= delta;
        }
    }

    private void actualizarHitbox() {
        if (this.colisionActiva) {
            this.hitbox.set(this.x, this.y, this.width, this.height);
        } else {
            // Si aún no se materializa (ej. sombra de Kadabra), no tiene caja de colisión
            this.hitbox.set(this.x, this.y, 0f, 0f);
        }
    }

    public void draw(SpriteBatch batch) {
        if (this.framesDireccion != null) {
            if (this.esShiny) {
                batch.setColor(1f, 0.9f, 0.4f, 1f);
            }
            batch.draw(this.framesDireccion[this.direccionActual][this.frameActual], this.x, this.y, this.width, this.height);
            if (this.esShiny) {
                batch.setColor(Color.WHITE);
            }
        }
        // Dibuja el signo de exclamación encima de la cabeza mientras dure la alerta
        if (this.tiempoAlerta > 0f && texturaExclamacion != null) {
            float tamanoIcono = 32f;
            float iconoX = this.x + (this.width / 2f) - (tamanoIcono / 2f);
            float iconoY = this.y + this.height + 4f;
            batch.draw(texturaExclamacion, iconoX, iconoY, tamanoIcono, tamanoIcono);
        }
    }

    public void actualizarPosicionObjetivo(float targetX, float targetY) {
        this.objetivoX = targetX;
        this.objetivoY = targetY;
    }

    public boolean salioDePantalla() {
        // Sale por abajo (margen normal) o sale por arriba (con margen de 100px por los spawns iniciales)
        return (this.y + this.height) < 0 || this.y > (Gdx.graphics.getHeight() + 100f);
    }

    public void dispose() {
        if (this.hojaSprites != null) {
            this.hojaSprites.dispose();
        }
    }

    // GETTERS Y SETTERS ESTRICTOS (GM-5)
    public static Texture getTexturaExclamacion() { return texturaExclamacion; }
    public static void setTexturaExclamacion(Texture tex) { texturaExclamacion = tex; }

    public float getX() { return x; }
    public void setX(float x) { this.x = x; }
    public float getY() { return y; }
    public void setY(float y) { this.y = y; }
    public float getWidth() { return width; }
    public void setWidth(float width) { this.width = width; }
    public float getHeight() { return height; }
    public void setHeight(float height) { this.height = height; }
    public float getVelocidad() { return velocidad; }
    public void setVelocidad(float velocidad) { this.velocidad = velocidad; }
    public int getPuntosPorCaptura() { return puntosPorCaptura; }
    public void setPuntosPorCaptura(int puntosPorCaptura) { this.puntosPorCaptura = puntosPorCaptura; }
    public boolean isEsShiny() { return esShiny; }
    public void setEsShiny(boolean esShiny) { this.esShiny = esShiny; }
    public float getObjetivoX() { return objetivoX; }
    public void setObjetivoX(float objetivoX) { this.objetivoX = objetivoX; }
    public float getObjetivoY() { return objetivoY; }
    public void setObjetivoY(float objetivoY) { this.objetivoY = objetivoY; }
    public float getTiempoAlerta() { return tiempoAlerta; }
    public void setTiempoAlerta(float tiempoAlerta) { this.tiempoAlerta = tiempoAlerta; }
    public Texture getHojaSprites() { return hojaSprites; }
    public void setHojaSprites(Texture hojaSprites) { this.hojaSprites = hojaSprites; }
    public TextureRegion[][] getFramesDireccion() { return framesDireccion; }
    public void setFramesDireccion(TextureRegion[][] framesDireccion) { this.framesDireccion = framesDireccion; }
    public int getDireccionActual() { return direccionActual; }
    public void setDireccionActual(int direccionActual) { this.direccionActual = direccionActual; }
    public float getAnimTimer() { return animTimer; }
    public void setAnimTimer(float animTimer) { this.animTimer = animTimer; }
    public int getFrameActual() { return frameActual; }
    public void setFrameActual(int frameActual) { this.frameActual = frameActual; }
    public Rectangle getHitbox() { return hitbox; }
    public void setHitbox(Rectangle hitbox) { this.hitbox = hitbox; }
    public EstrategiaMovimiento getEstrategiaMovimiento() { return estrategiaMovimiento; }
    public void setEstrategiaMovimiento(EstrategiaMovimiento estrategiaMovimiento) {
        this.estrategiaMovimiento = estrategiaMovimiento;
    }
    public boolean isColisionActiva() { return colisionActiva; }
    public void setColisionActiva(boolean colisionActiva) { this.colisionActiva = colisionActiva; }
}