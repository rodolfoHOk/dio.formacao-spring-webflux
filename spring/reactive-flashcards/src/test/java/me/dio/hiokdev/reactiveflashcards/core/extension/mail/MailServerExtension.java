package me.dio.hiokdev.reactiveflashcards.core.extension.mail;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Properties;

@Slf4j
public class MailServerExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    private GreenMail smtpServer;
    private Integer port = 80;
    private final String user = "teste@teste.com.br";
    private final String password = "123456";

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        var field = Arrays.stream(extensionContext.getRequiredTestClass().getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(SMTPPort.class))
                .findFirst();
        var testInstance = extensionContext.getTestInstance();
        if (field.isPresent() && testInstance.isPresent()) {
            port = (Integer) ReflectionTestUtils.getField(testInstance.get(), field.get().getName());
        }
        log.info("==== starting mail server in port {}", port);
        smtpServer = new GreenMail(new ServerSetup(port, null, "smtp"));
        smtpServer.setUser(user, password);
        smtpServer.start();
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) throws Exception {
        log.info("==== stopping mail server");
        smtpServer.stop();
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().isAnnotationPresent(MainSender.class) ||
                parameterContext.getParameter().isAnnotationPresent(MainServer.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        if (parameterContext.getParameter().isAnnotationPresent(MainSender.class)) {
            return smtpServer;
        } else if (parameterContext.getParameter().isAnnotationPresent(MainServer.class)) {
            return createSender();
        } else {
            return null;
        }
    }

    private JavaMailSenderImpl createSender() {
        var sender = new JavaMailSenderImpl();
        sender.setHost(smtpServer.getSmtp().getServerSetup().getBindAddress());
        sender.setPort(port);
        var mailProperties = new Properties();
        mailProperties.setProperty("mail.transport.protocol", "smtp");
        mailProperties.setProperty("mail.smtp.auth", "true");
        mailProperties.setProperty("mail.smtp.starttls.enable", "true");
        mailProperties.setProperty("mail.debug", "false");
        sender.setJavaMailProperties(mailProperties);
        sender.setUsername(user);
        sender.setPassword(password);
        return sender;
    }

}
