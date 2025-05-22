package com.arcadegame.game;

public enum DifficultyLevel {
    EASY("easy", 60, 20, 3000, 60),
    MEDIUM("medium", 40, 30, 2000, 45),
    HARD("hard", 25, 40, 1000, 30);
    
    private final String name;
    private final int targetSize;
    private final int totalTargets;
    private final int targetDuration; // in milliseconds
    private final int gameDuration; // in seconds
    
    DifficultyLevel(String name, int targetSize, int totalTargets, int targetDuration, int gameDuration) {
        this.name = name;
        this.targetSize = targetSize;
        this.totalTargets = totalTargets;
        this.targetDuration = targetDuration;
        this.gameDuration = gameDuration;
    }
    
    public String getName() {
        return name;
    }
    
    public int getTargetSize() {
        return targetSize;
    }
    
    public int getTotalTargets() {
        return totalTargets;
    }
    
    public int getTargetDuration() {
        return targetDuration;
    }
    
    public int getGameDuration() {
        return gameDuration;
    }
}