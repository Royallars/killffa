package com.killffa.killffa;

public class PlayerStats {
    private int kills;
    private int deaths;

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void incrementKills() {
        kills++;
    }

    public void incrementDeaths() {
        deaths++;
    }
}
