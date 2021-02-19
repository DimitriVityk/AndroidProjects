package com.example.rewards;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Profile implements Serializable, Comparable<Profile>{

    private String firstName = "";
    private String lastName = "";
    private String username = "";
    private String department = "";
    private String story = "";
    private String position = "";
    private String password = "";
    private int remainingPoints;
    private String location = "";
    private int pointsAwarded;
    private String image64 = "";
    private List<Reward> rewardList;

    public Profile(String firstName, String lastName, String username, String department, String story, String position, String password, int remainingPoints, String location, String image64)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.department = department;
        this.story = story;
        this.position = position;
        this.password = password;
        this.remainingPoints = remainingPoints;
        this.location = location;
        this.pointsAwarded = 0;
        this.image64 = image64;
        this.rewardList = new ArrayList<>();
    }

    public Profile(String firstName, String lastName, String username, String department, String story, String position, String image64)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.department = department;
        this.story = story;
        this.position = position;
        this.password = "";
        this.remainingPoints = 0;
        this.location = "";
        this.pointsAwarded = 0;
        this.image64 = image64;
        this.rewardList = new ArrayList<>();
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getDepartment() {
        return department;
    }

    public String getStory() {
        return story;
    }

    public String getPosition() {
        return position;
    }

    public String getPassword() {
        return password;
    }

    public int getRemainingPoints() {
        return remainingPoints;
    }

    public String getLocation() {
        return location;
    }

    public int getPointsAwarded() {
        return pointsAwarded;
    }

    public List<Reward> getRewardList() {
        return rewardList;
    }

    public void addReward(Reward reward)
    {
        rewardList.add(reward);
    }
    public String getImage64() {
        return image64;
    }

    public void setImage64(String image64) {
        this.image64 = image64;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRemainingPoints(int remainingPoints) {
        this.remainingPoints = remainingPoints;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setPointsAwarded(int pointsAwarded) {
        this.pointsAwarded = pointsAwarded;
    }

    @Override
    public int compareTo(Profile o) {
        if(pointsAwarded < o.pointsAwarded)
        {
            return 1;
        }
        else if(pointsAwarded > o.pointsAwarded)
        {
            return -1;
        }
        else
            return 0;
    }


}
