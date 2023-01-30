package tm;

public class ToDoTM {
    private String id;
    private String description;
    private String user_name;

    public ToDoTM() {

    }

    public ToDoTM(String id, String description, String user_name) {
        this.id = id;
        this.description = description;
        this.user_name = user_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    @Override
    public String toString() {
        return description;
    }
}
