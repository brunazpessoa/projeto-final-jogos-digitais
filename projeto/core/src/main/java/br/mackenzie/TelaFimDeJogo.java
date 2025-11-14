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

/**
 * Tela de Game Over / Vitória.
 */
public class TelaFimDeJogo implements Screen {

    // Referências principais
    final JogoPrincipal game;
    OrthographicCamera camera;

    // Fontes e imagens
    BitmapFont font;         // usada apenas para o botão (TTF, se existir)
    BitmapFont titleFont;    // usada para título e estatísticas (Bitmap padrão)
    private Texture texturaBackground;
    private static final String NOME_FONTE = "fonteMenu.ttf";
    String message;
    Color messageColor;

    // Sons
    private Music perdeuSom;
    private Music ganhouSom;
    private Sound clickSound;

    // Estética do botão
    Color corFundoNormal = new Color(0.8f, 0.5f, 0.0f, 1.0f);
    Color corFundoPressionado = new Color(1.0f, 0.7f, 0.0f, 1.0f);
    private final float BOTAO_LARGURA = 350;
    private final float BOTAO_ALTURA = 80;

    // coordenadas do botão (calculadas no render)
    private float botaoX, botaoY;

    // Estatísticas do jogo
    private float finalTime;
    private float finalDistance; // Já armazenada em metros
    private int finalPresses;
    private float avgSpeed;

    // Constante de conversão
    private final float UNITS_PER_METER = 100f;

    // Desenho do painel
    private ShapeRenderer shapeRenderer;

    // Controle de som e estado
    private boolean somTocado = false;
    private boolean venceu = false;

    // Construtor
    public TelaFimDeJogo(final JogoPrincipal game, EstadoJogo estadoFinal, float time, float distance, int presses) {
        this.game = game;

        // câmera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // ShapeRenderer
        shapeRenderer = new ShapeRenderer();

        // carrega background
        if (Gdx.files.internal("background.png").exists()) {
            texturaBackground = new Texture(Gdx.files.internal("background.png"));
        } else {
            texturaBackground = null;
        }

        // click curto (som do botão)
        if (Gdx.files.internal("click_botao_madeira.mp3").exists()) {
            clickSound = Gdx.audio.newSound(Gdx.files.internal("click_botao_madeira.mp3"));
        } else {
            clickSound = null;
        }

        // sons de vitória/derrota
        if (Gdx.files.internal("perdeu.mp3").exists()) {
            perdeuSom = Gdx.audio.newMusic(Gdx.files.internal("perdeu.mp3"));
        } else {
            perdeuSom = null;
        }
        if (Gdx.files.internal("ganhou.mp3").exists()) {
            ganhouSom = Gdx.audio.newMusic(Gdx.files.internal("ganhou.mp3"));
        } else {
            ganhouSom = null;
        }

        // Carrega TTF apenas para o botão (se existir)
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

        // título e estatísticas: fonte bitmap padrão
        titleFont = new BitmapFont();
        titleFont.getData().setScale(3.2f);

        // estatísticas
        this.finalTime = time;
        this.finalDistance = distance / UNITS_PER_METER;
        this.finalPresses = presses;
        this.avgSpeed = (time > 0) ? this.finalDistance / time : 0f;

        // mensagem e cor
        if (estadoFinal == EstadoJogo.VENCEU) {
            this.message = "VITORIA! Voce pegou a comida!";
            this.messageColor = Color.GREEN;
            venceu = true;
        } else {
            this.message = "FIM DE JOGO. A raposa te pegou.";
            this.messageColor = Color.RED;
            venceu = false;
        }

        somTocado = false;
    }

