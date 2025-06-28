package io.github.cristhianm30.heikoh.application.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterUserRequest {
    private String username;
    private String email;
    private String password;
}