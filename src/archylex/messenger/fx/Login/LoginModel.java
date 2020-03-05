package archylex.messenger.fx.Login;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import archylex.messenger.fx.DB.Connetion;

import static archylex.messenger.fx.Utils.Encrypt.encodeData;

public class LoginModel {
    Connection connection;

    public LoginModel() {
        try {
            this.connection = Connetion.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            createTable();
        }

        if(this.connection == null)
            System.exit(1);

    }

    private static void createTable() {
        String sql = "CREATE TABLE users_data (\n" +
                "    ID       INTEGER PRIMARY KEY\n" +
                "                     UNIQUE\n" +
                "                     NOT NULL,\n" +
                "    USER     STRING  NOT NULL\n" +
                "                     UNIQUE,\n" +
                "    PASSWORD STRING  NOT NULL,\n" +
                "    EMAIL    STRING\n" +
                ");";

        Connection conn = null;
        try {
            conn = Connetion.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.execute();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public boolean isDBConnected() {
        return this.connection != null;
    }

    public boolean isLogin(String user, String password) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM users_data WHERE USER = ? AND PASSWORD = ?";

        try {
            String enpass = encodeData(user, password);

            ps = this.connection.prepareStatement(sql);
            ps.setString(1, user);
            ps.setString(2, enpass);

            rs = ps.executeQuery();

            if(rs.next()) {
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        finally {
            {
                ps.close();
                rs.close();
            }
        }
    }


    public int getID(String user, String password) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM users_data WHERE USER = ? AND PASSWORD = ?";

        try {
            String enpass = encodeData(user, password);

            ps = this.connection.prepareStatement(sql);
            ps.setString(1, user);
            ps.setString(2, enpass);

            rs = ps.executeQuery();

            if(rs.next()) {
                return Integer.valueOf(rs.getString("ID"));
            }
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }

        finally {
            {
                ps.close();
                rs.close();
            }
        }
    }

    public String getEmail(int id) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM users_data WHERE ID = ?";

        try {
            ps = this.connection.prepareStatement(sql);
            ps.setInt(1, id);

            rs = ps.executeQuery();

            if(rs.next()) {
                return rs.getString("EMAIL");
            }
            return "";
        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }

        finally {
            {
                ps.close();
                rs.close();
            }
        }
    }
}
