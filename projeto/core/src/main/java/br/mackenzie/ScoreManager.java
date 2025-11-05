package br.mackenzie;

public class ScoreManager {
    private float score;
    private float scorePerSecond; 

    public ScoreManager(float initialScorePerSecond) {
        this.score = 0;
        this.scorePerSecond = initialScorePerSecond;
    }

    public void update(float deltaTime) {
        score += scorePerSecond * deltaTime;
    }

    public int getScore() {
        return (int) score; 
    }

    public void reset() {
        score = 0;
    }
}