package me.dio.hiokdev.reactiveflashcards.domain.service.query;

import me.dio.hiokdev.reactiveflashcards.domain.repository.UserRepository;
import me.dio.hiokdev.reactiveflashcards.domain.repository.UserRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class UserQueryServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserRepositoryImpl userRepositoryImpl;
    private UserQueryService userQueryService;

    @BeforeEach
    void setup() {
        this.userQueryService = new UserQueryService(userRepository, userRepositoryImpl);
    }

    

}
