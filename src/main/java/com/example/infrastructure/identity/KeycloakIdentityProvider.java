package com.example.infrastructure.identity;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;

import com.example.user.service.IdentityProvider;

import io.quarkus.arc.properties.IfBuildProperty;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
@IfBuildProperty(name = "app.identity.provider", stringValue = "keycloak")
public class KeycloakIdentityProvider implements IdentityProvider {

    @Inject
    Keycloak keycloak;

    @ConfigProperty(name = "app.identity.keycloak.realm")
    String targetRealm;

    @Override
    public void updateEmail(String userId, String newEmail) {
        UserRepresentation userRep = new UserRepresentation();
        userRep.setEmail(newEmail);
        userRep.setEmailVerified(true);

        keycloak.realm(targetRealm).users().get(userId).update(userRep);
    }
}
