package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.audio.Music;

public class PantallaGameOver implements Screen {
    private Pokewalk game;
    private int puntajeAlcanzado;

    private Texture texFondo;
    private Texture texEntrenador;
    private Texture texBtnVolver;
    private Texture texBtnSalir;
    private BitmapFont fuentePuntaje;

    private Rectangle boundsVolver;
    private Rectangle boundsSalir;

    private float escalaVolver;
    private float escalaSalir;
    private boolean hoverVolver;
    private boolean hoverSalir;
    
    private Music musicaMuerte;

    public PantallaGameOver(Pokewalk game, int puntajeAlcanzado, Music musicaMuerte) {
        this.game = game;
        this.puntajeAlcanzado = puntajeAlcanzado;
        this.musicaMuerte = musicaMuerte;
        
        this.game.actualizarHighScore(puntajeAlcanzado);

        this.texFondo = new Texture(Gdx.files.internal("fondo_cesped.png"));
        this.texFondo.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

        // Carga de las 3 imágenes recortadas con filtro Pixel Art nítido
        this.texEntrenador = new Texture(Gdx.files.internal(this.game.getPersonajeSeleccionado() + "_gameover.png"));
        this.texEntrenador.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

        this.texBtnVolver = new Texture(Gdx.files.internal("btn_volver.png"));
        this.texBtnVolver.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

        this.texBtnSalir = new Texture(Gdx.files.internal("btn_salir.png"));
        this.texBtnSalir.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

        this.fuentePuntaje = new BitmapFont();
        this.fuentePuntaje.getData().setScale(2.0f);
        this.fuentePuntaje.setColor(Color.WHITE);

        float ancho = Gdx.graphics.getWidth();
        float alto = Gdx.graphics.getHeight();

        // Posiciones y proporciones basadas en el boceto de Seba
        float volverW = 340f;
        float volverH = 110f;
        float volverX = (ancho * 0.62f) - (volverW / 2f);
        float volverY = alto * 0.50f;
        this.boundsVolver = new Rectangle(volverX, volverY, volverW, volverH);

        float salirW = 250f;
        float salirH = 82f;
        float salirX = (ancho * 0.62f) - (salirW / 2f);
        float salirY = alto * 0.30f;
        this.boundsSalir = new Rectangle(salirX, salirY, salirW, salirH);

        this.escalaVolver = 1.0f;
        this.escalaSalir = 1.0f;
        this.hoverVolver = false;
        this.hoverSalir = false;
    }

    @Override
    public void render(float delta) {
        float ancho = Gdx.graphics.getWidth();
        float alto = Gdx.graphics.getHeight();

        // 1. Invertir coordenada Y del mouse para que coincida con el sistema de LibGDX
        float mouseX = Gdx.input.getX();
        float mouseY = alto - Gdx.input.getY();

        this.hoverVolver = this.boundsVolver.contains(mouseX, mouseY);
        this.hoverSalir = this.boundsSalir.contains(mouseX, mouseY);

        // 2. Animación suave de zoom al pasar el mouse (crece un 12%)
        float metaVolver = this.hoverVolver ? 1.12f : 1.0f;
        float metaSalir = this.hoverSalir ? 1.12f : 1.0f;
        this.escalaVolver += (metaVolver - this.escalaVolver) * 12f * delta;
        this.escalaSalir += (metaSalir - this.escalaSalir) * 12f * delta;

        // 3. Dibujado en pantalla
        Gdx.gl.glClearColor(0.12f, 0.12f, 0.16f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        this.game.getBatch().begin();

        // Fondo oscurecido para que destaquen el entrenador y los botones
        float altoFondo = ancho * ((float) this.texFondo.getHeight() / this.texFondo.getWidth());
        this.game.getBatch().setColor(0.35f, 0.35f, 0.42f, 1f);
        this.game.getBatch().draw(this.texFondo, 0, 0, ancho, altoFondo);
        this.game.getBatch().setColor(Color.WHITE);

        // Puntaje y Récord en la parte superior
        this.fuentePuntaje.draw(this.game.getBatch(), "Puntaje obtenido: " + this.puntajeAlcanzado, ancho / 2f - 140f, alto - 45f);
        this.fuentePuntaje.setColor(Color.GOLD);
        this.fuentePuntaje.draw(this.game.getBatch(), "Mejor Record: " + this.game.getHighScore(), ancho / 2f - 140f, alto - 85f);
        this.fuentePuntaje.setColor(Color.WHITE);

        // Imagen del entrenador en la esquina inferior izquierda (manteniendo proporción original)
        float entAncho = 270f;
        float entAlto = entAncho * ((float) this.texEntrenador.getHeight() / this.texEntrenador.getWidth());
        this.game.getBatch().draw(this.texEntrenador, 20f, 0f, entAncho, entAlto);

        // Botones interactivos con efecto de zoom e iluminación celeste claro
        dibujarBoton(this.texBtnVolver, this.boundsVolver, this.escalaVolver, this.hoverVolver);
        dibujarBoton(this.texBtnSalir, this.boundsSalir, this.escalaSalir, this.hoverSalir);

        this.game.getBatch().end();

        // 4. Detección de clic izquierdo del mouse
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (this.hoverVolver) {
                this.game.setScreen(new PantallaJuego(this.game));
                this.dispose();
            } else if (this.hoverSalir) {
                this.game.setScreen(new PantallaMenu(this.game));
                this.dispose();
            }
        }
    }

