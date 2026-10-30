package com.TrackFile.app.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DemoAccountsTest {

    @Test
    void knownDemoEmailsAreRecognizedRegardlessOfCaseAndWhitespace() {
        assertTrue(DemoAccounts.isDemo("empresademo@test.com"));
        assertTrue(DemoAccounts.isDemo(" PROPIETARIO@TEST.COM "));
        assertTrue(DemoAccounts.isDemo("conductor@test.com"));
    }

    @Test
    void regularUsersNeverBecomeDemoUsers() {
        assertFalse(DemoAccounts.isDemo("usuario@empresa.com"));
        assertFalse(DemoAccounts.isDemo("demo@empresa.com"));
        assertFalse(DemoAccounts.isDemo(""));
        assertFalse(DemoAccounts.isDemo(null));
    }
}
