package puppy.code.pokemones;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import puppy.code.movimientos.EstrategiaMovimiento;
import puppy.code.movimientos.MovimientoRayquaza;

public class Rayquaza extends BossPokemon {
    private Texture texIdle, texAtaque, texEnfado, texCansado;

    public Rayquaza(float x, float y) {
        super(x, y, "pokemones/rayquaza.png");
        setEstrategiaMovimiento(new MovimientoRayquaza());
        setWidth(192f); setHeight(192f);
        getHitbox().setSize(192f, 192f);
        
        this.texIdle = new Texture(Gdx.files.internal("pokemones/rayquaza.png"));
        this.texAtaque = new Texture(Gdx.files.internal("pokemones/rayquazaAtaque.png"));
        this.texEnfado = new Texture(Gdx.files.internal("pokemones/rayquazaEnfado.png"));
        this.texCansado = new Texture(Gdx.files.internal("pokemones/rayquazaAturdido.png"));
    }

    @Override public boolean salioDePantalla() { return false; }

    @Override
    public void draw(SpriteBatch batch) {
        Texture texturaActual = this.texIdle;
        EstrategiaMovimiento estrat = getEstrategiaMovimiento();

        if (estrat instanceof MovimientoRayquaza) {
            MovimientoRayquaza mov = (MovimientoRayquaza) estrat;
            switch (mov.getEstado()) {
                case 1: 
                case 2: 
                case 3: texturaActual = this.texAtaque; break; 
                case 4: texturaActual = this.texEnfado; break; 
                case 5: texturaActual = this.texCansado; break; 
                default: texturaActual = this.texIdle; break;
            }
        }

        if (getTiempoGolpeado() > 0f) {
            batch.setColor(1f, 0.15f, 0.15f, 1f); 
        } else {
            batch.setColor(Color.WHITE);
        }

        batch.draw(texturaActual, getX(), getY(), getWidth(), getHeight());
        batch.setColor(Color.WHITE);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (texIdle != null) texIdle.dispose();
        if (texAtaque != null) texAtaque.dispose();
        if (texEnfado != null) texEnfado.dispose();
        if (texCansado != null) texCansado.dispose();
    }

    @Override protected void comportamientoEspecial(float delta) {}
}