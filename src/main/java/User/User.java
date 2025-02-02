package User;

import java.sql.Timestamp;

public class User {
    private int id;
    private String username;
    private String email;
    private String passwordHash;
    private String photoProfil;
    private boolean enLigne;
    private Timestamp derniereConnexion;

    public User(int id, String username, String email, String passwordHash, String photoProfil, boolean enLigne, Timestamp derniereConnexion) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.photoProfil = photoProfil;
        this.enLigne = enLigne;
        this.derniereConnexion = derniereConnexion;
    }

    // Getters et Setters (Obligatoire pour Gson)
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPhotoProfil() { return photoProfil; }
    public boolean isEnLigne() { return enLigne; }
    public Timestamp getDerniereConnexion() { return derniereConnexion; }
}
