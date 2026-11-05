package puppy.code;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class ZonaHielo {
    private static Texture texturaGlobal;
    private float x, y;
    private float ancho = 260f;
    private float alto = 140f;
    private float tiempoVida = 5.0f;
    private Rectangle hitbox;

    public ZonaHielo(float x, float y) {
        this.x = x;
        this.y = y;
        this.hitbox = new Rectangle(x, y, ancho, alto);
    }

    public void update(float delta, float velocidadFondo) {
        this.tiempoVida -= delta;
        this.y -= velocidadFondo * delta;
        this.hitbox.setPosition(x, y);
    }

    public void draw(SpriteBatch batch) {
        if (texturaGlobal == null) {
            Pixmap pixmap = new Pixmap(140, 140, Pixmap.Format.RGBA8888);
            pixmap.setColor(0.5f, 0.9f, 1.0f, 0.4f);
            pixmap.fillCircle(70, 70, 70);
            texturaGlobal = new Texture(pixmap);
            pixmap.dispose();
        }
        float alpha = Math.min(1f, tiempoVida);
        batch.setColor(1f, 1f, 1f, alpha);
        batch.draw(texturaGlobal, x, y, ancho, alto);
        batch.setColor(Color.WHITE);
    }

    public boolean isExpirada() { return tiempoVida <= 0 || y + alto < 0; }
    public Rectangle getHitbox() { return hitbox; }
    
    public static void disposeTexturaGlobal() {
        if (texturaGlobal != null) { texturaGlobal.dispose(); texturaGlobal = null; }
    }
}