package puppy.code;

import java.util.ArrayList;
import java.util.Iterator;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import puppy.code.pokemones.Pokemon;
import puppy.code.spawns.GestorSpawns;

public class PantallaJuego implements Screen {
    private Pokewalk game;
    private Texture texturaFondo;
    private Texture texturaCorazon;
    private float fondoY1;
    private float fondoY2;
    private float velocidadFondo = 150f;
    
    private Entrenador entrenador;
    private ArrayList<Pokemon> pokemonesActivos;

    public PantallaJuego(Pokewalk game) {
        this.game = game;
        
        this.texturaFondo = new Texture(Gdx.files.internal("fondo_cesped.png"));
        this.texturaFondo.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        
        // Textura para los corazones de vida en la esquina
        this.texturaCorazon = new Texture(Gdx.files.internal("corazon.png"));
        this.texturaCorazon.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        
        float altoEscalado = Gdx.graphics.getWidth() * ((float) this.texturaFondo.getHeight() / this.texturaFondo.getWidth());
        this.fondoY1 = 0;
        this.fondoY2 = altoEscalado;
        
        this.entrenador = new Entrenador(Gdx.graphics.getWidth() / 2f - 48f, 100f);
        // Si quieres probar con más vidas desde aquí, basta con descomentar la siguiente línea:
        // this.entrenador.setVidasMaximas(3); this.entrenador.setVidasActuales(3);
        
        this.pokemonesActivos = new ArrayList<>();
    }

    @Override
    public void render(float delta) {
        float anchoEscalado = Gdx.graphics.getWidth();
        float altoEscalado = anchoEscalado * ((float) this.texturaFondo.getHeight() / this.texturaFondo.getWidth());

        // 1. Actualizar fondo
        this.fondoY1 -= this.velocidadFondo * delta;
        this.fondoY2 -= this.velocidadFondo * delta;

        if (this.fondoY1 + altoEscalado <= 0) {
            this.fondoY1 = this.fondoY2 + altoEscalado;
        }
        if (this.fondoY2 + altoEscalado <= 0) {
            this.fondoY2 = this.fondoY1 + altoEscalado;
        }
        
        // 2. Actualizar entrenador
        this.entrenador.update(delta, this.velocidadFondo);

        // 3. Spawnear nuevos Pokémon
        Pokemon nuevo = GestorSpawns.getInstancia().intentarGenerarPokemon(delta);
        if (nuevo != null) {
            this.pokemonesActivos.add(nuevo);
        }

        // 4. Actualizar Pokémon activos y verificar colisión con el entrenador
        float centroEntrenadorX = this.entrenador.getX() + (this.entrenador.getWidth() / 2f);
        float centroEntrenadorY = this.entrenador.getY() + (this.entrenador.getHeight() / 2f);

        Iterator<Pokemon> iter = this.pokemonesActivos.iterator();
        while (iter.hasNext()) {
            Pokemon p = iter.next();
            p.actualizarPosicionObjetivo(centroEntrenadorX, centroEntrenadorY); // <-- NUEVA LÍNEA
            p.actualizar(delta, this.velocidadFondo);
            
            if (this.entrenador.verificarColision(p)) {
                p.dispose();
                iter.remove();
                continue;
            }
            
            if (p.salioDePantalla()) {
                p.dispose();
                iter.remove();
            }
        }

        // Si perdió todos los corazones, reinicia la partida (temporal hasta tener PantallaGameOver)
        if (this.entrenador.estaDerrotado()) {
            this.game.setScreen(new PantallaJuego(this.game));
            this.dispose();
            return;
        }

        // 5. Dibujar escena y HUD de corazones
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        this.game.getBatch().begin();
        this.game.getBatch().draw(this.texturaFondo, 0, this.fondoY1, anchoEscalado, altoEscalado);
        this.game.getBatch().draw(this.texturaFondo, 0, this.fondoY2, anchoEscalado, altoEscalado);
        
        for (Pokemon p : this.pokemonesActivos) {
            p.draw(this.game.getBatch());
        }
        
        this.entrenador.draw(this.game.getBatch());
        dibujarCorazonesHUD();
        this.game.getBatch().end();
    }

    private void dibujarCorazonesHUD() {
        float tamanoCorazon = 40f;
        float espacio = 8f;
        float margenX = 20f;
        float posY = Gdx.graphics.getHeight() - tamanoCorazon - 20f; // Esquina superior izquierda

        for (int i = 0; i < this.entrenador.getVidasMaximas(); i++) {
            float posX = margenX + i * (tamanoCorazon + espacio);
            
            if (i < this.entrenador.getVidasActuales()) {
                // Corazón lleno (color normal)
                this.game.getBatch().setColor(Color.WHITE);
            } else {
                // Corazón vacío estilo contenedor de Minecraft (oscuro semitransparente)
                this.game.getBatch().setColor(0.15f, 0.15f, 0.15f, 0.75f);
            }
            
            this.game.getBatch().draw(this.texturaCorazon, posX, posY, tamanoCorazon, tamanoCorazon);
        }
        // Siempre restaurar el color del batch a blanco al terminar
        this.game.getBatch().setColor(Color.WHITE);
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        this.texturaFondo.dispose();
        this.texturaCorazon.dispose();
        this.entrenador.dispose();
        for (Pokemon p : this.pokemonesActivos) {
            p.dispose();
        }
    }

    // Getters y Setters (GM-5)
    public Pokewalk getGame() { return game; }
    public void setGame(Pokewalk game) { this.game = game; }
    public Texture getTexturaFondo() { return texturaFondo; }
    public void setTexturaFondo(Texture texturaFondo) { this.texturaFondo = texturaFondo; }
    public Texture getTexturaCorazon() { return texturaCorazon; }
    public void setTexturaCorazon(Texture texturaCorazon) { this.texturaCorazon = texturaCorazon; }
    public float getFondoY1() { return fondoY1; }
    public void setFondoY1(float fondoY1) { this.fondoY1 = fondoY1; }
    public float getFondoY2() { return fondoY2; }
    public void setFondoY2(float fondoY2) { this.fondoY2 = fondoY2; }
    public float getVelocidadFondo() { return velocidadFondo; }
    public void setVelocidadFondo(float velocidadFondo) { this.velocidadFondo = velocidadFondo; }
    public Entrenador getEntrenador() { return entrenador; }
    public void setEntrenador(Entrenador entrenador) { this.entrenador = entrenador; }
    public ArrayList<Pokemon> getPokemonesActivos() { return pokemonesActivos; }
    public void setPokemonesActivos(ArrayList<Pokemon> pokemonesActivos) { this.pokemonesActivos = pokemonesActivos; }
}