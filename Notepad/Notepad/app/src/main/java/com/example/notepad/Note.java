package com.example.notepad;

import java.util.Date;

public class Note implements Comparable<Note>{


    public String title;
    private String content;
    private Date lastDate;

    public Note(String title, String content)
    {
        this.title = title;
        this.content = content;
        lastDate = new Date();
    }

    public String getTitle()
    {
        return title;
    }

    public String getContent()
    {
        return content;
    }

    public Date getLastDate()
    {
        return lastDate;
    }

    public void setLastDate(long lastTimeMS)
    {
        this.lastDate = new Date(lastTimeMS);
    }

    @Override
    public int compareTo(Note o) {
        if (lastDate.before(o.lastDate))
            return 1;
        else if (lastDate.after(o.lastDate))
            return -1;
        else
            return 0;
    }
}
