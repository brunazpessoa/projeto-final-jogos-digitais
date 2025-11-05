package br.mackenzie;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class TelaFimDeJogo implements Screen {

    final JogoPrincipal game;
    OrthographicCamera camera;
    BitmapFont font;
    String message;
    Color messageColor;

    // --- ATRIBUTOS DE ESTATÍSTICAS ---
    private float finalTime;
    private float finalDistance; // Já armazenada em metros
    private int finalPresses;
    private float avgSpeed;

    // Constante de conversão
    private final float UNITS_PER_METER = 100f;


    // Construtor que recebe o estado final e as estatísticas
    public TelaFimDeJogo(final JogoPrincipal game, EstadoJogo estadoFinal, float time, float distance, int presses) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        font = new BitmapFont();
        font.getData().setScale(3);

        // Armazena e calcula as estatísticas
        this.finalTime = time;
        // CONVERSÃO: Distância em metros
        this.finalDistance = distance / UNITS_PER_METER;
        this.finalPresses = presses;

        // CONVERSÃO: Velocidade Média em metros/s
        this.avgSpeed = (time > 0) ? this.finalDistance / time : 0;

        // Define a mensagem e a cor com base no estado final
        if (estadoFinal == EstadoJogo.VENCEU) {
            message = "VITORIA! Voce pegou a comida!";
            messageColor = Color.GREEN;
        } else {
            message = "FIM DE JOGO. A raposa te pegou.";
            messageColor = Color.RED;
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();

        // --- Desenho da Mensagem Principal ---
        font.setColor(messageColor);
        font.getData().setScale(3f);
        GlyphLayout layoutTitle = new GlyphLayout(font, message);
        float xTitle = (Gdx.graphics.getWidth() - layoutTitle.width) / 2;
        float yTitle = Gdx.graphics.getHeight() - 100;
        font.draw(game.batch, message, xTitle, yTitle);

        // --- Desenho das Estatísticas ---
        font.getData().setScale(1.2f);
        font.setColor(Color.WHITE);

        // Exibindo as estatísticas convertidas para metros
        String stats = String.format(
            "Estatisticas da Corrida:\n" +
                "  Tempo Gasto: %.2f segundos\n" +
                "  Distancia Percorrida: %.2f metros\n" +
                "  Cliques na Barra de Espaco: %d\n" +
                "  Media de Velocidade: %.2f metros/s",
            finalTime, finalDistance, finalPresses, avgSpeed
        );

        // Centraliza e desenha o bloco de estatísticas
        float xStats = Gdx.graphics.getWidth() / 2 - 250;
        float yStats = Gdx.graphics.getHeight() / 2 + 100;
        font.draw(game.batch, stats, xStats, yStats);

        // --- Mensagem de Instrução ---
        font.getData().setScale(1.5f);
        font.setColor(Color.YELLOW);
        font.draw(game.batch, "Toque/Clique para voltar ao Menu",
            Gdx.graphics.getWidth() / 2 - 200, Gdx.graphics.getHeight() / 2 - 200);

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
        font.dispose();
    }
}

