package org.jahia.modules.templateimporter.tests.selection.tests;

import org.jahia.modules.templateimporter.tests.TemplateImporterRepository;
import org.jahia.modules.tests.utils.SoftAssertWithScreenshot;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

/**
 * Created by sergey on 2016-07-12.
 */
public class TemplateManipulationTest extends TemplateImporterRepository{
    @Test
    public void createMultipleTemplatesTest(){
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "TemplateManipulationTest.twoTemplatesSameNameTest");
        String projectName = randomWord(20);
        String xPathAreaA1 = "//body/div[1]";
        String areaA1Name = randomWord(5);
        String templateOneName = "one";
        String templateTwoName = "two";
        String templateThreeName = "Three";

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        selectArea(areaA1Name, xPathAreaA1, 1, 0);
        createNewTemplate(templateOneName, "page1.html");
        createNewTemplate(templateTwoName, "page2.html");

        switchToTemplate("home");
        switchToProjectFrame();
        WebElement linkToPageThree = findByXpath("//a[@href='page3.html']");
        clickOn(linkToPageThree);
        switchToDefaultContent();
        WebElement templateNameField = findByXpath("//input[@name='pageName']");
        WebElement createBtn = findByXpath("//button[@ng-click='pac.createPage()']");
        typeInto(templateNameField, templateThreeName);
        waitForElementToBeEnabled(createBtn, 5);
        clickOn(createBtn);
        waitForElementToBeInvisible(createBtn);

        WebElement templateOne = findByXpath("//ti-tab[contains(., '"+templateOneName+"')]");
        WebElement templateTwo = findByXpath("//ti-tab[contains(., '"+templateTwoName+"')]");
        WebElement templateThree = findByXpath("//ti-tab[contains(., '"+templateThreeName+"')]");

        softAssert.assertTrue(isVisible(templateOne), "Template with name "+templateOneName+" is not visible");
        softAssert.assertTrue(isVisible(templateTwo), "Template with name "+templateTwoName+" is not visible");
        softAssert.assertTrue(isVisible(templateThree), "Template with name "+templateThreeName+" is not visible");

