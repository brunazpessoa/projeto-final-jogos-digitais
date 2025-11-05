package br.mackenzie;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class Cesta {
    public float x;
    public float y;
    public float tamanho;
    public Rectangle limites;
    public Texture textura;
    public boolean ativo = false;

    private final float MARGEM_FATOR = 4f; // 4x o tamanho do jogador à frente da tela

    public Cesta(float tamanhoSprite) {
        this.tamanho = tamanhoSprite;
        this.textura = new Texture("basket.png");
        this.limites = new Rectangle(0, 0, tamanho, tamanho);
    }

    public void checarSpawn(float jogadorX, float tamanhoJogador, float distanciaAtual, float distanciaVitoria) {

        if (!ativo && distanciaAtual >= distanciaVitoria) {
            ativo = true;

            // Posiciona a cesta no mundo (distância alvo + margem)
            this.x = jogadorX + tamanhoJogador * MARGEM_FATOR;
            this.y = 0f; // No chão
            limites.setPosition(x, y);
        }
    }

    public void dispose() {
        textura.dispose();
    }
}
