package tests;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.time.Duration;
import java.util.List;


import org.testng.annotations.*;
import java.io.*;


public class AdminUpdateMarksTest extends BaseTest {
	
	
	@BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        System.out.println("[BeforeSuite] Setting up the test suite.");
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        System.out.println("[AfterSuite] Tearing down the test suite.");
    }

    @BeforeTest(alwaysRun = true)
    public void beforeTest() {
        System.out.println("[BeforeTest] Setting up before tests.");
    }

    @AfterTest(alwaysRun = true)
    public void afterTest() {
        System.out.println("[AfterTest] Cleaning up after tests.");
    }

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        System.out.println("[BeforeClass] Setting up before class.");
    }

    @AfterClass(alwaysRun = true)
    public void afterClass() {
        System.out.println("[AfterClass] Cleaning up after class.");
    }

    @BeforeGroups("admin")
    public void beforeGroups() {
        System.out.println("[BeforeGroups] Setting up before 'admin' group.");
    }

    @AfterGroups("admin")
    public void afterGroups() {
        System.out.println("[AfterGroups] Cleaning up after 'admin' group.");
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod() {
        System.out.println("[BeforeMethod] Setting up before each test method.");
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        System.out.println("[AfterMethod] Cleaning up after each test method.");
    }
	

    @Test(
    		
    		groups = {"admin"},
	        priority = 3,
	        description = "Test admin users' views' functionalities.",
	        //dependsOnGroups = {"login"},
	        //dependsOnMethods = {"verifyLogin"},
	        enabled = true,
	        alwaysRun = true,
	        timeOut = 300000
    		
    		)
    public void runAdminUpdateMarksTest() {
        String excelPath = "C:\\Users\\Dell\\eclipse-workspace\\StudentManagementTest\\TestResults.xlsx";

        // 1. Navigate & Login
        driver.get("http://127.0.0.1:3000/login");
        driver.findElement(By.id("email")).sendKeys("test@example.com");
        driver.findElement(By.id("password")).sendKeys("Test1234");
        driver.findElement(By.id("loginButton")).click();

        try (FileInputStream file = new FileInputStream(new File(excelPath));
             Workbook workbook = new XSSFWorkbook(file)) {

            Sheet sheet = workbook.getSheetAt(17);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

            // Loop through test cases in Excel
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String action   = row.getCell(1).getStringCellValue();
                String expected = row.getCell(2).getStringCellValue();
                String actual   = "";

                if ("SearchStudentID".equalsIgnoreCase(action)) {
                    // -- Navigate to Update Marks page --
                    driver.findElement(By.id("marksManageLink")).click();
                    driver.findElement(By.id("updateMarksButton")).click();

                    // -- Perform the search --
                    WebElement searchInput = wait.until(
                        ExpectedConditions.elementToBeClickable(
                            By.cssSelector("form.search-bar input[name='studentID']")
                        )
                    );
                    searchInput.clear();
                    searchInput.sendKeys("STU006");
                    searchInput.submit();

                    // -- Check for any result rows --
                    List<WebElement> rows = wait.until(d ->
                        d.findElements(By.cssSelector("table tbody tr"))
                    );
                    boolean found = rows.size() > 0;
                    actual = found ? "Success" : "Failure";
                    System.out.println("SearchStudentID => " + actual);

                    // -- Return back to the search page --
                    driver.findElement(By.id("backLink")).click();

                } else if ("UpdateStudentID".equalsIgnoreCase(action)) {
                    // -- We're already back on the search page, so rerun the search:
                    WebElement searchInput = wait.until(
                        ExpectedConditions.elementToBeClickable(
                            By.cssSelector("form.search-bar input[name='studentID']")
                        )
                    );
                    searchInput.clear();
                    searchInput.sendKeys("STU006");
                    searchInput.submit();

                    // -- Click the edit button you added --
                    WebElement editBtn = wait.until(
                        ExpectedConditions.elementToBeClickable(By.id("editButton"))
                    );
                    editBtn.click();

                    // -- Wait for the internal input, clear and set new value --
                    WebElement internalInput = wait.until(
                        ExpectedConditions.elementToBeClickable(By.id("internal"))
                    );
                    internalInput.clear();
                    internalInput.sendKeys("20");

                    // -- Submit the form --
                    driver.findElement(By.id("saveChangesButton")).click();

                    // -- Verify update by checking the URL or a flash message --
                    boolean updated = wait.until(
                        ExpectedConditions.urlContains("/marks")
                    );
                    actual = updated ? "Success" : "Failure";
                    System.out.println("UpdateStudentID => " + actual);
                }

                // Write back the actual result into Excel
                row.createCell(3).setCellValue(actual);
                System.out.println(String.format(
                    "TestCase: %s | Expected: %s | Actual: %s",
                    row.getCell(0).getStringCellValue(),
                    expected,
                    actual
                ));

                // small pause between iterations
                Thread.sleep(500);
            }

            // Save results back to file
            try (FileOutputStream outFile = new FileOutputStream(new File(excelPath))) {
                workbook.write(outFile);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Exception in AdminUpdateMarksTest: " + e.getMessage());
        }
    }
}
