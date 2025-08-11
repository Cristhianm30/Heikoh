package io.github.cristhianm30.heikoh.infrastructure.configuration.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.security.Principal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticatedUser implements Principal {
    private Long id;
    private String username;

    @Override
    public String getName() {
        return username;
    }
}
