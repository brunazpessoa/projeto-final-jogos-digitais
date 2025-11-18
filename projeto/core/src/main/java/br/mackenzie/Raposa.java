package br.mackenzie;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Raposa {

    public float x;
    public float y;
    public float tamanho;
    public Rectangle limites;
    public boolean ativo = false;

    // --- ATRIBUTOS DE ANIMAÇÃO DA ONÇA ---
    private Array<Texture> texturasFrames;
    private Animation<TextureRegion> animacaoCorrida;

    // *** AQUI ESTÁ A CORREÇÃO: A variável deve ser pública ***
    public TextureRegion frameAtual;

    private float tempoEstado = 0f;
    private static final int NUMERO_FRAMES = 7;
    private static final float DURACAO_FRAME = 0.08f;

    // Velocidades
    private final float VELOCIDADE_INICIAL = 400f;
    private final float VELOCIDADE_BASE = 550f;
    private final float VELOCIDADE_ACELERAR = 650f;
    private final float VELOCIDADE_FREIO = 400f;
    public float velocidadeAtual;

    // Lógica de Proximidade
    private final float LIMITE_TRASEIRO_FATOR = 0.1f;
    private final float LIMITE_PROXIMIDADE_FATOR = 0.05f;
    private final float DURACAO_VELOCIDADE_INICIAL = 3.0f;
    private float tempoVelocidadeInicial = 0f;

    public Raposa(float tamanhoSprite) {
        this.tamanho = tamanhoSprite;

        setupAnimacao();

        this.limites = new Rectangle(0, 0, tamanho * 0.8f, tamanho * 0.9f);
        this.velocidadeAtual = VELOCIDADE_INICIAL;

        // Inicializa o frameAtual para evitar NullPointerException no primeiro frame
        if (animacaoCorrida != null) {
            this.frameAtual = animacaoCorrida.getKeyFrame(0);
        }
    }

    private void setupAnimacao() {
        texturasFrames = new Array<>();
        TextureRegion[] frames = new TextureRegion[NUMERO_FRAMES];

        for (int i = 0; i < NUMERO_FRAMES; i++) {
            // Certifique-se de que as imagens Onca1.png a Onca7.png estão na pasta assets
            Texture frameTexture = new Texture("Onca" + (i + 1) + ".png");
            texturasFrames.add(frameTexture);
            frames[i] = new TextureRegion(frameTexture);
        }

        animacaoCorrida = new Animation<TextureRegion>(DURACAO_FRAME, frames);
        animacaoCorrida.setPlayMode(Animation.PlayMode.LOOP);
    }

    public void ativar(float jogadorX, Viewport viewport) {
        this.ativo = true;
        this.y = 0f;
        this.tempoVelocidadeInicial = 0f;

        this.x = jogadorX - viewport.getWorldWidth() / 3f;
        this.velocidadeAtual = VELOCIDADE_INICIAL;
    }

    public void atualizar(float delta, float jogadorX, OrthographicCamera camera, Viewport viewport) {
        if (!ativo) return;

        tempoEstado += delta;
        tempoVelocidadeInicial += delta;

        float velocidadeAlvo;

        if (tempoVelocidadeInicial < DURACAO_VELOCIDADE_INICIAL) {
            velocidadeAlvo = VELOCIDADE_INICIAL;
        } else {
            float limiteTraseiro = camera.position.x - viewport.getWorldWidth() / 2;
            float limiteSprint = limiteTraseiro + (viewport.getWorldWidth() * LIMITE_TRASEIRO_FATOR);
            float limiteFreio = jogadorX + (viewport.getWorldWidth() * LIMITE_PROXIMIDADE_FATOR);

            if (this.x > jogadorX && this.x < limiteFreio) {
                velocidadeAlvo = VELOCIDADE_FREIO;
            } else if (this.x < limiteSprint) {
                velocidadeAlvo = VELOCIDADE_ACELERAR;
            } else {
                velocidadeAlvo = VELOCIDADE_BASE;
            }
        }

        velocidadeAtual = velocidadeAlvo;
        this.x += velocidadeAtual * delta;

        // Atualiza o frame atual da animação
        this.frameAtual = animacaoCorrida.getKeyFrame(tempoEstado, true);

        limites.setPosition(x, y);
    }

    public void dispose() {
        for(Texture texture : texturasFrames) {
            texture.dispose();
        }
    }
}
