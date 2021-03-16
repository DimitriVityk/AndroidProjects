package com.example.defensecommander;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler implements Runnable {
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

    private LeaderboardActivity leaderboardActivity;
    private MainActivity mainActivity;
    private static String dbURL;
    private Connection conn;
    private static final String SCORE_TABLE = "AppScores";

    private int level = -1;
    private int score = -1;
    private String init = null;

    private static final String TAG = "DatabaseHandler";

    DatabaseHandler(LeaderboardActivity ctx, int level, int score, String init) {
        leaderboardActivity = ctx;
        this.level = level;
        this.score = score;
        this.init = init;
        dbURL = "jdbc:mysql://christopherhield.com:3306/chri5558_missile_defense";
    }

    DatabaseHandler(MainActivity mainActivity, int level, int score){
        this.mainActivity = mainActivity;
        this.level = level;
        this.score = score;
        dbURL = "jdbc:mysql://christopherhield.com:3306/chri5558_missile_defense";
    }

    DatabaseHandler(LeaderboardActivity leaderboardActivity)
    {
        this.leaderboardActivity = leaderboardActivity;
    }

    public void run() {

        if(init != null && score > -1 && level > -1) {
            try {
                Class.forName(JDBC_DRIVER);
                conn = DriverManager.getConnection(dbURL, "chri5558_student", "ABC.123");

                Statement stmt = conn.createStatement();

                String sql = "insert into " + SCORE_TABLE + " values (" +
                        System.currentTimeMillis() + ", '" + init + "', " + score + ", " +
                        + level + ")";

                int result = stmt.executeUpdate(sql);

                stmt.close();

                String response = "Player " + init + " added (" + result + " record)\n\n";
                Log.d(TAG, "run: " + response);

                List<Player> playList = getAll();

                leaderboardActivity.runOnUiThread(() -> leaderboardActivity.initializeList(playList));

                conn.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if(init == null && score == -1 && level == -1)
        {
            try {
                Class.forName(JDBC_DRIVER);

                conn = DriverManager.getConnection(dbURL, "chri5558_student", "ABC.123");
                List<Player> playList = getAll();

                leaderboardActivity.runOnUiThread(() -> leaderboardActivity.initializeList(playList));
        } catch (Exception e) {
            e.printStackTrace();
        }
        }else if(init == null && score > -1 && level > -1)
        {
            try {
                Class.forName(JDBC_DRIVER);
                conn = DriverManager.getConnection(dbURL, "chri5558_student", "ABC.123");

                checkIfTop(mainActivity);

            } catch (Exception e)
            {
                e.printStackTrace();
            }


        }

    }

    private List<Player> getAll() throws SQLException {
        List<Player> playerList = new ArrayList<>();
        Statement stmt = conn.createStatement();

        String sql = "SELECT * FROM " + SCORE_TABLE + " ORDER BY Score DESC LIMIT 10";


        ResultSet resSet = stmt.executeQuery(sql);
        int counter = 1;
        while (resSet.next()) {

            int playerScore = resSet.getInt(3);
            String playerInitials = resSet.getString(2);
            int playerLevel = resSet.getInt(4);
            long millis = resSet.getLong(1);
            Player player = new Player(counter, playerInitials, playerLevel, playerScore, millis);
            playerList.add(player);
            counter++;
        }
        resSet.close();
        stmt.close();

        return playerList;
    }

    private void checkIfTop(MainActivity mainActivity) throws SQLException{
        Statement stmt = conn.createStatement();

        String sql = "SELECT * FROM " + SCORE_TABLE + " ORDER BY Score DESC LIMIT 10";

        ResultSet rs = stmt.executeQuery(sql);
        int count = 1;
        while (rs.next()) {
            if(count == 10)
            {
                int lowestScore = rs.getInt(3);
                if(score > lowestScore)
                {
                    mainActivity.runOnUiThread(mainActivity::displayTopDialog);
                } else {
                    mainActivity.runOnUiThread(mainActivity::goToLeaderboard);
                }
            }
            count++;
        }
    }

}
