package io.github.cristhianm30.Heikoh.infrastructure.infrastructure.persistence.adapter;

import io.github.cristhianm30.Heikoh.domain.model.UserModel;
import io.github.cristhianm30.Heikoh.domain.port.out.UserRepositoryPort;
import io.github.cristhianm30.Heikoh.infrastructure.infrastructure.entity.UserEntity;
import io.github.cristhianm30.Heikoh.infrastructure.infrastructure.mapper.IUserEntityMapper;
import io.github.cristhianm30.Heikoh.infrastructure.infrastructure.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final UserRepository UserRepository;
    private final IUserEntityMapper userEntityMapper;

    @Override
    public Mono<UserModel> save(UserModel user) {

        UserEntity userEntity = userEntityMapper.toEntity(user);

        if (userEntity.getId() == null) {
            userEntity.setId(UUID.randomUUID());

        }

        return UserRepository.save(userEntity)
                .map(userEntityMapper::toModel);
    }

    @Override
    public Mono<UserModel> findByUsername(String username) {
        return UserRepository.findByUsername(username)
                .map(userEntityMapper::toModel);
    }

    @Override
    public Mono<UserModel> findById(UUID id) {
        return UserRepository.findById(id)
                .map(userEntityMapper::toModel);
    }

    @Override
    public Mono<Boolean> existsByUsername(String username) {
        return UserRepository.existsByUsername(username);
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return UserRepository.existsByEmail(email);
    }

}