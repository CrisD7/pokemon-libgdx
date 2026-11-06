package puppy.code;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class ProyectilPsiquico implements Colisionable {
    private static Texture texturaGlobal;
    private static final float TAMANO = 28f;
    private static final float VELOCIDAD_Y = 350f;
    private static final float VELOCIDAD_X = 180f;
    private final Rectangle hitbox;
    private float tiempoSeguimiento = 1.8f;

    public ProyectilPsiquico(float centroX, float startY) {
        this.hitbox = new Rectangle(centroX - TAMANO / 2f, startY - TAMANO, TAMANO, TAMANO);
    }

    public void update(float delta, float objetivoX) {
        this.hitbox.y -= VELOCIDAD_Y * delta;
        if (tiempoSeguimiento > 0) {
            tiempoSeguimiento -= delta;
            float centro = this.hitbox.x + (this.hitbox.width / 2f);
            if (centro < objetivoX - 10f) this.hitbox.x += VELOCIDAD_X * delta;
            else if (centro > objetivoX + 10f) this.hitbox.x -= VELOCIDAD_X * delta;
        }
    }

    @Override
    public Rectangle getHitbox() { return hitbox; }

    @Override
    public boolean verificarColision(Colisionable otro) { return this.hitbox.overlaps(otro.getHitbox()); }
    
    public boolean salioDePantalla() { return this.hitbox.y + this.hitbox.height < 0f; }

    public void draw(SpriteBatch batch) {
        if (texturaGlobal == null) {
            Pixmap pixmap = new Pixmap(28, 28, Pixmap.Format.RGBA8888);
            pixmap.setColor(0.6f, 0.2f, 0.8f, 0.9f);
            pixmap.fillCircle(14, 14, 14);
            pixmap.setColor(0.9f, 0.5f, 1.0f, 1f);
            pixmap.fillCircle(14, 14, 6);
            texturaGlobal = new Texture(pixmap);
            pixmap.dispose();
        }
        batch.draw(texturaGlobal, hitbox.x, hitbox.y, hitbox.width, hitbox.height);
    }

    public static void disposeTexturaGlobal() {
        if (texturaGlobal != null) { texturaGlobal.dispose(); texturaGlobal = null; }
    }
}