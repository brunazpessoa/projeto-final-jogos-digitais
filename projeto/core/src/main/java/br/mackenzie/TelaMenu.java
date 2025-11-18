package br.mackenzie;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class TelaMenu implements Screen {

    final JogoPrincipal game;
    OrthographicCamera camera;

    private Stage stage;
    private Skin skin;
    private FreeTypeFontGenerator generator;
    private BitmapFont fontTitulo;
    private BitmapFont fontBotao;
    private Texture texturaBackground;
    private Music menuMusic;
    private Sound clickSound;

    private static final String NOME_FONTE = "fonteMenu.ttf";
    private final float BOTAO_LARGURA = 350;
    private final float BOTAO_ALTURA = 80;

    public TelaMenu(final JogoPrincipal game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage = new Stage(new ScreenViewport(), game.batch);
        texturaBackground = new Texture(Gdx.files.internal("background.png"));

        try {
            menuMusic = Gdx.audio.newMusic(Gdx.files.internal("som_menu.mp3"));
            menuMusic.setLooping(true);
            menuMusic.setVolume(0.5f);
            if (Gdx.files.internal("click_botao_madeira.mp3").exists()) clickSound = Gdx.audio.newSound(Gdx.files.internal("click_botao_madeira.mp3"));
        } catch (Exception e) {}

        if (Gdx.files.internal(NOME_FONTE).exists()) generator = new FreeTypeFontGenerator(Gdx.files.internal(NOME_FONTE));

        FreeTypeFontParameter paramsTitulo = new FreeTypeFontParameter();
        paramsTitulo.size = 96;
        paramsTitulo.color = Color.WHITE;
        paramsTitulo.shadowColor = Color.BLACK;
        paramsTitulo.shadowOffsetX = 6;
        paramsTitulo.shadowOffsetY = 6;
        paramsTitulo.borderColor = Color.BLACK;
        paramsTitulo.borderWidth = 3f;

        FreeTypeFontParameter paramsBotao = new FreeTypeFontParameter();
        paramsBotao.size = 48;
        paramsBotao.color = Color.WHITE;
        paramsBotao.shadowColor = Color.BLACK;
        paramsBotao.shadowOffsetY = 3;

        if (generator != null) {
            fontTitulo = generator.generateFont(paramsTitulo);
            fontBotao = generator.generateFont(paramsBotao);
        } else {
            fontTitulo = new BitmapFont(); fontTitulo.getData().setScale(4.5f);
            fontBotao = new BitmapFont(); fontBotao.getData().setScale(2.0f);
        }

        skin = new Skin();
        skin.add("default-font", fontBotao, BitmapFont.class);
        if (Gdx.files.internal("white_pixel.png").exists()) skin.add("white", new Texture(Gdx.files.internal("white_pixel.png")));
        else {
            com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.WHITE); pixmap.fill();
            skin.add("white", new Texture(pixmap)); pixmap.dispose();
        }

        Color corFundoNormal = new Color(0.8f, 0.5f, 0.0f, 1.0f);
        Color corFundoPressionado = new Color(1.0f, 0.7f, 0.0f, 1.0f);
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = fontBotao;
        textButtonStyle.fontColor = Color.WHITE;
        textButtonStyle.up = skin.newDrawable("white", corFundoNormal);
        textButtonStyle.down = skin.newDrawable("white", corFundoPressionado);
        skin.add("default", textButtonStyle);

        TextButton botaoIniciar = new TextButton("Iniciar Corrida", skin);
        botaoIniciar.setSize(BOTAO_LARGURA, BOTAO_ALTURA);
        botaoIniciar.setPosition((Gdx.graphics.getWidth() - BOTAO_LARGURA) / 2, Gdx.graphics.getHeight() / 2 + 10);

        TextButton botaoSair = new TextButton("Sair do Jogo", skin);
        botaoSair.setSize(BOTAO_LARGURA, BOTAO_ALTURA);
        botaoSair.setPosition((Gdx.graphics.getWidth() - BOTAO_LARGURA) / 2, botaoIniciar.getY() - BOTAO_ALTURA - 20);

        botaoIniciar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(clickSound != null) clickSound.play();
                // Inicia Nível 1
                game.setScreen(new TelaJogo(game, 1));
                dispose();
            }
        });

        botaoSair.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(clickSound != null) clickSound.play();
                Gdx.app.exit();
            }
        });

        stage.addActor(botaoIniciar);
        stage.addActor(botaoSair);
    }

    @Override public void show() {
        Gdx.input.setInputProcessor(stage);
        if (menuMusic != null && !menuMusic.isPlaying()) menuMusic.play();
    }

    @Override public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.batch.draw(texturaBackground, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        String tituloTexto = "Pique-Forest";
        GlyphLayout layoutTitulo = new GlyphLayout(fontTitulo, tituloTexto);
        fontTitulo.draw(game.batch, tituloTexto, (Gdx.graphics.getWidth() - layoutTitulo.width) / 2, Gdx.graphics.getHeight() - 100);
        game.batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { camera.setToOrtho(false, width, height); stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() { Gdx.input.setInputProcessor(null); }

    @Override public void dispose() {
        fontTitulo.dispose(); fontBotao.dispose(); texturaBackground.dispose(); skin.dispose(); stage.dispose();
        if (generator != null) generator.dispose();
        if (menuMusic != null) { menuMusic.stop(); menuMusic.dispose(); }
        if (clickSound != null) clickSound.dispose();
    }
}
