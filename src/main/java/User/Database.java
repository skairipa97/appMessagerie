package User;

import java.sql.*;
import java.util.*;
import java.time.LocalDateTime;

public class Database {

    private static final String URL = "jdbc:postgresql://localhost:5432/GTX";
    private static final String DB_USER = "gtx";
    private static final String DB_PASSWORD = "CARD24";

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Pilote PostgreSQL introuvable", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
    }

    public static User getUserById(int userId) {
        String query = "SELECT id, username, email, password_hash, photo_profil, en_ligne, derniere_connexion FROM USERS WHERE id = ?";
        User user = null;

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getString("password_hash"),
                            rs.getString("photo_profil"),
                            rs.getBoolean("en_ligne"),
                            rs.getTimestamp("derniere_connexion")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public static List<Message> getMessagesByConversation(int conversationId) {
        List<Message> messages = new ArrayList<>();
        String query = "SELECT id, fromuser, touser, contenu, date_envoi, lu, conversation FROM message WHERE conversation = ? ORDER BY date_envoi ASC";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, conversationId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Message message = new Message(
                            rs.getInt("id"),
                            rs.getInt("fromuser"),
                            rs.getInt("touser"),
                            rs.getString("contenu"),
                            rs.getTimestamp("date_envoi"),
                            rs.getBoolean("lu"),
                            rs.getInt("conversation") // Ajout du champ manquant
                    );
                    messages.add(message);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des messages pour la conversation ID: " + conversationId);
            e.printStackTrace();
        }
        return messages;
    }




    public static void updateUserStatus(int userId, boolean online) {
            String sql = "UPDATE users SET en_ligne = ?, derniere_connexion = ? WHERE id = ?";

            try (Connection conn = getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setBoolean(1, online);
                pstmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                pstmt.setInt(3, userId);

                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public static void markMessageAsRead(int messageId) {
            String sql = "UPDATE message SET lu = true WHERE id = ?";

            try (Connection conn = getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, messageId);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Update user's last connection time
        public static void updateUserLastConnection(int userId) {
            try (Connection conn = getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(
                         "UPDATE users SET derniere_connexion = CURRENT_TIMESTAMP, en_ligne = true WHERE id = ?")) {
                pstmt.setInt(1, userId);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Mark messages as read
        public static void markMessagesAsRead(int conversationId, int userId) {
            try (Connection conn = getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(
                         "UPDATE message SET lu = true " +
                                 "WHERE conversation = ? AND touser = ? AND lu = false")) {
                pstmt.setInt(1, conversationId);
                pstmt.setInt(2, userId);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    public static Message saveMessage(Message message) {
        String sql = "INSERT INTO message (fromuser, touser, contenu, date_envoi, lu, conversation) " +
                "VALUES (?, ?, ?, ?, ?, ?) RETURNING id, fromuser, touser, contenu, date_envoi, lu, conversation";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, message.getFromuser());
            pstmt.setInt(2, message.getTouser());
            pstmt.setString(3, message.getContenu());
            pstmt.setTimestamp(4, message.getDate_envoi());
            pstmt.setBoolean(5, message.getLu());
            pstmt.setInt(6, message.getConversation());

            System.out.println("Executing SQL: " + pstmt.toString());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Message savedMessage = new Message();
                    savedMessage.setId(rs.getInt("id"));
                    savedMessage.setFromuser(rs.getInt("fromuser"));
                    savedMessage.setTouser(rs.getInt("touser"));
                    savedMessage.setContenu(rs.getString("contenu"));
                    savedMessage.setDate_envoi(rs.getTimestamp("date_envoi"));
                    savedMessage.setLu(rs.getBoolean("lu"));
                    savedMessage.setConversation(rs.getInt("conversation"));
                    return savedMessage;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving message: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static Conversation getConversationByUserId(int userId) {
        String sql = "SELECT * FROM conversation WHERE user1 = ? OR user2 = ? " +
                "ORDER BY created_at DESC LIMIT 1";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);

            System.out.println("Executing conversation query for userId: " + userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Conversation conv = new Conversation(
                            rs.getInt("id"),
                            rs.getTimestamp("created_at").toLocalDateTime(),
                            rs.getInt("user1"),
                            rs.getInt("user2")
                    );
                    System.out.println("Found conversation: " + conv.toString());
                    return conv;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting conversation: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isUserInConversation(int conversationId, int userId) {
        String sql = "SELECT 1 FROM conversation WHERE id = ? AND (user1 = ? OR user2 = ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, conversationId);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error checking user in conversation: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

}
