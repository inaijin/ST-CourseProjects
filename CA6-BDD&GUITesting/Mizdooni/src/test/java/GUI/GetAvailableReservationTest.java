package GUI;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class GetAvailableReservationTest {
    private WebDriver driver;
    private boolean acceptNextAlert = true;
    private final StringBuffer verificationErrors = new StringBuffer();

    @Before
    public void setUp() throws Exception {
        driver = new FirefoxDriver();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    @Test
    public void testGetAvailableReservation() throws Exception {
        driver.get("http://localhost:8080/api/swagger-ui/index.html#/");
        driver.findElement(By.xpath("//div[@id='operations-reservation-controller-getAvailableTimes']/div/button/span[2]/a/span")).click();
        driver.findElement(By.xpath("//div[@id='operations-reservation-controller-getAvailableTimes']/div[2]/div/div/div/div[2]/button")).click();
        driver.findElement(By.xpath("//input[@value='']")).click();
        driver.findElement(By.xpath("//input[@value='1']")).clear();
        driver.findElement(By.xpath("//input[@value='1']")).sendKeys("1");
        driver.findElement(By.xpath("//div[@id='operations-reservation-controller-getAvailableTimes']/div[2]/div/div/div[2]/div/table/tbody/tr/td[2]")).click();
        driver.findElement(By.xpath("//input[@value='']")).click();
        driver.findElement(By.xpath("//input[@value='2']")).clear();
        driver.findElement(By.xpath("//input[@value='2']")).sendKeys("2");
        driver.findElement(By.xpath("//div[@id='operations-reservation-controller-getAvailableTimes']/div[2]/div/div/div[2]/div/table/tbody/tr[2]/td[2]")).click();
        driver.findElement(By.xpath("//input[@value='']")).click();
        driver.findElement(By.xpath("//input[@value='2025-10-26']")).clear();
        driver.findElement(By.xpath("//input[@value='2025-10-26']")).sendKeys("2025-10-26");
        driver.findElement(By.xpath("//div[@id='operations-reservation-controller-getAvailableTimes']/div[2]/div/div/div[2]/div/table/tbody/tr[3]/td[2]")).click();
        driver.findElement(By.xpath("//div[@id='operations-reservation-controller-getAvailableTimes']/div[2]/div/div[2]/button")).click();
        driver.findElement(By.xpath("//div[@id='operations-reservation-controller-getAvailableTimes']/div[2]/div/div[3]/div[2]/div/div/table/tbody/tr/td[2]/div/div/pre/code/span[10]")).click();
        driver.findElement(By.xpath("//div[@id='operations-reservation-controller-getAvailableTimes']/div[2]/div/div[3]/div[2]/div/div/table/tbody/tr/td[2]/div/div/pre/code/span[10]")).click();
        // ERROR: Caught exception [ERROR: Unsupported command [doubleClick | xpath=//div[@id='operations-reservation-controller-getAvailableTimes']/div[2]/div/div[3]/div[2]/div/div/table/tbody/tr/td[2]/div/div/pre/code/span[10] | ]]
        try {
            assertEquals("200", driver.findElement(By.xpath("//div[@id='operations-reservation-controller-getAvailableTimes']/div[2]/div/div[3]/div[2]/div/div/table/tbody/tr/td[2]/div/div/pre/code/span[10]")).getText());
        } catch (Error e) {
            verificationErrors.append(e);
        }
        driver.findElement(By.xpath("//div[@id='operations-reservation-controller-getAvailableTimes']/div[2]/div/div[3]/div[2]/div/div/table/tbody/tr/td[2]/div/div/pre/code/span[20]")).click();
        try {
            assertEquals("\"available times\"", driver.findElement(By.xpath("//div[@id='operations-reservation-controller-getAvailableTimes']/div[2]/div/div[3]/div[2]/div/div/table/tbody/tr/td[2]/div/div/pre/code/span[20]")).getText());
        } catch (Error e) {
            verificationErrors.append(e);
        }
        driver.findElement(By.xpath("//div[@id='operations-reservation-controller-getAvailableTimes']/div[2]/div/div[3]/div[2]/div/div/table/tbody/tr/td[2]/div/div/pre/code/span[26]")).click();
        try {
            assertEquals("\"13:00\"", driver.findElement(By.xpath("//div[@id='operations-reservation-controller-getAvailableTimes']/div[2]/div/div[3]/div[2]/div/div/table/tbody/tr/td[2]/div/div/pre/code/span[26]")).getText());
        } catch (Error e) {
            verificationErrors.append(e);
        }
        driver.findElement(By.xpath("//div[@id='operations-reservation-controller-getAvailableTimes']/div[2]/div/div[2]/button[2]")).click();
        driver.findElement(By.xpath("//div[@id='operations-reservation-controller-getAvailableTimes']/div[2]/div/div/div/div[2]/button")).click();
        driver.findElement(By.xpath("//div[@id='operations-reservation-controller-getAvailableTimes']/div/button")).click();
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
