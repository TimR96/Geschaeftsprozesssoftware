package de.riexinger.dbconnector;

import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Tim
 */
public class DBConnectorMySQL extends DBConnector {

    public DBConnectorMySQL(String user, String password, String database, String host, String port) {
        super(user, password, database, host, port);
    }

    @Override
    public boolean connect() throws ClassNotFoundException, SQLException {
        boolean result = false;
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://"
                + host + ":" + port + "/" + database, user, password);
        if (connection != null) {
            result = true;
        }
        return result;
    }
}