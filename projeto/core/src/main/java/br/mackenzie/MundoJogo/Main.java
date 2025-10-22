package br.mackenzie;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
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


public class Main implements ApplicationListener {

    // ====================================================================================
    // ATRIBUTOS
    // ====================================================================================

    private Array<Texture> playerTextures; // Armazena as 8 texturas dos frames
    SpriteBatch spriteBatch;
    FitViewport viewport;
    OrthographicCamera camera;
    Texture backgroundTexture;

    // Posição do jogador (canto inferior esquerdo do sprite)
    private float playerX;
    private float playerY;

    // Atributos de Animação
    private Animation<TextureRegion> walkAnimation;
    private TextureRegion currentFrame;
    private float stateTime; // Tempo acumulado para controle da animação

    private float playerSize = 80f; // Tamanho do personagem em unidades de mundo
    private boolean isMoving = false; // Indica se o personagem está se movendo
    private boolean facingRight = true; // Indica a direção para o espelhamento

    private static final float FRAME_DURATION = 0.1f;
    private static final int FRAME_COUNT = 8;

    // Fator de Paralaxe: Controla a velocidade de movimento do fundo
    private static final float PARALLAX_FACTOR = 0.0005f;

    private float worldWidth;  // Largura do mundo (baseado no background)
    private float worldHeight; // Altura do mundo (baseado no background)

    // ====================================================================================
    // MÉTODOS DO CICLO DE VIDA
    // ====================================================================================

    @Override
    public void create() {
        // Carrega e configura o background para repetição
        backgroundTexture = new Texture("background.png");
        backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        // Define as dimensões do mundo com base no tamanho da textura
        worldWidth = backgroundTexture.getWidth();
        worldHeight = backgroundTexture.getHeight();

        spriteBatch = new SpriteBatch();
        camera = new OrthographicCamera();
        // Viewport para adaptar o mundo (worldWidth x worldHeight) à janela
        viewport = new FitViewport(worldWidth, worldHeight, camera);

        // Configura a animação carregando os 8 arquivos
        setupAnimation();

        currentFrame = walkAnimation.getKeyFrame(0, true);

        // Define a posição inicial: Y=0 (chão) e X centralizado
        playerY = 0f;
        playerX = worldWidth / 2f;

        stateTime = 0f;
        camera.position.set(worldWidth / 2f, worldHeight / 2f, 0);
    }

    // Carrega os 8 frames individuais e monta o objeto Animation
    private void setupAnimation() {
        playerTextures = new Array<>();
        TextureRegion[] walkFrames = new TextureRegion[FRAME_COUNT];

        for (int i = 0; i < FRAME_COUNT; i++) {
            // Assume nomes como: "personagem1.png", "personagem2.png", etc.
            String fileName = "personagem" + (i + 1) + ".png";

            Texture frameTexture = new Texture(fileName);
            playerTextures.add(frameTexture); // Guarda para descarte

            walkFrames[i] = new TextureRegion(frameTexture);
        }

        walkAnimation = new Animation<TextureRegion>(FRAME_DURATION, walkFrames);
        walkAnimation.setPlayMode(Animation.PlayMode.LOOP);
    }

    @Override
    public void resize(int width, int height) {
        // Adapta o viewport ao novo tamanho da janela
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        // Atualiza o tempo e a lógica de movimento
        stateTime += Gdx.graphics.getDeltaTime();
        input();

        // Obtém o frame atual da animação
        currentFrame = walkAnimation.getKeyFrame(stateTime, true);

        // A câmera segue o jogador horizontalmente
        camera.position.x = playerX + playerSize / 2f;
        camera.position.y = worldHeight / 2f; // Mantém-se centralizada verticalmente

        camera.update();
        draw();
    }

    // Processa a entrada do usuário e atualiza a posição e o estado
    private void input() {
        float speed = 500f;
        float delta = Gdx.graphics.getDeltaTime();
        isMoving = false;

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            playerX += speed * delta;
            isMoving = true;
            facingRight = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            playerX -= speed * delta;
            isMoving = true;
            facingRight = false;
        }
    }

    // Desenha todos os elementos na tela
    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        spriteBatch.begin();

        // --- Desenho do Background com Paralaxe ---
        float parallaxX = camera.position.x * PARALLAX_FACTOR;

        // Desenha o background rolando a textura (UVs) para o efeito paralaxe
        spriteBatch.draw(backgroundTexture,
            camera.position.x - viewport.getWorldWidth() / 2,
            camera.position.y - viewport.getWorldHeight() / 2,
            viewport.getWorldWidth(),
            viewport.getWorldHeight(),
            parallaxX, 0 + 1, // V2 (Y invertido)
            parallaxX + 1, 0  // V (Y invertido)
        );

        // --- Desenho do Personagem ---
        TextureRegion frameToDraw = currentFrame;

        // Congela no primeiro frame se o personagem estiver parado
        if (!isMoving) {
            frameToDraw = walkAnimation.getKeyFrames()[0];
        }

        // Prepara uma cópia do frame para evitar alterar o original com o flip
        TextureRegion finalFrame = new TextureRegion(frameToDraw);

        // Lógica de espelhamento (flip)
        if ((!facingRight && !finalFrame.isFlipX()) || (facingRight && finalFrame.isFlipX())) {
            finalFrame.flip(true, false);
        }

        // Desenha o frame final na posição do jogador
        spriteBatch.draw(finalFrame,
            playerX,
            playerY,
            playerSize,
            playerSize);

        spriteBatch.end();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        // Libera da memória todas as texturas carregadas
        for (Texture texture : playerTextures) {
            texture.dispose();
        }
        spriteBatch.dispose();
        backgroundTexture.dispose();
    }
}
