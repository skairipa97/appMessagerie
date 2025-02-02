package User;
import java.time.LocalDateTime;

public class Conversation {
    private int id;
    private LocalDateTime createdAt;
    private int user1;
    private int user2;

    public Conversation(int id, LocalDateTime createdAt, int user1, int user2) {
        this.id = id;
        this.createdAt = createdAt;
        this.user1 = user1;
        this.user2 = user2;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getUser1() {
        return user1;
    }

    public void setUser1(int user1) {
        this.user1 = user1;
    }

    public int getUser2() {
        return user2;
    }

    public void setUser2(int user2) {
        this.user2 = user2;
    }
}
