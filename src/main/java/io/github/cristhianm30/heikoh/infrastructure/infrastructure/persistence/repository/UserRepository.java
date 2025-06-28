package io.github.cristhianm30.Heikoh.infrastructure.infrastructure.persistence.repository;

import io.github.cristhianm30.Heikoh.infrastructure.infrastructure.entity.UserEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserRepository extends ReactiveCrudRepository<UserEntity, UUID> {

    Mono<UserEntity> findByUsername(String username);

    Mono<Boolean> existsByUsername(String username);

    Mono<UserEntity> findByEmail(String email);

    Mono<Boolean> existsByEmail(String email);
}
