package org.jahia.modules.templateimporter.tests.documentvalidation.tests;

import org.jahia.modules.templateimporter.tests.TemplateImporterRepository;
import org.jahia.modules.tests.utils.SoftAssertWithScreenshot;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

/**
 * Created by sergey on 2016-08-16.
 */
public class ValidateDocumentTest extends TemplateImporterRepository{
    /**
     * Tests that cheks document validation
     * @param createByLink boolean, true to create new template by clicking on a link, false to click on button
     * @param pageFileName String, filename of the page to associate with template
     * @param expectedElementCountError boolean, true if overall element count error message should appear
     * @param expectedSelectionError boolean, true if invalid selection validation error message should appear
     * @param baseAreaSelectedInOtherPage boolean, true if area inherited from base template should be visible and should
     *                                    be exactly what we select on base
     * @param errorMsg String, test case description
     */
    @Test(dataProvider = "validationCases")
    public void validateDocumentTest(boolean    createByLink,
                                     String     pageFileName,
                                     boolean    expectedElementCountError,
                                     boolean    expectedSelectionError,
                                     boolean    baseAreaSelectedInOtherPage,
                                     String     errorMsg){
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "AreaSelectionTest.validateDocumentTest");
        String projectName = randomWord(10);
        String xPathToSelectInBase = "/html/body/div[4][contains(text(), 'base4')]";
        String baseAreaName = randomWord(5);
        String newTemplateName = randomWord(9);

