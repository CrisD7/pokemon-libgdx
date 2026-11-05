package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class ProyectilHielo implements Colisionable {
    private static Texture texturaGlobal;
    private static final String RUTA_TEXTURA = "proyectiles/hielo.png";
    private static final float TAMANO = 24f;
    private static final float VELOCIDAD_Y = 320f;
    private static final float VELOCIDAD_X = 160f;
    private final Rectangle hitbox;
    
    private float tiempoSeguimiento = 2.5f;

    public ProyectilHielo(float centroX, float startY) {
        this.hitbox = new Rectangle(centroX - TAMANO / 2f, startY - TAMANO, TAMANO, TAMANO);
    }

    public void update(float delta, float objetivoX) {
        this.hitbox.y -= VELOCIDAD_Y * delta;
        
        if (tiempoSeguimiento > 0) {
            tiempoSeguimiento -= delta;
            float centroProyectil = this.hitbox.x + (this.hitbox.width / 2f);
            
            if (centroProyectil < objetivoX - 10f) {
                this.hitbox.x += VELOCIDAD_X * delta;
            } else if (centroProyectil > objetivoX + 10f) {
                this.hitbox.x -= VELOCIDAD_X * delta;
            }
        }
    }

    @Override
    public Rectangle getHitbox() { return hitbox; }

    @Override
    public boolean verificarColision(Colisionable otro) {
        return this.hitbox.overlaps(otro.getHitbox());
    }

    public boolean salioDePantalla() {
        return this.hitbox.y + this.hitbox.height < 0f;
    }

    public void draw(SpriteBatch batch) {
        if (texturaGlobal == null) {
            if (Gdx.files.internal(RUTA_TEXTURA).exists()) {
                texturaGlobal = new Texture(Gdx.files.internal(RUTA_TEXTURA));
            } else {
                Pixmap pixmap = new Pixmap(24, 24, Pixmap.Format.RGBA8888);
                try {
                    pixmap.setColor(0.2f, 0.8f, 1f, 0.9f);
                    pixmap.fillCircle(12, 12, 11);
                    pixmap.setColor(Color.WHITE);
                    pixmap.drawCircle(12, 12, 6);
                    texturaGlobal = new Texture(pixmap);
                } finally {
                    pixmap.dispose();
                }
            }
        }
        batch.draw(texturaGlobal, hitbox.x, hitbox.y, hitbox.width, hitbox.height);
    }

    public static void disposeTexturaGlobal() {
        if (texturaGlobal != null) {
            texturaGlobal.dispose();
            texturaGlobal = null;
        }
    }
}