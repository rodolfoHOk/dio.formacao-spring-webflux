package me.dio.hiokdev.reactiveflashcards.api.controller.study;


import me.dio.hiokdev.reactiveflashcards.api.controller.AbstractControllerTest;
import me.dio.hiokdev.reactiveflashcards.api.controller.StudyController;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.AnswerQuestionResponse;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.ProblemResponse;
import me.dio.hiokdev.reactiveflashcards.api.mapper.StudyMapperImpl;
import me.dio.hiokdev.reactiveflashcards.domain.service.StudyService;
import me.dio.hiokdev.reactiveflashcards.domain.service.query.StudyQueryService;
import me.dio.hiokdev.reactiveflashcards.utils.request.RequestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(StudyController.class)
@ContextConfiguration(classes = {StudyMapperImpl.class})
public class StudyControllerAnswerTest extends AbstractControllerTest {

    @MockBean
    private StudyService studyService;
    @MockBean
    private StudyQueryService studyQueryService;
    private RequestBuilder<AnswerQuestionResponse> answerQuestionResponseRequestBuilder;
    private RequestBuilder<ProblemResponse> problemResponseRequestBuilder;

    @BeforeEach
    void setup() {
        answerQuestionResponseRequestBuilder = RequestBuilder
                .answerQuestionResponseRequestBuilder(applicationContext, "/studies");
        problemResponseRequestBuilder = RequestBuilder
                .problemResponseRequestBuilder(applicationContext, "/studies");
    }

}
