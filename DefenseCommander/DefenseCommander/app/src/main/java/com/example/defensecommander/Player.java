package com.example.defensecommander;

public class Player implements Comparable<Player>{
    int position;
    String initials;
    int level;
    int score;
    long date;

    public Player(int position, String initials, int level, int score, long date) {
        this.position = position;
        this.initials = initials;
        this.level = level;
        this.score = score;
        this.date = date;
    }

    public int getPosition() {
        return position;
    }

    public String getInitials() {
        return initials;
    }

    public int getLevel() {
        return level;
    }

    public int getScore() {
        return score;
    }

    public long getDate() {
        return date;
    }


    @Override
    public int compareTo(Player that) {
        return Integer.compare(that.score, this.score);
    }
}
