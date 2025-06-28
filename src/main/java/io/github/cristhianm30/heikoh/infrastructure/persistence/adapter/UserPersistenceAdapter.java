package io.github.cristhianm30.heikoh.infrastructure.persistence.adapter;

import io.github.cristhianm30.heikoh.domain.model.UserModel;
import io.github.cristhianm30.heikoh.domain.port.out.UserRepositoryPort;
import io.github.cristhianm30.heikoh.infrastructure.entity.UserEntity;
import io.github.cristhianm30.heikoh.infrastructure.mapper.IUserEntityMapper;
import io.github.cristhianm30.heikoh.infrastructure.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Repository
@Transactional
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final UserRepository UserRepository;
    private final IUserEntityMapper userEntityMapper;

    @Override
    public Mono<UserModel> save(UserModel user) {

        UserEntity userEntity = userEntityMapper.toEntity(user);
        return UserRepository.save(userEntity)
                .map(userEntityMapper::toModel);
    }

    @Override
    public Mono<UserModel> findByUsername(String username) {
        return UserRepository.findByUsername(username)
                .map(userEntityMapper::toModel);
    }

    @Override
    public Mono<UserModel> findById(Long id) {
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

    @Override
    public Mono<UserModel> findByUsernameOrEmail(String username, String email) {
        return UserRepository.findByUsernameOrEmail(username, email)
                .map(userEntityMapper::toModel);
    }


}