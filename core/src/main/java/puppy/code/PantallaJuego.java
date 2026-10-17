package puppy.code;

import java.util.ArrayList;
import java.util.Iterator;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import puppy.code.pokemones.Pokemon;
import puppy.code.spawns.GestorSpawns;

public class PantallaJuego implements Screen {
    private Pokewalk game;
    private Texture texturaFondo;
    private float fondoY1;
    private float fondoY2;
    private float velocidadFondo = 150f;
    
    private Entrenador entrenador;
    private ArrayList<Pokemon> pokemonesActivos;

    public PantallaJuego(Pokewalk game) {
        this.game = game;
        
        this.texturaFondo = new Texture(Gdx.files.internal("fondo_cesped.png"));
        this.texturaFondo.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        
        float altoEscalado = Gdx.graphics.getWidth() * ((float) this.texturaFondo.getHeight() / this.texturaFondo.getWidth());
        this.fondoY1 = 0;
        this.fondoY2 = altoEscalado;
        
        this.entrenador = new Entrenador(Gdx.graphics.getWidth() / 2f - 48f, 100f);
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

        // 3. Spawnear nuevos Pokémon usando el Singleton (GM-6) y Builder (GM-9)
        Pokemon nuevo = GestorSpawns.getInstancia().intentarGenerarPokemon(delta);
        if (nuevo != null) {
            this.pokemonesActivos.add(nuevo);
        }

        // 4. Actualizar Pokémon activos y limpiar los que salieron de pantalla
        Iterator<Pokemon> iter = this.pokemonesActivos.iterator();
        while (iter.hasNext()) {
            Pokemon p = iter.next();
            p.actualizar(delta, this.velocidadFondo); // Llama al Template Method (GM-8)
            if (p.salioDePantalla()) {
                p.dispose();
                iter.remove();
            }
        }

        // 5. Dibujar todo
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        this.game.getBatch().begin();
        this.game.getBatch().draw(this.texturaFondo, 0, this.fondoY1, anchoEscalado, altoEscalado);
        this.game.getBatch().draw(this.texturaFondo, 0, this.fondoY2, anchoEscalado, altoEscalado);
        
        for (Pokemon p : this.pokemonesActivos) {
            p.draw(this.game.getBatch());
        }
        
        this.entrenador.draw(this.game.getBatch());
        this.game.getBatch().end();
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        this.texturaFondo.dispose();
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