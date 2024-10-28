package controllers;

import mizdooni.controllers.AuthenticationController;
import mizdooni.controllers.ControllerUtils;
import mizdooni.model.Address;
import mizdooni.model.User;
import mizdooni.response.Response;
import mizdooni.response.ResponseException;
import mizdooni.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class AuthenticationControllerTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private AuthenticationController authenticationController;

    private AutoCloseable mocks;

    @BeforeEach
    public void setup() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void closeMocks() throws Exception {
        mocks.close();
    }

    private Object getField(Object object, String fieldName) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        Object receivedObj = field.get(object);
        field.setAccessible(false);
        return receivedObj;
    }

    private void assertResponseMatches(Response expectedResponse, Response actualResponse) throws Exception {
        assertEquals(getField(expectedResponse, "status"), getField(actualResponse, "status"),
                "Status field did not match!");
        assertEquals(getField(expectedResponse, "message"), getField(actualResponse, "message"),
                "Message field did not match!");
        assertEquals(getField(expectedResponse, "success"), getField(actualResponse, "success"),
                "Success field did not match!");
        assertEquals(getField(expectedResponse, "data"), getField(actualResponse, "data"),
                "Data field did not match!");
        assertEquals(getField(expectedResponse, "error"), getField(actualResponse, "error"),
                "Error field did not match!");
    }

    @Test
    @DisplayName("Test fetching current user when logged in")
    public void testGetCurrentUserSuccess() throws Exception {
        User mockUser = new User("testUser", "testPass", "test@example.com",
                new Address("Country", "City", "Street"), User.Role.client);
        when(userService.getCurrentUser()).thenReturn(mockUser);

        Response response = authenticationController.user();
        Response expectedResponse = Response.ok("current user", mockUser);

        verify(userService).getCurrentUser();
        assertResponseMatches(expectedResponse, response);
    }

    @Test
    @DisplayName("Test fetching current user when no user is logged in")
    public void testGetCurrentUserUnauthorized() {
        when(userService.getCurrentUser()).thenReturn(null);

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            authenticationController.user();
        });

        verify(userService).getCurrentUser();
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertEquals("no user logged in", exception.getMessage());
    }

    @Test
    @DisplayName("Test successful login")
    public void testLoginSuccess() throws Exception {
        Map<String, String> loginParams = new HashMap<>();
        loginParams.put("username", "testUser");
        loginParams.put("password", "testPass");

        User mockUser = new User("testUser", "testPass", "test@example.com",
                new Address("Country", "City", "Street"), User.Role.client);

        when(userService.login("testUser", "testPass")).thenReturn(true);
        when(userService.getCurrentUser()).thenReturn(mockUser);

        Response response = authenticationController.login(loginParams);
        Response expectedResponse = Response.ok("login successful", userService.getCurrentUser());

        verify(userService).login("testUser", "testPass");
        verify(userService, times(2)).getCurrentUser();
        assertResponseMatches(expectedResponse, response);
    }

    @Test
    @DisplayName("Test invalid login credentials")
    public void testLoginInvalidCredentials() {
        Map<String, String> invalidLoginParams = new HashMap<>();
        invalidLoginParams.put("username", "invalidUser");
        invalidLoginParams.put("password", "wrongPass");

        when(userService.login("invalidUser", "wrongPass")).thenReturn(false);

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            authenticationController.login(invalidLoginParams);
        });

        verify(userService).login("invalidUser", "wrongPass");
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertEquals("invalid username or password", exception.getMessage());
    }

    @Test
    @DisplayName("Test login with missing parameters")
    public void testLoginMissingParameters() {
        Map<String, String> params = new HashMap<>();

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            authenticationController.login(params);
        });

        verifyNoInteractions(userService);
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(ControllerUtils.PARAMS_MISSING, exception.getMessage());
    }

    @Test
    @DisplayName("Test successful signup")
    public void testSignupSuccess() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("username", "newUser");
        params.put("password", "password");
        params.put("email", "email@example.com");
        params.put("role", "client");

        Map<String, String> addressParams = new HashMap<>();
        addressParams.put("country", "Country");
        addressParams.put("city", "City");
        params.put("address", addressParams);

        User newUser = new User("newUser", "password", "email@example.com",
                new Address("Country", "City", null), User.Role.client);

        doNothing().when(userService).signup(
                eq("newUser"),
                eq("password"),
                eq("email@example.com"),
                argThat(addr -> addr.getCountry().equals("Country") && addr.getCity().equals("City")),
                eq(User.Role.client)
        );

        when(userService.login("newUser", "password")).thenReturn(true);
        when(userService.getCurrentUser()).thenReturn(newUser);

        Response response = authenticationController.signup(params);
        Response expectedResponse = Response.ok("signup successful", userService.getCurrentUser());

        verify(userService).signup(
                eq("newUser"),
                eq("password"),
                eq("email@example.com"),
                argThat(addr -> addr.getCountry().equals("Country") && addr.getCity().equals("City")),
                eq(User.Role.client)
        );
        verify(userService).login("newUser", "password");
        assertResponseMatches(expectedResponse, response);
    }

    @Test
    @DisplayName("Test signup with missing parameters")
    public void testSignupWithMissingParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("username", "newUser");

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            authenticationController.signup(params);
        });

        verifyNoInteractions(userService);
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(ControllerUtils.PARAMS_MISSING, exception.getMessage());
    }

    @Test
    @DisplayName("Test signup with invalid parameter types")
    public void testSignupWithInvalidParamTypes() {
        Map<String, Object> params = new HashMap<>();
        params.put("username", "newUser");
        params.put("password", "password");
        params.put("email", "email@example.com");
        params.put("role", "client");

        params.put("address", new String[]{"invalidAddress"});

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            authenticationController.signup(params);
        });

        verifyNoInteractions(userService);
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(ControllerUtils.PARAMS_BAD_TYPE, exception.getMessage());
    }

    @Test
    @DisplayName("Test signup with missing values in parameters")
    public void testSignupWithMissingValues() {
        Map<String, Object> params = new HashMap<>();
        params.put("username", "newUser");
        params.put("password", "password");
        params.put("email", "");
        params.put("role", "client");

        Map<String, String> addressParams = new HashMap<>();
        addressParams.put("country", "Country");
        addressParams.put("city", "City");
        params.put("address", addressParams);

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            authenticationController.signup(params);
        });

        verifyNoInteractions(userService);
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(ControllerUtils.PARAMS_MISSING, exception.getMessage());
    }

    @Test
    @DisplayName("Test signup with service exception")
    public void testSignupWithServiceException() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("username", "newUser");
        params.put("password", "password");
        params.put("email", "email@example.com");
        params.put("role", "client");

        Map<String, String> addressParams = new HashMap<>();
        addressParams.put("country", "Country");
        addressParams.put("city", "City");
        params.put("address", addressParams);

        doThrow(new RuntimeException("Service failure")).when(userService).signup(
                eq("newUser"),
                eq("password"),
                eq("email@example.com"),
                argThat(addr -> addr.getCountry().equals("Country") && addr.getCity().equals("City")),
                eq(User.Role.client)
        );

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            authenticationController.signup(params);
        });

        verify(userService).signup(
                eq("newUser"),
                eq("password"),
                eq("email@example.com"),
                argThat(addr -> addr.getCountry().equals("Country") && addr.getCity().equals("City")),
                eq(User.Role.client)
        );
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Service failure", exception.getMessage());
    }

    @Test
    @DisplayName("Test successful logout")
    public void testLogoutSuccess() throws Exception {
        when(userService.logout()).thenReturn(true);

        Response response = authenticationController.logout();
        Response expectedResponse = Response.ok("logout successful");

        verify(userService).logout();
        assertResponseMatches(expectedResponse, response);
    }

    @Test
    @DisplayName("Test logout when no user is logged in")
    public void testLogoutNoUserLoggedIn() throws Exception {
        when(userService.logout()).thenReturn(false);

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            authenticationController.logout();
        });

        verify(userService).logout();
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertEquals("no user logged in", exception.getMessage());
    }

    @Test
    @DisplayName("Test invalid username format")
    public void testInvalidUsernameFormat() {
        String invalidUsername = "invalid username";

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            authenticationController.validateUsername(invalidUsername);
        });

        verifyNoInteractions(userService);
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("invalid username format", exception.getMessage());
    }

    @Test
    @DisplayName("Test username already exists")
    public void testUsernameAlreadyExists() {
        String existingUsername = "existingUser";

        when(userService.usernameExists(existingUsername)).thenReturn(true);

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            authenticationController.validateUsername(existingUsername);
        });

        verify(userService).usernameExists(existingUsername);
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("username already exists", exception.getMessage());
    }

    @Test
    @DisplayName("Test username is available")
    public void testUsernameIsAvailable() throws Exception {
        String newUsername = "newUser";

        Response response = authenticationController.validateUsername(newUsername);
        Response expectedResponse = Response.ok("username is available");

        verify(userService).usernameExists(newUsername);
        assertResponseMatches(expectedResponse, response);
    }

    @Test
    @DisplayName("Test invalid email format")
    public void testInvalidEmailFormat() {
        String invalidEmail = "invalid.email@";

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            authenticationController.validateEmail(invalidEmail);
        });

        verifyNoInteractions(userService);
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("invalid email format", exception.getMessage());
    }

    @Test
    @DisplayName("Test email already registered")
    public void testEmailAlreadyRegistered() {
        String registeredEmail = "registered@example.com";

        when(userService.emailExists(registeredEmail)).thenReturn(true);

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            authenticationController.validateEmail(registeredEmail);
        });

        verify(userService).emailExists(registeredEmail);
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("email already registered", exception.getMessage());
    }

    @Test
    @DisplayName("Test email not registered")
    public void testEmailNotRegistered() throws Exception {
        String newEmail = "newuser@example.com";

        Response response = authenticationController.validateEmail(newEmail);
        Response expectedResponse = Response.ok("email not registered");

        verify(userService).emailExists(newEmail);
        assertResponseMatches(expectedResponse, response);
    }
}
