package GUI;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ViewRestaurantsTest {
    private WebDriver driver;
    private boolean acceptNextAlert = true;
    private final StringBuffer verificationErrors = new StringBuffer();

    @Before
    public void setUp() throws Exception {
        driver = new FirefoxDriver();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    @Test
    public void testViewRestaurants() throws Exception {
        driver.get("http://localhost:8080/api/swagger-ui/index.html#/");
        driver.findElement(By.xpath("//div[@id='operations-restaurant-controller-getRestaurants']/div/button")).click();
        driver.findElement(By.xpath("//div[@id='operations-restaurant-controller-getRestaurants']/div[2]/div/div/div/div[2]/button")).click();
        driver.findElement(By.xpath("//input[@value='']")).click();
        driver.findElement(By.xpath("//input[@value='1']")).clear();
        driver.findElement(By.xpath("//input[@value='1']")).sendKeys("1");
        driver.findElement(By.xpath("//div[@id='operations-restaurant-controller-getRestaurants']/div[2]/div/div/div[2]/div/table/tbody/tr/td[2]")).click();
        driver.findElement(By.xpath("//div[@id='operations-restaurant-controller-getRestaurants']/div[2]/div/div/div[2]/div/table/tbody/tr[2]/td[2]/div/textarea")).click();
        driver.findElement(By.xpath("//div[@id='operations-restaurant-controller-getRestaurants']/div[2]/div/div/div[2]/div/table/tbody/tr[2]/td[2]/div/textarea")).clear();
        driver.findElement(By.xpath("//div[@id='operations-restaurant-controller-getRestaurants']/div[2]/div/div/div[2]/div/table/tbody/tr[2]/td[2]/div/textarea")).sendKeys("{ }");
        driver.findElement(By.xpath("//div[@id='operations-restaurant-controller-getRestaurants']/div[2]/div/div/div[2]/div/table/tbody/tr/td[2]")).click();
        driver.findElement(By.xpath("//div[@id='operations-restaurant-controller-getRestaurants']/div[2]/div/div[2]/button")).click();
        driver.findElement(By.xpath("//div[@id='operations-restaurant-controller-getRestaurants']/div[2]/div/div[3]/div[2]/div/div/table/tbody/tr/td[2]/div/div/pre")).click();
        driver.findElement(By.xpath("//div[@id='operations-restaurant-controller-getRestaurants']/div[2]/div/div[3]/div[2]/div/div/table/tbody/tr/td[2]/div/div/pre/code/span[3]")).click();
        driver.findElement(By.xpath("//div[@id='operations-restaurant-controller-getRestaurants']/div[2]/div/div[3]/div[2]/div/div/table/tbody/tr/td[2]/div/div/pre/code/span[3]")).click();
        driver.findElement(By.xpath("//div[@id='operations-restaurant-controller-getRestaurants']/div[2]/div/div[3]/div[2]/div/div/table/tbody/tr/td[2]/div/div/pre/code/span[3]")).click();
        // ERROR: Caught exception [ERROR: Unsupported command [doubleClick | xpath=//div[@id='operations-restaurant-controller-getRestaurants']/div[2]/div/div[3]/div[2]/div/div/table/tbody/tr/td[2]/div/div/pre/code/span[3] | ]]
        driver.findElement(By.xpath("//div[@id='operations-restaurant-controller-getRestaurants']/div[2]/div/div[3]/div[2]/div/div/table/tbody/tr/td[2]/div/div/pre/code/span[64]")).click();
        driver.findElement(By.xpath("//div[@id='operations-restaurant-controller-getRestaurants']/div[2]/div/div[3]/div[2]/div/div/table/tbody/tr/td[2]/div/div/pre/code/span[8]")).click();
        // ERROR: Caught exception [ERROR: Unsupported command [doubleClick | xpath=//div[@id='operations-restaurant-controller-getRestaurants']/div[2]/div/div[3]/div[2]/div/div/table/tbody/tr/td[2]/div/div/pre/code/span[8] | ]]
        driver.findElement(By.xpath("//div[@id='operations-restaurant-controller-getRestaurants']/div[2]/div/div[3]/div[2]/div/div/table/tbody/tr/td[2]/div/div/pre/code/span[10]")).click();
        driver.findElement(By.xpath("//div[@id='operations-restaurant-controller-getRestaurants']/div[2]/div/div[3]/div[2]/div/div/table/tbody/tr/td[2]/div/div/pre/code/span[10]")).click();
        // ERROR: Caught exception [ERROR: Unsupported command [doubleClick | xpath=//div[@id='operations-restaurant-controller-getRestaurants']/div[2]/div/div[3]/div[2]/div/div/table/tbody/tr/td[2]/div/div/pre/code/span[10] | ]]
        try {
            assertEquals("200", driver.findElement(By.xpath("//div[@id='operations-restaurant-controller-getRestaurants']/div[2]/div/div[3]/div[2]/div/div/table/tbody/tr/td[2]/div/div/pre/code/span[10]")).getText());
        } catch (Error e) {
            verificationErrors.append(e);
        }
        driver.findElement(By.xpath("//div[@id='operations-restaurant-controller-getRestaurants']/div[2]/div/div[3]/div[2]/div/div/table/tbody/tr/td[2]/div/div/pre/code/span[54]")).click();
        driver.findElement(By.xpath("//div[@id='operations-restaurant-controller-getRestaurants']/div[2]/div/div[3]/div[2]/div/div/table/tbody/tr/td[2]/div/div/pre/code/span[54]")).click();
        // ERROR: Caught exception [ERROR: Unsupported command [doubleClick | xpath=//div[@id='operations-restaurant-controller-getRestaurants']/div[2]/div/div[3]/div[2]/div/div/table/tbody/tr/td[2]/div/div/pre/code/span[54] | ]]
        driver.findElement(By.xpath("//div[@id='operations-restaurant-controller-getRestaurants']/div[2]/div/div[3]/div[2]/div/div/table/tbody/tr/td[2]/div/div/pre/code/span[56]")).click();
        // ERROR: Caught exception [ERROR: Unsupported command [doubleClick | xpath=//div[@id='operations-restaurant-controller-getRestaurants']/div[2]/div/div[3]/div[2]/div/div/table/tbody/tr/td[2]/div/div/pre/code/span[56] | ]]
        driver.findElement(By.xpath("//div[@id='operations-restaurant-controller-getRestaurants']/div[2]/div/div[3]/div[2]/div/div/table/tbody/tr/td[2]/div/div/pre/code/span[56]")).click();
        driver.findElement(By.xpath("//div[@id='operations-restaurant-controller-getRestaurants']/div[2]/div/div[3]/div[2]/div/div/table/tbody/tr/td[2]/div/div/pre/code/span[56]")).click();
        driver.findElement(By.xpath("//div[@id='operations-restaurant-controller-getRestaurants']/div[2]/div/div[3]/div[2]/div/div/table/tbody/tr/td[2]/div/div/pre/code/span[56]")).click();
        try {
            assertEquals("\"Downtown Burger\"", driver.findElement(By.xpath("//div[@id='operations-restaurant-controller-getRestaurants']/div[2]/div/div[3]/div[2]/div/div/table/tbody/tr/td[2]/div/div/pre/code/span[56]")).getText());
        } catch (Error e) {
            verificationErrors.append(e);
        }
        driver.findElement(By.xpath("//div[@id='operations-restaurant-controller-getRestaurants']/div[2]/div/div[3]/div[2]/div/div/table/tbody/tr/td[2]/div/div/pre/code/span[152]")).click();
        driver.findElement(By.xpath("//div[@id='operations-restaurant-controller-getRestaurants']/div[2]/div/div[3]/div[2]/div/div/table/tbody/tr/td[2]/div/div/pre/code/span[152]")).click();
        // ERROR: Caught exception [ERROR: Unsupported command [doubleClick | xpath=//div[@id='operations-restaurant-controller-getRestaurants']/div[2]/div/div[3]/div[2]/div/div/table/tbody/tr/td[2]/div/div/pre/code/span[152] | ]]
        try {
            assertEquals("\"Perperook\"", driver.findElement(By.xpath("//div[@id='operations-restaurant-controller-getRestaurants']/div[2]/div/div[3]/div[2]/div/div/table/tbody/tr/td[2]/div/div/pre/code/span[152]")).getText());
        } catch (Error e) {
            verificationErrors.append(e);
        }
        driver.findElement(By.xpath("//div[@id='operations-restaurant-controller-getRestaurants']/div[2]/div/div[2]/button[2]")).click();
        driver.findElement(By.xpath("//div[@id='operations-restaurant-controller-getRestaurants']/div[2]/div/div/div/div[2]/button")).click();
        driver.findElement(By.xpath("//div[@id='operations-restaurant-controller-getRestaurants']/div/button")).click();
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
