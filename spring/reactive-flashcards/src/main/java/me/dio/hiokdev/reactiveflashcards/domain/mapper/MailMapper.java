package me.dio.hiokdev.reactiveflashcards.domain.mapper;

import jakarta.mail.MessagingException;
import me.dio.hiokdev.reactiveflashcards.domain.document.DeckDocument;
import me.dio.hiokdev.reactiveflashcards.domain.document.StudyDocument;
import me.dio.hiokdev.reactiveflashcards.domain.document.UserDocument;
import me.dio.hiokdev.reactiveflashcards.domain.dto.MailMessageDTO;
import org.mapstruct.DecoratedWith;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.mail.javamail.MimeMessageHelper;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
@DecoratedWith(MainMapperDecorator.class)
public interface MailMapper {

    @Mapping(target = "username", source = "user.name")
    @Mapping(target = "destination", source = "user.email")
    @Mapping(target = "subject", constant = "Relatório de estudos")
    @Mapping(target = "template", defaultValue = "mail/studyResult")
    @Mapping(target = "deck", source = "deck")
    @Mapping(target = "questions", source = "study.questions")
    MailMessageDTO toDTO(final StudyDocument study, final DeckDocument deck, final UserDocument user);

    @Mapping(target = "to", expression = "java(new String[]{mailMessageDTO.destination()})")
    @Mapping(target = "from", source = "sender")
    @Mapping(target = "subject", source = "mailMessageDTO.subject")
    @Mapping(target = "fileTypeMap", ignore = true)
    @Mapping(target = "encodeFilenames", ignore = true)
    @Mapping(target = "validateAddresses", ignore = true)
    @Mapping(target = "replyTo", ignore = true)
    @Mapping(target = "cc", ignore = true)
    @Mapping(target = "bcc", ignore = true)
    @Mapping(target = "priority", ignore = true)
    @Mapping(target = "sentDate", ignore = true)
    @Mapping(target = "text", ignore = true)
    @Mapping(target = "mimeMessage", ignore = true)
    @Mapping(target = "mimeMultipart", ignore = true)
    @Mapping(target = "rootMimeMultipart", ignore = true)
    MimeMessageHelper toMimeMessageHelper(
            @MappingTarget final MimeMessageHelper helper,
            final MailMessageDTO mailMessageDTO,
            final String sender,
            final String body
    ) throws MessagingException;

}
