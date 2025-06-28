package io.github.cristhianm30.heikoh.infrastructure.persistence.repository;

import io.github.cristhianm30.heikoh.infrastructure.entity.UserEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;


public interface UserRepository extends ReactiveCrudRepository<UserEntity, Long> {

    Mono<UserEntity> findByUsername(String username);

    Mono<Boolean> existsByUsername(String username);

    Mono<UserEntity> findByEmail(String email);

    Mono<Boolean> existsByEmail(String email);

    Mono<UserEntity> findByUsernameOrEmail(String username, String email);

}
