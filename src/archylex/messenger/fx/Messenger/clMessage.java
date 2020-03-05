package archylex.messenger.fx.Messenger;

public class clMessage {
    private String text;
    private String snname;
    private String branch_id;
    private String person_id;
    private java.sql.Timestamp timestamp;

    public String getText() {
        return text;
    }

    public String getBranchID() {
        return branch_id;
    }

    public String getSenderID() {
        return person_id;
    }

    public String getSNName() {
        return snname;
    }

    public java.sql.Timestamp getTimeStamp() {
        return timestamp;
    }


    public clMessage(String snnname, String branch_id, String person_id, String text, java.sql.Timestamp timestamp) {
        super();

        this.snname = snnname;
        this.branch_id = branch_id;
        this.person_id = person_id;
        this.text = text;
        this.timestamp = timestamp;
    }
}
