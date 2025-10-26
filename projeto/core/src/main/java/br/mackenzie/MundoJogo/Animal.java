package br.mackenzie.MundoJogo;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Animal {
    private Vector2 posicao; // posição do animal
    private float velocidade;
    private float largura; //dimensões do animal
    private float altura;
    private TextureRegion textura; // config padrão no libgdx para os objetos
    private Rectangle bounds; // config padrão no libgdx para os objetos

    //construtor completo
    public Animal(TextureRegion textura, float x, float y, float largura, float altura, float velocidade) {
        this.textura = textura;
        this.posicao = new Vector2(x, y);
        this.velocidade = velocidade;
        this.largura = largura;
        this.altura = altura;
        this.bounds = new Rectangle(x, y, largura, altura);
    }
    // construtor simples
    public Animal(TextureRegion textura, float x, float y, float velocidade){
        this.textura = textura;
        this.posicao = new Vector2(x, y);
    }

    // atualiza a posição do animal
    public void update(float dt) {
        posicao.x -= velocidade * dt;  // move para esquerda
        atualizarBounds();
    }

    // desenha o animal na tela
    public void render(SpriteBatch batch) {
        batch.draw(textura, posicao.x, posicao.y, largura, altura);
    }

    // atualiza área de colisão
    private void atualizarBounds() {
        bounds.set(posicao.x, posicao.y, largura, altura);
    }

    // retorna o frame de colisão
    public Rectangle getBounds() {
        return bounds;
    }

    // getters
    public Vector2 getPosicao() {
        return posicao;
    }

    // retorna se o animal saiu da tela 
    public boolean estaForaDaTela() {
        return posicao.x + largura < 0; // se saiu da tela
    }
}

