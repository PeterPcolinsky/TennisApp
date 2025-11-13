package sk.peter.tenis.annotations;

import org.springframework.context.annotation.Import;
import sk.peter.tenis.config.TestSecurityConfig;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(TestSecurityConfig.class)
public @interface TestWithoutSecurity {
}