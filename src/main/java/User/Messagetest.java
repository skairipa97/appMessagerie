package User;

import java.util.List;

public class Messagetest {
    public static void main(String[] args) {
        // Tester la récupération d'un utilisateur
        int testUserId = 1;
        User user = Database.getUserById(testUserId);

        if (user != null) {
            System.out.println("User found:");
            System.out.println("ID: " + user.getId());
            System.out.println("Username: " + user.getUsername());
            System.out.println("Email: " + user.getEmail());
            System.out.println("Profile Photo: " + user.getPhotoProfil());
            System.out.println("Online: " + user.isEnLigne());
            System.out.println("Last Connection: " + user.getDerniereConnexion());
        } else {
            System.out.println("No user found with ID " + testUserId);
        }

        // Tester la récupération des messages d'une conversation
        int conversationId = 1;  // La conversation entre users 1 et 2
        List<Message> messages = Database.getMessagesByConversation(conversationId);

        if (!messages.isEmpty()) {
            System.out.println("\nMessages in conversation " + conversationId + ":");
            for (Message message : messages) {
                System.out.println("From User " + message.getFromuser() + " to User " + message.getTouser() + ": " + message.getContenu());
                System.out.println("Sent at: " + message.getDate_envoi() +"lu?" + message.getLu());
                System.out.println("--------------------------------------");
            }
        } else {
            System.out.println("No messages found in conversation " + conversationId);
        }
    }
}
