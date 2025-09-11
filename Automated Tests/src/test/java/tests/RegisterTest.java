package tests;

import org.openqa.selenium.*;
import utils.ExcelUtils;
import utils.MongoDBUtils;
import java.io.IOException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.Duration;

import org.testng.annotations.*;
import java.io.*;



public class RegisterTest extends BaseTest {
	
	
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

    @BeforeGroups("login")
    public void beforeGroups() {
        System.out.println("[BeforeGroups] Setting up before 'login' group.");
    }

    @AfterGroups("login")
    public void afterGroups() {
        System.out.println("[AfterGroups] Cleaning up after 'login' group.");
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
			
			groups = {"login"},
	        priority = 1,
	        description = "Test login and main page functionalities.",
	        //dependsOnGroups = {"login"},
	        //dependsOnMethods = {"verifyLogin"},
	        enabled = true,
	        alwaysRun = true,
	        timeOut = 300000
			
			)
	
    public void runRegisterTest() {

        // Open Excel File
        String excelPath = "C:\\Users\\Dell\\eclipse-workspace\\StudentManagementTest\\TestResults.xlsx";
        try {
            FileInputStream file = new FileInputStream(new File(excelPath));
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(2);
            
            driver.get("http://127.0.0.1:3000/register");

            // Loop Through Excel Rows
                // Check if row is null
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                DataFormatter formatter = new DataFormatter();
                
                if (row == null) {
                    System.out.println("Row " + i + " is NULL. Exiting loop.");
                    break; // Exit loop immediately
                }

                String username = row.getCell(1) != null ? formatter.formatCellValue(row.getCell(1)) : "";
                String regEmail = row.getCell(2) != null ? formatter.formatCellValue(row.getCell(2)) : "";
                String regPassword = row.getCell(3) != null ? formatter.formatCellValue(row.getCell(3)) : "";
                String regDob = row.getCell(4) != null ? formatter.formatCellValue(row.getCell(4)) : "";
                String expected = row.getCell(5) != null ? formatter.formatCellValue(row.getCell(5)) : "";
                
                // Find Elements
                WebElement usernameInput = driver.findElement(By.id("username"));
                WebElement emailInput = driver.findElement(By.id("regEmail"));
                WebElement passwordInput = driver.findElement(By.id("regPassword"));
                WebElement dobInput = driver.findElement(By.id("regDob"));
                WebElement registerButton = driver.findElement(By.id("registerButton"));
                
                // Perform Register
                usernameInput.clear();
                usernameInput.sendKeys(username);
                emailInput.clear();
                emailInput.sendKeys(regEmail);
                passwordInput.clear();
                passwordInput.sendKeys(regPassword);
                dobInput.clear();
                dobInput.sendKeys(regDob);
                registerButton.click();
              /*  
                try {
                    Alert alert = driver.switchTo().alert();
                    alert.accept();  // Click OK or Close the alert
                  } catch (NoAlertPresentException a) {
                	Thread.sleep(100); // Wait for result
                }
				*/
                
                // Check Result
                Thread.sleep(2000); // Wait for result
                String result;
                if (driver.getCurrentUrl().contains("login")) {
                    result = "Success";
                    // Write Test Result to Excel
                    Cell resultCell = row.createCell(6);
                    resultCell.setCellValue(result);
                    
                    // Print Console Output
                    System.out.println("Test Case: " + row.getCell(0).getStringCellValue() + " - Expected: " + expected + " - Actual: " + result);
                    WebElement registerButtonLogin = driver.findElement(By.id("registerLink"));
                    registerButtonLogin.click();
                } else {
                    result = "Failure";
                    // Write Test Result to Excel
                    Cell resultCell = row.createCell(6);
                    resultCell.setCellValue(result);
                    
                    // Print Console Output
                    System.out.println("Test Case: " + row.getCell(0).getStringCellValue() + " - Expected: " + expected + " - Actual: " + result);
                    
                }
                
             /*   
                // Write Test Result to Excel
                Cell resultCell = row.createCell(6);
                resultCell.setCellValue(result);
                
                // Print Console Output
                System.out.println("Test Case: " + row.getCell(0).getStringCellValue() + " - Expected: " + expected + " - Actual: " + result);
                WebElement registerButtonLogin = driver.findElement(By.id("registerLink"));
                if(result.equals("Failure")) {
                	 usernameInput.clear();
                     emailInput.clear();
                     passwordInput.clear();
                     dobInput.clear();
                }
                else {
                registerButtonLogin.click();
                }
                */
            }
            
                
            // Save Excel Results
            file.close();
            FileOutputStream outFile = new FileOutputStream(new File(excelPath));
            workbook.write(outFile);
            outFile.close();

            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
