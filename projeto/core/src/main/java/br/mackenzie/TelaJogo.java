package br.mackenzie;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.MathUtils;

public class TelaJogo implements Screen {

    final JogoPrincipal game;

    private final float UNIDADES_PER_METER = 100f;
    private float DISTANCIA_VITORIA_METROS;
    private final float DISTANCIA_VITORIA;

    private int currentLevel;

    public TelaJogo(final JogoPrincipal game, int level) {
        this.game = game;
        this.currentLevel = level;

        // Configuração de Distância
        if (level == 3) {
            this.DISTANCIA_VITORIA_METROS = 500f; // Sprint Final (Difícil)
        } else if (level == 2) {
            this.DISTANCIA_VITORIA_METROS = 1000f; // Maratona
        } else {
            this.DISTANCIA_VITORIA_METROS = 300f; // Tutorial
        }

        this.DISTANCIA_VITORIA = DISTANCIA_VITORIA_METROS * UNIDADES_PER_METER;
    }

    // Atributos
    private Jogador jogador;
    private Raposa raposa;
    private Cesta cesta;
    private FitViewport viewport;
    private OrthographicCamera camera;
    private BitmapFont fontHUD;
    private Texture[] layers;
    private float[] parallaxSpeeds;
    private final int NUM_LAYERS = 7;
    private Sound passoSound;
    private Music gameMusic;
    private Sound somOnca;
    private Sound somOnca2;
    private final float MIN_VELOCIDADE_SOM = 10f;
    private final float INTERVALO_PASSO = 0.35f;
    private float stepTimer = 0f;
    private float distanciaUltimoRugido = 0f;
    private float proximoRugidoDistancia = 0f;
    private float tempoJogo = 0f;
    private int cliquesBarraEspaco = 0;
    private final float larguraMundo = 1280f;
    private final float alturaMundo = 720f;
    private float posicaoInicialX;
    private final float TAMANHO_JOGADOR = 150f;
    private final float TAMANHO_RAPOSA_FATOR = 1.6f;
    private final float CHAO_Y = 100f;
    private final float RESET_THRESHOLD = 100000f;
    private final float RESET_AMOUNT = 90000f;
    EstadoJogo estado = EstadoJogo.CORRENDO;

    @Override
    public void show() {
        camera = new OrthographicCamera(larguraMundo, alturaMundo);
        viewport = new FitViewport(larguraMundo, alturaMundo, camera);
        viewport.apply();

        layers = new Texture[NUM_LAYERS];
        for (int i = 0; i < NUM_LAYERS; i++) {
            layers[i] = new Texture(Gdx.files.internal("Fundo" + (i + 1) + ".png"));
            layers[i].setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        }

        parallaxSpeeds = new float[] { 0.2f, 0.4f, 0.3f, 0.5f, 0.7f, 0.05f, 1.0f };

        posicaoInicialX = larguraMundo / 2f;
        final float TAMANHO_RAPOSA = TAMANHO_JOGADOR * TAMANHO_RAPOSA_FATOR;

        // CONFIGURAÇÃO DE DIFICULDADE DO JOGADOR (DESACELERAÇÃO)
        float desaceleracaoJogador;
        if (currentLevel == 3) {
            desaceleracaoJogador = 5.5f; // MUITO DIFÍCIL no Nível 3
        } else {
            desaceleracaoJogador = 2.5f; // Padrão
        }

        // Instancia jogador com a desaceleração correta
        jogador = new Jogador(posicaoInicialX, CHAO_Y, TAMANHO_JOGADOR, larguraMundo, desaceleracaoJogador);
        raposa = new Raposa(TAMANHO_RAPOSA);
        cesta = new Cesta(TAMANHO_JOGADOR, CHAO_Y);

        raposa.x = jogador.x - (larguraMundo * 0.4f);
        raposa.y = CHAO_Y;
        raposa.ativo = true;

        setupFont();

        try { passoSound = Gdx.audio.newSound(Gdx.files.internal("passos.mp3")); } catch (Exception e) {}
        try {
            gameMusic = Gdx.audio.newMusic(Gdx.files.internal("perseguicao.mp3"));
            gameMusic.setLooping(true);
            gameMusic.setVolume(0.9f);
            gameMusic.play();
        } catch (Exception e) {}

        try {
            somOnca = Gdx.audio.newSound(Gdx.files.internal("somOnca.mp3"));
            somOnca2 = Gdx.audio.newSound(Gdx.files.internal("somOnca2.mp3"));

            // CONFIGURAÇÃO DE DIFICULDADE DE SOM (FREQUÊNCIA)
            if (currentLevel == 3) {
                proximoRugidoDistancia = MathUtils.random(15f, 30f); // Rugido muito frequente
            } else {
                proximoRugidoDistancia = MathUtils.random(50f, 70f); // Padrão
            }
        } catch (Exception e) {}

        Gdx.input.setInputProcessor(null);
    }

