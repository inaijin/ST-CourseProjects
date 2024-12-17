package steps;

import io.cucumber.java.en.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest
@ContextConfiguration
public class StepDefinitions {

    private WebDriver driver;

    @Given("the application is running")
    public void the_application_is_running() {
        io.github.bonigarcia.wdm.WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
    }

    @When("I access the home page")
    public void i_access_the_home_page() {
        driver.get("http://localhost:8080/");
    }

    @Then("I should see {string}")
    public void i_should_see(String expectedText) {
        String pageSource = driver.getPageSource();
        assertThat(pageSource, containsString(expectedText));
        driver.quit();
    }
}
