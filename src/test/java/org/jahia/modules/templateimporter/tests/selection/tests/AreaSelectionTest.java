package org.jahia.modules.templateimporter.tests.selection.tests;

import org.jahia.modules.templateimporter.tests.TemplateImporterRepository;
import org.jahia.modules.tests.utils.SoftAssertWithScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
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

    @Test //TI_S2C4, TI_S2C12, TI_S2C14, TI_S2C9
    public void selectAreaInUserCreatedTemplate() {
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "AreaSelectionTest.selectAreaInUserCreatedTemplate");
        String projectName = randomWord(8);
        String xPathToSelectInBaseTemplate = "//body/div[1]";
        String xPathToSelectInUsersTemplate = "//body/div[1]//div[contains(., 'Level 2-1')]";
        String baseAreaName = randomWord(5);
        String userAreaName = randomWord(4);
        String newTemplateName = randomWord(10);
        String newTemplateNameTwo = randomWord(10);
        String newTemplatePageFileName = "page2.html";
        String newTemplatePageFileNameTwo = "page3.html";

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");//Select area on base
        selectArea(baseAreaName, xPathToSelectInBaseTemplate, 1, 0, true);//Create new template
        createNewTemplate(newTemplateName, newTemplatePageFileName);//Check if selection from base is inherited to new template
        checkIfAreaSelected(xPathToSelectInBaseTemplate, softAssert, true);//Select area in new page template
        selectArea(userAreaName, xPathToSelectInUsersTemplate, 1, 0, true);//Create another template
        createNewTemplate(newTemplateNameTwo, newTemplatePageFileNameTwo);//Check that base and home did not lost their selections
        checkIfAreaSelected(xPathToSelectInBaseTemplate, softAssert, true);
        switchToTemplate("base");
        checkIfAreaSelected(xPathToSelectInBaseTemplate, softAssert, true);
        switchToTemplate("home");
        checkIfAreaSelected(xPathToSelectInBaseTemplate, softAssert, true);

        softAssert.assertAll();
    }

    @Test //TI_S2C6, TI_S2C7
    public void selectTwoAreasSameName() {
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "AreaSelectionTest.selectTwoAreasSameName");
        String projectName = randomWord(8);
        String xPathToSelectInBaseTemplate = "//body/div[1]";
        String xPathToBaseAreaSibling = "//div[contains(., 'Level 1-3')]";
        String xPathToBaseAreaSiblingChild = "//div[contains(., 'Level 1-3')]/div";
        String xPathToBaseAreaChild = "//body/div[1]//div[contains(., 'Level 2-1')]";
        String baseAreaName = randomWord(5);
        String newTemplateName = randomWord(10);
        String newTemplatePageFileName = "page1.html";

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        selectArea(baseAreaName, xPathToSelectInBaseTemplate, 1, 0, true);
        selectAreaToCheckName(baseAreaName, false, xPathToBaseAreaSibling, 1, 0, true, softAssert, "2 sibling areas on base.");
        selectArea(baseAreaName+"Two", xPathToBaseAreaSibling, 1, 0, true);
        switchToTemplate("home");
        selectAreaToCheckName(baseAreaName, false, xPathToBaseAreaSiblingChild, 1, 0, true, softAssert, "Base sibling on home.");
        selectAreaToCheckName(baseAreaName, false, xPathToBaseAreaChild, 1, 0, true, softAssert, "Base child on home.");
        createNewTemplate(newTemplateName, newTemplatePageFileName);
        selectAreaToCheckName(baseAreaName, false, xPathToBaseAreaSiblingChild, 1, 0, true, softAssert, "Base sibling on custom template.");
        selectAreaToCheckName(baseAreaName, false, xPathToBaseAreaChild, 1, 0, true, softAssert, "Base child on custom template.");

        softAssert.assertAll();
    }

    protected boolean checkIfAreaSelected(String xPath,
                                          SoftAssert softAssert,
                                          boolean expectedResult) {
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

    /**
     * Select area, enter area name and check for error messages, close the dialogue after all
     * @param areaName String, name of area
     * @param isNameValid boolean, specify is that name supposed to be a valid name
     * @param xPath String, xPath to area you want to select
     * @param xOffset int, Horizontal offset in pixels, from the <u>left</u> border of element.
     *                Pass negative value to move left. Pass 0 to use calculated center of the element (Default click behaviour).
     * @param yOffset int, Vertical offset in pixels, from the <u>top</u> border of element.
     *                Pass negative value to move left. Pass 0 to use calculated center of the element (Default click behaviour).
     * @param closeDialogueAfterall True if you want to close dialogue window after all asserts
     * @param softAssert SoftAssert, instance of soft assert that will collect all the errors
     * @param errorMsg String, will be prepended to all the error messages inside that method
     */
    protected void selectAreaToCheckName(String     areaName,
                                         boolean    isNameValid,
                                         String     xPath,
                                         int        xOffset,
                                         int        yOffset,
                                         boolean    closeDialogueAfterall,
                                         SoftAssert softAssert,
                                         String     errorMsg) {
        switchToProjectFrame();
        WebElement area = findByXpath(xPath);
        Assert.assertNotNull(area, "Cannot find an element that you are trying to select as area. XPath: '" + xPath + "'.");

        if (xOffset == 0) {
            xOffset = area.getSize().getWidth() / 2;
        }
        if (yOffset == 0) {
            yOffset = area.getSize().getHeight() / 2;
        }
        new Actions(getDriver()).moveToElement(area, xOffset, yOffset).contextClick().build().perform();
        switchToDefaultContent();

        WebElement areaNameField = findByXpath("//input[@name='areaName']");

        typeInto(areaNameField, areaName);
        WebElement cancelButton = findByXpath("//button[@ng-click='dac.cancel()']");
        WebElement okButton = findByXpath("//button[@ng-click='dac.ok()']");
        areaNameField = findByXpath("//input[@name='areaName']");
        boolean hasNameError = areaNameField.getAttribute("class").contains("ng-invalid-");
        boolean okButtonEnabled = okButton.isEnabled();

        if (isNameValid) {
            softAssert.assertTrue(okButtonEnabled, errorMsg+" Area name is valid, but 'OK' button disabled."+"XPath:'"+xPath+"', Area Name:"+areaName);
            softAssert.assertFalse(hasNameError, errorMsg+" Area name is valid, but 'Area name field' is marked as invalid."+"XPath:'"+xPath+"', Area Name:"+areaName);
        } else {
            softAssert.assertFalse(okButtonEnabled, errorMsg+" Area name is not valid, but 'OK' button is enabled."+"XPath:'"+xPath+"', Area Name:"+areaName);
            softAssert.assertTrue(hasNameError, errorMsg+" Area name is not valid, but 'Area name field' is not marked as invalid."+"XPath:'"+xPath+"', Area Name:"+areaName);
        }

        if (closeDialogueAfterall) {
            waitForElementToBeEnabled(cancelButton, 5);
            clickOn(cancelButton);
            waitForElementToBeInvisible(cancelButton);
        }
    }
}
