package br.mackenzie;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Raposa {

    public float x;
    public float y;
    public float tamanho;
    public Rectangle limites;
    public Texture textura;
    public boolean ativo = false;

    // Velocidades
    private final float VELOCIDADE_INICIAL = 300f;
    private final float VELOCIDADE_BASE = 500f;
    private final float VELOCIDADE_ACELERAR = 1000f; // Catch-up Sprint
    private final float VELOCIDADE_FREIO = 300f; // Freio agressivo
    public float velocidadeAtual;

    // Lógica de Proximidade e Tempo
    private final float LIMITE_TRASEIRO_FATOR = 0.35f; // 35% da tela para ativar sprint
    private final float LIMITE_PROXIMIDADE_FATOR = 0.05f; // 5% da tela para frear
    private final float DURACAO_VELOCIDADE_INICIAL = 3.0f;
    private float tempoVelocidadeInicial = 0f;

    public Raposa(float tamanhoSprite) {
        this.tamanho = tamanhoSprite;
        this.textura = new Texture("fox.png");
        this.limites = new Rectangle(0, 0, tamanho * 0.8f, tamanho * 0.9f);
        this.velocidadeAtual = VELOCIDADE_INICIAL;
    }

    public void ativar(float jogadorX, Viewport viewport) {
        this.ativo = true;
        this.y = 0f;
        this.tempoVelocidadeInicial = 0f;

        // Posiciona a raposa em relação à POSIÇÃO ATUAL do jogador (1/3 da tela atrás)
        this.x = jogadorX - viewport.getWorldWidth() / 3f;
        this.velocidadeAtual = VELOCIDADE_INICIAL;
    }

    public void atualizar(float delta, float jogadorX, OrthographicCamera camera, Viewport viewport) {
        if (!ativo) return;

        // 1. Atualiza Timer de Velocidade Inicial
        tempoVelocidadeInicial += delta;

        float velocidadeAlvo;

        if (tempoVelocidadeInicial < DURACAO_VELOCIDADE_INICIAL) {
            // Fase Lenta:
            velocidadeAlvo = VELOCIDADE_INICIAL;
        } else {
            // Fase de Perseguição Dinâmica:

            float limiteTraseiro = camera.position.x - viewport.getWorldWidth() / 2;
            float limiteSprint = limiteTraseiro + (viewport.getWorldWidth() * LIMITE_TRASEIRO_FATOR);
            float limiteFreio = jogadorX + (viewport.getWorldWidth() * LIMITE_PROXIMIDADE_FATOR);

            if (this.x > jogadorX && this.x < limiteFreio) {
                // Prioridade 1: Frear (Se estiver perto demais)
                velocidadeAlvo = VELOCIDADE_FREIO;
            } else if (this.x < limiteSprint) {
                // Prioridade 2: Sprint (Se estiver saindo da visão traseira)
                velocidadeAlvo = VELOCIDADE_ACELERAR;
            } else {
                // Prioridade 3: Velocidade base (Deixa cair para trás lentamente)
                velocidadeAlvo = VELOCIDADE_BASE;
            }
        }

        // 2. Aplica o movimento
        velocidadeAtual = velocidadeAlvo;
        this.x += velocidadeAtual * delta;

        // 3. Atualiza Hitbox
        limites.setPosition(x, y);
    }

    public void dispose() {
        textura.dispose();
    }
}
