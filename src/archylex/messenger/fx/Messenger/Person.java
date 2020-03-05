package archylex.messenger.fx.Messenger;

public class Person {
    private String name;
    private String lastname;
    private String snid;
    private String snname;
    private String photo_url;

    public String getName() {
        return name;
    }

    public String getLastname() {
        return lastname;
    }

    public String getSNID() {
        return snid;
    }

    public String getSNName() {
        return snname;
    }

    public String getPhotoURL() {
        return photo_url;
    }

    public Person(String name, String lastname, String snid, String snname, String photo_url) {
        super();
        this.name = name;
        this.lastname = lastname;
        this.snid = snid;
        this.snname = snname;
        this.photo_url = photo_url;
    }
}
