package puppy.code;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Pokewalk extends Game {
    public SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
        // Al iniciar, cargamos inmediatamente la pantalla de juego
        this.setScreen(new PantallaJuego(this));
    }

    @Override
    public void render() {
        super.render(); // Importante para que el Game renderice la pantalla activa
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}