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

    private final float MARGEM_FATOR = 4f;

    public Cesta(float tamanhoSprite, float yInicial) {
        this.tamanho = tamanhoSprite;
        this.y = yInicial;
        this.textura = new Texture("basket.png");
        this.limites = new Rectangle(0, yInicial, tamanho, tamanho);
    }

    public void checarSpawn(float jogadorX, float tamanhoJogador, float distanciaAtual, float distanciaVitoria) {
        if (!ativo && distanciaAtual >= distanciaVitoria) {
            ativo = true;
            this.x = jogadorX + tamanhoJogador * MARGEM_FATOR;
            limites.setPosition(x, y);
        }
    }

    public void dispose() {
        textura.dispose();
    }
}
