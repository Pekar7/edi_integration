package ru.edi.edi_integration.cfg;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Добавляем DevProfileCondition чтобы отключить KafkaListener
 * Для local - отключить, для dev - включить
 */
public class DevProfileCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment environment = context.getEnvironment();
        String conditionalContext = environment.getProperty("project.profile.conditional");
        String[] activeProfiles = environment.getActiveProfiles();
        for (String profile : activeProfiles) {
            if (conditionalContext.equalsIgnoreCase(profile)) {
                return true;
            }
        }
        return false;
    }
}
