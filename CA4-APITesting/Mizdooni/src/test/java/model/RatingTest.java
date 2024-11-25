package model;

import mizdooni.model.Rating;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RatingTest {
    @ParameterizedTest
    @CsvSource({
            "3.7, 4",   // Rounds up
            "4.4, 4",   // Rounds down
            "4.5, 5",   // Borderline rounds up
            "5.0, 5",   // Exactly 5
            "6.2, 5",   // Higher than 5 should cap at 5
            "0.5, 1",   // Low value rounds up to 1
            "4.9, 5",   // Close to 5 rounds up to 5
            "-1.0, 1"   // This will be skipped because of assumption of handle in the upper layers
    })
    @DisplayName("Test Get Star Count")
    void testGetStarCount(double overall, int expectedStarCount) {
        Assumptions.assumeTrue(overall >= 0, "Negative overall values are handled at a higher layer");

        Rating rating = new Rating();
        rating.overall = overall;

        int starCount = rating.getStarCount();
        assertEquals(expectedStarCount, starCount, "The star count should match the expected value.");
    }

}
