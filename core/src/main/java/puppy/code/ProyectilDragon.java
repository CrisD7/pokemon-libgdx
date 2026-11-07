package puppy.code;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class ProyectilDragon implements Colisionable {
    private static Texture texturaGlobal;
    private float x, y;
    private float ancho = 30f, alto = 30f;
    private float velocidadY = 250f;
    private float tasaCrecimiento = 60f;
    private Rectangle hitbox;

    public ProyectilDragon(float centroX, float startY) {
        this.x = centroX - ancho / 2f;
        this.y = startY - alto;
        this.hitbox = new Rectangle(x, y, ancho, alto);
    }

    public void update(float delta) {
        this.y -= velocidadY * delta;
        this.ancho += tasaCrecimiento * delta;
        this.alto += tasaCrecimiento * delta;
        this.x -= (tasaCrecimiento * delta) / 2f;
        
        this.hitbox.set(x, y, ancho, alto);
    }

    @Override public Rectangle getHitbox() { return hitbox; }
    @Override public boolean verificarColision(Colisionable otro) { return this.hitbox.overlaps(otro.getHitbox()); }
    public boolean salioDePantalla() { return this.y + this.alto < 0f; }

    public void draw(SpriteBatch batch) {
        if (texturaGlobal == null) {
            Pixmap p = new Pixmap(100, 100, Pixmap.Format.RGBA8888);
            p.setColor(0.3f, 0f, 0.6f, 0.8f); p.fillCircle(50, 50, 50);
            p.setColor(0.8f, 0.2f, 1f, 1f); p.drawCircle(50, 50, 45);
            texturaGlobal = new Texture(p);
            p.dispose();
        }
        batch.draw(texturaGlobal, x, y, ancho, alto);
    }

    public static void disposeTexturaGlobal() {
        if (texturaGlobal != null) { texturaGlobal.dispose(); texturaGlobal = null; }
    }
}