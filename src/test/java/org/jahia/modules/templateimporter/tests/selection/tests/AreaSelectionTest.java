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
        String xPathToSelectInHome = "//body/div[1]//div[contains(., 'Level 2-1')]";
        String homeAreaName = randomWord(4);

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        selectArea(homeAreaName, xPathToSelectInHome, 2, 0);
        checkIfAreaSelected(xPathToSelectInHome, softAssert, true);

        softAssert.assertAll();
    }

    @Test //TI_S2C4, TI_S2C12, TI_S2C14, TI_S2C9, TI_S2C11
    public void selectAreaInUserCreatedTemplate() {
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "AreaSelectionTest.selectAreaInUserCreatedTemplate");
        String projectName = randomWord(8);
        String xPathToSelectInHomeTemplate = "//body/div[1]";
        String xPathToSelectInUsersTemplate = "//body/div[1]//div[contains(., 'Level 2-1')]";
        String homeAreaName = randomWord(5);
        String userAreaName = randomWord(4);
        String newTemplateName = randomWord(10);
        String newTemplateNameTwo = randomWord(10);
        String newTemplatePageFileName = "page2.html";
        String newTemplatePageFileNameTwo = "page3.html";

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");//Select area on base
        selectArea(homeAreaName, xPathToSelectInHomeTemplate, 1, 0);//Create new template
        createNewTemplate(newTemplateName, newTemplatePageFileName);//Select area in new page template
        selectArea(userAreaName, xPathToSelectInUsersTemplate, 2, 0);//Create another template
        createNewTemplate(newTemplateNameTwo, newTemplatePageFileNameTwo);//Check that base and home did not lost their selections
        //Check that both templates are present on page
        WebElement newTemplateTab = findByXpath("//ti-tab[contains(., '"+newTemplateName+"')]");
        softAssert.assertNotNull(newTemplateTab, "Cannot find tab with new template name '"+newTemplateName+"' after creating new template.");
        WebElement newTemplateTabTwo = findByXpath("//ti-tab[contains(., '"+newTemplateNameTwo+"')]");
        softAssert.assertNotNull(newTemplateTabTwo, "Cannot find tab with new template name '"+newTemplateNameTwo+"' after creating new template.");

        switchToTemplate("home");
        checkIfAreaSelected(xPathToSelectInHomeTemplate, softAssert, true);
        switchToTemplate(newTemplateName);
        checkIfAreaSelected(xPathToSelectInUsersTemplate, softAssert, true);

        softAssert.assertAll();
    }

    @Test //TI_S2C6, TI_S2C7, MOD-1478
    public void selectTwoAreasSameName() {
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "AreaSelectionTest.selectTwoAreasSameName");
        String projectName = randomWord(8);
        String xPathToSelectInHomeTemplate = "//body/div[1]";
        String xPathToHomeAreaSibling = "//div[contains(., 'Level 1-3')]";
        String homeAreaName = randomWord(5);
        String newTemplateName = randomWord(10);
        String xPathNewTemplateArea = "//body/div[1]/div[1]";
        String xPathNewTemplateAreaTwo = "//body/div[1]/div[2]";
        String newTemplatePageFileName = "page1.html";

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        selectArea(homeAreaName, xPathToSelectInHomeTemplate, 1, 0);
        selectAreaToCheckName(homeAreaName, false, xPathToHomeAreaSibling, 1, 0, true, softAssert, "2 sibling areas on home.");

        createNewTemplate(newTemplateName, newTemplatePageFileName);
        selectArea(homeAreaName, xPathNewTemplateArea, 2, 0);
        selectAreaToCheckName(homeAreaName, false, xPathNewTemplateAreaTwo, 2, 0, true, softAssert, "Duplicate area name on same user created template.");
        selectArea(homeAreaName+"two", xPathNewTemplateAreaTwo, 2, 0);

        softAssert.assertAll();
    }

    @Test (enabled = false)//TI_S2C5
    public void selectAreaInHomeWithoutBase(){
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "AreaSelectionTest.selectAreaInHomeWithoutBase");
        String projectName = randomWord(8);
        String xPathToSelectInHome = "//body/div[1]";
        String expectedToastText = "Cannot select inside an area not selected in base page";
        String newTemplateName = randomWord(3);

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        switchToTemplate("home");
        selectWrongElement(xPathToSelectInHome, 1, 0, expectedToastText, softAssert, "Selecting home when base is not selected");
        createNewTemplate(newTemplateName, "page1.html");
        selectWrongElement(xPathToSelectInHome, 1, 0, expectedToastText, softAssert, "Selecting home when base is not selected");
        softAssert.assertAll();
    }

    @Test (enabled = false)//TI_S2C8, TI_S2C19
    public void selectParentArea(){
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "AreaSelectionTest.selectParentArea");
        String projectName = randomWord(8);
        String xPathToArea = "//body/div[1]/div[contains(., 'Level 3-1')][1]/div[1]";
        String areaName = randomWord(7);
        String xPathToParentArea = "//body/div[1]";
        String expectedToastText = "Cannot select an area that contains a selected area";

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        selectArea(areaName, xPathToArea, 2, 0);
        selectWrongElement(xPathToParentArea, 1, 0, expectedToastText, softAssert, "After selecting parent of existing area");

        softAssert.assertAll();
    }

    @Test (enabled = false)//TI_S2C33
    public void selectSiblingAreaInOtherTemplate(){
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "AreaSelectionTest.selectSiblingAreaInOtherTemplate");
        String projectName = randomWord(8);
        String xPathToArea = "//body/div[1]//div[contains(., 'Level 2-1')]";
        String areaName = randomWord(7);
        String xPathToSiblingArea = "//body/div[3]//div[contains(., 'Level 2-1')]";
        String expectedToastText = "Cannot select inside an area not selected in base page";

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        selectArea(areaName, xPathToArea, 2, 0);
        switchToTemplate("home");
        checkIfAreaSelected(xPathToArea, softAssert, true);
        selectWrongElement(xPathToSiblingArea, 2, 0, expectedToastText, softAssert, "After selecting sibling of base-area on home");
        checkIfAreaSelected(xPathToSiblingArea, softAssert, false);
        softAssert.assertAll();
    }

    @Test (enabled = false)//TI_S2C18, TI_S2C20, TI_S2C21
    public void selectParentOfView(){
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "AreaSelectionTest.selectParentOfView");
        String projectName = randomWord(8);
        String areaA1Name = randomWord(6);
        String viewV1Name = randomWord(8);
        String viewNodeType = "jnt:bootstrapMainContent";
        String areaA1xPath =        "//body/div[1]/div[contains(., 'Level 3-1')][1]";
        String viewV1xPath =        "//body/div[1]/div[contains(., 'Level 3-1')][1]/div[1]/div[1]";
        String parentOfViewXpath =  "//body/div[1]/div[contains(., 'Level 3-1')][1]/div[1]";
        String childOfViewXpath =   "//body/div[1]/div[contains(., 'Level 3-1')][1]/div[1]/div[1]/div[1]";
        String expectedToastParentOfView = "Cannot select a view that contains a view";
        String expectedToastChildOfView = "Cannot select a view that is contained in a view";

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        selectArea(areaA1Name, areaA1xPath, 2, 0);
        selectView(viewV1Name, viewNodeType, viewV1xPath, 1, 0);
        selectWrongElement(parentOfViewXpath, 1, 0, expectedToastParentOfView, softAssert, "Selecting node between area and view.");
        selectWrongElement(childOfViewXpath, 1, 0, expectedToastChildOfView, softAssert, "Selecting node between area and view.");

        softAssert.assertAll();
    }

    @Test (enabled = false)//TI_S2C26
    public void selectSameAreaOnAnotherTemplate(){
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "AreaSelectionTest.selectSameAreaOnAnotherTemplate");
        String projectName = randomWord(8);
        String xPathToArea = "//body/div[1]/div[contains(., 'Level 3-1')][1]/div[1]";
        String areaName = randomWord(7);
        String expectedToastText = "Cannot select an area if already selected on base page";

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        selectArea(areaName, xPathToArea, 2, 0);
        switchToTemplate("home");
        selectWrongElement(xPathToArea, 2, 0, expectedToastText, softAssert, true, false, "Selecting the same element on base and home");

        softAssert.assertAll();
    }

    @Test (enabled = false)//TI_S2C27
    public void selectSameViewOnAnotherTemplate(){
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "AreaSelectionTest.selectSameViewOnAnotherTemplate");
        String projectName = randomWord(8);
        String xPathToArea = "//body/div[1]/div[contains(., 'Level 3-1')][1]/div[1]";
        String xPathToView = "//body/div[1]/div[contains(., 'Level 3-1')][1]/div[1]/div[1]";
        String areaName = randomWord(7);
        String viewName = randomWord(9)+"Capital";
        String nodeType = "jnt:html";
        String expectedToastText = "Cannot select a view if already selected on base page";

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        selectArea(areaName, xPathToArea, 2, 0);
        selectView(viewName, nodeType, xPathToView, 1, 0);
        switchToTemplate("home");
        selectWrongElement(xPathToView, 1, 0, expectedToastText, softAssert, false, true, "Selecting the same element on base and home");

        softAssert.assertAll();
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
        rightMouseClick(xPath, xOffset, yOffset);
        WebElement menuAreaBtn = findByXpath("//div[@ng-click='rmc.canBeArea && rmc.showArea()']");
        waitForElementToStopMoving(menuAreaBtn);
        clickOn(menuAreaBtn);

        WebElement areaNameField = findByXpath("//input[@name='areaName']");
        waitForElementToStopMoving(areaNameField);
        typeInto(areaNameField, areaName);
        WebElement cancelButton = findByXpath("//button[@ng-click='sac.area.cancel()']");
        WebElement selectButton = findByXpath("//button[@ng-click='sac.area.ok()']");
        areaNameField = findByXpath("//input[@name='areaName']");
        boolean hasNameError = areaNameField.getAttribute("class").contains("ng-invalid-");
        boolean okButtonEnabled = selectButton.isEnabled();

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

    protected void selectWrongElement(String       xPath,
                                      int          xOffset,
                                      int          yOffset,
                                      String       toastErrorMsg,
                                      SoftAssert   softAssert,
                                      boolean      expectedAreaSelection,
                                      boolean      expectedViewSelection,
                                      String       errorMsg){
        rightMouseClick(xPath, xOffset, yOffset);
        WebElement toast = findByXpath("//div[contains(@class, 'toast-warning')]//div[contains(., '" + toastErrorMsg + "')]");
        if (toast == null) {
            softAssert.fail(errorMsg + ". Toast not found. Expected toast message:'" + toastErrorMsg + "'");
        } else {
            softAssert.assertTrue(isVisible(toast, 3), errorMsg + ". Toast is not visible. Toast message:'" + toastErrorMsg + "'");
            if (toast.isDisplayed()) {
                clickOn(toast);
                waitForElementToBeInvisible(toast);
            }
        }
        checkIfAreaSelected(xPath, softAssert, expectedAreaSelection, errorMsg);
        checkIfViewSelected(xPath, softAssert, expectedViewSelection, errorMsg);
    }

    protected void selectWrongElement(String       xPath,
                                      int          xOffset,
                                      int          yOffset,
                                      String       toastErrorMsg,
                                      SoftAssert   softAssert,
                                      String       errorMsg){
        selectWrongElement(xPath, xOffset, yOffset, toastErrorMsg, softAssert, false, false, errorMsg);
    }
}
