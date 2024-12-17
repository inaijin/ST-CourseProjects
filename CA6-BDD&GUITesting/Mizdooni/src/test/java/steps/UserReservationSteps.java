package steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import mizdooni.model.Reservation;
import mizdooni.model.Restaurant;
import mizdooni.model.Table;
import mizdooni.model.User;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class UserReservationSteps {
    private User user;
    private Restaurant restaurant;
    private boolean reservationSuccess;

    @Given("a user with username {string} and role {string}")
    public void a_user_with_username_and_role(String username, String role) {
        user = new User(username, "password123", username + "@example.com", null,
                User.Role.valueOf(role));
    }

    @Given("a restaurant {string} managed by {string}")
    public void a_restaurant_managed_by(String restaurantName, String managerUsername) {
        User manager = new User(managerUsername, "password123", managerUsername + "@example.com",
                null, User.Role.manager);
        restaurant = new Restaurant(restaurantName, manager, "General", null, null,
                "A chill place", null, " ");
    }

    @Given("the restaurant has a table with table number {int}, restaurant ID {int}, and {int} seats")
    public void the_restaurant_has_a_table_with_table_number_and_seats(int tableNumber, int restaurantId, int seats) {
        Table table = new Table(tableNumber, restaurantId, seats);
        restaurant.addTable(table);
    }

    @Given("the table {int} with {int} seats is already reserved at {string}")
    public void the_table_is_already_reserved_at(int tableNumber, int seatNum, String datetime) {
        Table reservedTable = new Table(tableNumber, restaurant.getId(), seatNum);
        restaurant.addTable(reservedTable);
        Reservation reservation = new Reservation(user, restaurant, reservedTable, LocalDateTime.parse(datetime));
        reservedTable.addReservation(reservation);
        assertTrue(reservedTable.isReserved(LocalDateTime.parse(datetime)));
    }

    @When("the user reserves table {int} at {string}")
    public void the_user_reserves_table_in_at(int tableNumber, String datetime) {
        Table targetTable = restaurant.getTable(tableNumber);
        assertNotNull(targetTable);

        if (!targetTable.isReserved(LocalDateTime.parse(datetime))) {
            Reservation reservation = new Reservation(user, restaurant, targetTable, LocalDateTime.parse(datetime));
            user.addReservation(reservation);
            targetTable.addReservation(reservation);
            reservationSuccess = true;
        } else {
            reservationSuccess = false;
        }
    }

    @When("the user tries to reserve table {int} at {string}")
    public void the_user_tries_to_reserve_table_in_at(int tableNumber, String datetime) {
        the_user_reserves_table_in_at(tableNumber, datetime);
    }

    @Then("the user should have {int} reservation")
    public void the_user_should_have_reservation(int count) {
        assertEquals(count, user.getReservations().size());
    }

    @Then("table {int} should be reserved at {string}")
    public void table_should_be_reserved_at(int tableNumber, String datetime) {
        Table targetTable = restaurant.getTable(tableNumber);
        assertTrue(targetTable.isReserved(LocalDateTime.parse(datetime)));
    }

    @Then("the reservation should fail")
    public void the_reservation_should_fail() {
        assertFalse(reservationSuccess);
    }
}
