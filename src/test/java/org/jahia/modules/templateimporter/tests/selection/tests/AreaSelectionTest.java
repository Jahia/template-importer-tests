package org.jahia.modules.templateimporter.tests.selection.tests;

import org.jahia.modules.templateimporter.tests.TemplateImporterRepository;
import org.jahia.modules.tests.utils.SoftAssertWithScreenshot;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

/**
 * Created by sergey on 2016-07-06.
 */
public class AreaSelectionTest extends TemplateImporterRepository {

    @Test //TI_S2C1, //TI_S2C2
    public void selectAreaInHomeTest() {
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "AreaSelectionTest.selectAreaInHomeTest");
        String projectName = randomWord(8);
        String xPathToSelectInBase = "//body/div[1]";
        String xPathToSelectInHome = "//body/div[1]//div[contains(., 'Level 2-1')]";
        String baseAreaName = randomWord(5);
        String homeAreaName = randomWord(4);

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        selectArea(baseAreaName, xPathToSelectInBase, 1, 0, true);

        switchToTemplate("home");
        checkIfAreaSelected(xPathToSelectInBase, softAssert, true);

        selectArea(homeAreaName, xPathToSelectInHome, 1, 0, true);
        checkIfAreaSelected(xPathToSelectInBase, softAssert, true);
        softAssert.assertAll();
    }

    protected boolean checkIfAreaSelected(String xPath, SoftAssert softAssert, boolean expectedResult) {
        switchToProjectFrame();
        WebElement area = findByXpath(xPath);
        softAssert.assertNotNull(area, "Cannot find an element that you are trying to check if selected as area. XPath: '" + xPath + "'.");

        boolean isAreaSelected = area.getAttribute("class").contains(SELECTED_AREA_MARK);
        switchToDefaultContent();
        softAssert.assertEquals(
                isAreaSelected,
                expectedResult,
                "Assertion if element: '" + xPath + "' has class '" + SELECTED_AREA_MARK + "' (is selected) Failed");
        return isAreaSelected;
    }
}
