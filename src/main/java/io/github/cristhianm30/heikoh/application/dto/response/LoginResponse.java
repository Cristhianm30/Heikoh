package io.github.cristhianm30.heikoh.application.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {
    private Long id;
    private String username;
    private String email;
    private LocalDateTime createdAt;
    private Boolean enabled;
    private String role;
    private String token;

}
