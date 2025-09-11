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
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.service.DriverService;


public class FullTest {
	public static void main(String[] args) {
		try {
			//System.out.println("Testing Home Page.");
			//HomePageTest.runHomePageTest();
			//System.out.println("Testing Login Page.");
			//LoginTest.runLoginTest();
			//System.out.println("Testing Register Page.");
			//RegisterTest.runRegisterTest();
		}
		
		catch (Exception e) {
			
            e.printStackTrace();
        }
	}
}