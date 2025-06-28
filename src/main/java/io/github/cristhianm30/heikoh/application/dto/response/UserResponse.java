package io.github.cristhianm30.heikoh.application.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private Boolean enabled;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}