package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import puppy.code.pokemones.Pokemon;

public class Entrenador implements Colisionable {
    private float x;
    private float y;
    private float width = 96f;
    private float height = 96f;
    private float velocidad = 280f;

    private Texture texQuieto;
    private Texture texPasoIzq;
    private Texture texPasoDer;

    private float animTimer = 0f;
    private int estadoPaso = 0;
    private boolean estaQuieto = false;

    private int vidasMaximas;
    private int vidasActuales;
    private boolean herido;
    private float tiempoHerido;
    private Rectangle hitbox;

    public Entrenador(float startX, float startY) {
        this(startX, startY, "ethan");
    }

    public Entrenador(float startX, float startY, String nombrePersonaje) {
        this.x = startX;
        this.y = startY;
        this.vidasMaximas = 3;
        this.vidasActuales = 3;
        this.herido = false;
        this.tiempoHerido = 0f;

        String base = "entrenador/" + nombrePersonaje.toLowerCase();
        this.texQuieto = new Texture(Gdx.files.internal(base + "_quieto.png"));
        this.texPasoIzq = new Texture(Gdx.files.internal(base + "_paso_izq.png"));
        this.texPasoDer = new Texture(Gdx.files.internal(base + "_paso_der.png"));

        this.texQuieto.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        this.texPasoIzq.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        this.texPasoDer.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

        // Ajustar el ancho proporcionalmente a la altura (96f) para evitar deformaciones
        this.height = 96f;
        this.width = this.height * ((float) this.texQuieto.getWidth() / this.texQuieto.getHeight());

        this.hitbox = new Rectangle(this.x + 10f, this.y + 5f, this.width - 20f, this.height - 10f);
    }

    public void update(float delta, float velocidadFondo) {
        if (this.herido) {
            this.tiempoHerido -= delta;
            if (this.tiempoHerido <= 0f) {
                this.herido = false;
            }
        }

        this.estaQuieto = false;

        // Si se presiona ABAJO, tiene prioridad absoluta y se anula cualquier otro movimiento
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            this.y -= velocidadFondo * delta;
            this.estaQuieto = true;
        } else {
            // Movimiento lateral y hacia arriba solo si NO se está presionando abajo
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
                this.x -= this.velocidad * delta;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
                this.x += this.velocidad * delta;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
                this.y += (this.velocidad * 0.6f) * delta;
            }
        }

        // Límites de pantalla (restringido a los 3/4 inferiores en Y)
        if (this.x < 0) this.x = 0;
        if (this.x + this.width > Gdx.graphics.getWidth()) {
            this.x = Gdx.graphics.getWidth() - this.width;
        }

        float limiteSuperiorY = Gdx.graphics.getHeight() * 0.75f - this.height;
        if (this.y < 0) this.y = 0;
        if (this.y > limiteSuperiorY) {
            this.y = limiteSuperiorY;
        }

        if (!this.estaQuieto) {
            this.animTimer += delta;
            if (this.animTimer >= 0.18f) {
                this.animTimer = 0f;
                this.estadoPaso = (this.estadoPaso == 0) ? 1 : 0;
            }
        }

        this.hitbox.setPosition(this.x + 20f, this.y + 10f);
    }

    public void draw(SpriteBatch batch) {
        if (this.herido) {
            // Parpadeo rojizo durante la invulnerabilidad temporal
            if (((int) (this.tiempoHerido * 10)) % 2 == 0) {
                batch.setColor(1f, 0.3f, 0.3f, 0.8f);
            }
        }

        Texture actual;
        if (this.estaQuieto) {
            actual = this.texQuieto;
        } else {
            actual = (this.estadoPaso == 0) ? this.texPasoIzq : this.texPasoDer;
        }

        batch.draw(actual, this.x, this.y, this.width, this.height);
        batch.setColor(Color.WHITE);
    }

    // Sobrecarga para choque directo con Pokemon que aplica el daño
    public boolean verificarColision(Pokemon pokemon) {
        if (!this.herido && pokemon.isColisionActiva() && !pokemon.isCapturando() 
                && this.hitbox.overlaps(pokemon.getHitbox())) {
            recibirDano();
            return true;
        }
        return false;
    }

    // Implementación de la interfaz Colisionable (GM-4)
    @Override
    public boolean verificarColision(Colisionable otro) {
        return !this.herido && this.hitbox.overlaps(otro.getHitbox());
    }

    public void recibirDano() {
        if (!this.herido && this.vidasActuales > 0) {
            this.vidasActuales--;
            this.herido = true;
            this.tiempoHerido = 1.2f;
        }
    }

    public boolean estaDerrotado() {
        return this.vidasActuales <= 0;
    }

    public void dispose() {
        if (this.texQuieto != null) this.texQuieto.dispose();
        if (this.texPasoIzq != null) this.texPasoIzq.dispose();
        if (this.texPasoDer != null) this.texPasoDer.dispose();
    }

    // Getters y Setters estrictos (GM-5)
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
    public Texture getTexQuieto() { return texQuieto; }
    public void setTexQuieto(Texture texQuieto) { this.texQuieto = texQuieto; }
    public Texture getTexPasoIzq() { return texPasoIzq; }
    public void setTexPasoIzq(Texture texPasoIzq) { this.texPasoIzq = texPasoIzq; }
    public Texture getTexPasoDer() { return texPasoDer; }
    public void setTexPasoDer(Texture texPasoDer) { this.texPasoDer = texPasoDer; }
    public float getAnimTimer() { return animTimer; }
    public void setAnimTimer(float animTimer) { this.animTimer = animTimer; }
    public int getEstadoPaso() { return estadoPaso; }
    public void setEstadoPaso(int estadoPaso) { this.estadoPaso = estadoPaso; }
    public boolean isEstaQuieto() { return estaQuieto; }
    public void setEstaQuieto(boolean estaQuieto) { this.estaQuieto = estaQuieto; }
    public int getVidasMaximas() { return vidasMaximas; }
    public void setVidasMaximas(int vidasMaximas) { this.vidasMaximas = vidasMaximas; }
    public int getVidasActuales() { return vidasActuales; }
    public void setVidasActuales(int vidasActuales) { this.vidasActuales = vidasActuales; }
    public boolean isHerido() { return herido; }
    public void setHerido(boolean herido) { this.herido = herido; }
    public float getTiempoHerido() { return tiempoHerido; }
    public void setTiempoHerido(float tiempoHerido) { this.tiempoHerido = tiempoHerido; }
    @Override
    public Rectangle getHitbox() { return hitbox; }
    public void setHitbox(Rectangle hitbox) { this.hitbox = hitbox; }
}