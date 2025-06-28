package io.github.cristhianm30.heikoh.application.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {
    private Long id;
    private String username;
    private String email;
    private Boolean enabled;
    private String role;
    private String token;

}
