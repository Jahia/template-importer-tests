package org.jahia.modules.templateimporter.tests.selection.tests;

import org.jahia.modules.templateimporter.tests.TemplateImporterRepository;
import org.jahia.modules.tests.utils.SoftAssertWithScreenshot;
import org.openqa.selenium.WebElement;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

/**
 * Created by sergey on 2016-07-12.
 */
public class TemplateManipulationTest extends TemplateImporterRepository{
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
        selectArea(baseAreaName, xPathToSelectInBase, 1, 0, true);
        createNewTemplate(newTemplateName, newTemplatePage);
        checkTemplateName(newTemplateName, "", false, false, softAssert, "Two templates, same name.");

        WebElement iFrame = findByXpath("//iframe[@id='tiProjectFrame']");
        String oldIframeSource = iFrame.getAttribute("src");
        removeTemplate(newTemplateName);
        iFrame = findByXpath("//iframe[@id='tiProjectFrame']");
        String newIframeSource = iFrame.getAttribute("src");
        softAssert.assertNotEquals(oldIframeSource, newIframeSource, "iFrame's src attribute did not change after template removal. Old page is still loaded into iFrame.");
        createNewTemplate(newTemplateName, newTemplatePage);

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
        selectArea(baseAreaName, xPathToSelectInBase, 1, 0, true);
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
        WebElement createNewTemplateBtn = findByXpath("//button[@ng-click='cpc.showCreatePageDialog($event)']");

        clickOn(createNewTemplateBtn);
        WebElement templateNameField = findByXpath("//input[@name='pageName']");
        WebElement pageSelectDropdown = findByXpath("//md-select[@ng-model='selectedTemplate']");
        WebElement createBtn = findByXpath("//button[@ng-click='create()']");
        WebElement cancelBtn = findByXpath("//button[@ng-click='cancel()']");

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

    @DataProvider(name = "generateTemplateNames")
    public Object[][] generateTemplateNames(){
        return new Object[][]{
                {"ValidTemplateName1", "page1.html", true, true, "Valid name and page template creation", 0},
                {"base", "page2.html", false, false, "Creating clone of base template", 1}, //MOD-1157
                {"Base", "page2.html", false, false, "Creating clone of base template", 2}, //MOD-1157
                {"BASE", "page2.html", false, false, "Creating clone of base template", 3}, //MOD-1157
                {"home", "page1.html", false, false, "Creating clone of home template", 4}, //MOD-1157
                {"hOme", "page1.html", false, false, "Creating clone of home template", 5}, //MOD-1157
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
