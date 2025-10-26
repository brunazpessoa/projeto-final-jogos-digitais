package br.mackenzie.MundoJogo;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Animal {
    private Vector2 posicao;
    private float velocidade;
    private float largura;
    private float altura;
    private TextureRegion textura;
    private Rectangle bounds;

    public Animal(TextureRegion textura, float x, float y, float largura, float altura, float velocidade) {
        this.textura = textura;
        this.posicao = new Vector2(x, y);
        this.velocidade = velocidade;
        this.largura = largura;
        this.altura = altura;
        this.bounds = new Rectangle(x, y, largura, altura);
    }
    public Animal(TextureRegion textura, float x, float y, float velocidade){
        this.textura = textura;
        this.posicao = new Vector2(x, y);
    }

    // Atualiza a posição do animal
    public void update(float dt) {
        posicao.x -= velocidade * dt;  // move para esquerda
        atualizarBounds();
    }

    // Desenha o animal
    public void render(SpriteBatch batch) {
        batch.draw(textura, posicao.x, posicao.y, largura, altura);
    }

    // Atualiza a área de colisão
    private void atualizarBounds() {
        bounds.set(posicao.x, posicao.y, largura, altura);
    }

    // Retorna o retângulo de colisão
    public Rectangle getBounds() {
        return bounds;
    }

    // Getters
    public Vector2 getPosicao() {
        return posicao;
    }

    public boolean estaForaDaTela() {
        return posicao.x + largura < 0; // se saiu da tela
    }
}

