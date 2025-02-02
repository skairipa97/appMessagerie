package User;

public class DatabaseTest {
    public static void main(String[] args) {
        // Test user ID (you can change this to the ID you want to test with)
        int testUserId = 1;

        // Call the getUserById method
        User user = Database.getUserById(testUserId);

        // Print the result
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
    }
}