        softAssert.assertAll();
    }

    @Test //TI_S2C13, TI_S2C15
    public void twoTemplatesSameNameTest(){
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "TemplateManipulationTest.twoTemplatesSameNameTest");
        String projectName = randomWord(20);
        String xPathToSelectInBase = "//body/div[1]";
        String baseAreaName = randomWord(5);
        String newTemplateName = "NewTemplate";
        String newTemplatePage = "page1.html";

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        selectArea(baseAreaName, xPathToSelectInBase, 1, 0);
        createNewTemplate(newTemplateName, newTemplatePage);
        checkTemplateName(newTemplateName, "", false, false, softAssert, "Two templates, same name.");

        String oldIframeUrl = getCurrentIframeUrl();
        removeTemplate(newTemplateName);
        String newIframeUrl = getCurrentIframeUrl();
        softAssert.assertNotEquals(oldIframeUrl, newIframeUrl, "iFrame's URL did not change after template removal. Old page is still loaded into iFrame.");
        createNewTemplate(newTemplateName, newTemplatePage);

        softAssert.assertAll();
    }

    @Test //MOD-1189
    public void templateRemovalTest(){
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "TemplateManipulationTest.templateRemovalTest");
        String projectName = randomWord(20);
        String xPathToSelectInBase = "//body/div[1]";
        String baseAreaName = randomWord(5);
        String newTemplateOneName = "NewTemplateOne";
        String newTemplateOnePage = "page1.html";
        String newTemplateTwoName = "NewTemplateTwo";
        String newTemplateTwoPage = "page2.html";

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        selectArea(baseAreaName, xPathToSelectInBase, 1, 0);
        createNewTemplate(newTemplateOneName, newTemplateOnePage);
        createNewTemplate(newTemplateTwoName, newTemplateTwoPage);
        String ifarmeUrlBeforeRemoval = getCurrentIframeUrl();
        removeTemplate(newTemplateOneName);
        softAssert.assertEquals(
                getCurrentIframeUrl(),
                ifarmeUrlBeforeRemoval,
                "After removing not currently open template, iFrame's URL has changed. (Loaded another template)"
        );

        createNewTemplate(newTemplateOneName, newTemplateOnePage);
        ifarmeUrlBeforeRemoval = getCurrentIframeUrl();
        removeTemplate(newTemplateOneName);
        softAssert.assertNotEquals(
                getCurrentIframeUrl(),
                ifarmeUrlBeforeRemoval,
                "After removing currently open template, iFrame's URL has not changed. (Another template was not loaded)"
        );

        softAssert.assertAll();
    }

    @Test
    public void clearSelectionsTest(){
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "TemplateManipulationTest.clearSelectionsTest");
        String projectName = randomWord(20);
        String xPathAreaA1 = "//body/div[1]";
        String xPathViewV1 = "//body/div[1]/div[1]";
        String xPathAreaA2 = "//body/div[2]";
        String areaA1Name = randomWord(5);
        String areaA2Name = randomWord(5);
        String viewName = randomWord(5);
        String nodeType = "jnt:bootstrapMainContent";

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        selectArea(areaA1Name, xPathAreaA1, 2, 0);
        clearSelections("home");
        checkIfAreaSelected(xPathAreaA1, softAssert, false, "Clear selections button was pressed when only one area selected.");
        selectArea(areaA1Name, xPathAreaA1, 2, 0);
        selectView(viewName, nodeType, xPathViewV1, 2, 0);
        selectArea(areaA2Name, xPathAreaA2, 2, 0);
        clearSelections("home");
        checkIfAreaSelected(xPathAreaA1, softAssert, false, "Clear selections button was pressed when 2 areas and a view are selected.");
        checkIfAreaSelected(xPathAreaA2, softAssert, false, "Clear selections button was pressed when 2 areas and a view are selected.");
        checkIfViewSelected(xPathViewV1, softAssert, false, "Clear selections button was pressed when 2 areas and a view are selected.");

        softAssert.assertAll();
    }

    @Test(enabled = false) //Test disabled because there is no more inheritance
    public void clearSelectionsCrossTemplateTest(){
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "TemplateManipulationTest.clearSelectionsCrossTemplateTest");
        String projectName = randomWord(20);
        String xPathAreaA1 = "//body/div[1]";
        String xPathViewV1 = "//body/div[1]/div[1]/div[1]";
        String xPathAreaA2 = "//body/div[1]/div[1]";
        String areaA1Name = randomWord(5);
        String areaA2Name = randomWord(5);
        String viewName = randomWord(5);
        String nodeType = "jnt:bootstrapMainContent";

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        selectArea(areaA1Name, xPathAreaA1, 1, 0);
        switchToTemplate("home");
        selectArea(areaA2Name, xPathAreaA2, 1, 0);
        selectView(viewName, nodeType, xPathViewV1, 1, 0);
        clearSelections("base");
        checkIfAreaSelected(xPathAreaA2, softAssert, false, "Cleared areas on parent page (base).");
        checkIfViewSelected(xPathViewV1, softAssert, false, "Cleared areas on parent page (base).");
        checkIfAreaSelected(xPathAreaA1, softAssert, false, "Cleared areas on parent page (base).");
        switchToTemplate("base");
        checkIfAreaSelected(xPathAreaA1, softAssert, false, "Cleared areas on parent page (base).");
        //No areas at this point
        selectArea(areaA1Name, xPathAreaA1, 1, 0);
        switchToTemplate("home");
        selectArea(areaA2Name, xPathAreaA2, 1, 0);
        selectView(viewName, nodeType, xPathViewV1, 1, 0);
        clearSelections("home");
        checkIfAreaSelected(xPathAreaA2, softAssert, false, "Cleared areas on child page (home).");
        checkIfViewSelected(xPathViewV1, softAssert, false, "Cleared areas on child page (home).");
        checkIfAreaSelected(xPathAreaA1, softAssert, true, "Cleared areas on child page (home).");
        switchToTemplate("base");
        checkIfAreaSelected(xPathAreaA1, softAssert, true, "Cleared areas on child page (home).");

        softAssert.assertAll();
    }

    @Test (dataProvider = "generateTemplateNames")
    public void templateRenamingTest(String newTemplateName, String pageFileName, boolean isNameValid, boolean reallyRenameTemplate, String errorMsg, int testRunID){
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "TemplateManipulationTest.templateRenamingTest");
        String projectName = randomWord(20);
        String xPathToSelectInBase = "//body/div[1]";
        String baseAreaName = randomWord(5);
        String oldTemplateName = randomWord(14);
        String templatePage = "page1.html";

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        selectArea(baseAreaName, xPathToSelectInBase, 1, 0);
        createNewTemplate(oldTemplateName, templatePage);
        checkTemplateRenaming(oldTemplateName, newTemplateName, isNameValid, reallyRenameTemplate, softAssert, "Test run:"+testRunID+". "+errorMsg);

        softAssert.assertAll();
    }

    @Test(dataProvider = "generateTemplateNames")
    public void templateNamesTest(String templateName, String pageFileName, boolean isNameValid, boolean reallyCreateTemplate, String errorMsg, int testRunID){
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "TemplateManipulationTest.templateNamesTestID_"+testRunID);
        String projectName = randomWord(20);
        String xPathToSelectInBase = "//body/div[1]";
        String baseAreaName = randomWord(5);

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        selectArea(baseAreaName, xPathToSelectInBase, 2, 0);
        checkTemplateName(templateName, pageFileName, isNameValid, reallyCreateTemplate, softAssert, errorMsg);

        softAssert.assertAll();
    }

    /**
     * Click 'create new template', enter template name and page, validate that name field has error, click create or cancel.
     * @param templateName String, Desired template name
     * @param pageFileName String, page filename to associate with template. (If empty string, association will be skipped).
     * @param isValidName boolean, true if name valid and template name field should not indicate errors
     * @param reallyCreateTemplate boolean, True if you really want to create template with these settings.
     * @param softAssert SoftAssert instance you are working with
     * @param errorMsg Description og the test case. Will be prepended to assertion message
     */
    protected void checkTemplateName(String     templateName,
                                     String     pageFileName,
                                     boolean    isValidName,
                                     boolean    reallyCreateTemplate,
                                     SoftAssert softAssert,
                                     String     errorMsg){
        boolean nameFieldShowsError;
        WebElement menuBtn = findByXpath("//md-icon[text()='view_quilt']/ancestor::button");
        clickOn(menuBtn);
        WebElement createNewTemplateBtn = findByXpath("//button[@ ng-click='project.createNewTemplate($event)']");
        waitForElementToStopMoving(createNewTemplateBtn);
        clickOn(createNewTemplateBtn);
        WebElement templateNameField = findByXpath("//input[@name='pageName']");
        WebElement pageSelectDropdown = findByXpath("//md-select[@ng-model='cpc.selectedTemplate']");
        WebElement createBtn = findByXpath("//button[@ng-click='cpc.create()']");
        WebElement cancelBtn = findByXpath("//button[@ng-click='cpc.cancel()']");

        if(!pageFileName.equals("")) {
            clickOn(pageSelectDropdown);
            WebElement pageOption = findByXpath("//md-option[@value='" + pageFileName + "']");
            softAssert.assertNotNull(pageOption, "Page with filename '" + pageFileName + "' is not available for selection");
            waitForElementToBeEnabled(pageOption, 5);
            clickOn(pageOption);
        }
        typeInto(templateNameField, templateName);
        shortSleep(); //Handling validation delay
        nameFieldShowsError = templateNameField.getAttribute("class").contains("ng-invalid-");

        softAssert.assertNotEquals(
                nameFieldShowsError, isValidName,
                errorMsg+".\nTemplate name field shows error: "+nameFieldShowsError+",\n" +
                        "Template name is: "+templateName+".");
        if (isValidName){
            softAssert.assertTrue(waitForElementToBeEnabled(createBtn, 5), errorMsg+"Create button disabled with valid name: '"+templateName+"'");
            if(reallyCreateTemplate){
                clickOn(createBtn);
                waitForElementToBeInvisible(createBtn);
                WebElement newTemplateTab = findByXpath("//ti-tab[contains(., '"+templateName+"')]");
                softAssert.assertNotNull(newTemplateTab, "Cannot find tab with new template name '"+templateName+"' after creating new template.");
                switchToProjectFrame();
                WebElement body = findByXpath("//body");
                waitForElementToStopMoving(body);
                switchToDefaultContent();
            }
        }else{
            softAssert.assertFalse(waitForElementToBeEnabled(createBtn, 1), errorMsg+"Create button enabled with invalid name: '"+templateName+"'");
            if(!reallyCreateTemplate){
                clickOn(cancelBtn);
                waitForElementToBeInvisible(cancelBtn);
            }
        }
    }

    protected void clearSelections(String templateName){
        WebElement clearBtn = findByXpath("//ti-tab[contains(., '"+templateName+"')]/div[@ng-click='clearPage()']");
        Assert.assertNotNull(clearBtn, "Template you are trying to clear selections in is not found.");
        clickOn(clearBtn);
        WebElement confirmClearingBtn = findByXpath("//button[@ng-click='cpc.clearPage()'] ");
        clickOn(confirmClearingBtn);
        waitForElementToBeInvisible(confirmClearingBtn);
    }

    protected void checkTemplateRenaming(String     oldName,
                                         String     newName,
                                         boolean    isNameValid,
                                         boolean    reallyRename,
                                         SoftAssert softAssert,
                                         String     errorMsg) {
        WebElement pencilIconBtn = findByXpath("//ti-tab[contains(., '" + oldName + "')]/div[@ng-click='renamePage()']");
        softAssert.assertNotNull(pencilIconBtn, "Cannot find template you are trying to rename. Template Name:'" + oldName + "'");
        clickOn(pencilIconBtn);
        WebElement nameField = findByXpath("//input[@ng-model='rpc.pageName']");
        WebElement renameBtn = findByXpath("//button[@ng-click='rpc.renamePage()']");
        WebElement cancelBtn = findByXpath("//button[@ng-click='rpc.cancel()']");

        typeInto(nameField, newName);
        shortSleep();
        //Avoiding StaleElementException
        nameField = findByXpath("//input[@ng-model='rpc.pageName']");
        if (isNameValid) {
            softAssert.assertFalse(
                    nameField.getAttribute("class").contains("ng-invalid-"),
                    errorMsg + ". Name ("+newName+") is valid, but name field is marked as invalid.");
            softAssert.assertTrue(renameBtn.isEnabled(), errorMsg + ". Name  ("+newName+") is valid, but 'Rename' button disabled.");
        } else {
            softAssert.assertTrue(
                    nameField.getAttribute("class").contains("ng-invalid-"),
                    errorMsg + ". Name  ("+newName+") is not valid, but name field is not marked as invalid.");
            softAssert.assertFalse(renameBtn.isEnabled(), errorMsg + ". Name  ("+newName+") is not valid, but 'Rename' button enabled.");
        }

        if (reallyRename) {
            waitForElementToBeEnabled(renameBtn, 5);
            clickOn(renameBtn);
            waitForElementToBeInvisible(renameBtn);
            softAssert.assertEquals(
                    isVisible(By.xpath("//ti-tab[contains(., '" + newName + "')]"), 3),
                    true,
                    errorMsg + ". After renaming, template with new name (" + newName + ") is not visible."
            );
            softAssert.assertEquals(
                    isVisible(By.xpath("//ti-tab[contains(., '" + oldName + "')]"), 3),
                    false,
                    errorMsg + ". After renaming, template with old name (" + oldName + ") is still visible."
            );
        } else {
            clickOn(cancelBtn);
            waitForElementToBeInvisible(cancelBtn);
            if (!newName.equals("base") && !newName.equals("home")) {
                softAssert.assertEquals(
                        isVisible(By.xpath("//ti-tab[contains(., '" + newName + "')]"), 3),
                        false,
                        errorMsg + ". After cancelling renaming, template with new name (" + newName + ") is visible."
                );
                softAssert.assertEquals(
                        isVisible(By.xpath("//ti-tab[contains(., '" + oldName + "')]"), 3),
                        true,
                        errorMsg + ". After cancelling renaming, template with old name (" + oldName + ") is not visible."
                );
            }
        }
    }

    @DataProvider(name = "generateTemplateNames")
    public Object[][] generateTemplateNames(){
        return new Object[][]{
                {"ValidTemplateName1", "page1.html", true, true, "Valid name and page template creation", 0},
                {"base", "page2.html", true, false, "Creating clone of base template", 1}, //"base" is now a valid template name since everything will be under base automatically
                {"Base", "page2.html", true, true, "Creating clone of base template", 2},
                {"BASE", "page2.html", true, false, "Creating clone of base template", 3},
                {"home", "page1.html", false, false, "Creating clone of home template", 4}, //MOD-1157
                {"hOme", "page1.html", true, false, "Creating clone of home template", 5}, //MOD-1157
                {" template with spaces ", "page3.html", false, false, "Template name with spaces", 6},
                {"special!chars", "", false, false, "Special characters in template name", 7},
                {"ШаблонОдин", "", false, false, "Russian letters in template name", 8}, //MOD-1093
                {"99ALL_all0wed--__--name77", "page1.html", true, true, "All allowed characters", 9}, //MOD-1075
                {"*'!TEST", "page1.html", false, false, "Special chars", 10},
                {"ddf\\.\n_0", "page1.html", false, false, "Special chars", 11},
                {"404", "", false, false, "Al numbers name", 12}
        };
    }
}
