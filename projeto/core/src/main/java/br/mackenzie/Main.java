package br.mackenzie;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class Main implements ApplicationListener {
    SpriteBatch spriteBatch;

    // --- Recursos do Personagem ---
    Texture idleTexture;
    Texture[] runTextures;
    int NUM_RUN_FRAMES = 8;

    float characterX;
    float characterY;
    float speed = 200f;

    float animationTime;
    float frameDuration = 0.1f;
    boolean isMoving;
    boolean facingRight = true;

    // --- Recurso do Background ---
    Texture backgroundTexture; 

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();

        // Carrega as texturas do personagem
        idleTexture = new Texture("character.png");
        runTextures = new Texture[NUM_RUN_FRAMES];
        for (int i = 0; i < NUM_RUN_FRAMES; i++) {
            runTextures[i] = new Texture("characterRun/run" + (i + 1) + ".png");
        }

        characterX = 0;
        characterY = 0;

        backgroundTexture = new Texture("city1.png"); 
    }

    @Override
    public void resize(int width, int height) {
        characterY = 20;
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.2f, 0.2f, 0.8f, 1);

        float deltaTime = Gdx.graphics.getDeltaTime(); 

        isMoving = false; 

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            characterX -= speed * deltaTime;
            isMoving = true;
            facingRight = false;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            characterX += speed * deltaTime;
            isMoving = true;
            facingRight = true;
        }

        Texture currentFrameTexture;
        if (isMoving) {
            animationTime += deltaTime;
            // Calcula qual frame da animação de corrida deve ser exibido (looping)
            int currentFrameIndex = (int)(animationTime / frameDuration) % NUM_RUN_FRAMES;
            currentFrameTexture = runTextures[currentFrameIndex];
        } else {
            animationTime = 0;
            currentFrameTexture = idleTexture; 
        }

        // Limita o personagem dentro das bordas da tela
        if (characterX < 0) {
            characterX = 0;
        }
        float textureWidth = currentFrameTexture.getWidth();
        if (characterX > Gdx.graphics.getWidth() - textureWidth) {
            characterX = Gdx.graphics.getWidth() - textureWidth;
        }

        spriteBatch.begin();

        spriteBatch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        float textureHeight = currentFrameTexture.getHeight();
        if (!facingRight) {
            spriteBatch.draw(currentFrameTexture, characterX + textureWidth, characterY, -textureWidth, textureHeight);
        } else {
            spriteBatch.draw(currentFrameTexture, characterX, characterY, textureWidth, textureHeight);
        }

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
        // Libera todos os recursos da memória
        spriteBatch.dispose();
        idleTexture.dispose();
        for (Texture texture : runTextures) {
            texture.dispose();
        }
        backgroundTexture.dispose(); 
    }
}