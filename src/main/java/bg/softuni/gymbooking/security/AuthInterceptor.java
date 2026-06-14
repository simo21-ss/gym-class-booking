package bg.softuni.gymbooking.security;

import bg.softuni.gymbooking.entity.enums.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

/**
 * Enforces {@link RequireRole} on controller handlers. Guests hitting a
 * protected endpoint are redirected to login; authenticated users with the
 * wrong role get a 403.
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);
        if (requireRole == null) {
            return true;
        }

        HttpSession session = request.getSession(false);
        Object userId = session == null ? null : session.getAttribute(CurrentUser.USER_ID);
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }

        String role = (String) session.getAttribute(CurrentUser.ROLE);
        boolean allowed = Arrays.stream(requireRole.value())
                .map(Role::name)
                .anyMatch(allowedRole -> allowedRole.equals(role));
        if (!allowed) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not allowed to perform this action");
            return false;
        }

        return true;
    }
}
