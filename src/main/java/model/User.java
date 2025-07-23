package model;

public class User{
    private static User instance;
    private String id;
    private String fullName;
    private String profileImagePath;
    private double monthlyEarnings;
    private String profilePhotoPath;
    private String token;

    private User() {}

    public static User getInstance() {
        if (instance == null) {
            instance = new User();
        }
        return instance;
    }

    public String getFullName() {
        return fullName;
    }

    public String getProfileImagePath() {
        return profileImagePath;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }

    public String getId() { return id; }
    public double getMonthlyEarnings() { return monthlyEarnings; }
    public String getProfilePhotoPath() { return profilePhotoPath; }
    public String getToken() { return token; }

}
