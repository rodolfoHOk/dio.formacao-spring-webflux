package me.dio.hiokdev.reactiveflashcards.domain.mapper;

import jakarta.mail.MessagingException;
import me.dio.hiokdev.reactiveflashcards.domain.dto.MailMessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.MimeMessageHelper;

public abstract class MainMapperDecorator implements MailMapper {

    @Autowired
    @Qualifier("delegate")
    private MailMapper mailMapper;

    @Override
    public MimeMessageHelper toMimeMessageHelper(
            MimeMessageHelper helper,
            MailMessageDTO mailMessageDTO,
            String sender,
            String body
    ) throws MessagingException {
        mailMapper.toMimeMessageHelper(helper, mailMessageDTO, sender, body);
        helper.setText(body, true);
        return helper;
    }

}