    @Override
    public void render(float delta) {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        // limpa a tela
        Gdx.gl.glClearColor(0.06f, 0.06f, 0.07f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        // toca som de vitória/derrota apenas uma vez
        if (!somTocado) {
            if (venceu) {
                if (ganhouSom != null) {
                    ganhouSom.setVolume(0.8f);
                    ganhouSom.setLooping(false);
                    ganhouSom.play();
                }
            } else {
                if (perdeuSom != null) {
                    perdeuSom.setVolume(0.8f);
                    perdeuSom.setLooping(false);
                    perdeuSom.play();
                }
            }
            somTocado = true;
        }

        // desenha background
        game.batch.begin();
        if (texturaBackground != null) {
            game.batch.draw(texturaBackground, 0, 0, w, h);
        }
        game.batch.end();

        // dimensões do painel
        float panelW = Math.min(w * 0.75f, 900f);
        float panelH = Math.min(h * 0.62f, 600f);
        float panelX = (w - panelW) / 2f;
        float panelY = (h - panelH) / 2f;

        // sombra do painel
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(new Color(0f, 0f, 0f, 0.55f));
        shapeRenderer.rect(panelX + 10f, panelY - 10f, panelW, panelH);
        shapeRenderer.end();

        // painel principal
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(new Color(0f, 0f, 0f, 0.78f));
        shapeRenderer.rect(panelX, panelY, panelW, panelH);
        shapeRenderer.end();

        // barra superior colorida
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(messageColor.cpy().mul(0.9f));
        shapeRenderer.rect(panelX, panelY + panelH - 60f, panelW, 60f);
        shapeRenderer.end();

        // borda do painel
        shapeRenderer.begin(ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(panelX + 2f, panelY + 2f, panelW - 4f, panelH - 4f);
        shapeRenderer.end();

        //Desenha título e estatísticas
        game.batch.begin();

        // título (na barra)
        titleFont.setColor(Color.WHITE);
        titleFont.getData().setScale(2.4f);
        GlyphLayout layoutTitle = new GlyphLayout(titleFont, message);
        float maxTitleWidth = panelW - 40f;
        if (layoutTitle.width > maxTitleWidth) {
            float sf = maxTitleWidth / layoutTitle.width;
            titleFont.getData().setScale(2.4f * sf);
            layoutTitle.setText(titleFont, message);
        }
        float xTitle = panelX + (panelW - layoutTitle.width) / 2f;
        float yTitle = panelY + panelH - 18f; // posição na barra
        titleFont.draw(game.batch, message, xTitle, yTitle);

        // calcula onde ficará o botão (antes de desenhar as estatísticas, pois o layout depende da posição do botão)
        botaoX = panelX + (panelW - BOTAO_LARGURA) / 2f;
        botaoY = panelY + 40f;

        // Estatísticas: criamos um bloco e centralizamos verticalmente entre o final do título e o topo do botão
        String timeFormatted = formatTempo(finalTime);
        String stats = String.format(
            "Tempo: %s\nDistância: %.2f m\nCliques: %d\nVel. média: %.2f m/s",
            timeFormatted, finalDistance, finalPresses, avgSpeed
        );

        // escala inicial para stats
        float statsScale = 1.05f;
        titleFont.getData().setScale(statsScale);
        titleFont.setColor(Color.LIGHT_GRAY);
        GlyphLayout layoutStats = new GlyphLayout(titleFont, stats);

        // área vertical disponível entre o fim do título e o topo do botão
        float titleBottom = yTitle - layoutTitle.height;               // baseline do título menos sua altura
        float buttonTop = botaoY + BOTAO_ALTURA;                       // topo do botão
        float availableTop = titleBottom - 12f;                        // pequeno espaçamento
        float availableBottom = buttonTop + 12f;                       // pequeno espaçamento
        float availableHeight = availableTop - availableBottom;        // altura disponível para o bloco de stats

        // Se o layoutStats for maior que a área disponível, reduza a escala proporcionalmente
        if (layoutStats.height > availableHeight && availableHeight > 8f) {
            float scaleFactor = availableHeight / layoutStats.height;
            statsScale *= Math.max(0.5f, scaleFactor); // não reduzir demais (< 0.5)
            titleFont.getData().setScale(statsScale);
            layoutStats.setText(titleFont, stats);
        }

        // agora centraliza verticalmente o bloco de estatísticas dentro da área disponível
        float centerY = availableBottom + (availableHeight / 2f) + (layoutStats.height / 2f);
        float xStats = panelX + (panelW - layoutStats.width) / 2f;
        float yStats = centerY;

        // desenha as estatísticas
        titleFont.draw(game.batch, stats, xStats, yStats);

        game.batch.end();

        // Desenha botão (com feedback visual)
        // Detecta se há toque/pressionamento dentro do botão para desenhar cor de pressionado
        boolean pressionandoBotao = false;
        if (Gdx.input.isTouched()) {
            // converte coordenadas do touch
            float tx = Gdx.input.getX();
            float ty = h - Gdx.input.getY();
            if (tx >= botaoX && tx <= botaoX + BOTAO_LARGURA && ty >= botaoY && ty <= botaoY + BOTAO_ALTURA) {
                pressionandoBotao = true;
            }
        }

        // desenha sombra e retângulo do botão
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(new Color(0f, 0f, 0f, 0.6f));
        shapeRenderer.rect(botaoX + 6f, botaoY - 6f, BOTAO_LARGURA, BOTAO_ALTURA);
        shapeRenderer.setColor(pressionandoBotao ? corFundoPressionado : corFundoNormal);
        shapeRenderer.rect(botaoX, botaoY, BOTAO_LARGURA, BOTAO_ALTURA);
        shapeRenderer.end();

        // borda do botão
        shapeRenderer.begin(ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(botaoX, botaoY, BOTAO_LARGURA, BOTAO_ALTURA);
        shapeRenderer.end();

        // texto do botão
        game.batch.begin();
        String botaoTexto = "Clique aqui para voltar ao Menu";
        BitmapFont botFont = (font != null) ? font : titleFont;
        botFont.setColor(Color.WHITE);

        // Ajusta escala do texto do botão para caber
        float desiredBotScale = 1.0f;
        botFont.getData().setScale(desiredBotScale);
        GlyphLayout layoutBot = new GlyphLayout(botFont, botaoTexto);
        float maxBotWidth = BOTAO_LARGURA - 24f;
        if (layoutBot.width > maxBotWidth) {
            float sf = maxBotWidth / layoutBot.width;
            botFont.getData().setScale(desiredBotScale * sf);
            layoutBot.setText(botFont, botaoTexto);
        }

        float xBot = botaoX + (BOTAO_LARGURA - layoutBot.width) / 2f;
        float yBot = botaoY + (BOTAO_ALTURA + layoutBot.height) / 2f - 6f;
        botFont.draw(game.batch, botaoTexto, xBot, yBot);
        game.batch.end();

        //Input: clique final (justTouched) para executar ação
        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = h - Gdx.input.getY();

            if (touchX >= botaoX && touchX <= botaoX + BOTAO_LARGURA
                && touchY >= botaoY && touchY <= botaoY + BOTAO_ALTURA) {

                // som de clique curto
                if (clickSound != null) clickSound.play(0.9f);

                // para músicas ativas (opcional)
                if (ganhouSom != null && ganhouSom.isPlaying()) ganhouSom.stop();
                if (perdeuSom != null && perdeuSom.isPlaying()) perdeuSom.stop();

                // volta ao menu
                game.setScreen(new TelaMenu(game));
                dispose();
            }
        }
    }

    // util: formata segundos para mm:ss.xx
    private String formatTempo(float segundos) {
        int minutes = (int) (segundos / 60f);
        float secs = segundos - minutes * 60;
        return String.format("%02d:%05.2f", minutes, secs);
    }

    // lifecycle mínimos
    @Override public void show() {}
    @Override public void resize(int width, int height) { camera.setToOrtho(false, width, height); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    // limpeza segura de recursos
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
