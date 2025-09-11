//home page
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
import org.testng.annotations.Test;
import org.testng.annotations.*;
import java.io.*;

public class HomePageTest extends BaseTest {
	
	
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
	public void runHomePageTest() {
		
        
        // Open Excel File
        String excelPath = "C:\\Users\\Dell\\eclipse-workspace\\StudentManagementTest\\TestResults.xlsx";
        
        driver.get("http://127.0.0.1:3000/");
        
        try {
            FileInputStream file = new FileInputStream(new File(excelPath));
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);            
            
         // Loop through test cases
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String action = row.getCell(1).getStringCellValue(); // L or R
                String expected = row.getCell(2).getStringCellValue();
                String actual = "";
                
                // Click the appropriate button
                if (action.equalsIgnoreCase("loginAdmin")) {
                    driver.findElement(By.id("loginLinkMain")).click();
                    actual = driver.getCurrentUrl().contains("/login") ? "Success" : "Failed";
                } 
                else if (action.equalsIgnoreCase("registerAdmin")) {
                    driver.findElement(By.id("registerLinkMain")).click();
                    actual = driver.getCurrentUrl().contains("/register") ? "Success" : "Failed";
                } 
                else if (action.equalsIgnoreCase("loginStudent")) {
                    driver.findElement(By.id("loginLinkMain")).click();
                    driver.findElement(By.id("studentLoginLink")).click();
                    actual = driver.getCurrentUrl().contains("/login_norm") ? "Success" : "Failed";
                }
                
                // Check Result
                Thread.sleep(2000); // Wait for result                                
                              
                // Write Test Result to Excel
                Cell resultCell = row.createCell(3);
                resultCell.setCellValue(actual);
                
                // Print Console Output
                System.out.println("Test Case: " + row.getCell(0).getStringCellValue() + " - Expected: " + expected + " - Actual: " + actual);
                WebElement homeButton = driver.findElement(By.id("homeLink"));
                homeButton.click();
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