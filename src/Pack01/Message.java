package Pack01;

import java.io.Serializable;

public class Message implements Serializable {
    String type;
    int last,id;
    public Message(String type, int last, int id) {
        this.type = type;
        this.last = last;
        this.id = id;
    }
    public Message(String type, int id) {
        this.type = type;
        this.id = id;
    }
    public String toString() {
        if (last==0)
        return type + " " + id;
        else return type + " " + last + " " + id;
    }
}