    private void setupFont() {
        String NOME_FONTE = "fonteMenu.ttf";
        FreeTypeFontGenerator generator = null;
        if (Gdx.files.internal(NOME_FONTE).exists()) {
            generator = new FreeTypeFontGenerator(Gdx.files.internal(NOME_FONTE));
        }
        FreeTypeFontParameter params = new FreeTypeFontParameter();
        params.size = 30;
        params.color = Color.WHITE;
        params.shadowColor = Color.BLACK;
        params.shadowOffsetX = 2;
        params.shadowOffsetY = 2;
        if (generator != null) {
            fontHUD = generator.generateFont(params);
            generator.dispose();
        } else {
            fontHUD = new BitmapFont();
            fontHUD.getData().setScale(1.0f);
        }
    }

    @Override
    public void render(float delta) {
        if (estado == EstadoJogo.CORRENDO) {

            if (Gdx.input.isKeyJustPressed(Input.Keys.P) && !cesta.ativo) {
                jogador.x = DISTANCIA_VITORIA;
                Gdx.app.log("DevTool", "Cesta ativada manualmente (Tecla P).");
            }

            tempoJogo += delta;
            jogador.inputClique();
            cliquesBarraEspaco = jogador.cliquesBarraEspaco;

            jogador.atualizar(delta);
            if (raposa.ativo) raposa.atualizar(delta, jogador.x, camera, viewport);

            checarColisoes();
            checarVitoria();

            if (jogador.x > RESET_THRESHOLD) {
                final float deltaReset = RESET_AMOUNT;
                jogador.x -= deltaReset;
                raposa.x -= deltaReset;
                cesta.x -= deltaReset;
                posicaoInicialX -= deltaReset;
            }

            if (passoSound != null) {
                if (jogador.velocidadeAtual > MIN_VELOCIDADE_SOM) {
                    stepTimer += delta;
                    if (stepTimer >= INTERVALO_PASSO) {
                        passoSound.play(0.4f, 1.0f, 0f);
                        stepTimer = 0f;
                    }
                } else {
                    stepTimer = 0f;
                }
            }

            if (somOnca != null && somOnca2 != null && raposa.ativo) {
                float distanciaPercorridaMetros = (jogador.x - posicaoInicialX) / UNIDADES_PER_METER;

                if ((distanciaPercorridaMetros - distanciaUltimoRugido) >= proximoRugidoDistancia) {
                    if (MathUtils.randomBoolean()) somOnca.play(1.0f);
                    else somOnca2.play(1.0f);

                    distanciaUltimoRugido = distanciaPercorridaMetros;

                    // RECALCULA PRÓXIMO RUGIDO BASEADO NO NÍVEL
                    if (currentLevel == 3) {
                        proximoRugidoDistancia = MathUtils.random(15f, 30f);
                    } else {
                        proximoRugidoDistancia = MathUtils.random(50f, 70f);
                    }
                }
            }

            camera.position.x = jogador.x + (jogador.tamanho / 2f);
            camera.position.y = alturaMundo / 2f;
            camera.update();

        } else if (estado == EstadoJogo.FIM_DE_JOGO || estado == EstadoJogo.VENCEU) {
            if (passoSound != null) passoSound.stop();
            if (gameMusic != null) gameMusic.stop();
            navegarParaFimDeJogo(estado);
        }

        desenhar();
    }

