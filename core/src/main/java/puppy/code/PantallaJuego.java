package puppy.code;

import java.util.ArrayList;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import java.util.Iterator;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import puppy.code.pokemones.Pokemon;
import puppy.code.spawns.GestorSpawns;

public class PantallaJuego implements Screen {
    private Pokewalk game;
    private Texture texturaFondo;
    private Texture texturaCorazon;
    private BitmapFont fuentePuntaje;
    private Music musicaFondo;
    
    private float fondoY1;
    private float fondoY2;
    private float velocidadFondo = 150f;
    
    private Entrenador entrenador;
    private ArrayList<Pokemon> pokemonesActivos;
    private ArrayList<Pokeball> pokeballsActivas;
    
    private float timerDisparo;
    private float tiempoRecargaDisparo = 0.70f; // Cooldown ligeramente mayor entre Pokéballs
    private int puntaje;
    private Array<Revivir> reviviresActivos;
    private float timerRevivires;
    
    private String[] rutasBiomas = {"fondo_cesped.png", "fondo_desierto.png", "fondo_nevado.png"};
    private int umbralBiomaActual;
    private int indiceBiomaActual = -1;
    private Texture texturaFondoAnterior;
    private boolean enTransicion = false;
    private float alphaTransicion = 0f;

    public PantallaJuego(Pokewalk game) {
        this.game = game;
        
        this.umbralBiomaActual = 0;
        cambiarBiomaAleatorio();
        
        this.texturaCorazon = new Texture(Gdx.files.internal("corazon.png"));
        this.texturaCorazon.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        
        this.fuentePuntaje = new BitmapFont();
        this.fuentePuntaje.getData().setScale(2.0f);
        this.fuentePuntaje.setColor(Color.WHITE);
        
        // Carga y reproducción de música de fondo en bucle
        this.musicaFondo = Gdx.audio.newMusic(Gdx.files.internal("musica_fondo.wav"));
        this.musicaFondo.setLooping(true);
        
        com.badlogic.gdx.Preferences prefs = Gdx.app.getPreferences("PokewalkPrefs");
        float volumenGuardado = prefs.getFloat("volumenMusica", 0.3f);
        this.musicaFondo.setVolume(volumenGuardado);
        
        this.musicaFondo.play();
        
        float altoEscalado = Gdx.graphics.getWidth() * ((float) this.texturaFondo.getHeight() / this.texturaFondo.getWidth());
        this.fondoY1 = 0;
        this.fondoY2 = altoEscalado;
        
        this.entrenador = new Entrenador(Gdx.graphics.getWidth() / 2f - 48f, 100f, this.game.getPersonajeSeleccionado());
        this.pokemonesActivos = new ArrayList<>();
        this.pokeballsActivas = new ArrayList<>();
        this.timerDisparo = 0f;
        this.puntaje = 0;
        this.reviviresActivos = new Array<>();
        this.timerRevivires = 0f;
    }

