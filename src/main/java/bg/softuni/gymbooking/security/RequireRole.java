package bg.softuni.gymbooking.security;

import bg.softuni.gymbooking.entity.enums.Role;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a controller handler as requiring an authenticated user with one of the
 * given roles. Enforced by {@link AuthInterceptor}. Absence of this annotation
 * means the endpoint is public.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {

    Role[] value();
}
