package io.github.cristhianm30.heikoh.infrastructure.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("users")
public class UserEntity {

    @Id
    private Long id;

    @Column("email")
    private String email;

    @Column("password")
    private String password;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

    @Column("enabled")
    private Boolean enabled;

    @Column("username")
    private String username;

    @Column("role")
    private String role;

}
