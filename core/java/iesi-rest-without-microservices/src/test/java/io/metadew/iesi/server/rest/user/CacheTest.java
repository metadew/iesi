package io.metadew.iesi.server.rest.user;

import io.metadew.iesi.metadata.definition.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class CacheTest {

    @Test
    public void test() {
        Map<String, Object> userMap = UserBuilder.generateUser("test", new HashSet<>(), "iesi", new HashSet<>());
        User user = (User) userMap.get("user");
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("user", user);

        String username = (String) parser.parseExpression("#user.username").getValue(context);
        UUID uuid = (UUID) parser.parseExpression("#user.metadataKey.uuid").getValue(context);
    }
}