        if(createByLink){
            errorMsg = errorMsg + ". Template creation by link clicking";
        }else {
            errorMsg = errorMsg + ". Template creation by button";
        }

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "base.html");
        selectArea(baseAreaName, xPathToSelectInBase, 2, 1);

        createNewTemplate(createByLink, newTemplateName, pageFileName, expectedElementCountError, expectedSelectionError, softAssert, errorMsg+". Template creation step");
        checkIfAreaSelected(xPathToSelectInBase, softAssert, baseAreaSelectedInOtherPage, errorMsg+". Base template selection inheritance check");

        softAssert.assertAll();
    }

    /**
     *
     * @param createByLink boolean, true to create new template by clicking on a link, false to click on button
     * @param templateName String, name of the new template
     * @param pageFileName String, filename of the page to associate with template
     * @param expectedElementCountError boolean, true if overall element count error message should appear
     * @param expectedSelectionError boolean, true if invalid selection validation error message should appear
     * @param softAssert Instance of SoftAssert you are working with.
     * @param errorMsg String, test case description
     */
    private void createNewTemplate(boolean      createByLink,
                                   String       templateName,
                                   String       pageFileName,
                                   boolean      expectedElementCountError,
                                   boolean      expectedSelectionError,
                                   SoftAssert   softAssert,
                                   String       errorMsg) {
        String selectionErrorBorderClass = "tiRedAnimatedBorder";

        if (createByLink) {
            switchToProjectFrame();
            WebElement linkToPage = findByXpath("//a[@href='" + pageFileName + "']");
            clickOn(linkToPage);
            switchToDefaultContent();
            WebElement templateNameField = findByXpath("//input[@name='pageName']");
            WebElement createBtn = findByXpath("//button[@ng-click='pac.createPage()']");
            typeInto(templateNameField, templateName);
            waitForElementToBeEnabled(createBtn, 5);
            clickOn(createBtn);
            waitForElementToBeInvisible(createBtn);
        } else {
            WebElement createNewTemplateBtn = findByXpath("//button[@ng-click='cpc.showCreatePageDialog($event)']");

            clickOn(createNewTemplateBtn);
            WebElement templateNameField = findByXpath("//input[@name='pageName']");
            WebElement pageSelectDropdown = findByXpath("//md-select[@ng-model='selectedTemplate']");
            WebElement createBtn = findByXpath("//button[@ng-click='create()']");

            waitForElementToStopMoving(pageSelectDropdown);
            clickOn(pageSelectDropdown);
            WebElement pageOption = findByXpath("//md-option[@value='" + pageFileName + "']");
            waitForElementToBeEnabled(pageOption, 5);
            clickOn(pageOption);
            typeInto(templateNameField, templateName);
            waitForElementToBeEnabled(createBtn, 5);
            clickOn(createBtn);
            waitForElementToBeInvisible(createBtn);
        }
        WebElement newTemplateTab = findByXpath("//ti-tab[contains(., '" + templateName + "')]");
        Assert.assertNotNull(newTemplateTab, "Cannot find tab with new template name '" + templateName + "' after creating new template.");
        if (expectedElementCountError || expectedSelectionError) {
            Assert.assertTrue(
                    isVisible(By.xpath("//md-dialog"), 5),
                    errorMsg + ". Validation results dialogue - visibility check failed. \nNew template page: " +pageFileName+
                            "\n'Element count' error:" + expectedElementCountError + ",\n'Incorrect selection' error:" + expectedSelectionError + ". "
            );
            softAssert.assertEquals(
                    isVisible(By.xpath("//md-dialog//div[@ng-if='dvc.validations.invalidElementCount !== undefined']" +
                            "[contains(., \"Element count for '" + templateName + "' page does not match 'base'\")]"), 3),
                    expectedElementCountError,
                    errorMsg + ". 'Element count' error message visibility check failed."
            );
            softAssert.assertEquals(
                    newTemplateTab.getAttribute("class").contains(selectionErrorBorderClass),
                    expectedSelectionError,
                    errorMsg + ". New template tab orange border visibility check failed."
            );
            softAssert.assertEquals(
                    isVisible(By.xpath("//md-dialog//div[@ng-if='dvc.validations.invalidSelection !== undefined']//strong[contains(text(), '" + templateName + "')]"), 3),
                    expectedSelectionError,
                    errorMsg + ". 'Invalid selection' - error message visibility failed. Expected template name: " + templateName
            );
            WebElement okButton = findByXpath("//button[@ng-click='dvc.okay()']");
            waitForElementToStopMoving(okButton);
            clickOn(okButton);
            waitForElementToBeInvisible(okButton);
        } else {
            Assert.assertFalse(
                    isVisible(By.xpath("//md-dialog"), 3),
                    errorMsg + ". Validation results dialogue - visibility check failed. \nNew template page: " +pageFileName+
                            "\n'Element count' error:" + expectedElementCountError + ",\n'Incorrect selection' error:" + expectedSelectionError + ". "
            );
        }

        switchToProjectFrame();
        WebElement body = findByXpath("//body");
        waitForElementToStopMoving(body);
        String iFrameUtl = returnJavascriptResponse("document.location.pathname");
        switchToDefaultContent();

        boolean sourceContainsPageName = iFrameUtl.contains(pageFileName);
        Assert.assertTrue(sourceContainsPageName, errorMsg+". Iframe's SRC attribute does not contain page filename ("+pageFileName+"). SRC is: "+iFrameUtl);
    }

    /**
     * Assertion if area selected. Method will not fail or throw an error if element does not exist.
     * @param xPath String, XPath to the area
     * @param softAssert Instance of SoftAssert you are working with. Will fail if visibility result is not expected.
     * @param expectedResult boolean, your expectation if area should be selected.
     * @param errorMsg String, test case description
     * @return true if area selected, false if not selected or does not exist
     */
    protected boolean checkIfAreaSelected(String        xPath,
                                          SoftAssert    softAssert,
                                          boolean       expectedResult,
                                          String        errorMsg) {
        boolean isAreaSelected;
        switchToProjectFrame();
        WebElement area;
        try{
            area = createWaitDriver(5, 500).until(ExpectedConditions.presenceOfElementLocated(By.xpath(xPath)));
            isAreaSelected = area.getAttribute("class").contains(SELECTED_AREA_MARK);
        }catch (TimeoutException e){
            isAreaSelected = false;
        }

        switchToDefaultContent();
        softAssert.assertEquals(
                isAreaSelected,
                expectedResult,
                errorMsg+". Assertion if element: '" + xPath + "' has class '" + SELECTED_AREA_MARK + "' (is selected) Failed");
        return isAreaSelected;
    }

    @DataProvider(name = "validationCases")
    public Object[][] createValidationCases(){
        return new Object[][]{
                //Create new template by button
                {false, "valid_page.html", false, false, true, "Valid page"},
                {false, "valid-with-script.html", false, false, true, "Valid page with script, link and style tags"},
                {false, "valid-with-head.html", false, false, true, "Valid page with different head tag content"},
                {false, "valid_page_without_body.html", false, false, true, "Valid page without optional tags (html, body, head)"},
                {false, "invalid-missing.html", true, true, false, "Invalid page, one top-level div is missing"},
                {false, "invalid-additional.html", true, false, false, "Invalid page, one additional top level div"},
                //Create new template by clicking on a link
                {true, "valid_page.html", false, false, true, "Valid page"},
                {true, "valid-with-script.html", false, false, true, "Valid page with script, link and style tags"},
                {true, "valid-with-head.html", false, false, true, "Valid page with different head tag content"},
                {true, "valid_page_without_body.html", false, false, true, "Valid page without optional tags (html, body, head)"},
                {true, "invalid-missing.html", true, true, false, "Invalid page, one top-level div is missing"},
                {true, "invalid-additional.html", true, false, false, "Invalid page, one additional top level div"},
        };
    }
}
