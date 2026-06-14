package bg.softuni.gymbooking.security;

import bg.softuni.gymbooking.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Session-backed view of the currently logged-in user. The user id is stored in
 * the HTTP session on login, as required by the project specification.
 */
@Component
public class CurrentUser {

    public static final String USER_ID = "userId";
    public static final String USERNAME = "username";
    public static final String ROLE = "role";

    private final HttpSession session;

    public CurrentUser(HttpSession session) {
        this.session = session;
    }

    public void login(User user) {
        session.setAttribute(USER_ID, user.getId());
        session.setAttribute(USERNAME, user.getUsername());
        session.setAttribute(ROLE, user.getRole().name());
    }

    public void logout() {
        session.invalidate();
    }

    public boolean isAuthenticated() {
        return session.getAttribute(USER_ID) != null;
    }

    public UUID getId() {
        return (UUID) session.getAttribute(USER_ID);
    }

    public String getRole() {
        return (String) session.getAttribute(ROLE);
    }

    public String getUsername() {
        return (String) session.getAttribute(USERNAME);
    }
}
