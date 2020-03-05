package archylex.messenger.fx.Utils;

public class Timestamp {
    public static java.sql.Timestamp getTimeStamp() {
        java.util.Date today = new java.util.Date();
        return new java.sql.Timestamp(today.getTime());
    }
}
