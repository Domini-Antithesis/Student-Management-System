//Student Courses Test
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

public class AdminMainPageTest extends BaseTest {
	
	
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
	public void runAdminMainPageTest() {
		
        
		
        // Open Excel File
        String excelPath = "C:\\Users\\Dell\\eclipse-workspace\\StudentManagementTest\\TestResults.xlsx";
        
        driver.get("http://127.0.0.1:3000/login");
        
        
        // Find Elements
        WebElement emailInput = driver.findElement(By.id("email"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("loginButton"));
        
     // Perform Login
        emailInput.clear();
        emailInput.sendKeys("test@example.com");
        passwordInput.clear();
        passwordInput.sendKeys("Test1234");
        loginButton.click();
		
        
        
        try {
            FileInputStream file = new FileInputStream(new File(excelPath));
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(5);            
            
         // Loop through test cases
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String action = row.getCell(1).getStringCellValue(); 
                String expected = row.getCell(2).getStringCellValue();
                String actual = "";
                
                // Click the appropriate button
                if (action.equalsIgnoreCase("adminStudentManage")) {
                    driver.findElement(By.id("studentManageLink")).click();
                    actual = driver.getCurrentUrl().contains("/student_main") ? "Success" : "Failed";
                    WebElement backButtonCourse = driver.findElement(By.id("backLink"));
                    backButtonCourse.click();
                
                } 
               
                
                else if (action.equalsIgnoreCase("adminCoursesManage")) {
                    driver.findElement(By.id("coursesManageLink")).click();
                    actual = driver.getCurrentUrl().contains("/courses_main") ? "Success" : "Failed";
                    WebElement backButtonCourse = driver.findElement(By.id("backLink"));
                    backButtonCourse.click();
                } 
                
                else if (action.equalsIgnoreCase("adminMarksManage")) {
                    driver.findElement(By.id("marksManageLink")).click();
                    actual = driver.getCurrentUrl().contains("/marks") ? "Success" : "Failed";
                    WebElement backButtonCourse = driver.findElement(By.id("backLink"));
                    backButtonCourse.click();
                } 
                  
                
                
                // Check Result
                Thread.sleep(500); // Wait for result                                
                              
             // Write Test Result to Excel
                Cell resultCell = row.createCell(3);
                resultCell.setCellValue(actual);
                
                // Print Console Output
                System.out.println("Test Case: " + row.getCell(0).getStringCellValue() + " - Expected: " + expected + " - Actual: " + actual);
               
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