package puppy.code;

import java.util.ArrayList;
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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import puppy.code.movimientos.EstrategiaMovimiento;
import puppy.code.movimientos.MovimientoMewtwo;
import puppy.code.movimientos.MovimientoRegice;
import puppy.code.pokemones.BossPokemon;
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
    
    private final ArrayList<ProyectilConfusion> proyectilesConfusion = new ArrayList<>();
    
    private final ArrayList<ProyectilHielo> proyectilesHielo = new ArrayList<>();
    private final ArrayList<ZonaHielo> zonasHielo = new ArrayList<>();
    
    private final ArrayList<ProyectilPsiquico> proyectilesPsiquicos = new ArrayList<>();
    private final ArrayList<ZonaPsiquica> zonasPsiquicas = new ArrayList<>();

    private float timerDisparo;
    private float tiempoRecargaDisparo;
    private float tiempoRecargaDisparoBase;
    private int puntaje;
    private Array<Revivir> reviviresActivos;
    private float timerRevivires;
    
    private String[] rutasBiomas = {"fondo_cesped.png", "fondo_desierto.png", "fondo_nevado.png"};
    private int umbralBiomaActual;
    private int indiceBiomaActual = -1;
    private Texture texturaFondoAnterior;
    private boolean enTransicion = false;
    private float alphaTransicion = 0f;
    
    private boolean estaPausado = false;
    
    private boolean enAnimacionMuerte = false;
    private float timerMuerte = 0f;
    private ShapeRenderer shapeRenderer;
    private Music musicaMuerte;
    
    private int proximoPuntajeJefe = 2500;
    
    private float velocidadOriginalEntrenador;
    private float timerAcumuladoHielo = 0f;

    public PantallaJuego(Pokewalk game) {
        this.game = game;
        
        GestorSpawns.getInstancia().reiniciar();
        GestorSpawns.getInstancia().setSpawnsActivos(true);
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
        this.shapeRenderer = new ShapeRenderer();
        this.musicaMuerte = Gdx.audio.newMusic(Gdx.files.internal("muerte.wav"));
        
        com.badlogic.gdx.Preferences prefs = Gdx.app.getPreferences("PokewalkPrefs");
        float volumenGuardado = prefs.getFloat("volumenMusica", 0.3f);
        this.musicaFondo.setVolume(volumenGuardado);
        
        this.musicaFondo.play();
        
        float altoEscalado = Gdx.graphics.getWidth() * ((float) this.texturaFondo.getHeight() / this.texturaFondo.getWidth());
        this.fondoY1 = 0;
        this.fondoY2 = altoEscalado;
        
        this.entrenador = new Entrenador(Gdx.graphics.getWidth() / 2f - 48f, 100f, this.game.getPersonajeSeleccionado());
        this.velocidadOriginalEntrenador = this.entrenador.getVelocidad();
        this.pokemonesActivos = new ArrayList<>();
        this.pokeballsActivas = new ArrayList<>();
        this.timerDisparo = 0f;
        if (this.game.getPersonajeSeleccionado().equalsIgnoreCase("chupete")) {
            this.tiempoRecargaDisparoBase = 0.55f; 
        } else {
            this.tiempoRecargaDisparoBase = 0.70f;
        }
        this.tiempoRecargaDisparo = this.tiempoRecargaDisparoBase;
        this.puntaje = 0;
        this.reviviresActivos = new Array<>();
        this.timerRevivires = 0f;
    }

    @Override
    public void render(float delta) {
        float anchoEscalado = Gdx.graphics.getWidth();
        float altoEscalado = anchoEscalado * ((float) this.texturaFondo.getHeight() / this.texturaFondo.getWidth());
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.P) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            this.estaPausado = !this.estaPausado;
        }

        // TODO ESTE BLOQUE SE CONGELA AL PAUSAR
        if (!this.estaPausado && !this.enAnimacionMuerte) {
            
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
            
            if (this.puntaje >= this.proximoPuntajeJefe) {
                GestorSpawns.getInstancia().setSpawnsActivos(false);
                Pokemon jefe = GestorSpawns.getInstancia().generarJefe(this.indiceBiomaActual);
                this.pokemonesActivos.add(jefe);
                this.proximoPuntajeJefe += 2500;
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
                
                EstrategiaMovimiento estrategia = p.getEstrategiaMovimiento();
                if (estrategia instanceof MovimientoRegice) {
                    MovimientoRegice movRegice = (MovimientoRegice) estrategia;
                    if (movRegice.isDebeLanzarRayo()) {
                        this.proyectilesHielo.add(new ProyectilHielo(p.getX() + p.getWidth() / 2f, p.getY()));
                        movRegice.consumirRayo();
                    }
                    if (movRegice.isDebeGenerarZona()) {
                        this.zonasHielo.add(new ZonaHielo(centroEntrenadorX - 170f, this.entrenador.getY() - 20f));
                        movRegice.consumirZona();
                    }
                }
                
                if (estrategia instanceof MovimientoMewtwo) {
                    MovimientoMewtwo movMewtwo = (MovimientoMewtwo) estrategia;
                    movMewtwo.actualizarObjetivo(centroEntrenadorX, centroEntrenadorY);
                    if (movMewtwo.isDebeLanzarRayo()) {
                        this.proyectilesPsiquicos.add(new ProyectilPsiquico(p.getX() + p.getWidth() / 2f, p.getY()));
                        movMewtwo.consumirRayo();
                    }
                    if (movMewtwo.isDebeGenerarZonaTP()) {
                        float centroMewtwoX = p.getX() + (p.getWidth() / 2f);
                        float centroMewtwoY = p.getY() + (p.getHeight() / 2f);
                        this.zonasPsiquicas.add(new ZonaPsiquica(centroMewtwoX, centroMewtwoY));
                        movMewtwo.consumirZonaTP();
                    }
                }
                
                if (p instanceof EmisorConfusion) {
                    ProyectilConfusion proyectil = ((EmisorConfusion) p).consumirProyectil();
                    if (proyectil != null) this.proyectilesConfusion.add(proyectil);
                }
                
                if (p.capturaCompletada()) {
                    if (p instanceof BossPokemon) {
                        GestorSpawns.getInstancia().setSpawnsActivos(true);
                    }
                    p.dispose();
                    iterPoke.remove();
                    continue;
                }
                if (p.capturaCompletada()) {
                    p.dispose();
                    iterPoke.remove();
                    continue;
                }

                if (!p.isCapturando()) {
                    Iterator<Pokeball> iterColisionBall = this.pokeballsActivas.iterator();
                    while (iterColisionBall.hasNext()) {
                        Pokeball ball = iterColisionBall.next();
                        if (ball.verificarCaptura(p)) {
                            iterColisionBall.remove(); 
                            if (p.recibirImpactoPokeball()) {
                                this.puntaje += p.getPuntosPorCaptura();
                                GestorSpawns.getInstancia().actualizarDificultadSegunPuntaje(this.puntaje);
                                
                                com.badlogic.gdx.Preferences prefs = Gdx.app.getPreferences("PokewalkPrefs");
                                
                                String clave = p.getClaveCapturas();
                                prefs.putInteger(clave, prefs.getInteger(clave, 0) + 1);

                                prefs.flush();
                            }
                            break;
                        }
                    }
                }

                if (!p.isCapturando() && this.entrenador.verificarColision(p)) {
                    if (!(p instanceof BossPokemon)) {
                        p.dispose();
                        iterPoke.remove();
                    }
                    continue;
                }
                
                if (p.salioDePantalla()) {
                    p.dispose();
                    iterPoke.remove();
                }
            }
            
            Iterator<ProyectilConfusion> iterProyectiles = this.proyectilesConfusion.iterator();
            while (iterProyectiles.hasNext()) {
                ProyectilConfusion proyectil = iterProyectiles.next();
                proyectil.update(delta);
                if (proyectil.verificarColision(this.entrenador)) {
                    this.entrenador.recibirConfusion(4f);
                    iterProyectiles.remove();
                } else if (proyectil.salioDePantalla()) {
                    iterProyectiles.remove();
                }
            }
            
            Iterator<ProyectilHielo> iterHielo = this.proyectilesHielo.iterator();
            while (iterHielo.hasNext()) {
                ProyectilHielo rayo = iterHielo.next();
              
                rayo.update(delta, centroEntrenadorX); 
                
                if (rayo.verificarColision(this.entrenador)) {
                    this.entrenador.recibirCongelamiento(3f);
                    this.entrenador.recibirDano(); 
                    iterHielo.remove();
                } else if (rayo.salioDePantalla()) {
                    iterHielo.remove();
                }
            }
            
            Iterator<ProyectilPsiquico> iterPsiquico = this.proyectilesPsiquicos.iterator();
            while (iterPsiquico.hasNext()) {
                ProyectilPsiquico rayo = iterPsiquico.next();
                rayo.update(delta, centroEntrenadorX); 
                
                if (rayo.verificarColision(this.entrenador)) {
                    this.entrenador.recibirDano(); 
                    this.entrenador.recibirConfusion(3f);
                    iterPsiquico.remove();
                } else if (rayo.salioDePantalla()) {
                    iterPsiquico.remove();
                }
            }
            
            boolean pisandoHielo = false;
            Iterator<ZonaHielo> iterZonas = this.zonasHielo.iterator();
            while (iterZonas.hasNext()) {
                ZonaHielo zona = iterZonas.next();
                zona.update(delta, this.velocidadFondo);

                if (zona.getHitbox().overlaps(this.entrenador.getHitbox())) {
                    pisandoHielo = true;
                }
                if (zona.isExpirada()) {
                    iterZonas.remove();
                }
            }

            if (pisandoHielo && !this.entrenador.isCongelado()) {
                this.entrenador.setVelocidad(this.velocidadOriginalEntrenador * 0.4f);
                this.tiempoRecargaDisparo = this.tiempoRecargaDisparoBase * 2.0f;
                
                this.timerAcumuladoHielo += delta;
                if (this.timerAcumuladoHielo >= 1.25f) {
                    this.entrenador.recibirCongelamiento(2.25f);
                    this.timerAcumuladoHielo = 0f;
                }
            } else {
                this.entrenador.setVelocidad(this.velocidadOriginalEntrenador); 
                this.tiempoRecargaDisparo = this.tiempoRecargaDisparoBase;
                this.timerAcumuladoHielo = 0f;
            }
            
            Iterator<ZonaPsiquica> iterZonaPsiquica = this.zonasPsiquicas.iterator();
            while (iterZonaPsiquica.hasNext()) {
                ZonaPsiquica zona = iterZonaPsiquica.next();
                zona.update(delta, this.velocidadFondo);

                if (zona.isPulsoActivo() && zona.getHitbox().overlaps(this.entrenador.getHitbox())) {
                    if (zona.procesarDano()) {
                        this.entrenador.recibirDano();
                    }
                }
                if (zona.isExpirada()) iterZonaPsiquica.remove();
            }

            // Logica de Revivir
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

            if (this.entrenador.estaDerrotado() && !this.enAnimacionMuerte) {
                this.enAnimacionMuerte = true;
                this.timerMuerte = 0f;
                if (this.musicaFondo != null) {
                    this.musicaFondo.stop(); 
                }
                this.musicaMuerte.play();
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
        
        for (ZonaHielo z : this.zonasHielo) {
            z.draw(this.game.getBatch());
        }
        for (ZonaPsiquica zp : this.zonasPsiquicas) {
        	zp.draw(this.game.getBatch());
        }
        for (Pokemon p : this.pokemonesActivos) {
            p.draw(this.game.getBatch());
        }
        for (Pokeball ball : this.pokeballsActivas) {
            ball.draw(this.game.getBatch());
        }
        for (ProyectilConfusion proyectil : this.proyectilesConfusion) {
            proyectil.draw(this.game.getBatch());
        }
        for (ProyectilHielo ph : this.proyectilesHielo) {
            ph.draw(this.game.getBatch());
        }
        for (ProyectilPsiquico pp : this.proyectilesPsiquicos) {
        	pp.draw(this.game.getBatch());
        }
        for (Revivir r : this.reviviresActivos) {
            r.draw(this.game.getBatch());
        }
        
        this.entrenador.draw(this.game.getBatch());
        dibujarHUD();
        
        if (this.estaPausado) {
            dibujarMenuPausa(anchoEscalado, altoEscalado);
        }
        
        this.game.getBatch().end();
        
        if (this.enAnimacionMuerte) {
            this.timerMuerte += delta;
            
            float centroX = this.entrenador.getX() + (this.entrenador.getWidth() / 2f);
            float centroY = this.entrenador.getY() + (this.entrenador.getHeight() / 2f);
            
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            
            this.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            Gdx.gl.glLineWidth(6f);
            
            float radio = this.timerMuerte * 400f; 
            float opacidad = Math.max(0f, 1f - (this.timerMuerte / 1.5f));
            
            this.shapeRenderer.setColor(1f, 0.2f, 0.2f, opacidad);
            this.shapeRenderer.circle(centroX, centroY, radio);
            this.shapeRenderer.end();
            
            Gdx.gl.glDisable(GL20.GL_BLEND);
            
            if (this.timerMuerte >= 1.5f) {
                PantallaGameOver siguiente = new PantallaGameOver(this.game, this.puntaje, this.musicaMuerte);
                this.musicaMuerte = null; // La pantalla de Game Over recibe la propiedad.
                this.game.setScreen(siguiente);
                return;
            }
        }
    }
    
    private void dibujarMenuPausa(float ancho, float alto) {
        com.badlogic.gdx.Preferences prefs = Gdx.app.getPreferences("PokewalkPrefs");
        
        int caterpie = prefs.getInteger("caterpieCapturados", 0);
        int rattata = prefs.getInteger("rattataCapturados", 0);
        int kadabra = prefs.getInteger("kadabraCapturados", 0);
        int snorlax = prefs.getInteger("snorlaxCapturados", 0);
        int snorunt = prefs.getInteger("snoruntCapturados", 0);
        int glalie = prefs.getInteger("glalieCapturados", 0);
        
        this.game.getBatch().setColor(0f, 0f, 0f, 0.6f);
        this.game.getBatch().draw(this.texturaFondo, 0, 0, ancho, alto); 
        this.game.getBatch().setColor(Color.WHITE);
        
        this.fuentePuntaje.setColor(Color.YELLOW);
        this.fuentePuntaje.draw(this.game.getBatch(), "JUEGO PAUSADO", ancho / 2f - 140f, alto - 120f);
        
        this.fuentePuntaje.setColor(Color.WHITE);
        this.fuentePuntaje.draw(this.game.getBatch(), "Registro de Capturas", ancho / 2f - 180f, alto - 200f);
        
        this.fuentePuntaje.getData().setScale(1.2f);
        float col1 = ancho / 2f - 220f;
        float col2 = ancho / 2f + 40f;
        float filaBase = alto - 280f;
        float saltoFila = 45f;
        
        this.fuentePuntaje.draw(this.game.getBatch(), "Caterpie: " + caterpie, col1, filaBase);
        this.fuentePuntaje.draw(this.game.getBatch(), "Rattata: " + rattata, col1, filaBase - saltoFila);
        this.fuentePuntaje.draw(this.game.getBatch(), "Kadabra: " + kadabra, col1, filaBase - saltoFila * 2);
        
        this.fuentePuntaje.draw(this.game.getBatch(), "Snorlax: " + snorlax, col2, filaBase);
        this.fuentePuntaje.draw(this.game.getBatch(), "Snorunt: " + snorunt, col2, filaBase - saltoFila);
        this.fuentePuntaje.draw(this.game.getBatch(), "Glalie: " + glalie, col2, filaBase - saltoFila * 2);
        
        this.fuentePuntaje.getData().setScale(2.0f); 
        
        this.fuentePuntaje.setColor(Color.CYAN);
        this.fuentePuntaje.draw(this.game.getBatch(), "[P] o [ESC] Volver a la partida", ancho / 2f - 240f, filaBase - 160f);
        this.fuentePuntaje.setColor(Color.WHITE);
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

        float textoEstadoY = posY - 15f; 

        if (this.entrenador.isConfundido()) {
            Color rosaClaro = new Color(1f, 0.6f, 0.8f, 1f); 
            Color rosaOscuro = new Color(0.6f, 0f, 0.3f, 1f); 
            dibujarTextoConBorde("CONFUNDIDO", 20f, textoEstadoY, rosaClaro, rosaOscuro);
            textoEstadoY -= 35f;
        }

        if (this.entrenador.isCongelado()) {
            Color celeste = new Color(0.4f, 0.9f, 1f, 1f); 
            Color azulOscuro = new Color(0f, 0f, 0.5f, 1f); 
            dibujarTextoConBorde("CONGELADO", 20f, textoEstadoY, celeste, azulOscuro);
        }

        dibujarTextoConBorde("Puntos: " + this.puntaje, 
                Gdx.graphics.getWidth() - 200f, 
                Gdx.graphics.getHeight() - 25f, 
                Color.YELLOW, 
                Color.BLUE);
    }
    
    private void dibujarTextoConBorde(String texto, float x, float y, Color colorCentro, Color colorBorde) {
        this.fuentePuntaje.setColor(colorBorde);

        this.fuentePuntaje.draw(this.game.getBatch(), texto, x - 2, y);
        this.fuentePuntaje.draw(this.game.getBatch(), texto, x + 2, y);
        this.fuentePuntaje.draw(this.game.getBatch(), texto, x, y - 2);
        this.fuentePuntaje.draw(this.game.getBatch(), texto, x, y + 2);
        this.fuentePuntaje.draw(this.game.getBatch(), texto, x - 2, y - 2);
        this.fuentePuntaje.draw(this.game.getBatch(), texto, x + 2, y + 2);
        this.fuentePuntaje.draw(this.game.getBatch(), texto, x - 2, y + 2);
        this.fuentePuntaje.draw(this.game.getBatch(), texto, x + 2, y - 2);

        this.fuentePuntaje.setColor(colorCentro);
        this.fuentePuntaje.draw(this.game.getBatch(), texto, x, y);

        this.fuentePuntaje.setColor(Color.WHITE);
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
        if (this.musicaMuerte != null) {
            this.musicaMuerte.stop();
            this.musicaMuerte.dispose();
            this.musicaMuerte = null;
        }
        if (this.texturaFondoAnterior != null) this.texturaFondoAnterior.dispose();
        if (this.shapeRenderer != null) this.shapeRenderer.dispose();
        this.entrenador.dispose();
        this.proyectilesConfusion.clear();
        this.proyectilesHielo.clear();
        this.zonasHielo.clear();
        this.proyectilesPsiquicos.clear();
        this.zonasPsiquicas.clear();
        ProyectilConfusion.disposeTexturaGlobal();
        ProyectilHielo.disposeTexturaGlobal();
        ZonaHielo.disposeTexturaGlobal();
        ProyectilPsiquico.disposeTexturaGlobal();
        ZonaPsiquica.disposeTexturas();
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
        if (this.texturaFondoAnterior != null) this.texturaFondoAnterior.dispose();
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

    public String[] getRutasBiomas() { return rutasBiomas.clone(); }
    public void setRutasBiomas(String[] rutasBiomas) {
        if (rutasBiomas == null || rutasBiomas.length < 2)
            throw new IllegalArgumentException("Se necesitan al menos dos biomas");
        for (String ruta : rutasBiomas) {
            if (ruta == null || ruta.trim().isEmpty()) throw new IllegalArgumentException("Ruta de bioma vacía");
        }
        this.rutasBiomas = rutasBiomas.clone();
    }
    public int getUmbralBiomaActual() { return umbralBiomaActual; }
    public void setUmbralBiomaActual(int umbralBiomaActual) { this.umbralBiomaActual = umbralBiomaActual; }
    public int getIndiceBiomaActual() { return indiceBiomaActual; }
    public void setIndiceBiomaActual(int indiceBiomaActual) { this.indiceBiomaActual = indiceBiomaActual; }
    public Texture getTexturaFondoAnterior() { return texturaFondoAnterior; }
    public void setTexturaFondoAnterior(Texture texturaFondoAnterior) { this.texturaFondoAnterior = texturaFondoAnterior; }
    public boolean isEnTransicion() { return enTransicion; }
    public void setEnTransicion(boolean enTransicion) { this.enTransicion = enTransicion; }
    public float getAlphaTransicion() { return alphaTransicion; }
    public void setAlphaTransicion(float alphaTransicion) { this.alphaTransicion = alphaTransicion; }
    public boolean isEstaPausado() { return estaPausado; }
    public void setEstaPausado(boolean estaPausado) { this.estaPausado = estaPausado; }
    public boolean isEnAnimacionMuerte() { return enAnimacionMuerte; }
    public void setEnAnimacionMuerte(boolean enAnimacionMuerte) { this.enAnimacionMuerte = enAnimacionMuerte; }
    public float getTimerMuerte() { return timerMuerte; }
    public void setTimerMuerte(float timerMuerte) { this.timerMuerte = timerMuerte; }
    public ShapeRenderer getShapeRenderer() { return shapeRenderer; }
    public void setShapeRenderer(ShapeRenderer shapeRenderer) { this.shapeRenderer = shapeRenderer; }
    public Music getMusicaMuerte() { return musicaMuerte; }
    public void setMusicaMuerte(Music musicaMuerte) { this.musicaMuerte = musicaMuerte; }
}