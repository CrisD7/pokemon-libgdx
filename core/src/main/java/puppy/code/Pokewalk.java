package puppy.code;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Pokewalk extends Game {
    private SpriteBatch batch;

    @Override
    public void create() {
        this.batch = new SpriteBatch();
        this.setScreen(new PantallaJuego(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        if (this.batch != null) {
            this.batch.dispose();
        }
    }

    public SpriteBatch getBatch() { return batch; }
    public void setBatch(SpriteBatch batch) { this.batch = batch; }
}