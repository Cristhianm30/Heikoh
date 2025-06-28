package io.github.cristhianm30.Heikoh.domain.model;
import lombok.*;


import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserModel {
    private UUID id;
    private String username;
    private String email;
    private String password;
    private Boolean enabled;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
