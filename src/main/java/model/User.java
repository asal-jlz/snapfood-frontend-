package model;

public class User {
    private static User instance;
    private String id;
    private String fullName;
    private String phone;
    private String email;
    private String address;
    private String profilePhotoPath;
    private String profileImagePath;
    private String token;
    private String role;
    private BankInfo bankInfo;
    private String profileImageBase64;

    public User() {}

    public static User getInstance() {
        if (instance == null) {
            instance = new User();
        }
        return instance;
    }

    // Getters
    public String getId() { return id; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public String getProfilePhotoPath() { return profilePhotoPath; }
    public  String getToken() { return token; }
    public String getRole() { return role; }
    public void setRole(String role) {
        this.role = role;
    }

    public void setId(String id) { this.id = id; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setAddress(String address) { this.address = address; }
    public void setProfilePhotoPath(String profilePhotoPath) { this.profilePhotoPath = profilePhotoPath; }
    public void setToken(String token) { this.token = token; }
    public BankInfo getBankInfo() { return bankInfo; }
    public void setBankInfo(BankInfo bankInfo) { this.bankInfo = bankInfo; }
    public String getProfileImageBase64() { return profileImageBase64; }
    public void setProfileImageBase64(String base64) { this.profileImageBase64 = base64; }
}

