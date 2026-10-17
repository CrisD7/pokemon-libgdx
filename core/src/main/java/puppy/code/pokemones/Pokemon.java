package puppy.code.pokemones;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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

    private float x;
    private float y;
    private float width;
    private float height;
    private float velocidad;
    private int puntosPorCaptura;
    private boolean esShiny;
    
    private Texture hojaSprites;
    private TextureRegion[][] framesDireccion; // [direccion][frame 0 o 1]
    private int direccionActual;
    private float animTimer;
    private int frameActual;

    private Rectangle hitbox;
    private EstrategiaMovimiento estrategiaMovimiento;

    public Pokemon(float x, float y, float width, float height, float velocidad, int puntosPorCaptura, String rutaSprite) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.velocidad = velocidad;
        this.puntosPorCaptura = puntosPorCaptura;
        this.esShiny = false;
        this.direccionActual = DIR_ABAJO;
        this.animTimer = 0f;
        this.frameActual = 0;
        this.hitbox = new Rectangle(x, y, width, height);
        
        cargarSpritesheet(rutaSprite);
    }

    private void cargarSpritesheet(String rutaInterna) {
        this.hojaSprites = new Texture(Gdx.files.internal(rutaInterna));
        this.hojaSprites.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

        // Divide la hoja en 4 filas y 2 columnas exactas
        int anchoFrame = this.hojaSprites.getWidth() / 2;
        int altoFrame = this.hojaSprites.getHeight() / 4;
        TextureRegion[][] grilla = TextureRegion.split(this.hojaSprites, anchoFrame, altoFrame);

        this.framesDireccion = new TextureRegion[4][2];
        // Mapeo exacto según el formato de HeartGold/SoulSilver
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
    }

    private void actualizarHitbox() {
        this.hitbox.setPosition(this.x, this.y);
    }

    public void draw(SpriteBatch batch) {
        if (this.framesDireccion != null) {
            // Si es shiny, le aplicamos un destello dorado temporalmente (o puedes cargar otra hoja)
            if (this.esShiny) {
                batch.setColor(1f, 0.9f, 0.4f, 1f);
            }
            batch.draw(this.framesDireccion[this.direccionActual][this.frameActual], this.x, this.y, this.width, this.height);
            if (this.esShiny) {
                batch.setColor(Color.WHITE); // Restaurar color normal del batch
            }
        }
    }

    public boolean salioDePantalla() {
        return (this.y + this.height) < 0;
    }

    public void dispose() {
        if (this.hojaSprites != null) {
            this.hojaSprites.dispose();
        }
    }

    // GETTERS Y SETTERS ESTRICTOS (GM-5)
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
}