package com.example.rewards;

import java.io.Serializable;
import java.util.Calendar;

public class Reward implements Serializable, Comparable<Reward>{
    private String giverName;
    private int amount;
    private String note;
    private Calendar awardDate;

    public Reward(String giverName, int amount, String note, Calendar awardDate)
    {
        this.giverName = giverName;
        this.amount = amount;
        this.note = note;
        this.awardDate = awardDate;
    }

    public String getGiverName() {
        return giverName;
    }

    public int getAmount() {
        return amount;
    }

    public String getNote() {
        return note;
    }

    public Calendar getAwardDate() {
        return awardDate;
    }


    @Override
    public int compareTo(Reward o) {
        if (awardDate.before(o.awardDate))
            return 1;
        else if (awardDate.after(o.awardDate))
            return -1;
        else
            return 0;
    }
}
