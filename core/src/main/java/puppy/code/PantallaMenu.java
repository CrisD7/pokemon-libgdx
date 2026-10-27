package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;

public class PantallaMenu implements Screen {
    public static final int ESTADO_PRINCIPAL = 0;
    public static final int ESTADO_SELECCION = 1;

    private Pokewalk game;
    private int estadoMenu;

    private Texture texFondo;
    private Texture texLogo;
    private Texture texCajaControles;
    private Texture texCardEthan;
    private Texture texCardLyra;
    private Texture texCardSilver;
    private Texture texCardGio;
    private Texture texCandado;
    private BitmapFont fuenteTexto;

    private float fondoY1;
    private float fondoY2;
    private float velocidadFondo;

    private Rectangle boundsEthan;
    private Rectangle boundsLyra;
    private Rectangle boundsSilver;
    private Rectangle boundsGio;

    private float escalaEthan;
    private float escalaLyra;
    private float escalaSilver;
    private float escalaGio;

    public PantallaMenu(Pokewalk game) {
        this.game = game;
        this.estadoMenu = ESTADO_PRINCIPAL;
        this.velocidadFondo = 80f;

        this.texFondo = new Texture(Gdx.files.internal("fondo_cesped.png"));
        this.texFondo.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

        this.texLogo = cargarTexturaSegura("logo_pokewalk.png");
        this.texCajaControles = cargarTexturaSegura("caja_controles.png");
        this.texCardEthan = cargarTexturaSegura("card_ethan.png");
        this.texCardLyra = cargarTexturaSegura("card_lyra.png");
        this.texCardSilver = cargarTexturaSegura("card_silver.png");
        this.texCardGio = cargarTexturaSegura("card_gio.png");
        this.texCandado = cargarTexturaSegura("candado.png");

        float altoEscalado = Gdx.graphics.getWidth() * ((float) this.texFondo.getHeight() / this.texFondo.getWidth());
        this.fondoY1 = 0f;
        this.fondoY2 = altoEscalado;

        this.fuenteTexto = new BitmapFont();
        this.fuenteTexto.getData().setScale(2.0f);
        this.fuenteTexto.setColor(Color.WHITE);

        // Posicionamiento horizontal centrado de las 4 tarjetas de personaje
        float ancho = Gdx.graphics.getWidth();
        float alto = Gdx.graphics.getHeight();
        float cardW = 190f;
        float cardH = 210f;
        float espacio = 15f;
        float totalW = (cardW * 4f) + (espacio * 3f);
        float inicioX = (ancho - totalW) / 2f;
        float posY = (alto / 2f) - (cardH / 2f);

        this.boundsEthan = new Rectangle(inicioX, posY, cardW, cardH);
        this.boundsLyra = new Rectangle(inicioX + cardW + espacio, posY, cardW, cardH);
        this.boundsSilver = new Rectangle(inicioX + (cardW + espacio) * 2f, posY, cardW, cardH);
        this.boundsGio = new Rectangle(inicioX + (cardW + espacio) * 3f, posY, cardW, cardH);

        this.escalaEthan = 1.0f;
        this.escalaLyra = 1.0f;
        this.escalaSilver = 1.0f;
        this.escalaGio = 1.0f;
    }

    private Texture cargarTexturaSegura(String ruta) {
        if (Gdx.files.internal(ruta).exists()) {
            Texture t = new Texture(Gdx.files.internal(ruta));
            t.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
            return t;
        }
        return null;
    }

    @Override
    public void render(float delta) {
        float ancho = Gdx.graphics.getWidth();
        float alto = Gdx.graphics.getHeight();
        float altoEscalado = ancho * ((float) this.texFondo.getHeight() / this.texFondo.getWidth());

        this.fondoY1 -= this.velocidadFondo * delta;
        this.fondoY2 -= this.velocidadFondo * delta;
        if (this.fondoY1 + altoEscalado <= 0) this.fondoY1 = this.fondoY2 + altoEscalado;
        if (this.fondoY2 + altoEscalado <= 0) this.fondoY2 = this.fondoY1 + altoEscalado;

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        this.game.getBatch().begin();
        this.game.getBatch().setColor(0.5f, 0.5f, 0.55f, 1f);
        this.game.getBatch().draw(this.texFondo, 0, this.fondoY1, ancho, altoEscalado);
        this.game.getBatch().draw(this.texFondo, 0, this.fondoY2, ancho, altoEscalado);
        this.game.getBatch().setColor(Color.WHITE);

        if (this.estadoMenu == ESTADO_PRINCIPAL) {
            renderizarMenuPrincipal(ancho, alto);
        } else {
            renderizarSubmenuPersonajes(delta, ancho, alto);
        }

        this.game.getBatch().end();
    }

