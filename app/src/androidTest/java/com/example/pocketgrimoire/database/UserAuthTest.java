package com.example.pocketgrimoire.database;

import static org.junit.Assert.*;

import android.app.Application;
import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.pocketgrimoire.database.PocketGrimoireDatabase;
import com.example.pocketgrimoire.database.entities.User;
import com.example.pocketgrimoire.viewmodel.LoginViewModel;
import com.example.pocketgrimoire.viewmodel.RegistrationViewModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@RunWith(AndroidJUnit4.class)
public class UserAuthTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private Context context;
    private RegistrationViewModel registrationVM;
    private LoginViewModel loginVM;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();

        // Start each test with a clean DB
        context.deleteDatabase(PocketGrimoireDatabase.DB_NAME);

        registrationVM = new RegistrationViewModel((Application) context);
        loginVM = new LoginViewModel((Application) context);
    }

    @After
    public void tearDown() {
        context.deleteDatabase(PocketGrimoireDatabase.DB_NAME);
    }

    // normal user flow: register account, then successful login
    @Test
    public void register_then_login_success() throws Exception {
        String email = "alice@example.com";
        String username = "alice";
        String password = "Sup3r$ecret!";

        registrationVM.registerUser(email, username, password, password);

        // Wait for the ViewModel to post completion from the Completable
        Boolean registered = getOrAwaitValue(registrationVM.getRegistrationSuccess(), 6, TimeUnit.SECONDS);
        assertNotNull(registered);
        assertTrue(registered);

        loginVM.login(username, password);
        User loggedIn = getOrAwaitValue(loginVM.getLoginSuccess(), 6, TimeUnit.SECONDS);
        assertNotNull("Login should return a User", loggedIn);
        assertEquals(email, loggedIn.getEmail());
        assertEquals(username, loggedIn.getUsername());
    }

    // Duplicate username should fail registration
    @Test
    public void register_duplicate_username_fails() throws Exception {
        String email1 = "bob1@example.com";
        String email2 = "bob2@example.com";
        String username = "bob";
        String password = "A_strong_Passw0rd!";

        registrationVM.registerUser(email1, username, password, password);
        Thread.sleep(250);

        registrationVM.registerUser(email2, username, password, password);
        // Expected: VM posts errorMessage; Current: second insert succeeds.
        String err = getOrAwaitValue(registrationVM.getErrorMessage(), 3, TimeUnit.SECONDS);
        assertNotNull("Expected an error message for duplicate username", err);
    }

    // Invalid email should fail validation
    @Test
    public void register_invalid_email_fails() throws Exception {
        registrationVM.registerUser("not-an-email", "daisy", "Password123!", "Password123!");
        String err = getOrAwaitValue(registrationVM.getErrorMessage(), 3, TimeUnit.SECONDS);
        assertNotNull("Expected error message for invalid email", err);
    }

    // Different passwords should fail registration
    @Test
    public void register_password_mismatch_fails() throws Exception {
        registrationVM.registerUser("eve@example.com", "eve", "Password123!", "PasswordDIFF!");
        String err = getOrAwaitValue(registrationVM.getErrorMessage(), 4, TimeUnit.SECONDS);
        assertNotNull("Expected error message for mismatched passwords", err);
    }

    // Wrong password should fail login
    @Test
    public void login_wrong_password_fails() throws Exception {
        String email = "frank@example.com";
        String username = "frank";
        String password = "Correct#123";
        String wrong = "Wrong#123";

        registrationVM.registerUser(email, username, password, password);
        Boolean registered = getOrAwaitValue(registrationVM.getRegistrationSuccess(), 4, TimeUnit.SECONDS);
        assertNotNull(registered);
        assertTrue(registered);

        loginVM.login(username, wrong);
        // Expect some error message posted
        User maybeUser = getOrAwaitValueAllowNull(loginVM.getLoginSuccess(), 2, TimeUnit.SECONDS);
        if (maybeUser != null) {
            fail("Login unexpectedly returned a user with the wrong password");
        }
        String err = getOrAwaitValue(loginVM.getErrorMessage(), 4, TimeUnit.SECONDS);
        assertNotNull("Expected error message for wrong password", err);
    }

    // LiveData helpers

    private static <T> T getOrAwaitValue(LiveData<T> liveData, long time, TimeUnit unit) throws InterruptedException {
        AtomicReference<T> data = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);
        Observer<T> observer = new Observer<T>() {
            @Override public void onChanged(T t) {
                if (t != null) {
                    data.set(t);
                    latch.countDown();
                    liveData.removeObserver(this);
                }
            }
        };
        liveData.observeForever(observer);

        if (!latch.await(time, unit)) {
            liveData.removeObserver(observer);
            fail("LiveData value was never set within timeout");
        }
        return data.get();
    }

    private static <T> T getOrAwaitValueAllowNull(LiveData<T> liveData, long time, TimeUnit unit) throws InterruptedException {
        AtomicReference<T> data = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);
        Observer<T> observer = new Observer<T>() {
            @Override public void onChanged(T t) {
                data.set(t);
                latch.countDown();
                liveData.removeObserver(this);
            }
        };
        liveData.observeForever(observer);

        // If nothing is ever posted, we’ll time out and return null
        latch.await(time, unit);
        return data.get();
    }
}