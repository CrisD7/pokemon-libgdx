package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class ZonaTrueno {
    private static Texture texAviso, texImpacto;
    private Rectangle hitbox;
    private float tiempoVida = 0f;
    private boolean danoAplicado = false;

    public ZonaTrueno(float centroX, float centroY) {
        this.hitbox = new Rectangle(centroX - 35f, centroY - 35f, 70f, 70f); 
    }

    public void update(float delta, float velocidadFondo) {
        this.tiempoVida += delta;
        this.hitbox.y -= velocidadFondo * delta;
    }

    public void draw(SpriteBatch batch) {
        if (texAviso == null) {
            Pixmap p = new Pixmap(70, 70, Pixmap.Format.RGBA8888);
            p.setColor(1f, 1f, 0f, 0.4f); 
            p.fillCircle(35, 35, 35); 
            texAviso = new Texture(p);
            p.dispose();
            texImpacto = new Texture(Gdx.files.internal("trueno.png"));
        }
        
        batch.draw(texAviso, hitbox.x, hitbox.y, hitbox.width, hitbox.height);

        float progresoCaida = Math.min(1f, tiempoVida / 0.6f); 
        float alturaInicial = Gdx.graphics.getHeight(); 
        float actualY = hitbox.y + alturaInicial * (1f - progresoCaida);

        batch.draw(texImpacto, hitbox.x, actualY, hitbox.width, hitbox.height);
    }

    public boolean isFaseImpacto() { return tiempoVida >= 0.6f && tiempoVida <= 0.8f; } 
    
    public boolean procesarDano() {
        if (isFaseImpacto() && !danoAplicado) {
            danoAplicado = true;
            return true;
        }
        return false;
    }
    
    public boolean isExpirada() { return tiempoVida > 0.8f || hitbox.y + hitbox.height < 0; }
    public Rectangle getHitbox() { return hitbox; }

    public static void disposeTexturas() {
        if (texAviso != null) texAviso.dispose();
        if (texImpacto != null) texImpacto.dispose();
    }
}