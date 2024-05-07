package me.dio.hiokdev.reactiveflashcards.domain.service;

import jakarta.mail.internet.MimeMessage;
import me.dio.hiokdev.reactiveflashcards.domain.dto.MailMessageDTO;
import me.dio.hiokdev.reactiveflashcards.domain.helper.RetryHelper;
import me.dio.hiokdev.reactiveflashcards.domain.mapper.MailMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
public class MailService {

    private final String sender;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final MailMapper mailMapper;
    private final RetryHelper retryHelper;

    public MailService(
            @Value("${reactive-flashcards.mail.sender}") String sender,
            JavaMailSender mailSender,
            TemplateEngine templateEngine,
            MailMapper mailMapper,
            RetryHelper retryHelper
    ) {
        this.sender = sender;
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.mailMapper = mailMapper;
        this.retryHelper = retryHelper;
    }

    public Mono<Void> send(final MailMessageDTO mailMessage) {
        return Mono.just(mailSender.createMimeMessage())
                .flatMap(mimeMessage -> buildMessage(mimeMessage, mailMessage))
                .flatMap(this::send)
                .then();
    }

    private Mono<MimeMessage> buildMessage(MimeMessage mimeMessage, MailMessageDTO mailMessage) {
        return Mono.fromCallable(() -> {
            var helper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());
            mailMapper.toMimeMessageHelper(
                    helper, mailMessage, sender, buildTemplate(mailMessage.template(), mailMessage.variables()));
            return helper.getMimeMessage();
        });
    }

    private String buildTemplate(final String template, final Map<String, Object> variables) {
        var context = new Context(Locale.of("pt", "BR"));
        context.setVariables(variables);
        return templateEngine.process(template, context);
    }

    private Mono<Void> send(final MimeMessage mimeMessage) {
        return Mono.fromCallable(() -> {
                    mailSender.send(mimeMessage);
                    return mimeMessage;
                }).retryWhen(retryHelper.processRetry(UUID.randomUUID().toString(),
                        throwable -> throwable instanceof MailException))
                .then();
    }

}
