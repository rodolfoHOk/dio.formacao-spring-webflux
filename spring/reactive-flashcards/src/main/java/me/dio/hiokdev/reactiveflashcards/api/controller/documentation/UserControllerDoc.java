package me.dio.hiokdev.reactiveflashcards.api.controller.documentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import me.dio.hiokdev.reactiveflashcards.api.controller.request.UserPageRequest;
import me.dio.hiokdev.reactiveflashcards.api.controller.request.UserRequest;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.ProblemResponse;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.UserPageResponse;
import me.dio.hiokdev.reactiveflashcards.api.controller.response.UserResponse;
import me.dio.hiokdev.reactiveflashcards.core.validation.MongoId;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import reactor.core.publisher.Mono;

@Tag(name = "User", description = "Endpoints para manipulação de usuários")
public interface UserControllerDoc {

    @Operation(summary = "Endpoint para criar um novo usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Retornar o usuário criado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Bad request / Requisição inválida",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemResponse.class))
            )
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<UserResponse> save(@Valid @RequestBody UserRequest requestBody);

    @Operation(summary = "Endpoint para buscar usuários de forma paginada")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Retornar os usuários de acordo com as informações passadas na request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserPageResponse.class))
            )
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<UserPageResponse> findOnDemand(@ParameterObject @Valid UserPageRequest request);

    @Operation(summary = "Endpoint para buscar um usuário pelo seu identificador")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Retornar o usuário correspondente ao identificador",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Bad request / Requisição inválida",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Not found / Recurso não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemResponse.class))
            )
    })
    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<UserResponse> findById(
            @Parameter(description = "Identificador do usuário", example = "66342b8418c87a1a8a8ffcb0")
            @PathVariable @Valid @MongoId(message = "{userController.id}") String id
    );

    @Operation(summary = "Endpoint para atualizar um usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Retornar o usuário atualizado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Bad request / Requisição inválida",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Not found / Recurso não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemResponse.class))
            )
    })
    @PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<UserResponse> update(
            @Parameter(description = "Identificador do usuário", example = "66342b8418c87a1a8a8ffcb0")
            @PathVariable @Valid @MongoId(message = "{userController.id}") String id,
            @RequestBody @Valid UserRequest requestBody
    );

    @Operation(summary = "Endpoint para excluir um usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "O usuário foi excluido"),
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
            @Parameter(description = "Identificador do usuário", example = "66342b8418c87a1a8a8ffcb0")
            @PathVariable @Valid @MongoId(message = "{userController.id}") String id
    );

}
