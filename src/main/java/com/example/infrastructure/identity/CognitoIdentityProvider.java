package com.example.infrastructure.identity;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.example.user.service.IdentityProvider;

import io.quarkus.arc.properties.IfBuildProperty;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminUpdateUserAttributesRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;

@ApplicationScoped
@IfBuildProperty(name = "app.identity.provider", stringValue = "cognito")
public class CognitoIdentityProvider implements IdentityProvider {

    @Inject
    CognitoIdentityProviderClient cognito;

    @ConfigProperty(name = "app.identity.cognito.user-pool-id")
    String userPoolId;

    @Override
    public void updateEmail(String userId, String newEmail) {
        AttributeType emailAttr = AttributeType.builder()
                .name("email")
                .value(newEmail)
                .build();

        AttributeType verifiedAttr = AttributeType.builder()
                .name("email_verified")
                .value("true")
                .build();

        AdminUpdateUserAttributesRequest request = AdminUpdateUserAttributesRequest.builder()
                .userPoolId(userPoolId)
                .username(userId)
                .userAttributes(emailAttr, verifiedAttr)
                .build();

        cognito.adminUpdateUserAttributes(request);
    }
}
