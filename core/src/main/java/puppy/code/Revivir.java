package puppy.code;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Revivir implements Colisionable {
    private float x, y, width, height;
    private Texture textura;
    private Rectangle hitbox;
    private boolean maxRevivir;
    private float tiempoVida;

    public Revivir(float x, float y, boolean maxRevivir) {
        this.x = x;
        this.y = y;
        this.maxRevivir = maxRevivir;
        this.width = 48f;
        this.height = 48f;
        this.textura = TexturasCompartidas.adquirir(maxRevivir ? "maxRevivir.png" : "revivir.png");
        this.hitbox = new Rectangle(x, y, this.width, this.height);
        this.tiempoVida = 12f; 
    }

    public void update(float delta, float velocidadFondo) {
        this.tiempoVida -= delta;
        this.y -= velocidadFondo * delta;
        this.hitbox.setPosition(this.x, this.y);
    }
    
    public boolean salioDePantalla() {
        return (this.y + this.height) < 0;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(this.textura, this.x, this.y, this.width, this.height);
    }

    public boolean isMaxRevivir() { return this.maxRevivir; }
    
    public boolean isExpirada() { return this.tiempoVida <= 0f; }

    @Override
    public Rectangle getHitbox() { return this.hitbox; }
    
    @Override
    public boolean verificarColision(Colisionable otro) {
        return this.hitbox.overlaps(otro.getHitbox());
    }

    public void dispose() {
        if (this.textura != null) {
            TexturasCompartidas.liberar(this.textura);
            this.textura = null;
        }
    }

    public float getX() { return x; }
    public void setX(float x) { this.x = x; }
    public float getY() { return y; }
    public void setY(float y) { this.y = y; }
    public float getWidth() { return width; }
    public void setWidth(float width) { this.width = width; }
    public float getHeight() { return height; }
    public void setHeight(float height) { this.height = height; }
    public Texture getTextura() { return textura; }
    public void setTextura(Texture textura) { this.textura = TexturasCompartidas.reemplazar(this.textura, textura); }
    public void setHitbox(Rectangle hitbox) { this.hitbox = hitbox; }
    public void setMaxRevivir(boolean maxRevivir) { this.maxRevivir = maxRevivir; }
    public float getTiempoVida() { return tiempoVida; }
    public void setTiempoVida(float tiempoVida) { this.tiempoVida = tiempoVida; }
}
