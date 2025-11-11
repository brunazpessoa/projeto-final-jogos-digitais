package br.mackenzie;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class TelaMenu implements Screen {

    final JogoPrincipal game;
    OrthographicCamera camera;

    // Elementos de UI/Desenho
    private Stage stage;
    private Skin skin;

    // Gerador de fonte único
    private FreeTypeFontGenerator generator;

    // Fontes customizadas
    private BitmapFont fontTitulo;
    private BitmapFont fontBotao;
    private Texture texturaBackground;

    // Trilha sonora do Menu
    private Music menuMusic;

    // Nomes dos arquivos de fontes (AJUSTE ESTES NOMES se os seus forem diferentes)
    private static final String NOME_FONTE = "fonteMenu.ttf";

    // Dimensões do botão
    private final float BOTAO_LARGURA = 350;
    private final float BOTAO_ALTURA = 80;

    public TelaMenu(final JogoPrincipal game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // --- 1. Inicializa o Stage e Viewport ---
        stage = new Stage(new ScreenViewport(), game.batch);

        // --- 2. Carregamento de Texturas ---
        texturaBackground = new Texture(Gdx.files.internal("background.png"));

        //carregando a música
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("som_menu.mp3"));
        menuMusic.setLooping(true);  //repetição da música
        menuMusic.setVolume(0.5f);  //definindo volume



        // ====================================================================
        // GERAÇÃO DAS FONTES CUSTOMIZADAS (FreeType)
        // ====================================================================

        // 1. Inicializa o Gerador ÚNICO
        if (Gdx.files.internal(NOME_FONTE).exists()) {
            generator = new FreeTypeFontGenerator(Gdx.files.internal(NOME_FONTE));
        }


        // --- Configurações do Título ---
        FreeTypeFontParameter paramsTitulo = new FreeTypeFontParameter();
        paramsTitulo.size = 96; // AJUSTADO: Tamanho do título aumentado para 96px
        paramsTitulo.color = Color.WHITE;
        paramsTitulo.shadowColor = Color.BLACK;
        paramsTitulo.shadowOffsetX = 6; // Ajustando sombra para o tamanho maior
        paramsTitulo.shadowOffsetY = 6;
        paramsTitulo.borderColor = Color.BLACK;
        paramsTitulo.borderWidth = 3f; // Ajustando borda

        // --- Configurações do Botão ---
        FreeTypeFontParameter paramsBotao = new FreeTypeFontParameter();
        paramsBotao.size = 48;
        paramsBotao.color = Color.WHITE;
        paramsBotao.shadowColor = Color.BLACK;
        paramsBotao.shadowOffsetY = 3;


        // 3. GERA AS BITMAPFONTS e Trata Fallbacks

        if (generator != null) {
            fontTitulo = generator.generateFont(paramsTitulo);
            fontBotao = generator.generateFont(paramsBotao);
        } else {
            // Fallbacks (ajustando a escala para compensar a falta de FreeType)
            fontTitulo = new BitmapFont();
            fontTitulo.getData().setScale(4.5f);
            fontBotao = new BitmapFont();
            fontBotao.getData().setScale(2.0f);
        }

        // ====================================================================
        // FIM DA GERAÇÃO
        // ====================================================================

        // --- 4. Configuração do Skin (Estilo do Botão) ---
        skin = new Skin();
        skin.add("default-font", fontBotao, BitmapFont.class);

        // Nota: O "white_pixel.png" deve existir na assets
        skin.add("white", new Texture(Gdx.files.internal("white_pixel.png")));

        // Estilos de Cores
        Color corFundoNormal = new Color(0.8f, 0.5f, 0.0f, 1.0f);
        Color corFundoPressionado = new Color(1.0f, 0.7f, 0.0f, 1.0f);

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = fontBotao;
        textButtonStyle.fontColor = Color.WHITE;

        // Estilo normal (Amarelo Escuro OPACO)
        textButtonStyle.up = skin.newDrawable("white", corFundoNormal);
        // Estilo pressionado (Amarelo Claro OPACO)
        textButtonStyle.down = skin.newDrawable("white", corFundoPressionado);

        skin.add("default", textButtonStyle);

        // --- 5. Criação do Botão ---
        TextButton botaoIniciar = new TextButton("iniciar corrida" +
            "", skin);

        // Usa as dimensões ajustadas
        botaoIniciar.setSize(BOTAO_LARGURA, BOTAO_ALTURA);

        // Centraliza o botão
        botaoIniciar.setPosition((Gdx.graphics.getWidth() - BOTAO_LARGURA) / 2,
            Gdx.graphics.getHeight() / 2 + 10); // Posição ligeiramente acima do centro

        // --- 6. Criação do Botão SAIR ---
        TextButton botaoSair = new TextButton("sair do jogo", skin);
        botaoSair.setSize(BOTAO_LARGURA, BOTAO_ALTURA);
        botaoSair.setPosition((Gdx.graphics.getWidth() - BOTAO_LARGURA) / 2,
            botaoIniciar.getY() - BOTAO_ALTURA - 20);


        // --- 7. Adiciona Listeners ---
        botaoIniciar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new TelaJogo(game));
                dispose();
            }
        });

        botaoSair.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit(); // Fecha a aplicação
            }
        });

        stage.addActor(botaoIniciar);
        stage.addActor(botaoSair);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        if (menuMusic != null && !menuMusic.isPlaying()) { //toca a musica quando a tela MENU aparece
            menuMusic.play();
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();

        // 1. Desenha o Background
        game.batch.draw(texturaBackground, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // 2. Desenha o Título customizado
        String tituloTexto = "Pique-Forest";
        GlyphLayout layoutTitulo = new GlyphLayout(fontTitulo, tituloTexto);
        float xTitulo = (Gdx.graphics.getWidth() - layoutTitulo.width) / 2;
        float yTitulo = Gdx.graphics.getHeight() - 100; // Posição ajustada para o tamanho maior

        fontTitulo.draw(game.batch, tituloTexto, xTitulo, yTitulo);

        game.batch.end();

        // 3. Atualiza e Desenha o Stage (Botão)
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        stage.getViewport().update(width, height, true);
    }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        fontTitulo.dispose();
        fontBotao.dispose();
        texturaBackground.dispose();
        skin.dispose();
        stage.dispose();

        if (generator != null) {
            generator.dispose();
        }

        if(menuMusic != null) {
            menuMusic.stop(); //para a musica
            menuMusic.dispose(); //toca
        }
    }
}
