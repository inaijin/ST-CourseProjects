package GUI;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class SignUpFAILTest {
    private WebDriver driver;
    private boolean acceptNextAlert = true;
    private final StringBuffer verificationErrors = new StringBuffer();

    @Before
    public void setUp() throws Exception {
        driver = new FirefoxDriver();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    @Test
    public void testSignUpFAIL() throws Exception {
        driver.get("http://localhost:8080/api/swagger-ui/index.html#/");
        driver.findElement(By.xpath("//div[@id='operations-authentication-controller-signup']/div/button/span[2]/a/span")).click();
        driver.findElement(By.xpath("//div[@id='operations-authentication-controller-signup']/div[2]/div/div/div/div[2]/button")).click();
        driver.findElement(By.xpath("//div[@id='operations-authentication-controller-signup']/div[2]/div/div/div[3]/div[2]/div/div/div/textarea")).click();
        driver.findElement(By.xpath("//div[@id='operations-authentication-controller-signup']/div[2]/div/div/div[3]/div[2]/div/div/div/textarea")).clear();
        driver.findElement(By.xpath("//div[@id='operations-authentication-controller-signup']/div[2]/div/div/div[3]/div[2]/div/div/div/textarea")).sendKeys("{\n    \"username\": \"swt\",\n    \"password\": \"fall03\",\n    \"email\": \"swt@gmail.com\",\n    \"address\": {\n        \"country\": \"Iran\",\n        \"city\": \"Tehran\"\n    },\n    \"role\": \"client\"\n}");
        driver.findElement(By.xpath("//div[@id='operations-authentication-controller-signup']/div[2]/div/div/div[3]/div[2]")).click();
        driver.findElement(By.xpath("//div[@id='operations-authentication-controller-signup']/div[2]/div/div[2]/button")).click();
        driver.findElement(By.xpath("//div[@id='operations-authentication-controller-signup']/div[2]/div/div[3]/div[2]")).click();
        try {
            assertEquals("400 Undocumented", driver.findElement(By.xpath("//div[@id='operations-authentication-controller-signup']/div[2]/div/div[3]/div[2]/div/div/table/tbody/tr/td")).getText());
        } catch (Error e) {
            verificationErrors.append(e);
        }
        driver.findElement(By.xpath("//div[@id='operations-authentication-controller-signup']/div[2]/div/div[2]/button[2]")).click();
        driver.findElement(By.xpath("//div[@id='operations-authentication-controller-signup']/div[2]/div/div/div/div[2]/button[2]")).click();
        driver.findElement(By.xpath("//div[@id='operations-authentication-controller-signup']/div[2]/div/div/div/div[2]/button")).click();
        driver.findElement(By.xpath("//div[@id='operations-authentication-controller-signup']/div/button")).click();
    }

    @After
    public void tearDown() throws Exception {
        driver.quit();
        String verificationErrorString = verificationErrors.toString();
        if (!"".equals(verificationErrorString)) {
            fail(verificationErrorString);
        }
    }

    private boolean isElementPresent(By by) {
        try {
            driver.findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private boolean isAlertPresent() {
        try {
            driver.switchTo().alert();
            return true;
        } catch (NoAlertPresentException e) {
            return false;
        }
    }

    private String closeAlertAndGetItsText() {
        try {
            Alert alert = driver.switchTo().alert();
            String alertText = alert.getText();
            if (acceptNextAlert) {
                alert.accept();
            } else {
                alert.dismiss();
            }
            return alertText;
        } finally {
            acceptNextAlert = true;
        }
    }
}
