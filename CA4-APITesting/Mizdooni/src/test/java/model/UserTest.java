package model;

import mizdooni.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class UserTest {
    private User user;
    private Table table;
    private Restaurant restaurant;
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        table = mock(Table.class);
        restaurant = mock(Restaurant.class);
        user = new User("INARI", "123", "inari@gmail.com", mock(Address.class), User.Role.client);
        reservation = new Reservation(user, restaurant, table, LocalDateTime.now().minusMinutes(1));
    }

    @Test
    @DisplayName("Test Add Reservation")
    void testAddReservation() {
        user.addReservation(reservation);

        assertEquals(1, user.getReservations().size(), "User should have one reservation.");
        assertEquals(0, reservation.getReservationNumber(), "The reservation number should be 0.");
    }

    @Test
    @DisplayName("Test Add Multiple Reservations")
    void testAddMultipleReservations() {
        user.addReservation(reservation);

        Reservation reservation2 = new Reservation(user, restaurant, table, LocalDateTime.now().plusDays(1));
        user.addReservation(reservation2);

        assertEquals(2, user.getReservations().size(), "User should have two reservations.");
        assertEquals(0, reservation.getReservationNumber(), "The first reservation number should be 0.");
        assertEquals(1, reservation2.getReservationNumber(), "The second reservation number should be 1.");
    }

    @Test
    @DisplayName("Test Check Reserved - Reservation Exists")
    void testCheckReserved_Exists() {
        user.addReservation(reservation);

        assertTrue(user.checkReserved(restaurant), "User should have a valid reservation for this restaurant.");
    }

    @Test
    @DisplayName("Test Check Reserved - No Reservation")
    void testCheckReserved_NotExists() {
        assertFalse(user.checkReserved(restaurant), "User should not have a reservation for this restaurant.");
    }

    @Test
    @DisplayName("Test Check Reserved - Future Reservation Not Valid")
    void testCheckReserved_FutureReservation() {
        Reservation futureReservation = new Reservation(user, restaurant, table, LocalDateTime.now().plusDays(1));
        user.addReservation(futureReservation);

        assertFalse(user.checkReserved(restaurant), "Future reservation should not be considered valid.");
    }

    @Test
    @DisplayName("Test Check Reserved - Canceled Reservation Not Valid")
    void testCheckReserved_CanceledReservation() {
        reservation.cancel();
        user.addReservation(reservation);

        assertFalse(user.checkReserved(restaurant), "Canceled reservation should not be considered valid.");
    }

    @Test
    @DisplayName("Test Check Reserved - Reservation for Different Restaurant Not Valid")
    void testCheckReserved_DifferentRestaurant() {
        Restaurant anotherRestaurant = mock(Restaurant.class);
        Reservation otherRestaurantReservation = new Reservation(user, anotherRestaurant, table, LocalDateTime.now());
        user.addReservation(otherRestaurantReservation);

        assertFalse(user.checkReserved(restaurant), "Reservation for a different restaurant should not be valid.");
    }

    @Test
    @DisplayName("Test Check Reserved with Null Restaurant")
    void shouldReturnFalseWhenRestaurantIsNull() {
        user.addReservation(reservation);
        assertFalse(user.checkReserved(null), "Checking reserved with null restaurant should return false.");
    }

    @Test
    @DisplayName("Test Get Reservation - Reservation Exists")
    void testGetReservation_Exists() {
        user.addReservation(reservation);

        Reservation found = user.getReservation(0);

        assertNotNull(found, "Reservation should be found.");
        assertEquals(0, found.getReservationNumber(), "Reservation number should match.");
    }

    @Test
    @DisplayName("Test Get Reservation - Reservation Not Found")
    void testGetReservation_NotFound() {
        user.addReservation(reservation);

        Reservation found = user.getReservation(1);
        assertNull(found, "Reservation should not be found for invalid reservation number.");
    }

    @Test
    @DisplayName("Test Get Reservation - Multiple Reservations")
    void testGetReservation_MultipleReservations() {
        Reservation reservation2 = new Reservation(user, restaurant, table, LocalDateTime.now().plusDays(1));

        user.addReservation(reservation);
        user.addReservation(reservation2);

        Reservation found1 = user.getReservation(0);
        assertNotNull(found1, "First reservation should be found.");
        assertEquals(0, found1.getReservationNumber(),
                "Reservation number should match for the first reservation.");

        Reservation found2 = user.getReservation(1);
        assertNotNull(found2, "Second reservation should be found.");
        assertEquals(1, found2.getReservationNumber(),
                "Reservation number should match for the second reservation.");
    }

    @Test
    @DisplayName("Test Get Reservation - Canceled Reservation Not Found")
    void testGetReservation_CanceledReservation() {
        Reservation reservation2 = new Reservation(user, restaurant, table, LocalDateTime.now().plusDays(1));

        user.addReservation(reservation);
        user.addReservation(reservation2);

        reservation2.cancel();

        Reservation found = user.getReservation(1);
        assertNull(found, "Canceled reservation should not be found.");
    }

    @Test
    @DisplayName("Test Get Reservation with Negative Number")
    void shouldReturnNullWhenReservationNumberIsNegative() {
        user.addReservation(reservation);
        Reservation found = user.getReservation(-1);
        assertNull(found, "Retrieving with a negative reservation number should return null.");
    }

    @Test
    @DisplayName("Test Check Password with Null Input")
    void shouldReturnFalseWhenPasswordIsNull() {
        assertFalse(user.checkPassword(null), "Checking password with null input should return false.");
    }

    @ParameterizedTest
    @CsvSource({
            "123, true",
            "wrongPass, false"
    })
    @DisplayName("Test Check Password")
    void testCheckPassword(String inputPassword, boolean expectedResult) {
        assertEquals(expectedResult, user.checkPassword(inputPassword), "Password check result should match.");
    }

}
