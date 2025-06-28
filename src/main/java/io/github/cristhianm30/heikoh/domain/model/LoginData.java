package io.github.cristhianm30.heikoh.domain.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginData {
    private String token;
    private String username;
    private String email;
    private Boolean enabled;
    private String role;
    private Long id;
}
