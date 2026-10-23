package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import puppy.code.pokemones.Pokemon;

public class Entrenador {
    private float x;
    private float y;
    private float width; 
    private float height;
    private float velocidad = 350f; 
    private float escala = 4f; 

    // SISTEMA DE VIDAS (Cambia vidasMaximas a 3, 5, etc. cuando quieras más corazones)
    private int vidasMaximas;
    private int vidasActuales;
    private boolean herido;
    private float tiempoHerido;
    private Rectangle hitbox;

    private Texture txQuieto;
    private Texture txPasoIzq;
    private Texture txPasoDer;
    private Texture currentFrame;

    private float animTimer = 0f;
    private boolean pasoIzquierdoActivo = true;

    public Entrenador(float startX, float startY) {
        this.x = startX;
        this.y = startY;

        // Configuración inicial: 1 sola vida
        this.vidasMaximas = 1;
        this.vidasActuales = this.vidasMaximas;
        this.herido = false;
        this.tiempoHerido = 0f;

        this.txQuieto = new Texture(Gdx.files.internal("entrenador/ethan_quieto.png"));
        this.txPasoIzq = new Texture(Gdx.files.internal("entrenador/ethan_paso_izq.png"));
        this.txPasoDer = new Texture(Gdx.files.internal("entrenador/ethan_paso_der.png"));

        this.txQuieto.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        this.txPasoIzq.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        this.txPasoDer.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

        this.width = this.txQuieto.getWidth() * this.escala;
        this.height = this.txQuieto.getHeight() * this.escala;
        
        // Hitbox ligeramente más pequeña que el sprite para que los roces sean justos
        this.hitbox = new Rectangle(this.x + 12f, this.y + 8f, this.width - 24f, this.height - 16f);
        this.currentFrame = this.txPasoIzq;
    }

    public void update(float delta, float velocidadFondo) {
        // Control de invulnerabilidad temporal tras recibir golpe
        if (this.herido) {
            this.tiempoHerido -= delta;
            if (this.tiempoHerido <= 0f) {
                this.herido = false;
            }
        }

        boolean frenando = false;

        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            this.y -= velocidadFondo * delta;
            frenando = true;
        } else {
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                this.y += this.velocidad * delta;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && !Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                this.x -= this.velocidad * delta;
            } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && !Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                this.x += this.velocidad * delta;
            }
        }

        if (this.x < 0) this.x = 0;
        if (this.x + this.width > Gdx.graphics.getWidth()) this.x = Gdx.graphics.getWidth() - this.width;
        if (this.y < 0) this.y = 0;
        if (this.y + this.height > Gdx.graphics.getHeight()) this.y = Gdx.graphics.getHeight() - this.height;

        // Actualizar posición de la hitbox
        this.hitbox.setPosition(this.x + 12f, this.y + 8f);

        if (frenando) {
            this.currentFrame = this.txQuieto;
            this.animTimer = 0f;
        } else {
            this.animTimer += delta;
            if (this.animTimer > 0.2f) {
                this.animTimer = 0f;
                this.pasoIzquierdoActivo = !this.pasoIzquierdoActivo;
                this.currentFrame = this.pasoIzquierdoActivo ? this.txPasoIzq : this.txPasoDer;
            }
        }
    }

    public boolean verificarColision(Pokemon pokemon) {
        if (!this.herido && this.hitbox.overlaps(pokemon.getHitbox())) {
            recibirDano();
            return true;
        }
        return false;
    }

    public void recibirDano() {
        if (!this.herido && this.vidasActuales > 0) {
            this.vidasActuales--;
            this.herido = true;
            this.tiempoHerido = 1.5f; // 1.5 segundos de inmunidad tras un golpe
        }
    }

    public boolean estaDerrotado() {
        return this.vidasActuales <= 0;
    }

    public void draw(SpriteBatch batch) {
        // Si está herido, parpadea en tono rojizo/transparente
        if (this.herido) {
            batch.setColor(1f, 0.4f, 0.4f, 0.7f);
        }
        batch.draw(this.currentFrame, this.x, this.y, this.width, this.height);
        if (this.herido) {
            batch.setColor(Color.WHITE);
        }
    }

    public void dispose() {
        this.txQuieto.dispose();
        this.txPasoIzq.dispose();
        this.txPasoDer.dispose();
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
    public float getEscala() { return escala; }
    public void setEscala(float escala) { this.escala = escala; }
    public int getVidasMaximas() { return vidasMaximas; }
    public void setVidasMaximas(int vidasMaximas) { 
        this.vidasMaximas = vidasMaximas; 
        if (this.vidasActuales > vidasMaximas) {
            this.vidasActuales = vidasMaximas;
        }
    }
    public int getVidasActuales() { return vidasActuales; }
    public void setVidasActuales(int vidasActuales) { this.vidasActuales = vidasActuales; }
    public boolean isHerido() { return herido; }
    public void setHerido(boolean herido) { this.herido = herido; }
    public float getTiempoHerido() { return tiempoHerido; }
    public void setTiempoHerido(float tiempoHerido) { this.tiempoHerido = tiempoHerido; }
    public Rectangle getHitbox() { return hitbox; }
    public void setHitbox(Rectangle hitbox) { this.hitbox = hitbox; }
    public Texture getTxQuieto() { return txQuieto; }
    public void setTxQuieto(Texture txQuieto) { this.txQuieto = txQuieto; }
    public Texture getTxPasoIzq() { return txPasoIzq; }
    public void setTxPasoIzq(Texture txPasoIzq) { this.txPasoIzq = txPasoIzq; }
    public Texture getTxPasoDer() { return txPasoDer; }
    public void setTxPasoDer(Texture txPasoDer) { this.txPasoDer = txPasoDer; }
    public Texture getCurrentFrame() { return currentFrame; }
    public void setCurrentFrame(Texture currentFrame) { this.currentFrame = currentFrame; }
    public float getAnimTimer() { return animTimer; }
    public void setAnimTimer(float animTimer) { this.animTimer = animTimer; }
    public boolean isPasoIzquierdoActivo() { return pasoIzquierdoActivo; }
    public void setPasoIzquierdoActivo(boolean pasoIzquierdoActivo) { this.pasoIzquierdoActivo = pasoIzquierdoActivo; }
}