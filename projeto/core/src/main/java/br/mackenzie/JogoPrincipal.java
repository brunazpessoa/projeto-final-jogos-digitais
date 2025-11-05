package br.mackenzie;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class JogoPrincipal extends Game {

    // SpriteBatch compartilhado por todas as telas
    public SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
        // Define a tela inicial como o Menu
        this.setScreen(new TelaMenu(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
    }
}
