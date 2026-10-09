package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;

public class PantallaJuego implements Screen {
    private Pokewalk game;
    private Texture texturaFondo;
    
    private float fondoY1;
    private float fondoY2;
    private float velocidadFondo = 150f;
    
    // Nueva variable
    private Entrenador entrenador;

    public PantallaJuego(Pokewalk game) {
        this.game = game;
        
        texturaFondo = new Texture(Gdx.files.internal("fondo_cesped.png"));
        texturaFondo.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        
        float altoEscalado = Gdx.graphics.getWidth() * ((float) texturaFondo.getHeight() / texturaFondo.getWidth());
        
        fondoY1 = 0;
        fondoY2 = altoEscalado;
        
        // Inicializa al entrenador en el centro-abajo de la pantalla
        entrenador = new Entrenador(Gdx.graphics.getWidth() / 2f - 48f, 100f);
    }

    @Override
    public void render(float delta) {
        float anchoEscalado = Gdx.graphics.getWidth();
        float altoEscalado = anchoEscalado * ((float) texturaFondo.getHeight() / texturaFondo.getWidth());

        // Actualizar lógica (fondo y entrenador)
        fondoY1 -= velocidadFondo * delta;
        fondoY2 -= velocidadFondo * delta;

        if (fondoY1 + altoEscalado <= 0) {
            fondoY1 = fondoY2 + altoEscalado;
        }
        if (fondoY2 + altoEscalado <= 0) {
            fondoY2 = fondoY1 + altoEscalado;
        }
        
        entrenador.update(delta, velocidadFondo); // Actualiza movimiento y animaciones del entrenador

        // Limpiar pantalla
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Dibujar gráficos
        game.batch.begin();
        game.batch.draw(texturaFondo, 0, fondoY1, anchoEscalado, altoEscalado);
        game.batch.draw(texturaFondo, 0, fondoY2, anchoEscalado, altoEscalado);
        
        entrenador.draw(game.batch); // Dibuja al entrenador sobre el fondo
        game.batch.end();
    }

    @Override
    public void show() {}

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        texturaFondo.dispose();
        entrenador.dispose(); // Evita fugas de memoria
    }
}