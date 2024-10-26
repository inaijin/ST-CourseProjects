package controllers;

import mizdooni.controllers.ControllerUtils;
import mizdooni.controllers.ReservationController;
import mizdooni.exceptions.*;
import mizdooni.model.Reservation;
import mizdooni.response.Response;
import mizdooni.response.ResponseException;
import mizdooni.service.ReservationService;
import mizdooni.service.RestaurantService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static mizdooni.controllers.ControllerUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ReservationControllerTest {

    @Mock
    private RestaurantService restaurantService;

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private ReservationController reservationController;

    private AutoCloseable mocks;
    private MockedStatic<ControllerUtils> mockedControllerUtils;

    @BeforeEach
    public void setup() throws Exception {
        mocks = MockitoAnnotations.openMocks(this);
        setupStaticMocks();
    }

    @AfterEach
    public void closeMocks() throws Exception {
        resetStaticMocks();
        mocks.close();
        mockedControllerUtils.close();
    }

    private void setupStaticMocks() throws Exception {
        Method checkRestaurantMethod = ControllerUtils.class.getDeclaredMethod("checkRestaurant", int.class, RestaurantService.class);
        checkRestaurantMethod.setAccessible(true);
        mockedControllerUtils = mockStatic(ControllerUtils.class);
        mockedControllerUtils.when(() -> checkRestaurantMethod.invoke(null, anyInt(), any(RestaurantService.class)))
                .thenReturn(null);

        Method containsKeysMethod = ControllerUtils.class.getDeclaredMethod("containsKeys", Map.class, String[].class);
        containsKeysMethod.setAccessible(true);
        mockedControllerUtils.when(() -> containsKeysMethod.invoke(null, any(Map.class), any(String[].class)))
                .thenReturn(true);
    }

    private void resetStaticMocks() {
        mockedControllerUtils.clearInvocations();
    }

    private Map<String, String> getDefaultParams() {
        Map<String, String> params = new HashMap<>();
        params.put("people", "4");
        params.put("datetime", "2023-10-10 12:00");
        return params;
    }

    private void assertResponseMatches(Response expectedResponse, Response actualResponse) throws Exception {
        assertEquals(getField(expectedResponse, "status"), getField(actualResponse, "status"), "Status field mismatch");
        assertEquals(getField(expectedResponse, "message"), getField(actualResponse, "message"), "Message field mismatch");
        assertEquals(getField(expectedResponse, "success"), getField(actualResponse, "success"), "Success field mismatch");
        assertEquals(getField(expectedResponse, "data"), getField(actualResponse, "data"), "Data field mismatch");
    }

    private Object getField(Object object, String fieldName) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        Object receivedObj = field.get(object);
        field.setAccessible(false);
        return receivedObj;
    }

    @Test
    @DisplayName("Test getReservations with valid input")
    public void testGetReservationsSuccess() throws Exception {
        int restaurantId = 1;
        int tableNumber = 3;
        String date = "2023-10-10";
        LocalDate localDate = LocalDate.parse(date, DATE_FORMATTER);
        List<Reservation> reservations = List.of(new Reservation(null, null, null, LocalDateTime.now()));

        when(reservationService.getReservations(restaurantId, tableNumber, localDate)).thenReturn(reservations);

        Response response = reservationController.getReservations(restaurantId, tableNumber, date);
        Response expectedResponse = Response.ok("restaurant table reservations", reservations);

        verify(reservationService).getReservations(restaurantId, tableNumber, localDate);
        assertResponseMatches(expectedResponse, response);
    }

    @Test
    @DisplayName("Test getReservations with invalid date format")
    public void testGetReservationsInvalidDateFormat() {
        int restaurantId = 1;
        int tableNumber = 3;
        String invalidDate = "invalid-date";

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            reservationController.getReservations(restaurantId, tableNumber, invalidDate);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_BAD_TYPE, exception.getMessage());
    }

    @Test
    @DisplayName("Test getReservations when reserveService throws an exception")
    public void testGetReservationsServiceException() throws Exception {
        int restaurantId = 1;
        int tableNumber = 3;
        String date = "2023-10-10";
        LocalDate localDate = LocalDate.parse(date, DATE_FORMATTER);

        doThrow(new RuntimeException("Service failure")).when(reservationService).getReservations(restaurantId, tableNumber, localDate);

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            reservationController.getReservations(restaurantId, tableNumber, date);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Service failure", exception.getMessage());
    }

    @Test
    @DisplayName("Test getCustomerReservations success")
    public void testGetCustomerReservationsSuccess() throws Exception {
        int customerId = 1;
        List<Reservation> reservations = List.of(new Reservation(null, null, null, LocalDateTime.now()));

        when(reservationService.getCustomerReservations(customerId)).thenReturn(reservations);

        Response response = reservationController.getCustomerReservations(customerId);
        Response expectedResponse = Response.ok("user reservations", reservations);

        verify(reservationService).getCustomerReservations(customerId);
        assertResponseMatches(expectedResponse, response);
    }

    @Test
    @DisplayName("Test getCustomerReservations when reserveService throws an exception")
    public void testGetCustomerReservationsServiceException() throws UserNotFound, UserNoAccess {
        int customerId = 1;

        doThrow(new RuntimeException("Service failure")).when(reservationService).getCustomerReservations(customerId);

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            reservationController.getCustomerReservations(customerId);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Service failure", exception.getMessage());
    }

    @Test
    @DisplayName("Test getAvailableTimes with valid parameters")
    public void testGetAvailableTimesSuccess() throws Exception {
        int restaurantId = 1;
        int people = 4;
        String date = "2023-10-10";
        LocalDate localDate = LocalDate.parse(date, DATE_FORMATTER);
        List<LocalTime> availableTimes = List.of(LocalTime.of(12, 0), LocalTime.of(13, 0));

        when(reservationService.getAvailableTimes(restaurantId, people, localDate)).thenReturn(availableTimes);

        Response response = reservationController.getAvailableTimes(restaurantId, people, date);
        Response expectedResponse = Response.ok("available times", availableTimes);

        verify(reservationService).getAvailableTimes(restaurantId, people, localDate);
        assertResponseMatches(expectedResponse, response);
    }

    @Test
    @DisplayName("Test getAvailableTimes when reserveService throws an exception")
    public void testGetAvailableTimesServiceException() throws DateTimeInThePast, RestaurantNotFound, BadPeopleNumber {
        int restaurantId = 1;
        int people = 4;
        String date = "2023-10-10";
        LocalDate localDate = LocalDate.parse(date, DATE_FORMATTER);

        doThrow(new RuntimeException("Service failure")).when(reservationService).getAvailableTimes(restaurantId, people, localDate);

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            reservationController.getAvailableTimes(restaurantId, people, date);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Service failure", exception.getMessage());
    }

    @Test
    @DisplayName("Test getAvailableTimes with invalid date format")
    public void testGetAvailableTimesInvalidDateFormat() {
        int restaurantId = 1;
        int people = 4;
        String invalidDate = "invalid-date";

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            reservationController.getAvailableTimes(restaurantId, people, invalidDate);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_BAD_TYPE, exception.getMessage());
    }

    @Test
    @DisplayName("Test addReservation with valid parameters")
    public void testAddReservationSuccess() throws Exception {
        int restaurantId = 1;
        Map<String, String> params = getDefaultParams();

        LocalDateTime datetime = LocalDateTime.parse(params.get("datetime"), DATETIME_FORMATTER);
        Reservation reservation = new Reservation(null, null, null, datetime);

        when(reservationService.reserveTable(restaurantId, 4, datetime)).thenReturn(reservation);

        Response response = reservationController.addReservation(restaurantId, params);
        Response expectedResponse = Response.ok("reservation done", reservation);

        verify(reservationService).reserveTable(restaurantId, 4, datetime);
        assertResponseMatches(expectedResponse, response);
    }

    @Test
    @DisplayName("Test addReservation with missing parameters")
    public void testAddReservationMissingParams() throws NoSuchMethodException {
        int restaurantId = 1;
        Map<String, String> params = new HashMap<>();

        Method containsKeysMethod = ControllerUtils.class.getDeclaredMethod("containsKeys", Map.class, String[].class);
        containsKeysMethod.setAccessible(true);
        mockedControllerUtils.when(() -> containsKeysMethod.invoke(null, any(Map.class), any(String[].class)))
                .thenReturn(false);

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            reservationController.addReservation(restaurantId, params);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_MISSING, exception.getMessage());
    }

    @Test
    @DisplayName("Test addReservation with invalid parameters (PARAMS_BAD_TYPE)")
    public void testAddReservationParamsBadType() {
        int restaurantId = 1;
        Map<String, String> params = new HashMap<>();
        params.put("people", "invalid");
        params.put("datetime", "invalid-date");

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            reservationController.addReservation(restaurantId, params);
        }, "Expected ResponseException for invalid parameter format");

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_BAD_TYPE, exception.getMessage());
    }

    @Test
    @DisplayName("Test cancelReservation success")
    public void testCancelReservationSuccess() throws Exception {
        int reservationNumber = 101;

        doNothing().when(reservationService).cancelReservation(reservationNumber);

        Response response = reservationController.cancelReservation(reservationNumber);
        Response expectedResponse = Response.ok("reservation cancelled");

        verify(reservationService).cancelReservation(reservationNumber);
        assertResponseMatches(expectedResponse, response);
    }

    @Test
    @DisplayName("Test cancelReservation with exception")
    public void testCancelReservationException() throws ReservationCannotBeCancelled, UserNotFound, ReservationNotFound {
        int reservationNumber = 101;

        doThrow(new RuntimeException("Cancellation failed")).when(reservationService).cancelReservation(reservationNumber);

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            reservationController.cancelReservation(reservationNumber);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Cancellation failed", exception.getMessage());
    }
}
