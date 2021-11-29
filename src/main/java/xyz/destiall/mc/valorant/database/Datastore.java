package xyz.destiall.mc.valorant.database;

import org.bukkit.configuration.Configuration;
import xyz.destiall.mc.valorant.Valorant;
import xyz.destiall.mc.valorant.api.match.MatchResult;
import xyz.destiall.mc.valorant.api.player.VPlayer;
import xyz.destiall.mc.valorant.managers.ConfigManager;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class Datastore {
    private static Datastore instance;

    public static Datastore getInstance() {
        if (instance == null) {
            instance = new Datastore();
        }
        return instance;
    }

    private Connection connection;

    public Datastore() {
        Configuration config = ConfigManager.getInstance().getConfig();
        String type = config.getString("database.type", "sqlite").toLowerCase();
        switch (type) {
            case "sqlite": {
                try {
                    Class.forName("org.sqlite.JDBC");
                    File file = new File(Valorant.getInstance().getPlugin().getDataFolder(), "database.db");
                    if (!file.exists()) file.createNewFile();
                    connection = DriverManager.getConnection("jdbc:sqlite:" + file);
                    PreparedStatement statement = connection.prepareStatement(Query.CREATE_USER_TABLE);
                    statement.executeUpdate();
                    statement = connection.prepareStatement(Query.CREATE_MATCH_TABLE);
                    statement.executeUpdate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case "mariadb":
            case "mysql": {
                try {
                    String address = config.getString("database.mysql.address", "localhost");
                    String database = config.getString("database.mysql.database", "database");
                    String username = config.getString("database.mysql.username", "root");
                    String password = config.getString("database.mysql.password", "password");
                    String port = config.getString("database.mysql.port", "3306");
                    connection = DriverManager.getConnection("jdbc:mysql://" + address + ":" + port + "/" + database + "?useSSL=false", username, password);
                    PreparedStatement statement = connection.prepareStatement(Query.CREATE_USER_TABLE);
                    statement.executeUpdate();
                    statement = connection.prepareStatement(Query.CREATE_MATCH_TABLE);
                    statement.executeUpdate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            default: {
                connection = null;
                break;
            }
        }
    }

    public void loadPlayer(VPlayer vPlayer) {
        if (connection == null) return;
        try {
            PreparedStatement statement = connection.prepareStatement(Query.SELECT_PLAYER);
            statement.setString(1, vPlayer.getUUID().toString());
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                vPlayer.getStats().load(result.getString("data"));
            } else {
                newPlayer(vPlayer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void newPlayer(VPlayer vPlayer) {
        if (connection == null) return;
        try {
            PreparedStatement statement = connection.prepareStatement(Query.INSERT_PLAYER);
            statement.setString(1, vPlayer.getUUID().toString());
            statement.setString(2, vPlayer.getStats().toJSON());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePlayer(VPlayer vPlayer) {
        if (connection == null) return;
        try {
            PreparedStatement statement = connection.prepareStatement(Query.UPDATE_PLAYER);
            statement.setString(1, vPlayer.getStats().toJSON());
            statement.setString(2, vPlayer.getUUID().toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveMatch(MatchResult result) {
        if (connection == null) return;
        try {
            PreparedStatement statement = connection.prepareStatement(Query.INSERT_MATCH);
            statement.setString(1, result.getUUID().toString());
            statement.setString(2, result.toJSON());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String loadMatch(UUID uuid) {
        if (connection == null) return null;
        try {
            PreparedStatement statement = connection.prepareStatement(Query.SELECT_MATCH);
            statement.setString(1, uuid.toString());
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return result.getString("data");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
