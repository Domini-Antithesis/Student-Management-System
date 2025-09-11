package tests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import java.util.HashMap;
import java.util.Map;

import java.time.Duration;

public class BaseTest {
    protected WebDriver driver;
    
    @BeforeClass
    public void setup() {
    	// 1) Set path
        System.setProperty("webdriver.chrome.driver",
            "C:\\selenium webdriver\\ChromeDriver\\chromedriver-win64\\chromedriver.exe");

        // 2) Prepare prefs before creating driver
        Map<String,Object> prefs = new HashMap<>();
        prefs.put("download.default_directory", "C:\\Downloads");
        prefs.put("download.prompt_for_download", false);
        prefs.put("safebrowsing.enabled", true);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.setExperimentalOption("prefs", prefs);

        // 3) Now launch _one_ ChromeDriver
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        // 4) Navigate to the home page
        driver.get("http://127.0.0.1:3000/");

    	  
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}

