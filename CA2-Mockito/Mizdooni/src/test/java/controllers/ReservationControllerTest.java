package controllers;

import mizdooni.controllers.ReservationController;
import mizdooni.exceptions.*;
import mizdooni.model.Reservation;
import mizdooni.model.Restaurant;
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
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Field;
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

    @BeforeEach
    public void setup() throws Exception {
        mocks = MockitoAnnotations.openMocks(this);

        Restaurant mockRestaurant = new Restaurant(
                "Mock Restaurant",
                null,
                null,
                LocalTime.of(9, 0),
                LocalTime.of(22, 0),
                "A cozy Italian restaurant.",
                null,
                " "
        );

        Field idField = Restaurant.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(mockRestaurant, 0);
        idField.setAccessible(false);

        when(restaurantService.getRestaurant(0)).thenReturn(mockRestaurant);
    }

    @AfterEach
    public void closeMocks() throws Exception {
        mocks.close();
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
        int restaurantId = 0;
        int tableNumber = 3;
        String date = "2023-10-10";
        LocalDate localDate = LocalDate.parse(date, DATE_FORMATTER);
        List<Reservation> reservations = List.of(new Reservation(null, null, null, LocalDateTime.now()));

        when(reservationService.getReservations(restaurantId, tableNumber, localDate)).thenReturn(reservations);

        Response response = reservationController.getReservations(restaurantId, tableNumber, date);
        Response expectedResponse = Response.ok("restaurant table reservations", reservations);

        verify(restaurantService).getRestaurant(restaurantId);
        verify(reservationService).getReservations(restaurantId, tableNumber, localDate);
        assertResponseMatches(expectedResponse, response);
    }

    @Test
    @DisplayName("Test getReservations with invalid date format")
    public void testGetReservationsInvalidDateFormat() {
        int restaurantId = 0;
        int tableNumber = 3;
        String invalidDate = "invalid-date";

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            reservationController.getReservations(restaurantId, tableNumber, invalidDate);
        });

        verifyNoInteractions(reservationService);
        verify(restaurantService).getRestaurant(restaurantId);
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_BAD_TYPE, exception.getMessage());
    }

    @Test
    @DisplayName("Test getReservations when reserveService throws an exception")
    public void testGetReservationsServiceException() throws Exception {
        int restaurantId = 0;
        int tableNumber = 3;
        String date = "2023-10-10";
        LocalDate localDate = LocalDate.parse(date, DATE_FORMATTER);

        doThrow(new RuntimeException("Service failure")).when(reservationService).getReservations(restaurantId, tableNumber, localDate);

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            reservationController.getReservations(restaurantId, tableNumber, date);
        });

        verify(restaurantService).getRestaurant(restaurantId);
        verify(reservationService).getReservations(restaurantId, tableNumber, localDate);
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

        verify(reservationService).getCustomerReservations(customerId);
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Service failure", exception.getMessage());
    }

    @Test
    @DisplayName("Test getAvailableTimes with valid parameters")
    public void testGetAvailableTimesSuccess() throws Exception {
        int restaurantId = 0;
        int people = 4;
        String date = "2023-10-10";
        LocalDate localDate = LocalDate.parse(date, DATE_FORMATTER);
        List<LocalTime> availableTimes = List.of(LocalTime.of(12, 0), LocalTime.of(13, 0));

        when(reservationService.getAvailableTimes(restaurantId, people, localDate)).thenReturn(availableTimes);

        Response response = reservationController.getAvailableTimes(restaurantId, people, date);
        Response expectedResponse = Response.ok("available times", availableTimes);

        verify(restaurantService).getRestaurant(restaurantId);
        verify(reservationService).getAvailableTimes(restaurantId, people, localDate);
        assertResponseMatches(expectedResponse, response);
    }

    @Test
    @DisplayName("Test getAvailableTimes when reserveService throws an exception")
    public void testGetAvailableTimesServiceException() throws DateTimeInThePast, RestaurantNotFound, BadPeopleNumber {
        int restaurantId = 0;
        int people = 4;
        String date = "2023-10-10";
        LocalDate localDate = LocalDate.parse(date, DATE_FORMATTER);

        doThrow(new RuntimeException("Service failure")).when(reservationService).getAvailableTimes(restaurantId, people, localDate);

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            reservationController.getAvailableTimes(restaurantId, people, date);
        });

        verify(restaurantService).getRestaurant(restaurantId);
        verify(reservationService).getAvailableTimes(restaurantId, people, localDate);
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Service failure", exception.getMessage());
    }

    @Test
    @DisplayName("Test getAvailableTimes with invalid date format")
    public void testGetAvailableTimesInvalidDateFormat() {
        int restaurantId = 0;
        int people = 4;
        String invalidDate = "invalid-date";

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            reservationController.getAvailableTimes(restaurantId, people, invalidDate);
        });

        verifyNoInteractions(reservationService);
        verify(restaurantService).getRestaurant(restaurantId);
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_BAD_TYPE, exception.getMessage());
    }

    @Test
    @DisplayName("Test addReservation with valid parameters")
    public void testAddReservationSuccess() throws Exception {
        int restaurantId = 0;
        Map<String, String> params = new HashMap<>();
        params.put("people", "4");
        params.put("datetime", "2023-10-10 12:00");

        LocalDateTime datetime = LocalDateTime.parse(params.get("datetime"), DATETIME_FORMATTER);
        Reservation reservation = new Reservation(null, null, null, datetime);

        when(reservationService.reserveTable(restaurantId, 4, datetime)).thenReturn(reservation);

        Response response = reservationController.addReservation(restaurantId, params);
        Response expectedResponse = Response.ok("reservation done", reservation);

        verify(restaurantService).getRestaurant(restaurantId);
        verify(reservationService).reserveTable(restaurantId, 4, datetime);
        assertResponseMatches(expectedResponse, response);
    }

    @Test
    @DisplayName("Test addReservation with missing parameters")
    public void testAddReservationMissingParams() {
        int restaurantId = 0;
        Map<String, String> params = new HashMap<>();

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            reservationController.addReservation(restaurantId, params);
        });

        verifyNoInteractions(reservationService);
        verify(restaurantService).getRestaurant(restaurantId);
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_MISSING, exception.getMessage());
    }

    @Test
    @DisplayName("Test addReservation with invalid parameters")
    public void testAddReservationParamsBadType() {
        int restaurantId = 0;
        Map<String, String> params = new HashMap<>();
        params.put("people", "invalid");
        params.put("datetime", "invalid-date");

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            reservationController.addReservation(restaurantId, params);
        }, "Expected ResponseException for invalid parameter format");

        verifyNoInteractions(reservationService);
        verify(restaurantService).getRestaurant(restaurantId);
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(PARAMS_BAD_TYPE, exception.getMessage());
    }

    @Test
    @DisplayName("Test addReservation when reserveService throws an exception")
    public void testAddReservationServiceException() throws
            UserNotFound, DateTimeInThePast, TableNotFound,
            ReservationNotInOpenTimes, ManagerReservationNotAllowed, RestaurantNotFound, InvalidWorkingTime {
        int restaurantId = 0;
        Map<String, String> params = new HashMap<>();
        params.put("people", "4");
        params.put("datetime", "2023-10-10 12:00");

        LocalDateTime datetime = LocalDateTime.parse(params.get("datetime"), DATETIME_FORMATTER);

        doThrow(new RuntimeException("Service failure")).when(reservationService).reserveTable(restaurantId, 4, datetime);

        ResponseException exception = assertThrows(ResponseException.class, () -> {
            reservationController.addReservation(restaurantId, params);
        });

        verify(restaurantService).getRestaurant(restaurantId);
        verify(reservationService).reserveTable(restaurantId, 4, datetime);
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Service failure", exception.getMessage());
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

        verify(reservationService).cancelReservation(reservationNumber);
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Cancellation failed", exception.getMessage());
    }
}
