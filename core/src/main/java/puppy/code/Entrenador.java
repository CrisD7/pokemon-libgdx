package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.audio.Sound;

import puppy.code.pokemones.Pokemon;
import puppy.code.pokemones.Snorunt;

public class Entrenador implements Colisionable {
    private float x;
    private float y;
    private float width = 96f;
    private float height = 96f;
    private float velocidad = 280f;

    private Texture texQuieto;
    private Texture texPasoIzq;
    private Texture texPasoDer;
    private Texture texHielo;
    private Texture texPatitos;
    private TextureRegion[] framesPatitos;
    private float animacionPatitos;
    private Sound sonidoCurar;
    private Sound sonidoCongelar;
    private Sound sonidoGolpe;

    private float animTimer = 0f;
    private int estadoPaso = 0;
    private boolean estaQuieto = false;

    private int vidasMaximas;
    private int vidasActuales;
    private boolean herido;
    private float tiempoHerido;
    
    private boolean congelado;
    private float tiempoCongelado;
    
    private float tiempoConfusion;

    private Rectangle hitbox;

    public Entrenador(float startX, float startY) {
        this(startX, startY, "ethan");
    }

    public Entrenador(float startX, float startY, String nombrePersonaje) {
        this.x = startX;
        this.y = startY;
        this.vidasMaximas = 3;
        
        if (nombrePersonaje.equalsIgnoreCase("gio")) {
            this.vidasActuales = 4; // Habilidad de Gio : Inicia con 4 vidas
        } else {
            this.vidasActuales = 3;
        }
        this.herido = false;
        this.tiempoHerido = 0f;
        
        if (nombrePersonaje.equalsIgnoreCase("chupete")) {
            this.velocidad = 340f; // Habilidad de Chupete : Es mas rapido
        } else {
            this.velocidad = 280f; 
        }
        
        this.sonidoCurar = Gdx.audio.newSound(Gdx.files.internal("sonidoRevivir.wav"));
        this.sonidoCongelar = Gdx.audio.newSound(Gdx.files.internal("sonidoCongelado.wav"));
        this.sonidoGolpe = Gdx.audio.newSound(Gdx.files.internal("perderVida.wav"));

        String base = "entrenador/" + nombrePersonaje.toLowerCase();
        this.texQuieto = new Texture(Gdx.files.internal(base + "_quieto.png"));
        this.texPasoIzq = new Texture(Gdx.files.internal(base + "_paso_izq.png"));
        this.texPasoDer = new Texture(Gdx.files.internal(base + "_paso_der.png"));
        this.texHielo = new Texture(Gdx.files.internal("hielo.png"));
        if (Gdx.files.internal("entrenador/patitos_confusion.png").exists()) {
            this.texPatitos = new Texture(Gdx.files.internal("entrenador/patitos_confusion.png"));
            this.texPatitos.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
            int altoFrame = this.texPatitos.getHeight() / 3;
            this.framesPatitos = new TextureRegion[3];
            for (int i = 0; i < 3; i++) {
                this.framesPatitos[i] = new TextureRegion(this.texPatitos, 0,
                        i * altoFrame, this.texPatitos.getWidth(), altoFrame);
            }
        }

        this.texQuieto.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        this.texPasoIzq.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        this.texPasoDer.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        this.texHielo.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

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
        
        if (this.congelado) {
            this.tiempoCongelado -= delta;
            if (this.tiempoCongelado <= 0f) {
                this.congelado = false;
            }
        }
        
        this.tiempoConfusion = Math.max(0f, this.tiempoConfusion - delta);
        if (isConfundido()) this.animacionPatitos += delta;
        this.estaQuieto = false;

        boolean izquierda = Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A);
        boolean derecha = Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D);
        boolean arriba = Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W);
        boolean abajo = Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S);
        if (isConfundido()) {
            boolean temporal = izquierda;
            izquierda = derecha;
            derecha = temporal;
            temporal = arriba;
            arriba = abajo;
            abajo = temporal;
        }

        if (!this.congelado) {
            // Si se presiona ABAJO, tiene prioridad absoluta y se anula cualquier otro movimiento
            if (abajo) {
                this.y -= velocidadFondo * delta;
                this.estaQuieto = true;
            } else {
                // Movimiento lateral y hacia arriba cuando no se presiona abajo.
                if (izquierda) {
                    this.x -= this.velocidad * delta;
                }
                if (derecha) {
                    this.x += this.velocidad * delta;
                }
                if (arriba) {
                    this.y += (this.velocidad * 0.6f) * delta;
                }
            }
        } else {
            this.estaQuieto = true;
        }

        // Limites de pantalla (restringido a los 3/4 inferiores en Y)
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

    public void recibirConfusion(float duracion) {
        if (!Float.isFinite(duracion) || duracion <= 0f) return;
        this.tiempoConfusion = Math.max(this.tiempoConfusion, duracion);
    }

    public boolean isConfundido() { return this.tiempoConfusion > 0f; }
    public float getTiempoConfusion() { return this.tiempoConfusion; }

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
        
        if (isConfundido() && this.framesPatitos != null) {
            int cuadro = (int) (this.animacionPatitos / 0.3f) % 3;
            float centroX = this.x + this.width / 2f;
            float altura = this.y + this.height + 6f;
            for (int i = 0; i < 3; i++) {
                float fase = this.animacionPatitos * 2.5f + i * MathUtils.PI2 / 3f;
                batch.draw(this.framesPatitos[(cuadro + i) % 3],
                        centroX - 12f + MathUtils.cos(fase) * 27f,
                        altura + MathUtils.sin(fase) * 6f, 24f, 24f);
            }
        }

        if (this.congelado) {
            batch.setColor(1f, 1f, 1f, 0.8f); // 80% de opacidad
            batch.draw(this.texHielo, this.x - 10f, this.y - 10f, this.width + 20f, this.height + 20f);
            batch.setColor(Color.WHITE);
        }
    }

    // Sobrecarga para choque directo con Pokemon
    public boolean verificarColision(Pokemon pokemon) {
        if (!this.herido && pokemon.isColisionActiva() && !pokemon.isCapturando() 
                && this.hitbox.overlaps(pokemon.getHitbox())) {
            
            if (pokemon instanceof Snorunt) {
                recibirCongelamiento(2.15f);
            } else if (pokemon instanceof puppy.code.pokemones.Glalie) {
            	recibirCongelamiento(3.0f);
                recibirDano();
            } else {
                recibirDano();
            }
            
            return true;
        }
        return false;
    }

    // Implementacion de la interfaz Colisionable (GM-4)
    @Override
    public boolean verificarColision(Colisionable otro) {
        return !this.herido && this.hitbox.overlaps(otro.getHitbox());
    }

    public void recibirDano() {
        if (!this.herido && this.vidasActuales > 0) {
        	this.sonidoGolpe.play(0.65f);
            this.vidasActuales--;
            this.herido = true;
            this.tiempoHerido = 1.2f;
        }
    }
    
    public void recibirCongelamiento(float tiempo) {
        if (!this.congelado && !this.herido) {
        	this.sonidoCongelar.play(0.55f);
            this.congelado = true;
            this.tiempoCongelado = tiempo;
        }
    }

    public boolean estaDerrotado() {
        return this.vidasActuales <= 0;
    }

    public void recuperarVida(boolean alMaximo) {
    	this.sonidoCurar.play(0.7f);
    	if (alMaximo) {
            if (this.vidasActuales < this.vidasMaximas) {
                this.vidasActuales = this.vidasMaximas;
            }
        } else {
            if (this.vidasActuales < this.vidasMaximas) {
                this.vidasActuales++;
            }
        }
    }
    
    public void dispose() {
        if (this.texQuieto != null) this.texQuieto.dispose();
        if (this.texPasoIzq != null) this.texPasoIzq.dispose();
        if (this.texPasoDer != null) this.texPasoDer.dispose();
        if (this.texHielo != null) this.texHielo.dispose();
        if (this.texPatitos != null) this.texPatitos.dispose();
        if (this.sonidoGolpe != null) this.sonidoGolpe.dispose();
        if (this.sonidoCongelar != null) this.sonidoCongelar.dispose();
        if (this.sonidoCurar != null) this.sonidoCurar.dispose();
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
    public boolean isCongelado() { return congelado; }
    public void setCongelado(boolean congelado) { this.congelado = congelado; }
    public float getTiempoCongelado() { return tiempoCongelado; }
    public void setTiempoCongelado(float tiempoCongelado) { this.tiempoCongelado = tiempoCongelado; }
    @Override
    public Rectangle getHitbox() { return hitbox; }
    public void setHitbox(Rectangle hitbox) { this.hitbox = hitbox; }

    public Texture getTexHielo() { return texHielo; }
    public void setTexHielo(Texture texHielo) { this.texHielo = texHielo; }
    public com.badlogic.gdx.audio.Sound getSonidoCurar() { return sonidoCurar; }
    public void setSonidoCurar(com.badlogic.gdx.audio.Sound sonidoCurar) { this.sonidoCurar = sonidoCurar; }
    public com.badlogic.gdx.audio.Sound getSonidoCongelar() { return sonidoCongelar; }
    public void setSonidoCongelar(com.badlogic.gdx.audio.Sound sonidoCongelar) { this.sonidoCongelar = sonidoCongelar; }
    public com.badlogic.gdx.audio.Sound getSonidoGolpe() { return sonidoGolpe; }
    public void setSonidoGolpe(com.badlogic.gdx.audio.Sound sonidoGolpe) { this.sonidoGolpe = sonidoGolpe; }
}
