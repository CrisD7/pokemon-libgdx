package puppy.code;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/** Proyectil descendente; crear y actualizar no requiere recursos gráficos. */
public class ProyectilConfusion implements Colisionable {
    private static Texture texturaGlobal;
    private static final float TAMANO = 24f;
    private static final float VELOCIDAD = 320f;
    private final Rectangle hitbox;

    public ProyectilConfusion(float centroX, float startY) {
        this.hitbox = new Rectangle(centroX - TAMANO / 2f, startY - TAMANO, TAMANO, TAMANO);
    }

    public void update(float delta) {
        this.hitbox.y -= VELOCIDAD * delta;
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
            // Recurso generado: no depende de archivos que puedan faltar tras un pull.
            Pixmap pixmap = new Pixmap(24, 24, Pixmap.Format.RGBA8888);
            try {
                pixmap.setColor(0.65f, 0.2f, 1f, 0.8f);
                pixmap.fillCircle(12, 12, 11);
                pixmap.setColor(Color.WHITE);
                pixmap.drawCircle(12, 12, 6);
                texturaGlobal = new Texture(pixmap);
            } finally {
                pixmap.dispose();
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
