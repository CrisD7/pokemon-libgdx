package puppy.code.pokemones;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import puppy.code.movimientos.MovimientoRegice;
import puppy.code.movimientos.EstrategiaMovimiento;

public class Regice extends BossPokemon {
    
    private Texture texIdle;
    private Texture texEmbestida;
    private Texture texRayo;
    private Texture texZona;
    private float timerImpacto = 0f;

    public Regice(float x, float y) {
        super(x, y, "pokemones/regice.png"); 
        setEstrategiaMovimiento(new MovimientoRegice());
        
        setWidth(192f);
        setHeight(192f);
        getHitbox().setSize(192f, 192f);
        
        this.texIdle = new Texture(Gdx.files.internal("pokemones/regice.png"));
        this.texEmbestida = new Texture(Gdx.files.internal("pokemones/regiceEmbestida.png"));
        this.texRayo = new Texture(Gdx.files.internal("pokemones/regiceRayoHielo.png"));
        this.texZona = new Texture(Gdx.files.internal("pokemones/regiceZonaHielo.png"));
    }
    
    @Override
    public boolean recibirImpactoPokeball() {
        boolean impactado = super.recibirImpactoPokeball();
        if (impactado) {
            this.timerImpacto = 0.15f;
        }
        return impactado;
    }

    @Override
    public void draw(SpriteBatch batch) {
        Texture texturaActual = this.texIdle;
        EstrategiaMovimiento estrat = getEstrategiaMovimiento();

        if (estrat instanceof MovimientoRegice) {
            MovimientoRegice mov = (MovimientoRegice) estrat;
            
            switch (mov.getEstado()) {
                case 1: // Cayendo en picada
                case 2: // Subiendo
                    texturaActual = this.texEmbestida;
                    break;
                case 3: // Disparando Rayo
                    texturaActual = this.texRayo;
                    break;
                case 4: // Invocando Zona
                    texturaActual = this.texZona;
                    break;
                default: // Reposo
                    texturaActual = this.texIdle;
                    break;
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
        if (texEmbestida != null) texEmbestida.dispose();
        if (texRayo != null) texRayo.dispose();
        if (texZona != null) texZona.dispose();
    }

    @Override
    protected void comportamientoEspecial(float delta) {
    }
}
