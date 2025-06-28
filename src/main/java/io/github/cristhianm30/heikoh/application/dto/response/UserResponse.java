package io.github.cristhianm30.Heikoh.application.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private UUID id;
    private String username;
    private String email;
    private Boolean enabled;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}