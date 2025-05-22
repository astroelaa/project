package model;

public class GameSession {
    private int id;
    private int playerId;
    private String difficulty;
    private int score;
    private int duration;
    private int targetsHit;
    private int totalTargets;
    private boolean completed;
    private String createdAt;
    
    public GameSession(int id, int playerId, String difficulty, int score, 
                      int duration, int targetsHit, int totalTargets, 
                      boolean completed, String createdAt) {
        this.id = id;
        this.playerId = playerId;
        this.difficulty = difficulty;
        this.score = score;
        this.duration = duration;
        this.targetsHit = targetsHit;
        this.totalTargets = totalTargets;
        this.completed = completed;
        this.createdAt = createdAt;
    }
    
    public GameSession(int playerId, String difficulty, int score, 
                      int duration, int targetsHit, int totalTargets, 
                      boolean completed) {
        this.playerId = playerId;
        this.difficulty = difficulty;
        this.score = score;
        this.duration = duration;
        this.targetsHit = targetsHit;
        this.totalTargets = totalTargets;
        this.completed = completed;
    }
    
    // Getters and setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getPlayerId() {
        return playerId;
    }
    
    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
    
    public String getDifficulty() {
        return difficulty;
    }
    
    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
    
    public int getScore() {
        return score;
    }
    
    public void setScore(int score) {
        this.score = score;
    }
    
    public int getDuration() {
        return duration;
    }
    
    public void setDuration(int duration) {
        this.duration = duration;
    }
    
    public int getTargetsHit() {
        return targetsHit;
    }
    
    public void setTargetsHit(int targetsHit) {
        this.targetsHit = targetsHit;
    }
    
    public int getTotalTargets() {
        return totalTargets;
    }
    
    public void setTotalTargets(int totalTargets) {
        this.totalTargets = totalTargets;
    }
    
    public boolean isCompleted() {
        return completed;
    }
    
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}