    @Override
    public void render(float delta) {
        float anchoEscalado = Gdx.graphics.getWidth();
        float altoEscalado = anchoEscalado * ((float) this.texturaFondo.getHeight() / this.texturaFondo.getWidth());

        // 1. Actualizar fondo
        this.fondoY1 -= this.velocidadFondo * delta;
        this.fondoY2 -= this.velocidadFondo * delta;

        if (this.fondoY1 + altoEscalado <= 0) {
            this.fondoY1 = this.fondoY2 + altoEscalado;
        }
        if (this.fondoY2 + altoEscalado <= 0) {
            this.fondoY2 = this.fondoY1 + altoEscalado;
        }
        
        // 2. Actualizar entrenador y disparo
        this.entrenador.update(delta, this.velocidadFondo);
        actualizarDisparo(delta);
        gestionarVolumen();
        
        int umbralCalculado = this.puntaje / 1000;
        if (umbralCalculado > this.umbralBiomaActual) {
            this.umbralBiomaActual = umbralCalculado;
            cambiarBiomaAleatorio();
        }

        // 3. Spawnear nuevos Pokémon
        boolean biomaNevado = (this.indiceBiomaActual == 2); 
        Pokemon nuevo = GestorSpawns.getInstancia().intentarGenerarPokemon(delta, biomaNevado);
        
        if (nuevo != null) {
            this.pokemonesActivos.add(nuevo);
        }

        // 4. Actualizar Pokéballs activas
        Iterator<Pokeball> iterBalls = this.pokeballsActivas.iterator();
        while (iterBalls.hasNext()) {
            Pokeball ball = iterBalls.next();
            ball.update(delta);
            if (ball.salioDePantalla()) {
                iterBalls.remove();
            }
        }

        // 5. Actualizar Pokémon y verificar colisiones
        float centroEntrenadorX = this.entrenador.getX() + (this.entrenador.getWidth() / 2f);
        float centroEntrenadorY = this.entrenador.getY() + (this.entrenador.getHeight() / 2f);

        Iterator<Pokemon> iterPoke = this.pokemonesActivos.iterator();
        while (iterPoke.hasNext()) {
            Pokemon p = iterPoke.next();
            p.actualizarPosicionObjetivo(centroEntrenadorX, centroEntrenadorY);
            p.actualizar(delta, this.velocidadFondo);
            
            // Si ya terminó su animación de encogimiento tras ser capturado, se elimina
            if (p.capturaCompletada()) {
                p.dispose();
                iterPoke.remove();
                continue;
            }

            // A) Verificar impacto con Pokéballs (solo si aún no está encogiéndose)
            if (!p.isCapturando()) {
                Iterator<Pokeball> iterColisionBall = this.pokeballsActivas.iterator();
                while (iterColisionBall.hasNext()) {
                    Pokeball ball = iterColisionBall.next();
                    if (ball.verificarCaptura(p)) {
                        iterColisionBall.remove(); // La Pokéball se consume al impactar
                        // Si el golpe vence la resistencia del Pokémon, suma puntos e inicia encogimiento
                        if (p.recibirImpactoPokeball()) {
                            this.puntaje += p.getPuntosPorCaptura();
                            
                            if (p instanceof puppy.code.pokemones.Snorlax) {
                                com.badlogic.gdx.Preferences prefs = Gdx.app.getPreferences("PokewalkPrefs");
                                int capturados = prefs.getInteger("snorlaxCapturados", 0);
                                prefs.putInteger("snorlaxCapturados", capturados + 1);
                                prefs.flush();
                            }
                        }
                        break;
                    }
                }
            }

            // B) ¿Chocó contra el entrenador? (Solo si no está en animación de captura)
            if (!p.isCapturando() && this.entrenador.verificarColision(p)) {
                p.dispose();
                iterPoke.remove();
                continue;
            }
            
            // C) ¿Salió de los límites de la pantalla?
            if (p.salioDePantalla()) {
                p.dispose();
                iterPoke.remove();
            }
        }
        
        // Logica de Revivir (Recuperar Vidas)
        this.timerRevivires += delta;
        if (this.timerRevivires >= 3.5f) { 
            this.timerRevivires = 0f;
            
            if (this.entrenador.getVidasActuales() < this.entrenador.getVidasMaximas()) {
                if (MathUtils.random(1, 100) <= 15) { 
                    boolean esMax = MathUtils.random(1, 100) <= 10; 
                    float posX = MathUtils.random(0, anchoEscalado - 48f);
                    float posY = altoEscalado + 10f;
                    this.reviviresActivos.add(new Revivir(posX, posY, esMax));
                }
            }
        }

        for (int i = this.reviviresActivos.size - 1; i >= 0; i--) {
            Revivir rev = this.reviviresActivos.get(i);
            
            rev.update(delta, this.velocidadFondo);

            if (rev.isExpirada() || rev.salioDePantalla()) {
                rev.dispose();
                this.reviviresActivos.removeIndex(i);
                continue;
            }
            
            if (this.entrenador.getHitbox().overlaps(rev.getHitbox())) {
                this.entrenador.recuperarVida(rev.isMaxRevivir());
                rev.dispose();
                this.reviviresActivos.removeIndex(i);
            }
        }

        if (this.entrenador.estaDerrotado()) {
            this.game.setScreen(new PantallaGameOver(this.game, this.puntaje));
            this.dispose();
            return;
        }

        if (this.enTransicion) {
            this.alphaTransicion += delta * 0.5f;
            if (this.alphaTransicion >= 1f) {
                this.alphaTransicion = 1f;
                this.enTransicion = false;
                if (this.texturaFondoAnterior != null) {
                    this.texturaFondoAnterior.dispose();
                    this.texturaFondoAnterior = null;
                }
            }
        }

        // 6. Dibujar todo
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        this.game.getBatch().begin();
        
        if (this.enTransicion && this.texturaFondoAnterior != null) {
            this.game.getBatch().draw(this.texturaFondoAnterior, 0, this.fondoY1, anchoEscalado, altoEscalado);
            this.game.getBatch().draw(this.texturaFondoAnterior, 0, this.fondoY2, anchoEscalado, altoEscalado);
            
            this.game.getBatch().setColor(1f, 1f, 1f, this.alphaTransicion);
            this.game.getBatch().draw(this.texturaFondo, 0, this.fondoY1, anchoEscalado, altoEscalado);
            this.game.getBatch().draw(this.texturaFondo, 0, this.fondoY2, anchoEscalado, altoEscalado);
            
            this.game.getBatch().setColor(Color.WHITE); 
        } else {
            this.game.getBatch().draw(this.texturaFondo, 0, this.fondoY1, anchoEscalado, altoEscalado);
            this.game.getBatch().draw(this.texturaFondo, 0, this.fondoY2, anchoEscalado, altoEscalado);
        }
        
        for (Pokemon p : this.pokemonesActivos) {
            p.draw(this.game.getBatch());
        }
        for (Pokeball ball : this.pokeballsActivas) {
            ball.draw(this.game.getBatch());
        }
        
        for (Revivir r : this.reviviresActivos) {
            r.draw(this.game.getBatch());
        }
        
        this.entrenador.draw(this.game.getBatch());
        dibujarHUD();
        this.game.getBatch().end();
    }

