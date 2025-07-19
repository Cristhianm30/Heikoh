package io.github.cristhianm30.heikoh.domain.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginData {
    private String token;
    private String username;
    private String email;
    private LocalDateTime createdAt;
    private Boolean enabled;
    private String role;
    private Long id;
}
