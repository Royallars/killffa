package com.killffa.killffa;

public class PlayerStats {
    private int kills;
    private int deaths;
    private int currentStreak;
    private int bestStreak;

    public PlayerStats() {
    }

    public PlayerStats(int kills, int deaths, int currentStreak, int bestStreak) {
        this.kills = Math.max(0, kills);
        this.deaths = Math.max(0, deaths);
        this.currentStreak = Math.max(0, currentStreak);
        this.bestStreak = Math.max(this.currentStreak, Math.max(0, bestStreak));
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public int getBestStreak() {
        return bestStreak;
    }

    public int incrementKills() {
        kills++;
        currentStreak++;
        if (currentStreak > bestStreak) {
            bestStreak = currentStreak;
        }
        return currentStreak;
    }

    public void incrementDeaths() {
        deaths++;
        currentStreak = 0;
    }
}
