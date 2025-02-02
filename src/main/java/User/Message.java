package User;

import java.sql.Timestamp;

public class Message {
    private int id;
    private int fromuser;
    private int touser;
    private String contenu;
    private Timestamp date_envoi;
    private boolean lu;
    private int conversation;

    public Message(int id, int senderId, int receiverId, String content, Timestamp timestamp, boolean lu, int conversation) {
        this.id = id;
        this.fromuser = senderId;
        this.touser = receiverId;
        this.contenu = content;
        this.date_envoi = timestamp;
        this.lu = lu;
        this.conversation = conversation;
    }

    public Message() {

    }

    // Getters
    public int getId() { return id; }
    public int getFromuser() { return fromuser; }
    public int getTouser() { return touser; }
    public String getContenu() { return contenu; }
    public Timestamp getDate_envoi() { return date_envoi; }
    public boolean getLu() { return lu; }
    public int getConversation() { return conversation; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setFromuser(int fromuser) { this.fromuser = fromuser; }
    public void setTouser(int touser) { this.touser = touser; }
    public void setContenu(String contenu) { this.contenu = contenu; }
    public void setDate_envoi(Timestamp date_envoi) { this.date_envoi = date_envoi; }
    public void setLu(boolean lu) { this.lu = lu; }
    public void setConversation(int conversation) { this.conversation = conversation; }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", fromuser=" + fromuser +
                ", touser=" + touser +
                ", contenu='" + contenu + '\'' +
                ", date_envoi=" + date_envoi +
                ", lu=" + lu +
                ", conversation=" + conversation +
                '}';
    }
}