    private void renderizarMenuPrincipal(float ancho, float alto) {
        // 1. Logo superior
        if (this.texLogo != null) {
            float logoW = 420f;
            float logoH = logoW * ((float) this.texLogo.getHeight() / this.texLogo.getWidth());
            this.game.getBatch().draw(this.texLogo, (ancho - logoW) / 2f, alto - logoH - 40f, logoW, logoH);
        } else {
            this.fuenteTexto.draw(this.game.getBatch(), "POKEWALK", ancho / 2f - 80f, alto - 80f);
        }

        // 2. Caja de controles al centro
        if (this.texCajaControles != null) {
            float cajaW = 460f;
            float cajaH = cajaW * ((float) this.texCajaControles.getHeight() / this.texCajaControles.getWidth());
            this.game.getBatch().draw(this.texCajaControles, (ancho - cajaW) / 2f, (alto - cajaH) / 2f - 10f, cajaW, cajaH);
        }

        if (this.game.getHighScore() > 0) {
            this.fuenteTexto.setColor(Color.GOLD);
            this.fuenteTexto.draw(this.game.getBatch(), "Mejor Record: " + this.game.getHighScore() + " pts", ancho / 2f - 150f, 135f);
        }

        this.fuenteTexto.setColor(Color.WHITE);
        this.fuenteTexto.draw(this.game.getBatch(), "Haz clic o presiona [ENTER] para elegir Entrenador", ancho / 2f - 310f, 80f);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
                || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            this.estadoMenu = ESTADO_SELECCION;
        }
    }

    private void renderizarSubmenuPersonajes(float delta, float ancho, float alto) {
    	float mouseX = Gdx.input.getX();
        float mouseY = alto - Gdx.input.getY();
        
        int record = this.game.getHighScore();
        boolean lyraDesbloqueada = record >= 500;
        boolean silverDesbloqueado = record >= 1000;
        com.badlogic.gdx.Preferences prefs = Gdx.app.getPreferences("PokewalkPrefs");
        int snorlaxCapturados = prefs.getInteger("snorlaxCapturados", 0);
        boolean gioDesbloqueado = snorlaxCapturados >= 25;

        boolean hoverRealEthan = this.boundsEthan.contains(mouseX, mouseY);
        boolean hoverRealLyra = this.boundsLyra.contains(mouseX, mouseY);
        boolean hoverRealSilver = this.boundsSilver.contains(mouseX, mouseY);
        boolean hoverRealGio = this.boundsGio.contains(mouseX, mouseY);
        
        boolean hoverEthan = hoverRealEthan;
        boolean hoverLyra = lyraDesbloqueada && hoverRealLyra;
        boolean hoverSilver = silverDesbloqueado && hoverRealSilver;
        boolean hoverGio = gioDesbloqueado && hoverRealGio;

        this.escalaEthan += ((hoverEthan ? 1.12f : 1.0f) - this.escalaEthan) * 12f * delta;
        this.escalaLyra += ((hoverLyra ? 1.12f : 1.0f) - this.escalaLyra) * 12f * delta;
        this.escalaSilver += ((hoverSilver ? 1.12f : 1.0f) - this.escalaSilver) * 12f * delta;
        this.escalaGio += ((hoverGio ? 1.12f : 1.0f) - this.escalaGio) * 12f * delta;

        this.fuenteTexto.setColor(Color.YELLOW);
        this.fuenteTexto.draw(this.game.getBatch(), "ELIGE TU ENTRENADOR", ancho / 2f - 160f, alto - 90f);
        this.fuenteTexto.setColor(Color.WHITE);

        if (this.texCardEthan != null) dibujarTarjeta(this.texCardEthan, this.boundsEthan, this.escalaEthan, hoverEthan, true);
        if (this.texCardLyra != null) dibujarTarjeta(this.texCardLyra, this.boundsLyra, this.escalaLyra, hoverLyra, lyraDesbloqueada);
        if (this.texCardSilver != null) dibujarTarjeta(this.texCardSilver, this.boundsSilver, this.escalaSilver, hoverSilver, silverDesbloqueado);
        if (this.texCardGio != null) dibujarTarjeta(this.texCardGio, this.boundsGio, this.escalaGio, hoverGio, gioDesbloqueado);
        
        float candadoSize = 64f;
        this.fuenteTexto.getData().setScale(1.05f); 
        this.fuenteTexto.setColor(new Color(1f, 0.4f, 0.4f, 1f));
        
        if (!lyraDesbloqueada) {
            if (this.texCandado != null) {
                float candadoX = this.boundsLyra.x + (this.boundsLyra.width / 2f) - (candadoSize / 2f);
                float candadoY = this.boundsLyra.y + (this.boundsLyra.height / 2f) - (candadoSize / 2f) + 25f;
                this.game.getBatch().draw(this.texCandado, candadoX, candadoY, candadoSize, candadoSize);
            }
            if (hoverRealLyra) {
                this.fuenteTexto.draw(this.game.getBatch(), "Se desbloquea al\nalcanzar 500 pts.", this.boundsLyra.x + 12f, this.boundsLyra.y + 65f);
            }
        }
        
        if (!silverDesbloqueado) {
            if (this.texCandado != null) {
                float candadoX = this.boundsSilver.x + (this.boundsSilver.width / 2f) - (candadoSize / 2f);
                float candadoY = this.boundsSilver.y + (this.boundsSilver.height / 2f) - (candadoSize / 2f) + 25f;
                this.game.getBatch().draw(this.texCandado, candadoX, candadoY, candadoSize, candadoSize);
            }
            if (hoverRealSilver) {
                this.fuenteTexto.draw(this.game.getBatch(), "Se desbloquea al\nalcanzar 1000 pts.", this.boundsSilver.x + 5f, this.boundsSilver.y + 65f);
            }
        }

        if (!gioDesbloqueado) {
            if (this.texCandado != null) {
                float candadoX = this.boundsGio.x + (this.boundsGio.width / 2f) - (candadoSize / 2f);
                float candadoY = this.boundsGio.y + (this.boundsGio.height / 2f) - (candadoSize / 2f) + 25f;
                this.game.getBatch().draw(this.texCandado, candadoX, candadoY, candadoSize, candadoSize);
            }
            if (hoverRealGio) {
                this.fuenteTexto.draw(this.game.getBatch(), "Captura 25 Snorlax\nProgreso: " + snorlaxCapturados + " de 25", this.boundsGio.x + 8f, this.boundsGio.y + 65f);
            }
        }
        
        this.fuenteTexto.getData().setScale(2.0f);
        this.fuenteTexto.setColor(Color.WHITE);

        this.fuenteTexto.draw(this.game.getBatch(), "[ESC] Volver atras", ancho / 2f - 120f, 85f);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            this.estadoMenu = ESTADO_PRINCIPAL;
        } else if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (hoverEthan) iniciarPartidaCon("ethan");
            else if (hoverLyra) iniciarPartidaCon("lyra");
            else if (hoverSilver) iniciarPartidaCon("silver");
            else if (hoverGio) iniciarPartidaCon("gio");
        }
    }

    private void iniciarPartidaCon(String personaje) {
        this.game.setPersonajeSeleccionado(personaje);
        this.game.setScreen(new PantallaJuego(this.game));
        this.dispose();
    }

    private void dibujarTarjeta(Texture textura, Rectangle base, float escala, boolean enHover, boolean desbloqueado) {
        float w = base.width * escala;
        float h = base.height * escala;
        float x = base.x - (w - base.width) / 2f;
        float y = base.y - (h - base.height) / 2f;

        if (!desbloqueado) {
            this.game.getBatch().setColor(0.3f, 0.3f, 0.3f, 0.9f);
            this.game.getBatch().draw(textura, x, y, w, h);
            this.game.getBatch().setColor(Color.WHITE);
        } else if (enHover) {
            this.game.getBatch().setColor(0.75f, 0.95f, 1.0f, 1.0f);
            this.game.getBatch().draw(textura, x, y, w, h);

            this.game.getBatch().setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
            this.game.getBatch().setColor(0.25f, 0.65f, 1.0f, 0.35f);
            this.game.getBatch().draw(textura, x, y, w, h);

            this.game.getBatch().setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            this.game.getBatch().setColor(Color.WHITE);
        } else {
            this.game.getBatch().setColor(Color.WHITE);
            this.game.getBatch().draw(textura, x, y, w, h);
        }
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        this.texFondo.dispose();
        if (this.texLogo != null) this.texLogo.dispose();
        if (this.texCajaControles != null) this.texCajaControles.dispose();
        if (this.texCardEthan != null) this.texCardEthan.dispose();
        if (this.texCardLyra != null) this.texCardLyra.dispose();
        if (this.texCardSilver != null) this.texCardSilver.dispose();
        if (this.texCardGio != null) this.texCardGio.dispose();
        if (this.texCandado != null) this.texCandado.dispose();
        this.fuenteTexto.dispose();
    }

 // Getters y Setters estrictos (GM-5)
    public Pokewalk getGame() { return game; }
    public void setGame(Pokewalk game) { this.game = game; }
    public int getEstadoMenu() { return estadoMenu; }
    public void setEstadoMenu(int estadoMenu) { this.estadoMenu = estadoMenu; }
    public Texture getTexFondo() { return texFondo; }
    public void setTexFondo(Texture texFondo) { this.texFondo = texFondo; }
    public Texture getTexLogo() { return texLogo; }
    public void setTexLogo(Texture texLogo) { this.texLogo = texLogo; }
    public Texture getTexCajaControles() { return texCajaControles; }
    public void setTexCajaControles(Texture texCajaControles) { this.texCajaControles = texCajaControles; }
    public Texture getTexCardEthan() { return texCardEthan; }
    public void setTexCardEthan(Texture texCardEthan) { this.texCardEthan = texCardEthan; }
    public Texture getTexCardLyra() { return texCardLyra; }
    public void setTexCardLyra(Texture texCardLyra) { this.texCardLyra = texCardLyra; }
    public Texture getTexCardSilver() { return texCardSilver; }
    public void setTexCardSilver(Texture texCardSilver) { this.texCardSilver = texCardSilver; }
    public Texture getTexCandado() { return texCandado; }
    public void setTexCandado(Texture texCandado) { this.texCandado = texCandado; }
    public BitmapFont getFuenteTexto() { return fuenteTexto; }
    public void setFuenteTexto(BitmapFont fuenteTexto) { this.fuenteTexto = fuenteTexto; }
    public float getFondoY1() { return fondoY1; }
    public void setFondoY1(float fondoY1) { this.fondoY1 = fondoY1; }
    public float getFondoY2() { return fondoY2; }
    public void setFondoY2(float fondoY2) { this.fondoY2 = fondoY2; }
    public float getVelocidadFondo() { return velocidadFondo; }
    public void setVelocidadFondo(float velocidadFondo) { this.velocidadFondo = velocidadFondo; }
    public Rectangle getBoundsEthan() { return boundsEthan; }
    public void setBoundsEthan(Rectangle boundsEthan) { this.boundsEthan = boundsEthan; }
    public Rectangle getBoundsLyra() { return boundsLyra; }
    public void setBoundsLyra(Rectangle boundsLyra) { this.boundsLyra = boundsLyra; }
    public Rectangle getBoundsSilver() { return boundsSilver; }
    public void setBoundsSilver(Rectangle boundsSilver) { this.boundsSilver = boundsSilver; }
    public float getEscalaEthan() { return escalaEthan; }
    public void setEscalaEthan(float escalaEthan) { this.escalaEthan = escalaEthan; }
    public float getEscalaLyra() { return escalaLyra; }
    public void setEscalaLyra(float escalaLyra) { this.escalaLyra = escalaLyra; }
    public float getEscalaSilver() { return escalaSilver; }
    public void setEscalaSilver(float escalaSilver) { this.escalaSilver = escalaSilver; }
    public Texture getTexCardGio() { return texCardGio; }
    public void setTexCardGio(Texture texCardGio) { this.texCardGio = texCardGio; }
    public Rectangle getBoundsGio() { return boundsGio; }
    public void setBoundsGio(Rectangle boundsGio) { this.boundsGio = boundsGio; }
    public float getEscalaGio() { return escalaGio; }
    public void setEscalaGio(float escalaGio) { this.escalaGio = escalaGio; }
}