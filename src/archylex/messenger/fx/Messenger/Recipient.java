package archylex.messenger.fx.Messenger;

public class Recipient {
    private static String snid = null;
    private static String snname = null;

    public static String getPersonID() {
        return snid;
    }

    public static String getSocialNetworkName() {
        return snname;
    }

    public void setRecipient(String snid, String snname) {
        this.snid = snid;
        this.snname = snname;
    }
}
