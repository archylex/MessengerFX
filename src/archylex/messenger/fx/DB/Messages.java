package archylex.messenger.fx.DB;

import archylex.messenger.fx.Messenger.clMessage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Messages {
    public static void addMessageToDB(clMessage message) {
        String sql = "INSERT INTO messages(SNNAME,CONTACTIDBRANCH,PERSONID,MESSAGE,TIMESTAMP) VALUES(?,?,?,?,?)";

        Connection conn = null;

        try {
            conn = Connetion.getConnection();

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, message.getSNName());
            ps.setString(2, message.getBranchID());
            ps.setString(3, message.getSenderID());
            ps.setString(4, message.getText());
            ps.setTimestamp(5, message.getTimeStamp());
            ps.execute();

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<clMessage> getMessagesByBranch(String snname, String branchid) {
        Connection connection;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<clMessage> messages = new ArrayList<clMessage>();
        String sql = "SELECT * FROM messages WHERE SNNAME = ? AND CONTACTIDBRANCH = ?";

        try {
            connection = Connetion.getConnection();

            ps = connection.prepareStatement(sql);
            ps.setString(1, snname);
            ps.setString(2, branchid);

            rs = ps.executeQuery();

            while (rs.next()) {
                messages.add(new clMessage(rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getTimestamp(6)));
            }

            return messages;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        finally {
            {
                try {
                    ps.close();
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
