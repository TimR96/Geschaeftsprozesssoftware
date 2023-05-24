package de.riexinger.dbconnector;

import java.sql.*;

/**
 *
 * @author Tim
 */
public abstract class DBConnector {

    protected final String user;
    protected final String password;
    protected final String database;
    protected final String host;
    protected final String port;
    protected Connection connection = null;
    private Statement statement = null;
    private ResultSet resultset = null;

    public DBConnector(String user, String password, String database, String host, String port) {
        this.user = user;
        this.password = password;
        this.database = database;
        this.host = host;
        this.port = port;
    }

    public abstract boolean connect() throws ClassNotFoundException, SQLException;

    public boolean disconnect() throws SQLException {
        resultset.close();
        statement.close();
        connection.close();
        return true;
    }

    public ResultSet executeQuery(String sqlStatement) throws SQLException {
        statement = connection.createStatement();
        statement.executeQuery(sqlStatement);
        resultset = statement.getResultSet();
        return resultset;
    }

    public boolean executeDMLQuery(String sqlStatement) throws SQLException {
        boolean result = false;
        statement = connection.createStatement();
        if (statement.executeUpdate(sqlStatement) > 0) {
            result = true;
        }
        return result;
    }

    public Connection getConnection() {
        return connection;
    } 
}