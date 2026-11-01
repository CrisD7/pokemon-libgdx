package puppy.code;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

/** Propietario de texturas de entidades. Cada adquirir debe tener un liberar. */
public final class TexturasCompartidas {
    private static AssetManager gestor;

    private TexturasCompartidas() { }

    public static Texture adquirir(String ruta) {
        if (gestor == null) gestor = new AssetManager();
        gestor.load(ruta, Texture.class);
        gestor.finishLoadingAsset(ruta);
        Texture textura = gestor.get(ruta, Texture.class);
        textura.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        return textura;
    }

    public static void liberar(Texture textura) {
        if (textura == null || gestor == null) return;
        String ruta = gestor.getAssetFileName(textura);
        if (ruta != null) gestor.unload(ruta);
    }

    /** El setter de una entidad retiene su propia referencia antes de soltar la anterior. */
    public static Texture reemplazar(Texture anterior, Texture nueva) {
        if (anterior == nueva) return anterior;
        if (nueva != null) {
            String ruta = gestor == null ? null : gestor.getAssetFileName(nueva);
            if (ruta == null) throw new IllegalArgumentException("La textura debe pertenecer al almacén");
            adquirir(ruta);
        }
        liberar(anterior);
        return nueva;
    }

    public static int getCantidadCargada() { return gestor == null ? 0 : gestor.getLoadedAssets(); }

    /** Sólo al cerrar el juego, después de liberar la pantalla activa. */
    public static void dispose() {
        if (gestor != null) {
            gestor.dispose();
            gestor = null;
        }
    }
}
