package steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import mizdooni.model.Rating;
import mizdooni.model.Restaurant;
import mizdooni.model.Review;
import mizdooni.model.User;

import static org.junit.Assert.assertEquals;

public class RestaurantAverageRatingSteps {
    private Restaurant restaurant;
    private Rating averageRating;

    @Given("a Restaurant {string} managed by {string}")
    public void a_restaurant_managed_by(String restaurantName, String managerName) {
        User manager = new User(managerName, "password123", managerName + "@example.com", null, User.Role.manager);
        restaurant = new Restaurant(restaurantName, manager, "General", null, null, "A great place", null, "");
    }

    @And("a user {string} adds a review with food rating {double}, service rating {double}, ambiance rating {double}, and overall rating {double}")
    public void a_user_adds_a_review(String username, double food, double service, double ambiance, double overall) {
        User user = new User(username, "password123", username + "@example.com", null, User.Role.client);
        Rating rating = new Rating();
        rating.food = food;
        rating.service = service;
        rating.ambiance = ambiance;
        rating.overall = overall;

        Review review = new Review(user, rating, "Nice experience", null);
        restaurant.addReview(review);
    }

    @And("another user {string} adds a review with food rating {double}, service rating {double}, ambiance rating {double}, and overall rating {double}")
    public void another_user_adds_a_review_with_food_service_ambiance_and_overall_rating(String username, double food, double service, double ambiance, double overall) {
        User user = new User(username, "password123", username + "@example.com", null, User.Role.client);
        Rating rating = new Rating();
        rating.food = food;
        rating.service = service;
        rating.ambiance = ambiance;
        rating.overall = overall;
        Review review = new Review(user, rating, "Another user's review", null);
        restaurant.addReview(review);
    }

    @When("the average rating is calculated")
    public void the_average_rating_is_calculated() {
        averageRating = restaurant.getAverageRating();
    }

    @Then("the average rating should be {double} for all categories")
    public void the_average_rating_should_be_for_all_categories(double expected) {
        assertEquals(expected, averageRating.food, 0.01);
        assertEquals(expected, averageRating.service, 0.01);
        assertEquals(expected, averageRating.ambiance, 0.01);
        assertEquals(expected, averageRating.overall, 0.01);
    }

    @Then("the average food rating should be {double}")
    public void the_average_food_rating_should_be(double expected) {
        assertEquals(expected, averageRating.food, 0.01);
    }

    @Then("the average service rating should be {double}")
    public void the_average_service_rating_should_be(double expected) {
        assertEquals(expected, averageRating.service, 0.01);
    }

    @Then("the average ambiance rating should be {double}")
    public void the_average_ambiance_rating_should_be(double expected) {
        assertEquals(expected, averageRating.ambiance, 0.01);
    }

    @Then("the average overall rating should be {double}")
    public void the_average_overall_rating_should_be(double expected) {
        assertEquals(expected, averageRating.overall, 0.01);
    }
}
