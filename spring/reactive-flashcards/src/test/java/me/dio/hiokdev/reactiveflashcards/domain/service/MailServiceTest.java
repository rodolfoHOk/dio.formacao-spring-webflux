package me.dio.hiokdev.reactiveflashcards.domain.service;

import com.github.javafaker.Faker;
import com.icegreen.greenmail.util.GreenMail;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import me.dio.hiokdev.reactiveflashcards.core.TemplateMailConfigStub;
import me.dio.hiokdev.reactiveflashcards.core.extension.mail.MailSender;
import me.dio.hiokdev.reactiveflashcards.core.extension.mail.MailServer;
import me.dio.hiokdev.reactiveflashcards.core.extension.mail.MailServerExtension;
import me.dio.hiokdev.reactiveflashcards.core.extension.mail.SMTPPort;
import me.dio.hiokdev.reactiveflashcards.core.factorybot.RandomData;
import me.dio.hiokdev.reactiveflashcards.core.factorybot.document.DeckDocumentFactoryBot;
import me.dio.hiokdev.reactiveflashcards.core.factorybot.document.StudyDocumentFactoryBot;
import me.dio.hiokdev.reactiveflashcards.core.factorybot.dto.MailMessageDTOFactoryBot;
import me.dio.hiokdev.reactiveflashcards.core.retry.RetryConfig;
import me.dio.hiokdev.reactiveflashcards.domain.helper.RetryHelper;
import me.dio.hiokdev.reactiveflashcards.domain.mapper.MailMapper;
import me.dio.hiokdev.reactiveflashcards.domain.mapper.MailMapperImpl;
import me.dio.hiokdev.reactiveflashcards.domain.mapper.MailMapperImpl_;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thymeleaf.TemplateEngine;
import reactor.test.StepVerifier;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@ExtendWith({SpringExtension.class, MailServerExtension.class})
@ContextConfiguration(classes = {MailMapperImpl.class, MailMapperImpl_.class})
public class MailServiceTest {

    private JavaMailSender mailSender;
    private TemplateEngine templateEngine;
    @Autowired
    private MailMapper mailMapper;
    private final RetryHelper retryHelper = new RetryHelper(new RetryConfig(1L, 1L));

    @Autowired
    private ApplicationContext applicationContext;
    @SMTPPort
    private final Integer port = 8081;
    private final Faker faker = RandomData.getFaker();
    private String sender;
    private GreenMail smtpServer;
    private MailService mailService;

    @BeforeEach
    void setup(@MailServer final GreenMail smtpServer, @MailSender final JavaMailSender mailSender) {
        this.smtpServer = smtpServer;
        sender = faker.internet().emailAddress();
        templateEngine = TemplateMailConfigStub.templateEngine(applicationContext);
        mailService = new MailService(sender, mailSender, templateEngine, mailMapper, retryHelper);
    }

    @Test
    void sendTest() throws MessagingException {
        var userId = ObjectId.get().toString();
        var deck = DeckDocumentFactoryBot.builder().build();
        var study = StudyDocumentFactoryBot.builder(userId, deck).finishedStudy().build();
        var mailMessage = MailMessageDTOFactoryBot.builder(deck, study.questions()).build();

        StepVerifier.create(mailService.send(mailMessage)).verifyComplete();
        assertThat(smtpServer.getReceivedMessages().length).isOne();
        var message = Arrays.stream(smtpServer.getReceivedMessages()).findFirst().orElseThrow();
        assertThat(message.getSubject()).isEqualTo(mailMessage.subject());
        assertThat(message.getRecipients(Message.RecipientType.TO))
                .contains(new InternetAddress(mailMessage.destination()));
        assertThat(message.getHeader("FROM")).contains(sender);
    }

}
