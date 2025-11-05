package br.mackenzie;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.GL20;

public class TelaJogo implements Screen {

    final JogoPrincipal game;

    public TelaJogo(final JogoPrincipal game) {
        this.game = game;
        this.DISTANCIA_VITORIA = DISTANCIA_VITORIA_METROS * UNIDADES_POR_METRO;
    }

    // ====================================================================================
    // ATRIBUTOS
    // ====================================================================================

    // Entidades
    private Jogador jogador;
    private Raposa raposa;
    private Cesta cesta;

    // Gráficos e Câmera
    FitViewport viewport;
    OrthographicCamera camera;
    Texture texturaFundo;

    // Lógica de Perseguição e Jogo
    private final float UNIDADES_POR_METRO = 100f;
    private final float DISTANCIA_VITORIA_METROS = 200f;
    private final float DISTANCIA_VITORIA;
    private final float DELAY_SPAWN_RAPOSA = 1.0f;

    private float larguraMundo;
    private float alturaMundo;
    private float tempoJogo = 0f;
    private int cliquesBarraEspaco = 0; // Variável para armazenar a estatística
    private float posicaoInicialX;

    private float playerSize = 80f;

    // Estado do Jogo (usa o enum traduzido)
    EstadoJogo estado = EstadoJogo.CORRENDO;

    // ====================================================================================
    // MÉTODOS DA INTERFACE SCREEN
    // ====================================================================================

    @Override
    public void show() {
        // Inicialização de Texturas
        texturaFundo = new Texture("background.png");
        texturaFundo.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        larguraMundo = texturaFundo.getWidth();
        alturaMundo = texturaFundo.getHeight();

        camera = new OrthographicCamera();
        viewport = new FitViewport(larguraMundo, alturaMundo, camera);

        float inicioX = larguraMundo / 2f;

        // Inicializa Entidades
        jogador = new Jogador(inicioX, 0f, playerSize, larguraMundo);
        raposa = new Raposa(100f);
        cesta = new Cesta(70f);

        // Configurações iniciais
        posicaoInicialX = jogador.x;
        tempoJogo = 0f;
        cliquesBarraEspaco = 0;
        estado = EstadoJogo.CORRENDO;

        camera.position.set(larguraMundo / 2f, alturaMundo / 2f, 0);
    }

    @Override
    public void render(float delta) {
        if (estado == EstadoJogo.CORRENDO) {
            tempoJogo += delta;

            // 1. INPUT e Coleta de Estatísticas
            jogador.inputClique();
            cliquesBarraEspaco = jogador.cliquesBarraEspaco; // <-- Coleta CORRIGIDA

            // 2. ATUALIZAÇÃO DE ENTIDADES
            jogador.atualizar(delta);
            checarSpawnRaposa();
            if (raposa.ativo) raposa.atualizar(delta, jogador.x, camera, viewport);

            // 3. Lógica de Vitória
            cesta.checarSpawn(jogador.x, jogador.tamanho, jogador.x - posicaoInicialX, DISTANCIA_VITORIA);

            // 4. CÂMERA E COLISÕES
            camera.position.x = jogador.x + jogador.tamanho / 2f;
            camera.update();

            checarColisoes();
        }

        // 5. DESENHO
        desenhar();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}

    @Override
    public void dispose() {
        jogador.dispose();
        raposa.dispose();
        cesta.dispose();
        texturaFundo.dispose();
    }

    // --- MÉTODOS DE LÓGICA ---

    private void setupAnimation() {
        // Método movido para a classe Jogador.java
    }

    private void checarSpawnRaposa() {
        if (!raposa.ativo && tempoJogo >= DELAY_SPAWN_RAPOSA) {
            raposa.ativar(jogador.x, viewport);
        }
    }

    private void checarColisoes() {
        // Colisão Raposa
        if (raposa.ativo && jogador.limites.overlaps(raposa.limites)) {
            finalizarJogo(EstadoJogo.FIM_DE_JOGO);
            return;
        }

        // Colisão Cesta
        if (cesta.ativo && jogador.limites.overlaps(cesta.limites)) {
            finalizarJogo(EstadoJogo.VENCEU);
        }
    }

    private void finalizarJogo(EstadoJogo estadoFinal) {
        estado = estadoFinal;

        // Calcula a distância total percorrida
        float distanciaPercorrida = jogador.x - posicaoInicialX;

        // Manda para a tela de Game Over/Vitória com todas as estatísticas
        game.setScreen(new TelaFimDeJogo(game, estadoFinal, tempoJogo, distanciaPercorrida, cliquesBarraEspaco));
        dispose();
    }

    private void desenhar() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();

        // 1. Desenho do Fundo (Paralaxe)
        float parallaxX = camera.position.x * 0.0005f;
        game.batch.draw(texturaFundo,
            camera.position.x - viewport.getWorldWidth() / 2,
            camera.position.y - viewport.getWorldHeight() / 2,
            viewport.getWorldWidth(),
            viewport.getWorldHeight(),
            parallaxX, 0 + 1,
            parallaxX + 1, 0
        );

        // 2. Desenho do Inimigo
        if (raposa.ativo) {
            game.batch.draw(raposa.textura, raposa.x, raposa.y, raposa.tamanho, raposa.tamanho);
        }

        // 3. Desenho da Cesta
        if (cesta.ativo) {
            game.batch.draw(cesta.textura, cesta.x, cesta.y, cesta.tamanho, cesta.tamanho);
        }

        // 4. Desenho do Jogador
        game.batch.draw(jogador.frameAtual, jogador.x, jogador.y, jogador.tamanho, jogador.tamanho);

        game.batch.end();
    }
}
