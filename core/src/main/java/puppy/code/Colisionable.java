package puppy.code;

import com.badlogic.gdx.math.Rectangle;

public interface Colisionable {
    Rectangle getHitbox();
    boolean verificarColision(Colisionable otro);
}