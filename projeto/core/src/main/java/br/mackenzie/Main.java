package br.mackenzie;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class Main implements ApplicationListener {
    SpriteBatch spriteBatch;

    PlayerCharacter player;

    Texture backgroundTexture;

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();

        backgroundTexture = new Texture("city1.png");

        player = new PlayerCharacter(0, 0);
    }

    @Override
    public void resize(int width, int height) {
        if (player != null) {
            player.setY(20);
        }
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.2f, 0.2f, 0.8f, 1);

        float deltaTime = Gdx.graphics.getDeltaTime();

        player.update(deltaTime);

        if (player.getX() < 0) {
            player.setX(0);
        }
        if (player.getX() > Gdx.graphics.getWidth() - player.getWidth()) {
            player.setX(Gdx.graphics.getWidth() - player.getWidth());
        }

        spriteBatch.begin();

        spriteBatch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        player.render(spriteBatch);

        spriteBatch.end();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        backgroundTexture.dispose();
        
        player.dispose();
    }
}