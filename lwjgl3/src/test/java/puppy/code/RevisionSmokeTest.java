package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture;
import java.util.ArrayList;
import java.util.Collections;
import puppy.code.pokemones.*;
import puppy.code.spawns.*;

/** Regresiones ejecutadas con un contexto OpenGL real; no necesita JUnit ni modifica preferencias. */
public final class RevisionSmokeTest extends Pokewalk {
    private static Throwable fallo;
    private int comprobaciones;

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("PokeWalk regression");
        config.setWindowedMode(1280, 720);
        config.setInitialVisible(false);
        config.setPreferencesConfig(System.getProperty("java.io.tmpdir") + "/pokewalk-test/",
                com.badlogic.gdx.Files.FileType.Absolute);
        new Lwjgl3Application(new RevisionSmokeTest(), config);
        if (fallo != null) throw new AssertionError("Falló la regresión", fallo);
    }

    @Override
    public void create() {
        try {
            super.create();
            probarCatalogo();
            probarEntidades();
            probarConfusion();
            probarPantallas();
            System.out.println("REGRESION OK: " + comprobaciones + " comprobaciones");
        } catch (Throwable error) {
            fallo = error;
        } finally {
            Gdx.app.exit();
        }
    }

    @Override public void render() { /* Las pantallas se ejercitan de manera determinista en create. */ }

    private void comprobar(boolean condicion, String mensaje) {
        comprobaciones++;
        if (!condicion) throw new AssertionError(mensaje);
    }

    private void rechazar(Class<? extends RuntimeException> tipo, Runnable accion) {
        try {
            accion.run();
        } catch (RuntimeException error) {
            comprobar(tipo.isInstance(error), "Tipo de error inesperado: " + error);
            return;
        }
        throw new AssertionError("Se aceptó un estado inválido");
    }

    private void probarCatalogo() {
        GestorSpawns gestor = GestorSpawns.getInstancia();
        ArrayList<EntradaSpawn> original = gestor.getCatalogoSpawns();
        comprobar(gestor == GestorSpawns.getInstancia(), "Singleton");
        comprobar(gestor.getPesoTotal() == 135 && gestor.getPesoTotal(true) == 645, "Totales por bioma");
        try {
            for (boolean nevado : new boolean[] {false, true}) {
                for (EntradaSpawn entrada : original) {
                    int cantidad = 0;
                    for (int tirada = 1; tirada <= gestor.getPesoTotal(nevado); tirada++) {
                        if (gestor.seleccionarEntrada(nevado, tirada).getCreadorBase() == entrada.getCreadorBase())
                            cantidad++;
                    }
                    comprobar(cantidad == entrada.pesoPara(nevado), "Distribución original");
                }
            }
            ArrayList<EntradaSpawn> invertido = gestor.getCatalogoSpawns();
            Collections.reverse(invertido);
            gestor.setCatalogoSpawns(invertido);
            for (boolean nevado : new boolean[] {false, true}) {
                for (EntradaSpawn entrada : original) {
                    int cantidad = 0;
                    for (int tirada = 1; tirada <= gestor.getPesoTotal(nevado); tirada++) {
                        if (gestor.seleccionarEntrada(nevado, tirada).getCreadorBase() == entrada.getCreadorBase())
                            cantidad++;
                    }
                    comprobar(cantidad == entrada.pesoPara(nevado), "Distribución independiente del orden");
                }
            }
            invertido.get(0).setPesoNevado(999);
            invertido.clear();
            ArrayList<EntradaSpawn> copia = gestor.getCatalogoSpawns();
            copia.get(0).setPesoProbabilidad(999);
            copia.clear();
            comprobar(gestor.getPesoTotal() == 135 && gestor.getPesoTotal(true) == 645, "Copias defensivas profundas");
            rechazar(IllegalArgumentException.class, () -> gestor.setPesoTotal(1));
            rechazar(IllegalArgumentException.class, () -> gestor.seleccionarEntrada(false, 0));
            rechazar(IllegalArgumentException.class, () -> original.get(0).setPesoProbabilidad(-1));
            EntradaSpawn nueva = new EntradaSpawn(2, 3, Caterpie::new,
                    puppy.code.movimientos.MovimientoRecto::new);
            gestor.registrarSpawn(nueva);
            nueva.setPesoProbabilidad(999);
            comprobar(gestor.getPesoTotal() == 137 && gestor.getPesoTotal(true) == 648, "Registro extensible protegido");
            gestor.setCatalogoSpawns(new ArrayList<>());
            comprobar(gestor.intentarGenerarPokemon(2f, false) == null, "Catálogo vacío sin excepción");
        } finally {
            gestor.setCatalogoSpawns(original);
            gestor.reiniciar();
        }
    }

    private void probarEntidades() {
        rechazar(IllegalStateException.class, () -> new PokemonBuilder().build());
        Pokemon primero = new Caterpie(0, 0);
        Pokemon segundo = new Caterpie(0, 0);
        Texture compartida = primero.getHojaSprites();
        comprobar(compartida == segundo.getHojaSprites(), "Una textura por especie");
        float velocidad = primero.getVelocidad();
        PokemonBuilder builder = new PokemonBuilder().conPokemonBase(primero)
                .enPosicion(20, 30).conShiny(true).conMultiplicadorVelocidad(1.4f);
        comprobar(builder.build() == primero && primero.isEsShiny(), "Builder polimórfico");
        comprobar(primero.getX() == 20 && primero.getY() == 30, "Posición del Builder");
        comprobar(Math.abs(primero.getVelocidad() - velocidad * 1.4f) < 0.001f, "Multiplicador único");
        rechazar(IllegalStateException.class, builder::build);
        comprobar(Math.abs(primero.getVelocidad() - velocidad * 1.4f) < 0.001f, "Reutilización no multiplica");
        rechazar(IllegalArgumentException.class, () -> builder.conMultiplicadorVelocidad(Float.NaN));
        primero.dispose();
        primero.dispose();
        comprobar(Gdx.gl.glIsTexture(compartida.getTextureObjectHandle()), "Dispose individual conserva al otro");
        segundo.dispose();
        comprobar(compartida.getTextureObjectHandle() == 0, "Última referencia libera GPU");
        for (EntradaSpawn entrada : GestorSpawns.getInstancia().getCatalogoSpawns()) {
            Pokemon pokemon = new PokemonBuilder().conPokemonBase(entrada.crearInstanciaBase(300, 400))
                    .enPosicion(300, 400).conEstrategia(entrada.crearEstrategiaInicial()).build();
            pokemon.actualizarPosicionObjetivo(600, 100);
            pokemon.actualizar(0.2f, 150f);
            getBatch().begin();
            pokemon.draw(getBatch());
            getBatch().end();
            comprobar(pokemon.getHojaSprites().getTextureObjectHandle() != 0, "Carga y dibuja " + pokemon.getClass().getSimpleName());
            if (pokemon instanceof Snorlax) {
                comprobar(!pokemon.recibirImpactoPokeball() && !pokemon.recibirImpactoPokeball()
                        && pokemon.recibirImpactoPokeball(), "Snorlax resiste tres impactos");
            }
            if (pokemon instanceof Glalie) {
                comprobar(!pokemon.recibirImpactoPokeball() && pokemon.recibirImpactoPokeball(), "Glalie resiste dos impactos");
            }
            if (pokemon instanceof Kadabra) {
                for (int i = 0; i < 15; i++) pokemon.actualizar(0.2f, 0f);
                comprobar(pokemon.isColisionActiva()
                        && pokemon.getEstrategiaMovimiento() instanceof puppy.code.movimientos.MovimientoArriba,
                        "Kadabra materializa y cambia Strategy");
            }
            pokemon.dispose();
        }
        Revivir normal = new Revivir(0, 0, false);
        Revivir otro = new Revivir(0, 0, false);
        comprobar(normal.getTextura() == otro.getTextura(), "Revivir comparte textura");
        Revivir maximo = new Revivir(0, 0, true);
        Texture texturaMaxima = maximo.getTextura();
        normal.setTextura(texturaMaxima);
        maximo.dispose();
        comprobar(Gdx.gl.glIsTexture(texturaMaxima.getTextureObjectHandle()), "Setter retiene su referencia");
        normal.dispose(); otro.dispose();
        comprobar(texturaMaxima.getTextureObjectHandle() == 0, "Setter libera referencias sustituidas");
        new Pokeball(10, 10);
        Pokeball.disposeTexturaGlobal();
        comprobar(Pokeball.getRegionPokeball() == null, "Región global invalidada");
        new Pokeball(20, 20);
        comprobar(Pokeball.getRegionPokeball() != null, "Recarga después de liberar");
    }

    private void probarConfusion() {
        Espeon espeon = new Espeon(300, 500);
        Entrenador entrenador = new Entrenador(300, 100);
        com.badlogic.gdx.Input entradaOriginal = Gdx.input;
        final int[] tecla = {com.badlogic.gdx.Input.Keys.A};
        try {
            Gdx.input = (com.badlogic.gdx.Input) java.lang.reflect.Proxy.newProxyInstance(
                    com.badlogic.gdx.Input.class.getClassLoader(), new Class<?>[] {com.badlogic.gdx.Input.class},
                    (proxy, metodo, args) -> {
                        if (metodo.getName().equals("isKeyPressed")) return (int) args[0] == tecla[0];
                        throw new UnsupportedOperationException(metodo.getName());
                    });
            entrenador.update(0.1f, 150f);
            comprobar(entrenador.getX() < 300, "A mueve a la izquierda sin confusión");
            entrenador.recibirConfusion(4f);
            float x = entrenador.getX();
            entrenador.update(0.1f, 150f);
            comprobar(entrenador.getX() > x, "Confusión invierte A");
            tecla[0] = com.badlogic.gdx.Input.Keys.RIGHT;
            x = entrenador.getX();
            entrenador.update(0.1f, 150f);
            comprobar(entrenador.getX() < x, "Confusión invierte flechas");
            tecla[0] = com.badlogic.gdx.Input.Keys.W;
            float y = entrenador.getY();
            entrenador.update(0.1f, 150f);
            comprobar(entrenador.getY() < y && entrenador.isEstaQuieto(), "W invertida conserva detenerse");
            tecla[0] = com.badlogic.gdx.Input.Keys.S;
            y = entrenador.getY();
            entrenador.update(0.1f, 150f);
            comprobar(entrenador.getY() > y, "S invertida avanza");
            entrenador.recibirCongelamiento(2f);
            x = entrenador.getX(); y = entrenador.getY();
            entrenador.update(0.1f, 150f);
            comprobar(entrenador.getX() == x && entrenador.getY() == y, "Congelamiento conserva prioridad");
            tecla[0] = -1;
            entrenador.update(4f, 150f);
            comprobar(!entrenador.isConfundido(), "Confusión expira");
            entrenador.recibirConfusion(Float.NaN);
            comprobar(!entrenador.isConfundido(), "Duración inválida no deja confusión permanente");

            espeon.actualizarPosicionObjetivo(350, 100);
            espeon.actualizar(0f, 0f);
            ProyectilConfusion proyectil = espeon.consumirProyectil();
            comprobar(proyectil != null && espeon.consumirProyectil() == null, "Emite una sola vez");
            espeon.actualizar(0.5f, 0f);
            comprobar(espeon.consumirProyectil() == null, "Cooldown evita disparo prematuro");
            espeon.actualizarPosicionObjetivo(500, 100);
            espeon.actualizar(1f, 0f);
            comprobar(espeon.consumirProyectil() == null, "Fuera de alineación no dispara");
            espeon.actualizarPosicionObjetivo(350, 100);
            espeon.actualizar(0f, 0f);
            comprobar(espeon.consumirProyectil() != null, "Vuelve a disparar tras cooldown");
            espeon.setEsShiny(true);
            espeon.actualizar(0f, 0f);
            comprobar(espeon.getPuntosPorCaptura() == 70, "Bonus shiny de Espeon");
            espeon.recibirImpactoPokeball();
            espeon.actualizar(1f, 0f);
            comprobar(espeon.consumirProyectil() == null, "Captura detiene ataques por Template Method");
            proyectil = new ProyectilConfusion(entrenador.getHitbox().x + 12f,
                    entrenador.getHitbox().y + 24f);
            comprobar(proyectil.verificarColision(entrenador), "Impacto usa Colisionable");
            getBatch().begin(); proyectil.draw(getBatch()); getBatch().end();
            ProyectilConfusion.disposeTexturaGlobal();
            getBatch().begin(); proyectil.draw(getBatch()); getBatch().end();
            comprobar(true, "Textura recarga después de liberar");
            proyectil.update(10f);
            comprobar(proyectil.salioDePantalla(), "Proyectil fuera de pantalla se elimina");
        } finally {
            Gdx.input = entradaOriginal;
            entrenador.dispose(); espeon.dispose();
            ProyectilConfusion.disposeTexturaGlobal();
        }
    }

    private void probarPantallas() {
        PantallaMenu menu = (PantallaMenu) getScreen();
        menu.render(0.016f);
        Texture fondoMenu = menu.getTexFondo();
        PantallaJuego juego = new PantallaJuego(this);
        setScreen(juego);
        comprobar(fondoMenu.getTextureObjectHandle() == 0, "Transición libera menú");
        setScreen(juego);
        comprobar(juego.getTexturaFondo().getTextureObjectHandle() != 0, "Misma pantalla no se libera");
        Espeon atacante = new Espeon(juego.getEntrenador().getX()
                + juego.getEntrenador().getWidth() / 2f - 50f, 300f);
        juego.getPokemonesActivos().add(atacante);
        juego.render(0.1f);
        juego.setEstaPausado(true);
        juego.render(1f);
        comprobar(!juego.getEntrenador().isConfundido(), "Pausa congela proyectil");
        juego.setEstaPausado(false);
        juego.render(0.15f);
        comprobar(juego.getEntrenador().isConfundido(), "Impacto real aplica confusión en pantalla");
        float restante = juego.getEntrenador().getTiempoConfusion();
        juego.setEstaPausado(true);
        juego.render(1f);
        comprobar(juego.getEntrenador().getTiempoConfusion() == restante, "Pausa congela duración del efecto");
        juego.setEstaPausado(false);
        juego.getPokemonesActivos().add(new Snorlax(0, 400));
        juego.getReviviresActivos().add(new Revivir(0, 400, true));
        juego.render(0.016f);
        juego.getEntrenador().setVidasActuales(0);
        juego.render(0.016f);
        juego.render(2f);
        comprobar(getScreen() instanceof PantallaGameOver, "Transición de muerte real");
        comprobar(juego.getMusicaMuerte() == null, "Música transferida sin doble dispose");
        getScreen().render(0.016f);
        setScreen(new PantallaJuego(this));
        getScreen().render(0.016f);
        setScreen(new PantallaMenu(this));
        getScreen().render(0.016f);
        dispose();
        dispose();
        comprobar(getScreen() == null && getBatch() == null, "Cierre idempotente");
        comprobar(TexturasCompartidas.getCantidadCargada() == 0, "Cierre libera todas las texturas compartidas");
        comprobar(Pokemon.getTexturaExclamacion() == null && Kadabra.getHojaSombra() == null
                && Kadabra.getFramesSombra() == null && Pokeball.getRegionPokeball() == null, "Sin referencias estáticas obsoletas");
    }
}