    private void checarColisoes() {
        if (raposa.ativo && jogador.limites.overlaps(raposa.limites)) {
            navegarParaFimDeJogo(EstadoJogo.FIM_DE_JOGO);
        }
    }

    private void checarVitoria() {
        float distanciaAtual = jogador.x - posicaoInicialX;
        cesta.checarSpawn(jogador.x, jogador.tamanho, distanciaAtual, DISTANCIA_VITORIA);

        if (cesta.ativo && jogador.limites.overlaps(cesta.limites)) {
            navegarParaFimDeJogo(EstadoJogo.VENCEU);
        }
    }

    private void navegarParaFimDeJogo(EstadoJogo estadoFinal) {
        estado = estadoFinal;
        if (passoSound != null) passoSound.stop();
        if (gameMusic != null) gameMusic.stop();

        float distanciaPercorrida = jogador.x - posicaoInicialX;

        // Passa o Nível Atual
        game.setScreen(new TelaFimDeJogo(game, estadoFinal, tempoJogo, distanciaPercorrida, cliquesBarraEspaco, currentLevel));
        dispose();
    }

    private void desenhar() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();

        for (int i = 0; i < NUM_LAYERS - 1; i++) {
            Texture layer = layers[i];
            float parallaxSpeed = parallaxSpeeds[i];
            float scaledHeight = viewport.getWorldHeight();
            float aspectRatio = (float)layer.getWidth() / layer.getHeight();
            float scaledWidth = scaledHeight * aspectRatio;
            float parallaxOffset = camera.position.x * parallaxSpeed;
            float offsetModulo = parallaxOffset % scaledWidth;
            if (offsetModulo < 0) offsetModulo += scaledWidth;
            float startX = camera.position.x - viewport.getWorldWidth() / 2;
            startX -= offsetModulo;
            for (int j = -1; j <= 2; j++) {
                game.batch.draw(layer, startX + (j * scaledWidth), 0, scaledWidth, scaledHeight);
            }
        }

        if (raposa.ativo) game.batch.draw(raposa.frameAtual, raposa.x, raposa.y, raposa.tamanho, raposa.tamanho);
        if (cesta.ativo) game.batch.draw(cesta.textura, cesta.x, cesta.y, cesta.tamanho, cesta.tamanho);
        game.batch.draw(jogador.frameAtual, jogador.x, jogador.y, jogador.tamanho, jogador.tamanho);

        {
            int i = NUM_LAYERS - 1;
            Texture layer = layers[i];
            float parallaxSpeed = parallaxSpeeds[i];
            float scaledHeight = viewport.getWorldHeight();
            float aspectRatio = (float)layer.getWidth() / layer.getHeight();
            float scaledWidth = scaledHeight * aspectRatio;
            float parallaxOffset = camera.position.x * parallaxSpeed;
            float offsetModulo = parallaxOffset % scaledWidth;
            if (offsetModulo < 0) offsetModulo += scaledWidth;
            float startX = camera.position.x - viewport.getWorldWidth() / 2;
            startX -= offsetModulo;
            for (int j = -1; j <= 2; j++) {
                game.batch.draw(layer, startX + (j * scaledWidth), 0, scaledWidth, scaledHeight);
            }
        }

        float distanciaAtualMetros = (jogador.x - posicaoInicialX) / UNIDADES_PER_METER;
        String hudTexto = String.format("Nível %d | Distância: %.0f / %.0f m", currentLevel, distanciaAtualMetros, DISTANCIA_VITORIA_METROS);
        float hudX = camera.position.x - viewport.getWorldWidth() / 2 + 20;
        float hudY = camera.position.y + viewport.getWorldHeight() / 2 - 20;
        fontHUD.draw(game.batch, hudTexto, hudX, hudY);

        game.batch.end();
    }

    @Override public void resize(int width, int height) { viewport.update(width, height); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        jogador.dispose();
        raposa.dispose();
        cesta.dispose();
        for (Texture layer : layers) { if (layer != null) layer.dispose(); }
        if (fontHUD != null) fontHUD.dispose();
        if (passoSound != null) passoSound.dispose();
        if (gameMusic != null) gameMusic.dispose();
        if (somOnca != null) somOnca.dispose();
        if (somOnca2 != null) somOnca2.dispose();
    }
}