    private void actualizarDisparo(float delta) {
    	if (this.timerDisparo > 0f) {
            this.timerDisparo -= delta;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && this.timerDisparo <= 0f && !this.entrenador.isCongelado()) {
            float centroX = this.entrenador.getX() + (this.entrenador.getWidth() / 2f);
            float frenteY = this.entrenador.getY() + this.entrenador.getHeight() - 10f;
            this.pokeballsActivas.add(new Pokeball(centroX, frenteY));
            this.timerDisparo = this.tiempoRecargaDisparo;
        }
    }

    private void dibujarHUD() {
        float tamanoCorazon = 40f;
        float espacio = 8f;
        float margenX = 20f;
        float posY = Gdx.graphics.getHeight() - tamanoCorazon - 20f;

        for (int i = 0; i < this.entrenador.getVidasMaximas(); i++) {
            float posX = margenX + i * (tamanoCorazon + espacio);
            if (i < this.entrenador.getVidasActuales()) {
                this.game.getBatch().setColor(Color.WHITE);
            } else {
                this.game.getBatch().setColor(0.15f, 0.15f, 0.15f, 0.75f);
            }
            this.game.getBatch().draw(this.texturaCorazon, posX, posY, tamanoCorazon, tamanoCorazon);
        }
        this.game.getBatch().setColor(Color.WHITE);

        this.fuentePuntaje.draw(this.game.getBatch(), "Puntos: " + this.puntaje, 
                                Gdx.graphics.getWidth() - 200f, Gdx.graphics.getHeight() - 25f);
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        this.texturaFondo.dispose();
        this.texturaCorazon.dispose();
        this.fuentePuntaje.dispose();
        if (this.musicaFondo != null) {
            this.musicaFondo.stop();
            this.musicaFondo.dispose();
        }
        if (this.texturaFondoAnterior != null) this.texturaFondoAnterior.dispose();
        this.entrenador.dispose();
        for (Pokemon p : this.pokemonesActivos) {
            p.dispose();
        }
        for (Revivir r : this.reviviresActivos) {
            r.dispose();
        }
    }
    
