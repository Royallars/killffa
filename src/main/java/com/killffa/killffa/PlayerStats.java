package com.killffa.killffa;

public class PlayerStats {
    private int kills;
    private int deaths;
    private int currentStreak;
    private int bestStreak;

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
