package org.jahia.modules.templateimporter.tests.selection.tests;

import org.jahia.modules.templateimporter.tests.TemplateImporterRepository;
import org.jahia.modules.tests.utils.SoftAssertWithScreenshot;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

/**
 * Created by sergey on 2016-07-13.
 */
public class SelectionRemovalTest extends TemplateImporterRepository{
    @Test //TI_S2C22
    public void areaRemovalTest(){
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "SelectionRemovalTest.areaRemovalTest");
        String projectName = randomWord(8);
        String xPathToSelectInBase = "//body/div[1]";
        String areaNameA1 = randomWord(5);
        int xOffsetA1Area = 1;
        int yOffsetA1Area = 0;

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        selectArea(areaNameA1, xPathToSelectInBase, xOffsetA1Area, yOffsetA1Area, true);
        removeArea(xPathToSelectInBase, xOffsetA1Area, yOffsetA1Area);
        softAssert.assertAll();
    }
}
