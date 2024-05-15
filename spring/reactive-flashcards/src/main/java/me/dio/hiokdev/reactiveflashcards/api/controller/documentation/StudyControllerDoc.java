package me.dio.hiokdev.reactiveflashcards.api.controller.documentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import me.dio.hiokdev.reactiveflashcards.api.controller.request.AnswerQuestionRequest;
import me.dio.hiokdev.reactiveflashcards.api.controller.request.StudyRequest;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.AnswerQuestionResponse;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.ProblemResponse;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.QuestionResponse;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.StudyResponse;
import me.dio.hiokdev.reactiveflashcards.core.validation.MongoId;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Tag(name = "Study", description = "Endpoints para gerenciar estudos")
public interface StudyControllerDoc {

    @Operation(summary = "Inicia o estudo de um deck")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "O estudo foi criado e retorna a primeira pergunta gerada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = QuestionResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Bad request / Requisição inválida",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Not found / Recurso não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemResponse.class))
            )
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<QuestionResponse> start(@RequestBody @Valid StudyRequest requestBody);

    @Operation(summary = "Busca a ultima pergunta não respondida")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Retorna a ultima pergunta que não foi respondida",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = QuestionResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Bad request / Requisição inválida",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Not found / Recurso não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemResponse.class))
            )
    })
    @GetMapping(value = "{id}/current-question", produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<QuestionResponse> getCurrentQuestion(
            @Parameter(description = "Identificador do estudo", example = "66351f41c475b40f15b62591")
            @PathVariable @Valid @MongoId(message = "{studyController.id}") String id
    );

    @Operation(summary = "Endpoint para buscar todos os estudos do usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Retornar os estudos do usuário",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = StudyResponse.class)))
            ),
            @ApiResponse(responseCode = "400", description = "Bad request / Requisição inválida",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Not found / Recurso não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemResponse.class))
            )
    })
    @GetMapping(value = "user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    Flux<StudyResponse> listByUserId(
            @Parameter(description = "Identificador do usuário", example = "663510e3a3f1e05fa9f3c861")
            @PathVariable @Valid @MongoId(message = "{userController.id}") String userId
    );

    @Operation(summary = "Responde a pergunta atual")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Retorna a pergunta, a resposta fornecida e a resposta esperada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AnswerQuestionResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Bad request / Requisição inválida",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Not found / Recurso não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemResponse.class))
            )
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "{id}/answer", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<AnswerQuestionResponse> answer(
            @Parameter(description = "Identificador do estudo", example = "66351f41c475b40f15b62591")
            @PathVariable @Valid @MongoId(message = "{studyController.id}") String id,
            @RequestBody @Valid AnswerQuestionRequest requestBody
    );

}
