package br.mackenzie.MundoJogo;
    
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.utils.Array;

public class SistemaColisao {
    /**
     * Checa colisões entre o ciclista e a lista de animais.
     * Retorna um Array com os animais que colidiram.
     * Usa primeiro uma checagem de distância (broad-phase) para desempenho.
     */
    public Array<Animal> checarColisoes(Ciclista ciclista, Array<Animal> animais) {
        Array<Animal> colididos = new Array<>();

        Rectangle rc = ciclista.getBounds();

        for (int i = 0; i < animais.size; i++) {
            Animal a = animais.get(i);

            // checar distância aproximada 
            float dx = Math.abs(a.getPosicao().x - ciclista.getPosicao().x);
            if (dx > 300) { // se estiver muito longe no eixo X, pula (ajuste 300 conforme sua largura de tela)
                continue;
            }

            // checa se colidiu ou não com a função overlaps
            Rectangle ra = a.getBounds();
            if (rc.overlaps(ra)) {
                colididos.add(a);
            }
        }

        return colididos;
    }

    // Aplica ação de colisão: reduz vida, reduz velocidade e notifica o animal.
    public void tratarColisoes(Ciclista ciclista, Array<Animal> colididos) {
        if (colididos.size == 0) return;

        for (Animal a : colididos) {
            // exemplo de penalidade: reduzir uma vida e aplicar velocidade reduzida
            ciclista.reduzirVida(1);
            ciclista.reduzirVelocidade(0.6f); // reduz velocidade pra 60%
            a.onColidiuComCiclista(ciclista);

            // depois tocar som para quando colidir, etc.
        }
    }
}