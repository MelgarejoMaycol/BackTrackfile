package com.TrackFile.app.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class JwtServiceTest {
    private JwtService tokens;

    @BeforeEach
    void configure() {
        tokens = new JwtService();
        ReflectionTestUtils.setField(tokens, "secret", "ci-unit-test-secret-of-at-least-32-characters-long");
        ReflectionTestUtils.setField(tokens, "expirationMinutes", 120L);
    }

    @Test
    void signedTokenCarriesSubjectAndFutureExpiration() {
        String token = tokens.generateToken("usuario-de-prueba");
        assertTrue(tokens.isValid(token));
        assertEquals("usuario-de-prueba", tokens.extractSubject(token));
        assertTrue(tokens.extractExpiration(token).getTime() > System.currentTimeMillis());
    }

    @Test
    void malformedOrModifiedTokensAreRejected() {
        assertFalse(tokens.isValid("not-a-jwt"));
        String original = tokens.generateToken("usuario-1");
        assertFalse(tokens.isValid(original + "alterado"));
    }

    @Test
    void refreshPreservesIdentityAndRejectsExpiredTokens() {
        String original = tokens.generateToken("usuario-2");
        String renewed = tokens.refreshToken(original);
        assertTrue(tokens.isValid(renewed));
        assertEquals("usuario-2", tokens.extractSubject(renewed));

        ReflectionTestUtils.setField(tokens, "expirationMinutes", -1L);
        String expired = tokens.generateToken("usuario-3");
        assertFalse(tokens.isValid(expired));
        assertThrows(RuntimeException.class, () -> tokens.refreshToken(expired));
    }
}
