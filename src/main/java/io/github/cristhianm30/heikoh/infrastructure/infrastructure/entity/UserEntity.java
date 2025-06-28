package io.github.cristhianm30.Heikoh.infrastructure.infrastructure.entity;

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
    private UUID id;

    @Column("email")
    private String email;

    @Column("password")
    private String password;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

    @Column("enabled")
    private boolean enabled;

    @Column("username")
    private String username;

    @Column("role")
    private String role;





    public UserEntity(String username, String email, String password, String role, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.enabled = true;
    }
}
