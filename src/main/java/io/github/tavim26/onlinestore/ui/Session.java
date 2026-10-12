package io.github.tavim26.onlinestore.ui;

import io.github.tavim26.onlinestore.model.Role;
import io.github.tavim26.onlinestore.model.User;

/** Holds the state of the current login session. */
public final class Session {

    private static User currentUser;
    private static Cart cart = new Cart();

    private Session() {
    }

    public static void login(User user) {
        currentUser = user;
        cart = new Cart();
    }

    public static void logout() {
        currentUser = null;
        cart = new Cart();
    }

    /** Replaces the stored user with fresh data from the database. */
    public static void updateUser(User user) {
        currentUser = user;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static User currentUser() {
        if (currentUser == null) {
            throw new IllegalStateException("No user is logged in.");
        }
        return currentUser;
    }

    public static boolean isAdmin() {
        return currentUser != null && currentUser.getRole() == Role.ADMIN;
    }

    public static Cart cart() {
        return cart;
    }
}