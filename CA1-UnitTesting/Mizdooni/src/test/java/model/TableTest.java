package model;

import mizdooni.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class TableTest {
    private User user;
    private Table table;
    private Restaurant restaurant;
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        user = new User("INARI", "123", "inari@gmail.com", new Address("Iran",
                "Tehran", "Bahar"), User.Role.client);
        restaurant = new Restaurant("Shila", user, "HotDog", LocalTime.of(12, 0),
                LocalTime.of(23, 0), "Best HotDogs",
                new Address("Iran", "Tehran", "Gisha"), " ");
        table = new Table(1, 0, 4);
        reservation = new Reservation(user, restaurant, table, LocalDateTime.parse("2024-11-01T13:00"));
    }

    @Test
    @DisplayName("Test Add Reservation - Add Single Reservation to Table")
    void testAddReservation() {
        table.addReservation(reservation);

        assertEquals(1, table.getReservations().size(), "Table should have one reservation.");
        assertEquals(reservation, table.getReservations().getFirst(),
                "The reservation added should match the one retrieved.");
    }

    @Test
    @DisplayName("Test Add Reservation - Add Multiple Reservations to Table")
    void testAddMultipleReservations() {
        Reservation reservation2 = new Reservation(user, restaurant, table, LocalDateTime.now().plusDays(1));

        table.addReservation(reservation);
        table.addReservation(reservation2);

        assertEquals(2, table.getReservations().size(), "Table should have two reservations.");
        assertEquals(reservation, table.getReservations().get(0),
                "First reservation should match the first added reservation.");
        assertEquals(reservation2, table.getReservations().get(1),
                "Second reservation should match the second added reservation.");
    }

    @ParameterizedTest
    @CsvSource({
            "2024-11-01T13:00, false",
            "2024-11-01T12:00, true"
    })
    @DisplayName("Test Is Reserved")
    void testIsReserved(String reservationTime, boolean expectedResult) {
        reservation = new Reservation(user, restaurant, table, LocalDateTime.parse("2024-11-01T12:00"));
        table.addReservation(reservation);

        assertEquals(expectedResult, table.isReserved(LocalDateTime.parse(reservationTime)),
                "Reservation status should match expected result.");
    }

    @Test
    @DisplayName("Test Is Reserved - Canceled Reservation")
    void testIsReserved_CanceledReservation() {
        table.addReservation(reservation);
        reservation.cancel();

        assertFalse(table.isReserved(LocalDateTime.parse("2024-11-01T13:00")),
                "Table should not be reserved if reservation is canceled.");
    }
}
