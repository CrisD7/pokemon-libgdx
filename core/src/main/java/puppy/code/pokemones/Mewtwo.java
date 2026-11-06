package puppy.code.pokemones;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import puppy.code.movimientos.EstrategiaMovimiento;
import puppy.code.movimientos.MovimientoMewtwo;

public class Mewtwo extends BossPokemon {
    private Texture texIdle, texAviso, texDash, texRayo, texTP;
    private Texture texMarcaTP;
    private float timerImpacto = 0f;

    public Mewtwo(float x, float y) {
        super(x, y, "pokemones/mewtwo.png");
        setEstrategiaMovimiento(new MovimientoMewtwo());
        
        setWidth(192f); setHeight(192f);
        getHitbox().setSize(192f, 192f);
        
        this.texIdle = new Texture(Gdx.files.internal("pokemones/mewtwo.png"));
        this.texAviso = new Texture(Gdx.files.internal("pokemones/mewtwoAviso.png"));
        this.texDash = new Texture(Gdx.files.internal("pokemones/mewtwoDash.png"));
        this.texRayo = new Texture(Gdx.files.internal("pokemones/mewtwoRayo.png"));
        this.texTP = new Texture(Gdx.files.internal("pokemones/mewtwoTP.png"));

        com.badlogic.gdx.graphics.Pixmap p = new com.badlogic.gdx.graphics.Pixmap(100, 100, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        p.setColor(0f, 0.8f, 1f, 0.4f);
        p.fillCircle(50, 50, 45);
        
        p.setColor(0f, 1f, 1f, 0.9f);
        for (int i = 0; i < 360; i += 15) {
            float rad = (float) Math.toRadians(i);
            int cx = 50 + (int)(45 * Math.cos(rad));
            int cy = 50 + (int)(45 * Math.sin(rad));
            p.fillCircle(cx, cy, 3);
        }
        
        this.texMarcaTP = new Texture(p);
        p.dispose();
    }

    @Override
    public boolean salioDePantalla() {
        return false;
    }

    @Override
    public boolean recibirImpactoPokeball() {
        boolean impactado = super.recibirImpactoPokeball();
        if (impactado) this.timerImpacto = 0.15f;
        return impactado;
    }

    @Override
    public void draw(SpriteBatch batch) {
        Texture texturaActual = this.texIdle;
        EstrategiaMovimiento estrat = getEstrategiaMovimiento();

        if (estrat instanceof MovimientoMewtwo) {
            MovimientoMewtwo mov = (MovimientoMewtwo) estrat;
            
            if (mov.isMarcandoTP()) {
                batch.setColor(1f, 1f, 1f, 0.8f);
                batch.draw(texMarcaTP, mov.getMarcaTpX() + getWidth()/2f - 50f, mov.getMarcaTpY() - 20f, 100f, 100f);
            }
            
            switch (mov.getEstado()) {
                case 1: texturaActual = this.texAviso; break; 
                case 2: texturaActual = this.texDash; break;
                case 3: texturaActual = this.texRayo; break;
                case 4:
                case 5: texturaActual = this.texTP; break;
                default: texturaActual = this.texIdle; break;
            }
        }

        if (timerImpacto > 0) {
            timerImpacto -= Gdx.graphics.getDeltaTime();
            batch.setColor(1f, 0.4f, 0.4f, 1f);
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
        if (texAviso != null) texAviso.dispose();
        if (texDash != null) texDash.dispose();
        if (texMarcaTP != null) texMarcaTP.dispose();
    }

    @Override protected void comportamientoEspecial(float delta) {}
}