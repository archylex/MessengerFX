package archylex.messenger.fx.DB;

import archylex.messenger.fx.Messenger.Person;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class Contacts {
    public static boolean isContactFromDB(String snname, String snid) {
        Connection connection;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM contact_list WHERE SNNAME = ? AND SNID = ?";

        try {
            connection = Connetion.getConnection();

            ps = connection.prepareStatement(sql);
            ps.setString(1, snname);
            ps.setString(2, snid);

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
                try {
                    ps.close();
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void addContactToDB(Person person) {
        String sql = "INSERT INTO contact_list(NAME,LASTNAME,PHOTO,SNNAME,SNID) VALUES(?,?,?,?,?)";

        Connection conn = null;

        try {
            conn = Connetion.getConnection();

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, person.getName());
            ps.setString(2, person.getLastname());
            ps.setString(3, person.getPhotoURL());
            ps.setString(4, person.getSNName());
            ps.setString(5, person.getSNID());

            ps.execute();

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Person> getContactListFromDB(String snname) {
        Connection connection;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Person> contactList = new ArrayList<Person>();
        String sql = "SELECT * FROM contact_list WHERE SNNAME = ?";

        try {
            connection = Connetion.getConnection();

            ps = connection.prepareStatement(sql);
            ps.setString(1, snname);

            rs = ps.executeQuery();

            while (rs.next()) {
                contactList.add(new Person(rs.getString(2), rs.getString(3), rs.getString(6), snname, rs.getString(4)));
            }

            return contactList;
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

    public static Person getContactByIDFromDB(String snname, String snid) {
        Connection connection;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Person contact = null;
        String sql = "SELECT * FROM contact_list WHERE SNNAME = ? AND SNID = ?";

        try {
            connection = Connetion.getConnection();

            ps = connection.prepareStatement(sql);
            ps.setString(1, snname);
            ps.setString(2, snid);

            rs = ps.executeQuery();

            if (rs.next()) {
                contact = new Person(rs.getString(2), rs.getString(3), rs.getString(6), snname, rs.getString(4));
            }
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

        return contact;
    }
}
