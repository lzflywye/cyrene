package com.example.user.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.example.user.entity.UserProfile;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@QuarkusTest
public class UserSyncServiceTest {

    @Inject
    UserSyncService userSyncService;

    @InjectMock
    IdentityProvider identityProvider;

    @Test
    @Transactional
    public void testSyncEmail_Success() {
        UserProfile user = new UserProfile();
        user.userId = "test-user-1";
        user.email = "new@example.com";
        user.isEmailSynced = false;
        user.persist();

        userSyncService.syncEmail("test-user-1", "new@example.com");

        verify(identityProvider, Mockito.times(1)).updateEmail("test-user-1", "new@example.com");

        UserProfile updatedUser = UserProfile.findByUserId("test-user-1");
        assertTrue(updatedUser.isEmailSynced, "The sync flag is set to true");
    }

    @Test
    public void testSyncEmail_Failure() {
        doThrow(new RuntimeException("AWS Error"))
                .when(identityProvider).updateEmail(anyString(), anyString());

        assertThrows(RuntimeException.class, () -> {
            userSyncService.syncEmail("error-user", "new@example.com");
        });
    }
}
