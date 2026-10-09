package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Entrenador {
    private float x, y;
    private float width; 
    private float height;
    
    // Esta es la velocidad a la que se mueve hacia los lados y hacia arriba
    private float velocidad = 350f; 
    
    // Multiplicador para agrandar el pixel art sin perder su forma rectangular
    private float escala = 4f; 

    private Texture txQuieto;
    private Texture txPasoIzq;
    private Texture txPasoDer;
    private Texture currentFrame;

    private float animTimer = 0f;
    private boolean pasoIzquierdoActivo = true;

    public Entrenador(float startX, float startY) {
        this.x = startX;
        this.y = startY;

        txQuieto = new Texture(Gdx.files.internal("entrenador/ethan_quieto.png"));
        txPasoIzq = new Texture(Gdx.files.internal("entrenador/ethan_paso_izq.png"));
        txPasoDer = new Texture(Gdx.files.internal("entrenador/ethan_paso_der.png"));

        txQuieto.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        txPasoIzq.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        txPasoDer.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

        // Calcula el tamaño en pantalla respetando las proporciones reales del modelo
        this.width = txQuieto.getWidth() * escala;
        this.height = txQuieto.getHeight() * escala;

        currentFrame = txPasoIzq;
    }

    // Ahora le pedimos al método que reciba la velocidad del fondo
    public void update(float delta, float velocidadFondo) {
        boolean frenando = false;

        // Prioridad absoluta a la tecla de frenar (Abajo)
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            y -= velocidadFondo * delta;
            frenando = true;
        } else {
            // Si no está frenando, permitimos el movimiento en el resto de direcciones
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                y += velocidad * delta;
            }
            
            // Movimiento horizontal con prevención de bugs si se presionan ambas teclas a la vez
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && !Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                x -= velocidad * delta;
            } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && !Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                x += velocidad * delta;
            }
        }

        // Límites de pantalla para que no se escape
        if (x < 0) x = 0;
        if (x + width > Gdx.graphics.getWidth()) x = Gdx.graphics.getWidth() - width;
        if (y < 0) y = 0;
        if (y + height > Gdx.graphics.getHeight()) y = Gdx.graphics.getHeight() - height;

        // Lógica de animación
        if (frenando) {
            currentFrame = txQuieto;
            animTimer = 0f;
        } else {
            animTimer += delta;
            if (animTimer > 0.2f) {
                animTimer = 0f;
                pasoIzquierdoActivo = !pasoIzquierdoActivo;
                currentFrame = pasoIzquierdoActivo ? txPasoIzq : txPasoDer;
            }
        }
    }

    public void draw(SpriteBatch batch) {
        batch.draw(currentFrame, x, y, width, height);
    }

    public void dispose() {
        txQuieto.dispose();
        txPasoIzq.dispose();
        txPasoDer.dispose();
    }
}