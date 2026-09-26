package com.workouttracker.dto;

public class JwtResponseDTO {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;

    // True for usernames in ADMIN_USERNAMES
    private boolean admin;

    // True after an admin reset: the app asks for a new password first
    private boolean mustChangePassword;

    public JwtResponseDTO(String token, Long id, String username, String email) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public JwtResponseDTO(String token, Long id, String username, String email,
                          boolean admin, boolean mustChangePassword) {
        this(token, id, username, email);
        this.admin = admin;
        this.mustChangePassword = mustChangePassword;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isAdmin() { return admin; }
    public void setAdmin(boolean admin) { this.admin = admin; }

    public boolean isMustChangePassword() { return mustChangePassword; }
    public void setMustChangePassword(boolean mustChangePassword) { this.mustChangePassword = mustChangePassword; }
}
