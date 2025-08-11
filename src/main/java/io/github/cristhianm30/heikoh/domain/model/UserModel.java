package io.github.cristhianm30.heikoh.domain.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserModel {
    private Long id;
    private String username;
    private String email;
    private String password;
    private Boolean enabled;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
