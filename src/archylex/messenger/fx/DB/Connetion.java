package archylex.messenger.fx.DB;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connetion {
    public static Connection getConnection() throws SQLException {
        try {
            String path = new File(".").getCanonicalPath();
            String CONN = "jdbc:sqlite:" + path + "/src/resources/mfx.db";
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection(CONN);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
