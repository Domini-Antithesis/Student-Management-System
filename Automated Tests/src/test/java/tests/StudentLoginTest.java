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



public class StudentLoginTest extends BaseTest {
	
	
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
	
	
    public void runLoginTest() {
        
        // Open Excel File
        String excelPath = "C:\\Users\\Dell\\eclipse-workspace\\StudentManagementTest\\TestResults.xlsx";
        try {
            FileInputStream file = new FileInputStream(new File(excelPath));
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(3);
            
            driver.get("http://127.0.0.1:3000/login_norm");

            // Loop Through Excel Rows
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                DataFormatter formatter = new DataFormatter();
                
                if (row == null) {
                    System.out.println("Row " + i + " is NULL. Exiting loop.");
                    break; // Exit loop immediately
                }

                
                String email = row.getCell(1) != null ? formatter.formatCellValue(row.getCell(1)) : "";
                String password = row.getCell(3) != null ? formatter.formatCellValue(row.getCell(2)) : "";
                String expected = row.getCell(5) != null ? formatter.formatCellValue(row.getCell(3)) : "";
                
                /*
                String email = row.getCell(1).getStringCellValue();
                String password = row.getCell(2).getStringCellValue();
                String expected = row.getCell(3).getStringCellValue();
                */
                // Find Elements
                WebElement studentIDInput = driver.findElement(By.id("studentID"));
                WebElement passwordInput = driver.findElement(By.id("password"));
                WebElement studentloginButton = driver.findElement(By.id("studentLoginButton"));
                
                // Perform Login
                studentIDInput.clear();
                studentIDInput.sendKeys(email);
                passwordInput.clear();
                passwordInput.sendKeys(password);
                studentloginButton.click();
                
                // Check Result
                Thread.sleep(2000); // Wait for result
                String result;
                if (driver.getCurrentUrl().contains("norm_main")) {
                    result = "Success";
                 // Write Test Result to Excel
                    Cell resultCell = row.createCell(4);
                    resultCell.setCellValue(result);
                    
                    // Print Console Output
                    System.out.println("Test Case: " + row.getCell(0).getStringCellValue() + " - Expected: " + expected + " - Actual: " + result);
                    WebElement logoutButton = driver.findElement(By.id("logoutLink"));
                    logoutButton.click();
                    WebElement studentLoginButtonLogin = driver.findElement(By.id("studentLoginLink"));
                    studentLoginButtonLogin.click(); 
                } else {
                    result = "Failure";
                 // Write Test Result to Excel
                    Cell resultCell = row.createCell(4);
                    resultCell.setCellValue(result);
                    
                    // Print Console Output
                    System.out.println("Test Case: " + row.getCell(0).getStringCellValue() + " - Expected: " + expected + " - Actual: " + result);
                }
                
                // Write Test Result to Excel
              /*  Cell resultCell = row.createCell(4);
                resultCell.setCellValue(result);
                
                // Print Console Output
                System.out.println("Test Case: " + row.getCell(0).getStringCellValue() + " - Expected: " + expected + " - Actual: " + result);
                WebElement logoutButton = driver.findElement(By.id("logoutLink"));
                logoutButton.click();
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