    private void dibujarBoton(Texture textura, Rectangle base, float escala, boolean enHover) {
        float w = base.width * escala;
        float h = base.height * escala;
        float x = base.x - (w - base.width) / 2f;
        float y = base.y - (h - base.height) / 2f;

        if (enHover) {
            // Pasada 1: Tinte base celeste
            this.game.getBatch().setColor(0.65f, 0.92f, 1.0f, 1.0f);
            this.game.getBatch().draw(textura, x, y, w, h);

            // Pasada 2: Brillo aditivo para aclarar el gris oscuro del botón a celeste claro
            this.game.getBatch().setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
            this.game.getBatch().setColor(0.30f, 0.70f, 1.0f, 0.45f);
            this.game.getBatch().draw(textura, x, y, w, h);

            // Restaurar modo de mezcla y color por defecto
            this.game.getBatch().setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            this.game.getBatch().setColor(Color.WHITE);
        } else {
            this.game.getBatch().setColor(Color.WHITE);
            this.game.getBatch().draw(textura, x, y, w, h);
        }
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        this.texFondo.dispose();
        this.texEntrenador.dispose();
        this.texBtnVolver.dispose();
        this.texBtnSalir.dispose();
        this.fuentePuntaje.dispose();
        
        if (this.musicaMuerte != null) {
            this.musicaMuerte.stop();
            this.musicaMuerte.dispose();
        }
    }

    // Getters y Setters estrictos (GM-5)
    public Pokewalk getGame() { return game; }
    public void setGame(Pokewalk game) { this.game = game; }
    public int getPuntajeAlcanzado() { return puntajeAlcanzado; }
    public void setPuntajeAlcanzado(int puntajeAlcanzado) { this.puntajeAlcanzado = puntajeAlcanzado; }
    public Texture getTexFondo() { return texFondo; }
    public void setTexFondo(Texture texFondo) { this.texFondo = texFondo; }
    public Texture getTexEntrenador() { return texEntrenador; }
    public void setTexEntrenador(Texture texEntrenador) { this.texEntrenador = texEntrenador; }
    public Texture getTexBtnVolver() { return texBtnVolver; }
    public void setTexBtnVolver(Texture texBtnVolver) { this.texBtnVolver = texBtnVolver; }
    public Texture getTexBtnSalir() { return texBtnSalir; }
    public void setTexBtnSalir(Texture texBtnSalir) { this.texBtnSalir = texBtnSalir; }
    public BitmapFont getFuentePuntaje() { return fuentePuntaje; }
    public void setFuentePuntaje(BitmapFont fuentePuntaje) { this.fuentePuntaje = fuentePuntaje; }
    public Rectangle getBoundsVolver() { return boundsVolver; }
    public void setBoundsVolver(Rectangle boundsVolver) { this.boundsVolver = boundsVolver; }
    public Rectangle getBoundsSalir() { return boundsSalir; }
    public void setBoundsSalir(Rectangle boundsSalir) { this.boundsSalir = boundsSalir; }
    public float getEscalaVolver() { return escalaVolver; }
    public void setEscalaVolver(float escalaVolver) { this.escalaVolver = escalaVolver; }
    public float getEscalaSalir() { return escalaSalir; }
    public void setEscalaSalir(float escalaSalir) { this.escalaSalir = escalaSalir; }
    public boolean isHoverVolver() { return hoverVolver; }
    public void setHoverVolver(boolean hoverVolver) { this.hoverVolver = hoverVolver; }
    public boolean isHoverSalir() { return hoverSalir; }
    public void setHoverSalir(boolean hoverSalir) { this.hoverSalir = hoverSalir; }
}