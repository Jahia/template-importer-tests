package org.jahia.modules.templateimporter.tests.projectmanagement.tests;

import org.jahia.modules.templateimporter.tests.TemplateImporterRepository;
import org.jahia.modules.tests.utils.SoftAssertWithScreenshot;
import org.openqa.selenium.By;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

/**
 * Created by sergey on 2016-06-29.
 */
public class DeletProjectTest extends TemplateImporterRepository {
    @Test
    public void deleteProjectTest(){
        String projectToDeleteName = randomWord(7);
        String projectToKeepName = randomWord(5);
        SoftAssert softAssert = new SoftAssertWithScreenshot(driver, "DeletProjectTest.deleteProjectTest");

        importProject("en", projectToDeleteName, "", "AlexLevels.zip");
        importProject("en", projectToKeepName, "", "AlexLevels.zip");

        softAssert.assertEquals(deleteProject(projectToDeleteName),
                true,
                "Project with name '"+projectToDeleteName+" is still visible after deletion.");
        softAssert.assertEquals(isVisible(By.xpath("//md-card-title-text[contains(., '"+projectToKeepName+"')]/ancestor::md-card"), 10),
                true,
                "Project with name '"+projectToKeepName+"' should not be deleted, but become invisible after another project deletion.");
    }
}
