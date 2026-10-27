package puppy.code;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Pokewalk extends Game {
    private SpriteBatch batch;
    private int highScore;
    private String personajeSeleccionado;

    @Override
    public void create() {
        this.batch = new SpriteBatch();
        
        com.badlogic.gdx.Preferences prefs = com.badlogic.gdx.Gdx.app.getPreferences("PokewalkPrefs");
        this.highScore = prefs.getInteger("highScore", 0);
        
        this.personajeSeleccionado = "ethan";
        this.setScreen(new PantallaMenu(this));
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

    public void actualizarHighScore(int nuevoPuntaje) {
        if (nuevoPuntaje > this.highScore) {
            this.highScore = nuevoPuntaje;
            
            com.badlogic.gdx.Preferences prefs = com.badlogic.gdx.Gdx.app.getPreferences("PokewalkPrefs");
            prefs.putInteger("highScore", this.highScore);
            prefs.flush();
        }
    }

    // Getters y Setters estrictos (GM-5)
    public SpriteBatch getBatch() { return batch; }
    public void setBatch(SpriteBatch batch) { this.batch = batch; }
    public int getHighScore() { return highScore; }
    public void setHighScore(int highScore) { this.highScore = highScore; }
    public String getPersonajeSeleccionado() { return personajeSeleccionado; }
    public void setPersonajeSeleccionado(String personajeSeleccionado) { this.personajeSeleccionado = personajeSeleccionado; }
}