package model;

public class LoginResponse {
    private String id;
    private String role;
    private String message;
    private String token;
    private String name; // add this

    // getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getName() { return name; } // add getter
    public void setName(String name) { this.name = name; } // add setter
}
