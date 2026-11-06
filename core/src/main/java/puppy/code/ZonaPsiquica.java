package puppy.code;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class ZonaPsiquica {
    private static Texture texturaMorada, texturaRoja;
    private float x, y;
    private float ancho = 600f, alto = 230f;
    private float tiempoVida = 0f;
    private Rectangle hitbox;
    
    private boolean danoPulso1 = false, danoPulso2 = false;

    public ZonaPsiquica(float centroX, float centroY) {
        this.x = centroX - ancho / 2f;
        this.y = centroY - alto / 2f;
        this.hitbox = new Rectangle(x, y, ancho, alto);
    }

    public void update(float delta, float velocidadFondo) {
        this.tiempoVida += delta;
        this.y -= velocidadFondo * delta;
        this.hitbox.setPosition(x, y);
    }

    public void draw(SpriteBatch batch) {
        if (texturaMorada == null) {
            Pixmap p = new Pixmap(100, 100, Pixmap.Format.RGBA8888);
            p.setColor(0.5f, 0f, 0.8f, 0.4f); p.fillCircle(50, 50, 50);
            texturaMorada = new Texture(p);
            p.setColor(1f, 0f, 0f, 0.6f); p.fillCircle(50, 50, 50);
            texturaRoja = new Texture(p);
            p.dispose();
        }
        
        boolean enPulso = isPulsoActivo();
        Texture tex = enPulso ? texturaRoja : texturaMorada;
        
        batch.draw(tex, x, y, ancho, alto);
        batch.setColor(Color.WHITE);
    }

    public boolean isPulsoActivo() {
        return (tiempoVida >= 1.5f && tiempoVida <= 1.8f) || (tiempoVida >= 3.0f && tiempoVida <= 3.3f);
    }

    public boolean procesarDano() {
        if (tiempoVida >= 1.5f && tiempoVida <= 1.8f && !danoPulso1) {
            danoPulso1 = true;
            return true;
        }
        if (tiempoVida >= 3.0f && tiempoVida <= 3.3f && !danoPulso2) {
            danoPulso2 = true;
            return true;
        }
        return false;
    }

    public boolean isExpirada() { return tiempoVida >= 4.5f || y + alto < 0; }
    public Rectangle getHitbox() { return hitbox; }

    public static void disposeTexturas() {
        if (texturaMorada != null) texturaMorada.dispose();
        if (texturaRoja != null) texturaRoja.dispose();
    }
}