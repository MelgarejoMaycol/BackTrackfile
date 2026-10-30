package com.TrackFile.app.config;

import java.util.Locale;
import java.util.Set;

/** Identidades de prueba existentes; no son permisos para modificar datos. */
public final class DemoAccounts {
    private static final Set<String> EMAILS = Set.of(
            "empresademo@test.com",
            "propietario@test.com",
            "conductor@test.com"
    );

    private DemoAccounts() {}

    public static boolean isDemo(String email) {
        return email != null && EMAILS.contains(email.trim().toLowerCase(Locale.ROOT));
    }
}
