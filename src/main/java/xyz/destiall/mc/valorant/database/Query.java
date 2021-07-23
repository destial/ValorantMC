package xyz.destiall.mc.valorant.database;

public class Query {
    public static final String USER_TABLE_NAME = "valorant_user_database";
    public static final String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS " + USER_TABLE_NAME + " (uuid VARCHAR(100), data VARCHAR(2048))";
    public static final String SELECT_PLAYER = "SELECT * FROM " + USER_TABLE_NAME + " WHERE uuid=(?)";
    public static final String INSERT_PLAYER = "INSERT INTO " + USER_TABLE_NAME + " (uuid,data) VALUES (?,?)";
    public static final String UPDATE_PLAYER = "UPDATE " + USER_TABLE_NAME + " SET data=(?) WHERE uuid=(?)";

    public static final String MATCH_TABLE_NAME = "valorant_match_database";
    public static final String CREATE_MATCH_TABLE = "CREATE TABLE IF NOT EXISTS " + MATCH_TABLE_NAME + " (uuid VARCHAR(100), data VARCHAR(2048))";
    public static final String SELECT_MATCH = "SELECT * FROM " + MATCH_TABLE_NAME + " WHERE uuid=(?)";
    public static final String INSERT_MATCH = "INSERT INTO " + MATCH_TABLE_NAME + " (uuid,data) VALUES (?,?)";
}
