package br.mackenzie;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class Main implements ApplicationListener {
    SpriteBatch spriteBatch;
    Texture helloTexture;

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        helloTexture = new Texture("test.png");
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.2f, 0.2f, 0.8f, 1);

        spriteBatch.begin();

        float x = (Gdx.graphics.getWidth() - helloTexture.getWidth()) / 2f;
        float y = (Gdx.graphics.getHeight() - helloTexture.getHeight()) / 2f;
        spriteBatch.draw(helloTexture, x, y);

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
        helloTexture.dispose();
    }
}