package puppy.code.pokemones;
import puppy.code.movimientos.MovimientoRegice;

public class Regice extends BossPokemon {
    
    public Regice(float x, float y) {
        super(x, y, "pokemones/regice.png");
        setEstrategiaMovimiento(new MovimientoRegice());
        
        setWidth(192f);
        setHeight(192f);
        
        getHitbox().setSize(192f, 192f);
    }

    @Override
    protected void comportamientoEspecial(float delta) {
    }
}