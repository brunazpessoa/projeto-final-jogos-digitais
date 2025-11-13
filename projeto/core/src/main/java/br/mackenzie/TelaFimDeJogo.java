package br.mackenzie;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class TelaFimDeJogo implements Screen {

    final JogoPrincipal game;
    OrthographicCamera camera;
    BitmapFont font;         // usada para estatísticas e instrução (fonte da tela menu)
    BitmapFont titleFont;    // usada somente para o título (fonte padrão libgdx)
    String message;
    Color messageColor;

    // Fundo e Fonte (iguais ao da TelaMenu)
    private Texture texturaBackground;
    private static final String NOME_FONTE = "fonteMenu.ttf";

    // ATRIBUTOS DE ESTATÍSTICAS
    private float finalTime;
    private float finalDistance; // Já armazenada em metros
    private int finalPresses;
    private float avgSpeed;

    // Constante de conversão
    private final float UNITS_PER_METER = 100f;

    // ShapeRenderer para desenhar o painel de fundo
    private ShapeRenderer shapeRenderer;

    // Construtor que recebe o estado final e as estatísticas
    public TelaFimDeJogo(final JogoPrincipal game, EstadoJogo estadoFinal, float time, float distance, int presses) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Inicializa ShapeRenderer
        shapeRenderer = new ShapeRenderer();

        // Carrega textura de background de forma segura (se não existir, deixa null)
        if (Gdx.files.internal("background.png").exists()) {
            texturaBackground = new Texture(Gdx.files.internal("background.png"));
        } else {
            texturaBackground = null; // fallback: sem background
        }

        // --- Carrega fonte TTF (nova fonte) para o corpo do texto ---
        if (Gdx.files.internal(NOME_FONTE).exists()) {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(NOME_FONTE));
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

            // tamanho base adaptativo para ficar legível em diferentes resoluções
            int baseSize = Math.max(20, Gdx.graphics.getWidth() / 40);
            parameter.size = baseSize;
            parameter.color = Color.WHITE;
            parameter.shadowOffsetX = 2;
            parameter.shadowOffsetY = 2;
            parameter.shadowColor = new Color(0, 0, 0, 0.6f);

            font = generator.generateFont(parameter);
            generator.dispose();
        } else {
            // fallback: fonte padrão (se não tiver TTF)
            font = new BitmapFont();
            font.getData().setScale(1f);
        }

        // titulo - formatação
        titleFont = new BitmapFont();    // fonte padrão
        titleFont.getData().setScale(3.2f);    // escala do titulo

        // Armazena e calcula as estatísticas
        this.finalTime = time;
        // CONVERSÃO: Distância em metros
        this.finalDistance = distance / UNITS_PER_METER;
        this.finalPresses = presses;

        // CONVERSÃO: Velocidade Média em metros/s
        this.avgSpeed = (time > 0) ? this.finalDistance / time : 0;

        // Define a mensagem e a cor com base no estado final
        if (estadoFinal == EstadoJogo.VENCEU) {
            message = "VITÓRIA! Você pegou a comida!";
            messageColor = Color.GREEN;
        } else {
            message = "FIM DE JOGO. A raposa te pegou.";
            messageColor = Color.RED;
        }
    }

    @Override
    public void render(float delta) {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        Gdx.gl.glClearColor(0.06f, 0.06f, 0.07f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        // desenha background
        game.batch.begin();
        if (texturaBackground != null) {
            game.batch.draw(texturaBackground, 0, 0, w, h);
        }
        game.batch.end();

        // Parâmetros do painel
        float panelW = Math.min(w * 0.75f, 720f);
        float panelH = Math.min(h * 0.6f, 420f);
        float panelX = (w - panelW) / 2f;
        float panelY = (h - panelH) / 2f;

        // Desenha painel e borda
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(new Color(0f, 0f, 0f, 0.72f)); // fundo do painel
        shapeRenderer.rect(panelX, panelY, panelW, panelH);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(panelX + 2f, panelY + 2f, panelW - 4f, panelH - 4f); // borda fina
        shapeRenderer.end();

        // Começa a desenhar texto
        game.batch.begin();

        // título
        titleFont.setColor(messageColor);
        // Ajuste dinâmico da escala do título para caber dentro do painel, se necessário
        float desiredTitleScale = 3.2f;
        titleFont.getData().setScale(desiredTitleScale);
        GlyphLayout layoutTitle = new GlyphLayout(titleFont, message);

        // se título maior que painel (com margens), reduz proporcionalmente
        float maxTitleWidth = panelW - 40f; // margem interna
        if (layoutTitle.width > maxTitleWidth) {
            float scaleFactor = maxTitleWidth / layoutTitle.width;
            titleFont.getData().setScale(desiredTitleScale * scaleFactor); // ajuste para caber
            layoutTitle = new GlyphLayout(titleFont, message);             // recomputa layout
        }

        // Centraliza o título dentro do painel
        float xTitle = panelX + (panelW - layoutTitle.width) / 2f; //  centralizado no painel
        float yTitle = panelY + panelH - 40f;
        titleFont.draw(game.batch, message, xTitle, yTitle);

        // ESTATÍSTICAS
        // usa 'font' (TTF carregada) para estatísticas
        font.setColor(Color.WHITE);
        float statsScale = 1.2f;
        font.getData().setScale(statsScale);
        // Formata tempo como mm:ss.xx
        int minutes = (int) (finalTime / 60);
        float secondsFloat = finalTime - minutes * 60;
        String timeFormatted = String.format("%02d:%05.2f", minutes, secondsFloat);

        String stats = String.format(
            "Estatísticas da Corrida:\n" +
                "  Tempo Gasto: %s (mm:ss)\n" +
                "  Distância Percorrida: %.2f m\n" +
                "  Cliques na Barra de Espaço: %d\n" +
                "  Média de Velocidade: %.2f m/s",
            timeFormatted, finalDistance, finalPresses, avgSpeed
        );

        GlyphLayout layoutStats = new GlyphLayout(font, stats);

        // Se as estatísticas excederem a largura do painel, reduz a escala proporcionalmente
        float maxStatsWidth = panelW - 60f;
        if (layoutStats.width > maxStatsWidth) {
            float scaleFactor = maxStatsWidth / layoutStats.width;
            font.getData().setScale(statsScale * scaleFactor); // ajuste para caber
            layoutStats = new GlyphLayout(font, stats);        // recomputa layout
        }

        // centraliza bloco de estatísticas dentro do painel, abaixo do título
        float xStats = panelX + (panelW - layoutStats.width) / 2f; // centralizado no painel
        float yStats = yTitle - layoutTitle.height - 20f; // espaço entre título e stats
        font.draw(game.batch, stats, xStats, yStats);

        // --- INSTRUÇÃO ---
        font.getData().setScale(1.0f);
        font.setColor(Color.YELLOW);
        String instr = "Toque/Clique para voltar ao Menu";
        GlyphLayout layoutInstr = new GlyphLayout(font, instr);
        // centraliza instrução na parte inferior do painel
        float xInstr = panelX + (panelW - layoutInstr.width) / 2f; // <-- centralizado no painel
        float yInstr = panelY + 30f + layoutInstr.height;
        font.draw(game.batch, instr, xInstr, yInstr);

        game.batch.end();

        // Lógica para voltar ao Menu Principal
        if (Gdx.input.justTouched()) {
            game.setScreen(new TelaMenu(game));
            dispose();
        }
    }

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
    }
}
