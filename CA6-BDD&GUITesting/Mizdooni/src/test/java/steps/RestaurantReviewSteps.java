package steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import mizdooni.model.Rating;
import mizdooni.model.Restaurant;
import mizdooni.model.Review;
import mizdooni.model.User;

import static org.junit.Assert.assertEquals;

public class RestaurantReviewSteps {
    private Restaurant restaurant;
    private Review lastReview;
    private User user1, user2;

    @Given("a restaurant {string} managed by user {string}")
    public void a_restaurant_managed_by(String restaurantName, String managerName) {
        User manager = new User(managerName, "password123", managerName + "@example.com", null, User.Role.manager);
        restaurant = new Restaurant(restaurantName, manager, "General", null, null, "A fine place", null, "");
    }

    @When("a user {string} adds a review with the comment {string} and rating {int}")
    public void a_user_adds_a_review_with_the_comment_and_rating(String username, String comment, int rating) {
        user1 = new User(username, "password123", username + "@example.com", null, User.Role.client);
        Rating userRating = new Rating();
        userRating.food = rating;
        userRating.service = rating;
        userRating.ambiance = rating;
        userRating.overall = rating;

        lastReview = new Review(user1, userRating, comment, null);
        restaurant.addReview(lastReview);
    }

    @When("the same user adds another review with the comment {string} and rating {int}")
    public void the_same_user_adds_another_review_with_the_comment_and_rating(String comment, int rating) {
        Rating userRating = new Rating();
        userRating.food = rating;
        userRating.service = rating;
        userRating.ambiance = rating;
        userRating.overall = rating;

        lastReview = new Review(user1, userRating, comment, null);
        restaurant.addReview(lastReview);
    }

    @When("another user {string} adds a review with the comment {string} and rating {int}")
    public void another_user_adds_a_review_with_the_comment_and_rating(String username, String comment, int rating) {
        user2 = new User(username, "password123", username + "@example.com", null, User.Role.client);
        Rating userRating = new Rating();
        userRating.food = rating;
        userRating.service = rating;
        userRating.ambiance = rating;
        userRating.overall = rating;

        Review review = new Review(user2, userRating, comment, null);
        restaurant.addReview(review);
    }

    @Then("the restaurant should have {int} review(s)")
    public void the_restaurant_should_have_review(int count) {
        assertEquals(count, restaurant.getReviews().size());
    }

    @Then("the review comment should be {string}")
    public void the_review_comment_should_be(String comment) {
        assertEquals(comment, restaurant.getReviews().getFirst().getComment());
    }

    @Then("the first review comment should be {string}")
    public void the_first_review_comment_should_be(String comment) {
        assertEquals(comment, restaurant.getReviews().getFirst().getComment());
    }

    @Then("the second review comment should be {string}")
    public void the_second_review_comment_should_be(String comment) {
        assertEquals(comment, restaurant.getReviews().get(1).getComment());
    }
}
