package puppy.code;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class ProyectilFuria implements Colisionable {
    private static Texture texturaGlobal;
    private Rectangle hitbox;
    private float velocidadX, velocidadY;

    public ProyectilFuria(float centroX, float startY, float velX, float velY) {
        this.hitbox = new Rectangle(centroX - 12f, startY - 12f, 24f, 24f);
        this.velocidadX = velX;
        this.velocidadY = velY;
    }

    public void update(float delta) {
        this.hitbox.x += velocidadX * delta;
        this.hitbox.y -= velocidadY * delta;
    }

    @Override public Rectangle getHitbox() { return hitbox; }
    @Override public boolean verificarColision(Colisionable otro) { return this.hitbox.overlaps(otro.getHitbox()); }
    public boolean salioDePantalla() { 
        return this.hitbox.y + this.hitbox.height < 0f || this.hitbox.x < -50f || this.hitbox.x > 1000f; 
    }

    public void draw(SpriteBatch batch) {
        if (texturaGlobal == null) {
            Pixmap p = new Pixmap(24, 24, Pixmap.Format.RGBA8888);
            p.setColor(1f, 0.4f, 0f, 1f); p.fillCircle(12, 12, 12);
            texturaGlobal = new Texture(p);
            p.dispose();
        }
        batch.draw(texturaGlobal, hitbox.x, hitbox.y, hitbox.width, hitbox.height);
    }

    public static void disposeTexturaGlobal() {
        if (texturaGlobal != null) { texturaGlobal.dispose(); texturaGlobal = null; }
    }
}