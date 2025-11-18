package br.mackenzie;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TelaFimDeJogo implements Screen {

    final JogoPrincipal game;
    OrthographicCamera camera;
    BitmapFont font;
    BitmapFont titleFont;
    String message;
    Color messageColor;

    private Music perdeuSom;
    private Music ganhouSom;
    private Sound clickSound;
    private boolean somTocado = false;
    private boolean venceu = false;
    private int nivelCompletado;

    Color corFundoNormal = new Color(0.8f, 0.5f, 0.0f, 1.0f);
    Color corFundoPressionado = new Color(1.0f, 0.7f, 0.0f, 1.0f);
    Color corFundoProximoNivel = new Color(0.1f, 0.6f, 0.1f, 1.0f); // Verde

    private final float BOTAO_LARGURA = 350;
    private final float BOTAO_ALTURA = 80;

    private float botaoX, botaoY;
    private float botaoX2, botaoY2;
    private boolean proximoNivelDisponivel = false;

    private float finalTime;
    private float finalDistance;
    private int finalPresses;
    private float avgSpeed;
    private final float UNITS_PER_METER = 100f;
    private ShapeRenderer shapeRenderer;
    private Texture texturaBackground;
    private static final String NOME_FONTE = "fonteMenu.ttf";

    public TelaFimDeJogo(final JogoPrincipal game, EstadoJogo estadoFinal, float time, float distance, int presses, int nivel) {
        this.game = game;
        this.nivelCompletado = nivel;

        // ... (Inicialização de camera, shaperenderer, texturas, sons e fontes IGUAL)
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer = new ShapeRenderer();
        if (Gdx.files.internal("background.png").exists()) texturaBackground = new Texture(Gdx.files.internal("background.png"));
        if (Gdx.files.internal("click_botao_madeira.mp3").exists()) clickSound = Gdx.audio.newSound(Gdx.files.internal("click_botao_madeira.mp3"));
        if (Gdx.files.internal("perdeu.mp3").exists()) perdeuSom = Gdx.audio.newMusic(Gdx.files.internal("perdeu.mp3"));
        if (Gdx.files.internal("ganhou.mp3").exists()) ganhouSom = Gdx.audio.newMusic(Gdx.files.internal("ganhou.mp3"));

        if (Gdx.files.internal(NOME_FONTE).exists()) {
            FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal(NOME_FONTE));
            FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
            param.size = Math.max(18, Gdx.graphics.getWidth() / 50);
            param.color = Color.WHITE;
            font = gen.generateFont(param);
            gen.dispose();
        } else {
            font = null;
        }
        titleFont = new BitmapFont();
        titleFont.getData().setScale(3.2f);

        this.finalTime = time;
        this.finalDistance = distance / UNITS_PER_METER;
        this.finalPresses = presses;
        this.avgSpeed = (time > 0) ? this.finalDistance / time : 0f;

        // Lógica de Mensagem e Próximo Nível
        if (estadoFinal == EstadoJogo.VENCEU) {
            this.venceu = true;
            this.messageColor = Color.GREEN;

            if (nivel == 3) {
                this.message = "VOCÊ ZEROU O JOGO! É UM MESTRE!";
                this.proximoNivelDisponivel = false;
            } else {
                this.message = "VITÓRIA! Nível " + nivel + " Concluído!";
                this.proximoNivelDisponivel = true;
            }

        } else {
            this.message = "FIM DE JOGO. A onça te pegou.";
            this.messageColor = Color.RED;
            this.venceu = false;
            this.proximoNivelDisponivel = false;
        }

        somTocado = false;
    }

    private void desenharBotao(SpriteBatch batch, ShapeRenderer sr, float x, float y, String texto, Color corBase, Color corTexto, float scale, boolean pressionado) {
        sr.begin(ShapeType.Filled);
        sr.setColor(new Color(0f, 0f, 0f, 0.6f));
        sr.rect(x + 6f, y - 6f, BOTAO_LARGURA, BOTAO_ALTURA);
        sr.setColor(pressionado ? corBase.cpy().mul(1.2f) : corBase);
        sr.rect(x, y, BOTAO_LARGURA, BOTAO_ALTURA);
        sr.end();
        sr.begin(ShapeType.Line);
        sr.setColor(Color.WHITE);
        sr.rect(x, y, BOTAO_LARGURA, BOTAO_ALTURA);
        sr.end();
        batch.begin();
        BitmapFont botFont = (font != null) ? font : titleFont;
        botFont.setColor(corTexto);
        botFont.getData().setScale(scale);
        GlyphLayout layout = new GlyphLayout(botFont, texto);
        float xBot = x + (BOTAO_LARGURA - layout.width)/2f;
        float yBot = y + (BOTAO_ALTURA + layout.height)/2f - 6f;
        botFont.draw(batch, texto, xBot, yBot);
        botFont.getData().setScale(1.0f);
        batch.end();
    }

    @Override
    public void render(float delta) {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        Gdx.gl.glClearColor(0.06f, 0.06f, 0.07f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
        if (!somTocado) {
            if (venceu && ganhouSom != null) ganhouSom.play();
            else if (!venceu && perdeuSom != null) perdeuSom.play();
            somTocado = true;
        }
        game.batch.begin();
        if (texturaBackground != null) game.batch.draw(texturaBackground, 0, 0, w, h);
        game.batch.end();

        // Painel e Stats (igual ao anterior)
        float panelW = Math.min(w * 0.75f, 900f);
        float panelH = Math.min(h * 0.62f, 600f);
        float panelX = (w - panelW) / 2f;
        float panelY = (h - panelH) / 2f;
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(new Color(0f, 0f, 0f, 0.78f));
        shapeRenderer.rect(panelX, panelY, panelW, panelH);
        shapeRenderer.setColor(messageColor.cpy().mul(0.9f));
        shapeRenderer.rect(panelX, panelY + panelH - 60f, panelW, 60f);
        shapeRenderer.end();
        game.batch.begin();
        titleFont.setColor(Color.WHITE);
        titleFont.getData().setScale(2.4f);
        GlyphLayout layoutTitle = new GlyphLayout(titleFont, message);
        titleFont.draw(game.batch, message, panelX + (panelW - layoutTitle.width) / 2f, panelY + panelH - 18f);
        String timeFormatted = String.format("%02d:%05.2f", (int)(finalTime/60), finalTime%60);
        String stats = String.format("Tempo: %s\nDistância: %.2f m\nCliques: %d\nVel. média: %.2f m/s", timeFormatted, finalDistance, finalPresses, avgSpeed);
        titleFont.getData().setScale(1.05f);
        titleFont.draw(game.batch, stats, panelX + (panelW - 300)/2f, panelY + panelH/2f + 50);
        game.batch.end();

        // Botões
        botaoX = panelX + (panelW - BOTAO_LARGURA) / 2f;
        botaoY = panelY + 40f;
        float touchX = Gdx.input.getX();
        float touchY = h - Gdx.input.getY();
        boolean tocando = Gdx.input.isTouched();
        boolean clicou = Gdx.input.justTouched();

        if (proximoNivelDisponivel) {
            botaoY -= (BOTAO_ALTURA + 15f);
            botaoX2 = botaoX;
            botaoY2 = botaoY + BOTAO_ALTURA + 15f;

            String textoProxNivel = "Iniciar Nível " + (nivelCompletado + 1);
            boolean press2 = tocando && touchX >= botaoX2 && touchX <= botaoX2+BOTAO_LARGURA && touchY >= botaoY2 && touchY <= botaoY2+BOTAO_ALTURA;
            desenharBotao(game.batch, shapeRenderer, botaoX2, botaoY2, textoProxNivel, corFundoProximoNivel, Color.WHITE, 1.0f, press2);

            if (clicou && press2) {
                finalizarTransicao();
                game.setScreen(new TelaJogo(game, nivelCompletado + 1)); // Avança nível
                dispose();
                return;
            }
        }

        String textoBotao1 = (venceu && nivelCompletado == 3) ? "Voltar ao Menu" : "Jogar Novamente";
        if (proximoNivelDisponivel) textoBotao1 = "Voltar ao Menu";

        boolean press1 = tocando && touchX >= botaoX && touchX <= botaoX+BOTAO_LARGURA && touchY >= botaoY && touchY <= botaoY+BOTAO_ALTURA;
        desenharBotao(game.batch, shapeRenderer, botaoX, botaoY, textoBotao1, corFundoNormal, Color.WHITE, 1.0f, press1);

        if (clicou && press1) {
            finalizarTransicao();
            game.setScreen(new TelaMenu(game));
            dispose();
        }
    }

    private void finalizarTransicao() {
        if (clickSound != null) clickSound.play(0.9f);
        if (ganhouSom != null) ganhouSom.stop();
        if (perdeuSom != null) perdeuSom.stop();
    }

    // ... (Resto dos métodos de override e dispose) ...
    @Override public void show() {}
    @Override public void resize(int width, int height) { camera.setToOrtho(false, width, height); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (font != null) font.dispose();
        if (titleFont != null) titleFont.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (texturaBackground != null) texturaBackground.dispose();
        if (clickSound != null) clickSound.dispose();
        if (ganhouSom != null) ganhouSom.dispose();
        if (perdeuSom != null) perdeuSom.dispose();
    }
}
