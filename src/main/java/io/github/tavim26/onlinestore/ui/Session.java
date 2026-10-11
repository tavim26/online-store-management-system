package io.github.tavim26.onlinestore.ui;

import io.github.tavim26.onlinestore.model.Role;
import io.github.tavim26.onlinestore.model.User;

/** Holds the state of the current login session. */
public final class Session {

    private static User currentUser;

    private Session() {
    }

    public static void login(User user) {
        currentUser = user;
    }

    public static void logout() {
        currentUser = null;
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
}