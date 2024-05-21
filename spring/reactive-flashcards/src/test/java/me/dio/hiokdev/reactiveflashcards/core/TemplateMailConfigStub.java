package me.dio.hiokdev.reactiveflashcards.core;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TemplateMailConfigStub {

    public static SpringResourceTemplateResolver templateResolver(final ApplicationContext applicationContext) {
        var templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setPrefix("/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setApplicationContext(applicationContext);
        return templateResolver;
    }

    public static SpringTemplateEngine templateEngine(final ApplicationContext applicationContext){
        var templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver(applicationContext));
        templateEngine.setEnableSpringELCompiler(true);
        templateEngine.addDialect(new Java8TimeDialect());
        return templateEngine;
    }

}
