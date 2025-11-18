package br.mackenzie;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class Jogador {

    public float x;
    public float y;
    public float tamanho;
    public Rectangle limites;

    // Animação e Movimento
    private final float MAX_VELOCIDADE = 650f;
    private final float ACELERACAO_POR_CLIQUE = 200f;

    // AGORA UMA VARIÁVEL (Definida pelo nível)
    private float desaceleracao;

    public float velocidadeAtual = 0;
    public int cliquesBarraEspaco = 0;
    private float limiteMinimoX;

    private Array<Texture> texturasFrames;
    private Animation<TextureRegion> animacaoCorrida;
    public TextureRegion frameAtual;
    private float tempoEstado;
    private static final int NUMERO_FRAMES = 8;
    private static final float DURACAO_FRAME = 0.1f;
    private boolean viradoDireita = true;

    // Construtor recebe fatorDesaceleracao
    public Jogador(float inicioX, float inicioY, float tamanhoSprite, float mundoLargura, float fatorDesaceleracao) {
        this.x = inicioX;
        this.y = inicioY;
        this.tamanho = tamanhoSprite;
        this.limiteMinimoX = inicioX;

        this.desaceleracao = fatorDesaceleracao;

        this.limites = new Rectangle(x, y, tamanho * 0.8f, tamanho * 0.9f);

        setupAnimacao();
        this.frameAtual = animacaoCorrida.getKeyFrame(0, true);
    }

    private void setupAnimacao() {
        texturasFrames = new Array<>();
        TextureRegion[] frames = new TextureRegion[NUMERO_FRAMES];

        for (int i = 0; i < NUMERO_FRAMES; i++) {
            Texture frameTexture = new Texture("personagem" + (i + 1) + ".png");
            texturasFrames.add(frameTexture);
            frames[i] = new TextureRegion(frameTexture);
        }
        animacaoCorrida = new Animation<TextureRegion>(DURACAO_FRAME, frames);
        animacaoCorrida.setPlayMode(Animation.PlayMode.LOOP);
    }

    public void atualizar(float delta) {
        tempoEstado += delta;

        if (velocidadeAtual > 0) {
            x += velocidadeAtual * delta;
            // Usa a variável de instância
            velocidadeAtual -= desaceleracao;
            if (velocidadeAtual < 0) velocidadeAtual = 0;
        }

        this.x = MathUtils.clamp(this.x, limiteMinimoX, Float.MAX_VALUE);
        limites.setPosition(x, y);

        TextureRegion frameBase = animacaoCorrida.getKeyFrame(tempoEstado, velocidadeAtual > 0.1f);
        TextureRegion frameFinal = new TextureRegion(frameBase);

        if (velocidadeAtual <= 0.1f) frameBase = animacaoCorrida.getKeyFrames()[0];

        if (!viradoDireita && !frameFinal.isFlipX()) frameFinal.flip(true, false);
        else if (viradoDireita && frameFinal.isFlipX()) frameFinal.flip(true, false);

        frameAtual = frameFinal;
    }

    public void inputClique() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            cliquesBarraEspaco++;
            if(velocidadeAtual < MAX_VELOCIDADE) velocidadeAtual += ACELERACAO_POR_CLIQUE;
            if(velocidadeAtual > MAX_VELOCIDADE) velocidadeAtual = MAX_VELOCIDADE;
        }
    }

    public void dispose() {
        for (Texture texture : texturasFrames) texture.dispose();
    }
}
