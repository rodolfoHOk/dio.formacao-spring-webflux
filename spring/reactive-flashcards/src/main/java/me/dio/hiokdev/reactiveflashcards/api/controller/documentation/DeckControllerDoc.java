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
import me.dio.hiokdev.reactiveflashcards.api.controller.request.DeckRequest;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.DeckResponse;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.ProblemResponse;
import me.dio.hiokdev.reactiveflashcards.core.validation.MongoId;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Tag(name = "Deck", description = "Endpoints para manipulação de decks")
public interface DeckControllerDoc {

    @Operation(summary = "Endpoint para criar um novo deck")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Retornar o deck criado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DeckResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Bad request / Requisição inválida",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemResponse.class))
            )
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<DeckResponse> save(@Valid @RequestBody DeckRequest requestBody);

    @Operation(summary = "Endpoint para buscar decks de uma API terceira")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Foram incluidos na base novos decks"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error / Erro do lado do servidor",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemResponse.class))
            )
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping(value = "sync")
    Mono<Void> sync();

    @Operation(summary = "Endpoint para buscar um deck pelo seu identificador")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Retornar o deck correspondente ao identificador",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DeckResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Bad request / Requisição inválida",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Not found / Recurso não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemResponse.class))
            )
    })
    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<DeckResponse> findById(
            @Parameter(description = "Identificador do deck", example = "6633f853d783b72353b83013")
            @PathVariable @Valid @MongoId(message = "{deckController.id}") String id
    );

    @Operation(summary = "Endpoint para buscar todos os decks")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Retornar os decks cadastrados",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = DeckResponse.class)))
            )
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    Flux<DeckResponse> findAll();

    @Operation(summary = "Endpoint para atualizar um deck")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "O deck foi atualizado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DeckResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Bad request / Requisição inválida",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Not found / Recurso não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemResponse.class))
            )
    })
    @PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<DeckResponse> update(
            @Parameter(description = "Identificador do deck", example = "6633f853d783b72353b83013")
            @PathVariable @Valid @MongoId(message = "{deckController.id}") String id,
            @RequestBody @Valid DeckRequest requestBody
    );

    @Operation(summary = "Endpoint para excluir um deck")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "O deck foi excluido"),
            @ApiResponse(responseCode = "400", description = "Bad request / Requisição inválida",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Not found / Recurso não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemResponse.class))
            )
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "{id}")
    Mono<Void> delete(
            @Parameter(description = "Identificador do deck", example = "6633f853d783b72353b83013")
            @PathVariable @Valid @MongoId(message = "{deckController.id}") String id
    );

}
