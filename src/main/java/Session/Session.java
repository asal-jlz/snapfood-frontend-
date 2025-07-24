package Session;

import model.Seller;
import model.User;

public class Session {
    private static User loggedInUser;
    private static Seller currentSeller;

    public static void setUser(User user) {
        loggedInUser = user;
    }

    public static User getUser() {
        return loggedInUser;
    }

    public static void setCurrentSeller(Seller seller) {
        currentSeller = seller;
    }

    public static Seller getCurrentSeller() {
        return currentSeller;
    }
}