    private void gestionarVolumen() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.PLUS) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_ADD)) {
            ajustarVolumen(0.1f);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.MINUS) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_SUBTRACT)) {
            ajustarVolumen(-0.1f);
        }
    }

    private void ajustarVolumen(float variacion) {
        float volumenActual = this.musicaFondo.getVolume();
        float nuevoVolumen = Math.max(0f, Math.min(1f, volumenActual + variacion)); 
        
        this.musicaFondo.setVolume(nuevoVolumen);
        
        com.badlogic.gdx.Preferences prefs = Gdx.app.getPreferences("PokewalkPrefs");
        prefs.putFloat("volumenMusica", nuevoVolumen);
        prefs.flush();
    }
    
    private void cambiarBiomaAleatorio() {
        if (this.texturaFondo != null) {
            this.texturaFondoAnterior = this.texturaFondo;
            this.enTransicion = true;
            this.alphaTransicion = 0f;
        }
        
        int nuevoIndice;
        do {
            nuevoIndice = com.badlogic.gdx.math.MathUtils.random(0, this.rutasBiomas.length - 1);
        } while (nuevoIndice == this.indiceBiomaActual); 
        
        this.indiceBiomaActual = nuevoIndice; 
        
        this.texturaFondo = new Texture(Gdx.files.internal(this.rutasBiomas[this.indiceBiomaActual]));
        this.texturaFondo.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
    }

    // Getters y Setters estrictos (GM-5)
    public Pokewalk getGame() { return game; }
    public void setGame(Pokewalk game) { this.game = game; }
    public Texture getTexturaFondo() { return texturaFondo; }
    public void setTexturaFondo(Texture texturaFondo) { this.texturaFondo = texturaFondo; }
    public Texture getTexturaCorazon() { return texturaCorazon; }
    public void setTexturaCorazon(Texture texturaCorazon) { this.texturaCorazon = texturaCorazon; }
    public BitmapFont getFuentePuntaje() { return fuentePuntaje; }
    public void setFuentePuntaje(BitmapFont fuentePuntaje) { this.fuentePuntaje = fuentePuntaje; }
    public Music getMusicaFondo() { return musicaFondo; }
    public void setMusicaFondo(Music musicaFondo) { this.musicaFondo = musicaFondo; }
    public float getFondoY1() { return fondoY1; }
    public void setFondoY1(float fondoY1) { this.fondoY1 = fondoY1; }
    public float getFondoY2() { return fondoY2; }
    public void setFondoY2(float fondoY2) { this.fondoY2 = fondoY2; }
    public float getVelocidadFondo() { return velocidadFondo; }
    public void setVelocidadFondo(float velocidadFondo) { this.velocidadFondo = velocidadFondo; }
    public Entrenador getEntrenador() { return entrenador; }
    public void setEntrenador(Entrenador entrenador) { this.entrenador = entrenador; }
    public ArrayList<Pokemon> getPokemonesActivos() { return pokemonesActivos; }
    public void setPokemonesActivos(ArrayList<Pokemon> pokemonesActivos) { this.pokemonesActivos = pokemonesActivos; }
    public ArrayList<Pokeball> getPokeballsActivas() { return pokeballsActivas; }
    public void setPokeballsActivas(ArrayList<Pokeball> pokeballsActivas) { this.pokeballsActivas = pokeballsActivas; }
    public float getTimerDisparo() { return timerDisparo; }
    public void setTimerDisparo(float timerDisparo) { this.timerDisparo = timerDisparo; }
    public float getTiempoRecargaDisparo() { return tiempoRecargaDisparo; }
    public void setTiempoRecargaDisparo(float tiempoRecargaDisparo) { this.tiempoRecargaDisparo = tiempoRecargaDisparo; }
    public int getPuntaje() { return puntaje; }
    public void setPuntaje(int puntaje) { this.puntaje = puntaje; }
    public Array<Revivir> getReviviresActivos() { return reviviresActivos; }
    public void setReviviresActivos(Array<Revivir> reviviresActivos) { this.reviviresActivos = reviviresActivos; }
    public float getTimerRevivires() { return timerRevivires; }
    public void setTimerRevivires(float timerRevivires) { this.timerRevivires = timerRevivires; }
}