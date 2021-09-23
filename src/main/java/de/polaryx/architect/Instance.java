package de.polaryx.architect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a property as instance of a {@link IService}. When {@link Architect} starts this property gets filled
 * with the right {@link IService} instance.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Instance {
}
