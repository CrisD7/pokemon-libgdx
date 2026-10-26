package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import puppy.code.pokemones.Pokemon;

public class Pokeball implements Colisionable{
    private static Texture texturaGlobal;
    private static TextureRegion regionPokeball;

    private float x;
    private float y;
    private float width;
    private float height;
    private float velocidadY;
    private float rotacion;
    private Rectangle hitbox;

    public Pokeball(float startX, float startY) {
        asegurarTextura();
        this.width = 36f;
        this.height = 36f;
        this.x = startX - (this.width / 2f);
        this.y = startY;
        this.velocidadY = 550f; // Sube rápido hacia arriba
        this.rotacion = 0f;
        this.hitbox = new Rectangle(this.x, this.y, this.width, this.height);
    }

    private static void asegurarTextura() {
        if (texturaGlobal == null) {
            texturaGlobal = new Texture(Gdx.files.internal("pokeball.png"));
            texturaGlobal.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
            regionPokeball = new TextureRegion(texturaGlobal);
        }
    }

    public void update(float delta) {
        this.y += this.velocidadY * delta;
        this.rotacion -= 360f * delta; // Giros en el aire
        this.hitbox.setPosition(this.x, this.y);
    }

    public void draw(SpriteBatch batch) {
        if (regionPokeball != null) {
            // Dibuja la Pokéball rotando sobre su propio eje central
            batch.draw(regionPokeball, this.x, this.y, 
                       this.width / 2f, this.height / 2f, 
                       this.width, this.height, 
                       1f, 1f, this.rotacion);
        }
    }

    public boolean verificarCaptura(Pokemon pokemon) {
        // Solo captura si el Pokémon ya se materializó (colisionActiva == true) y chocan las hitboxes
        return pokemon.isColisionActiva() && this.hitbox.overlaps(pokemon.getHitbox());
    }

    public boolean salioDePantalla() {
        return this.y > Gdx.graphics.getHeight();
    }

    public static void disposeTexturaGlobal() {
        if (texturaGlobal != null) {
            texturaGlobal.dispose();
            texturaGlobal = null;
        }
    }
    
    @Override
    public boolean verificarColision(Colisionable otro) {
        return this.hitbox.overlaps(otro.getHitbox());
    }

    // Getters y Setters estrictos (GM-5)
    public static Texture getTexturaGlobal() { return texturaGlobal; }
    public static void setTexturaGlobal(Texture texturaGlobal) { Pokeball.texturaGlobal = texturaGlobal; }
    public static TextureRegion getRegionPokeball() { return regionPokeball; }
    public static void setRegionPokeball(TextureRegion regionPokeball) { Pokeball.regionPokeball = regionPokeball; }
    public float getX() { return x; }
    public void setX(float x) { this.x = x; }
    public float getY() { return y; }
    public void setY(float y) { this.y = y; }
    public float getWidth() { return width; }
    public void setWidth(float width) { this.width = width; }
    public float getHeight() { return height; }
    public void setHeight(float height) { this.height = height; }
    public float getVelocidadY() { return velocidadY; }
    public void setVelocidadY(float velocidadY) { this.velocidadY = velocidadY; }
    public float getRotacion() { return rotacion; }
    public void setRotacion(float rotacion) { this.rotacion = rotacion; }
    public Rectangle getHitbox() { return hitbox; }
    public void setHitbox(Rectangle hitbox) { this.hitbox = hitbox; }
}