package com.studentmanagement.StudentManagementTest;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;


public class GenerateZ {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		TransformerFactory factory = TransformerFactory.newInstance();
        Source xslt = new StreamSource(new File("xslt-reports/testng-results.xsl"));
        Source xml = new StreamSource(new File("test-output/testng-results.xml"));

        Transformer transformer = factory.newTransformer(xslt);
        transformer.transform(xml, new StreamResult(new File("test-output/XSLTReport.html")));

        System.out.println("XSLT Report generated successfully!");

	}

}
