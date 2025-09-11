package tests;

import com.mongodb.client.*;
import org.bson.Document;
import org.testng.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import utils.ExcelUtils;
import utils.MongoDBUtils;

import java.io.*;
import java.time.Duration;
import org.testng.annotations.*;
import java.io.*;

import static com.mongodb.client.model.Filters.eq;

public class AdminAddStudentTest extends BaseTest {
	
	
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
    public void runAdminAddStudentTest() {
        String excelPath = "C:\\Users\\Dell\\eclipse-workspace\\StudentManagementTest\\TestResults.xlsx";
        FileInputStream fileInput = null;
        Workbook workbook = null;
        FileOutputStream fileOutput = null;

        try {
            // 1. Open Excel workbook
            fileInput = new FileInputStream(new File(excelPath));
            workbook = new XSSFWorkbook(fileInput);
            Sheet sheet = workbook.getSheetAt(9);

            // 2. Navigate to login page
            driver.get("http://127.0.0.1:3000/login");

            // 3. Perform login
            WebElement emailInput = driver.findElement(By.id("email"));
            WebElement passwordInput = driver.findElement(By.id("password"));
            WebElement loginButton = driver.findElement(By.id("loginButton"));

            emailInput.clear();
            emailInput.sendKeys("test@example.com");
            passwordInput.clear();
            passwordInput.sendKeys("Test1234");
            loginButton.click();

            // 4. Navigate to Add Student page
            driver.findElement(By.id("studentManageLink")).click();
            driver.findElement(By.id("addStudentLink")).click();

            // 5. Iterate through each test case row in Excel
            DataFormatter formatter = new DataFormatter();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    System.out.println("Row " + i + " is NULL. Exiting loop.");
                    break;
                }

                // 5a. Read test data
                String testCaseId = formatter.formatCellValue(row.getCell(0));
                String studentID = formatter.formatCellValue(row.getCell(1));
                String name      = formatter.formatCellValue(row.getCell(2));
                String dob       = formatter.formatCellValue(row.getCell(3));
                String branch    = formatter.formatCellValue(row.getCell(4));
                String expected  = formatter.formatCellValue(row.getCell(5));

                // 5b. Fill form fields
                WebElement studentIDInput = driver.findElement(By.id("studentID"));
                WebElement nameInput      = driver.findElement(By.id("name"));
                WebElement dobInput       = driver.findElement(By.id("dob"));
                WebElement branchInput    = driver.findElement(By.id("branch"));
                WebElement addButton      = driver.findElement(By.id("addStudentButton"));

                studentIDInput.clear();
                studentIDInput.sendKeys(studentID);
                nameInput.clear();
                nameInput.sendKeys(name);
                dobInput.clear();
                dobInput.sendKeys(dob);
                branchInput.clear();
                branchInput.sendKeys(branch);

                // 5c. Submit form
                addButton.click();

                // 5d. Wait for either success or validation failure
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
                boolean successPage = false;
                try {
                    successPage = wait.until(d -> d.getCurrentUrl().contains("student_main"));
                } catch (TimeoutException te) {
                    successPage = false; // stayed on the form or saw errors
                }

                String actual;
                if (successPage) {
                    // 5e. Check database for the new student
                    boolean dbHasStudent = MongoDBUtils.studentExists(studentID);

                    if (dbHasStudent) {
                        actual = "Success";
                        // Return to Add Student page for next input
                        driver.findElement(By.id("addStudentLink")).click();
                    } else {
                        actual = "Failure";
                    }
                } else {
                    // Skip DB check, just consider it failure
                    actual = "Failure";

                    // Clear fields manually for next iteration
                    studentIDInput.clear();
                    nameInput.clear();
                    dobInput.clear();
                    branchInput.clear();
                }

                // 5f. Write result back to Excel
                Cell resultCell = row.createCell(6, CellType.STRING);
                resultCell.setCellValue(actual);

                // 5g. Assert
                if ("Success".equalsIgnoreCase(expected)) {
                    Assert.assertEquals(actual, "Success",
                        testCaseId + " - Expected Success but got " + actual);
                } else {
                    Assert.assertEquals(actual, "Failure",
                        testCaseId + " - Expected Failure but got " + actual);
                }

                // 5h. Log outcome
                System.out.printf("Test Case: %s - Expected: %s - Actual: %s%n",
                                  testCaseId, expected, actual);
            }

            // 6. Save Excel results
            fileInput.close();
            fileOutput = new FileOutputStream(new File(excelPath));
            workbook.write(fileOutput);
            fileOutput.close();

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Exception during test execution: " + e.getMessage());
        } finally {
            try {
                if (fileInput  != null) fileInput.close();
                if (fileOutput != null) fileOutput.close();
                if (workbook   != null) workbook.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}
