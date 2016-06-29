package org.jahia.modules.templateimporter.tests.projectmanagement.tests;

import org.jahia.modules.templateimporter.tests.TemplateImporterRepository;
import org.jahia.modules.tests.utils.SoftAssertWithScreenshot;
import org.openqa.selenium.By;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sergey on 2016-06-16.
 */
public class ImportTest extends TemplateImporterRepository {
    //Case TI_S1C1
    @Test
    public void importTest() {
        SoftAssert softAssert = new SoftAssertWithScreenshot(driver, "ImportTest.importTest");
        String projectName = randomWord(10);
        String projectDescription = randomWord(25)+" "+randomWord(10)+" "+randomWord(3);

        importProject("en", projectName, projectDescription, "AlexLevels.zip");

        softAssert.assertEquals(
                isVisible(By.xpath("//*[@ng-if='p.description'][contains(., '"+projectDescription+"')]"), 7),
                true,
                "Project descriptions is not visible. Should be: "+projectDescription);
    }

    //TI_S1C2
    @Test
    public void importSeveralProjectsTest(){
        SoftAssert softAssert = new SoftAssertWithScreenshot(driver, "ImportTest.importSeveralProjectsTest");
        int projectsToImport = 3;
        Map<String, String> projectsInfo = new HashMap<String, String>();

        for(int i = 0; i < projectsToImport; i++) {
            String projectName = randomWord(10);
            String projectDescription = randomWord(1) + " " + randomWord(2) + " " + randomWord(3);

            projectsInfo.put(projectName, projectDescription);

            importProject("en", projectName, projectDescription, "AlexLevels.zip");

            softAssert.assertEquals(
                    isVisible(By.xpath("//*[@ng-if='p.description'][contains(., '" + projectDescription + "')]"), 7),
                    true,
                    "Project descriptions is not visible. Should be: " + projectDescription);
        }

        for(String projectName: projectsInfo.keySet()){
            String projectDescription = projectsInfo.get(projectName);

            softAssert.assertEquals(
                    isVisible(By.xpath("//md-card-title-text/span[contains(text(), '"+projectName+"')]"), 5),
                    true,
                    "After importing several projects, cannot find name of one of them:"+ projectName);
            softAssert.assertEquals(
                    isVisible(By.xpath("//*[@ng-if='p.description'][contains(., '" + projectDescription + "')]"), 7),
                    true,
                    "Project descriptions is not visible. Should be: " + projectDescription);
        }
    }
}
