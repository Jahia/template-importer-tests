package org.jahia.modules.templateimporter.tests.selection.tests;

import org.jahia.modules.templateimporter.tests.TemplateImporterRepository;
import org.jahia.modules.templateimporter.tests.businessobjects.Area;
import org.jahia.modules.templateimporter.tests.businessobjects.Component;
import org.jahia.modules.templateimporter.tests.businessobjects.View;
import org.jahia.modules.tests.utils.CustomExpectedConditions;
import org.jahia.modules.tests.utils.SoftAssertWithScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.io.File;

/**
 * Created by sergey on 2016-08-22.
 */
public class LayoutTests extends TemplateImporterRepository{
    @Test
    public void layoutReimportTest(){
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "LayoutTests.layoutReimportTest");
        String oldProjectName = randomWord(8);
        String newProjectName = randomWord(10);
        String usersTemplateName = randomWord(5);
        File exportedLayout;
        Area homeA1 = new Area(randomWord(4), "//body/div[1]//div[contains(., 'Level 2-1')]", 2, 0, "home");
        Area userA1 = new Area(randomWord(7), "//body/div[1]/div[2]", 2, 0, usersTemplateName);
        View homeV1 = new View(randomWord(10), "jnt:html", homeA1.getXpath()+"/div[1]", 2, 0, homeA1.getTemplateName());
        View userV1 = new View(randomWord(9), "jnt:html", "//body/div[1]/div[2]/div[text()='Level 3-1']", 2, 0, usersTemplateName);
        Component homeC1 = new Component(randomWord(2), randomWord(6), randomWord(6), "jnt:html", "/html/body/div[3]", 2, 0, homeA1.getTemplateName());

        //Create and export layout
        importProject("en", oldProjectName, "", "AlexLevels.zip");
        openProjectFirstTime(oldProjectName, "index.html");
        selectArea(homeA1);
        selectView(homeV1);
        selectComponent(homeC1, "");
        createNewTemplate(usersTemplateName, "page1.html");
        selectArea(userA1);
        selectView(userV1);

        //Re import layout into another project and check areas and views.
        String[] templatesToExport = new String[]{homeA1.getTemplateName(), userA1.getTemplateName()};
        exportedLayout = exportLayout(templatesToExport, oldProjectName, softAssert);
        Assert.assertNotNull(exportedLayout, "Exported layout zip file not found");
        importProject("en", newProjectName, "Project for reimport test.", "AlexLevels.zip");
        openProjectFirstTime(newProjectName, "index.html");
        templatesToExport = new String[]{homeA1.getTemplateName(), userA1.getTemplateName()};
        importLayout(exportedLayout, templatesToExport, softAssert);
        checkAreaViewImport(softAssert, homeA1);
        checkAreaViewImport(softAssert, homeV1);
        checkIfComponentSelected(homeC1, softAssert, true, "Component re-imported");
        checkAreaViewImport(softAssert, userA1);
        checkAreaViewImport(softAssert, userV1);

        softAssert.assertAll();
    }

    private void checkAreaViewImport(SoftAssert softAssert,
                                     Area       area){
        String areaOrVIew;

        switchToTemplate(area.getTemplateName());
        if(area.isArea()){
            areaOrVIew = "Area";
            checkIfAreaSelected(area.getXpath(), softAssert, true, areaOrVIew+" is not selected on template '"+area.getTemplateName()+
                    "' after re import");
        }else{
            areaOrVIew = "View";
            checkIfViewSelected(area.getXpath(), softAssert, true, areaOrVIew+" is not selected on template '"+area.getTemplateName()+
                    "' after re import");
        }
    }

    private void importLayout(File          archivedLayout,
                              String[]      templatesToImport,
                              SoftAssert    softAssert){
        String zipFilePath = archivedLayout.getAbsolutePath();
        String jsToEnableInput = "function getElementByXpath(path) {" +
                "return document.evaluate(path, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;" +
                "}" +
                "fileInput = getElementByXpath(\"//label[input[@type='file']]\");" +
                "fileInput.setAttribute(\"style\", \"\");";

        WebElement menuBtn = findByXpath("//md-icon[text()='view_quilt']/ancestor::button");
        clickOn(menuBtn);

        WebElement importLayoutBtn = findByXpath("//button[@ng-click='project.importLayout($event)']");
        waitForElementToStopMoving(importLayoutBtn);
        clickOn(importLayoutBtn);
        WebElement layoutZipFileField = findByXpath("//input[@type='file']");
        WebElement importButton = findByXpath("//button[@ng-click='lifc.import()']");
        waitForElementToStopMoving(importButton);
        try {
            createWaitDriver(5, 500).until(CustomExpectedConditions.javascriptWithoutException(jsToEnableInput));
        }catch(TimeoutException e){
            getLogger().error("Hidden Input field might be not activated. JavaScript execution still produces errors even after 5 - 7 attempts.");
        }
        layoutZipFileField.sendKeys(zipFilePath);

        WebElement selectAllCheckbox = findByXpath("//md-checkbox[@ng-click='lifc.selectAll()']");
        Assert.assertNotNull(selectAllCheckbox, "Select all checkbox in 'Layout Import dialogue not found'");
        if(selectAllCheckbox.getAttribute("class").contains("md-checked")){
            clickOn(selectAllCheckbox);
        }
        for(String templateName:templatesToImport){
            WebElement checkBox = findByXpath("//md-checkbox[@ng-click='lifc.selectOption($index)'][contains(., '"+templateName+"')]");
            Assert.assertNotNull(checkBox, "Checkbox for template' "+templateName+"' does not exist in layout import dialogue");
            if (!checkBox.getAttribute("class").contains("md-checked")){
                clickOn(checkBox);
            }
        }
        for(String templateName:templatesToImport){
            WebElement checkBox = findByXpath("//md-checkbox[@ng-click='lifc.selectOption($index)'][contains(., '"+templateName+"')]");
            softAssert.assertTrue(
                    checkBox.getAttribute("class").contains("md-checked"),
                    "Checkbox for template '"+templateName+"' is not selected in layout import dialogue"
            );
        }

        waitForElementToBeEnabled(importButton, 7);
        Assert.assertEquals(
                importButton.isEnabled(),
                true,
                "All fields are filled, but 'Import' button is disabled. Cannot import layout.");
        clickOn(importButton);
        waitForElementToBeInvisible(importButton);
    }

    private File exportLayout(String[]      templateNames,
                              String        projectName,
                              SoftAssert    softAssert){
        WebElement menuBtn = findByXpath("//md-icon[text()='view_quilt']/ancestor::button");
        clickOn(menuBtn);
        WebElement exportLayoutBtn = findByXpath("//button[@ng-click='project.exportLayout($event)']");
        Assert.assertNotNull(exportLayoutBtn, "Export layout button not found");
        Assert.assertTrue(
                isVisible(exportLayoutBtn, 3),
                "Export layout button is not visible"
        );
        Assert.assertTrue(
                exportLayoutBtn.isEnabled(),
                "Export layout button disabled."
        );
        cleanDownloadsFolder();
        waitForElementToStopMoving(exportLayoutBtn);
        clickOn(exportLayoutBtn);

        WebElement selectAllCheckbox = findByXpath("//md-checkbox[@ng-click='lec.selectAll()']");
        Assert.assertNotNull(selectAllCheckbox, "Select all checkbox in 'Layout Export dialogue not found'");
        waitForElementToStopMoving(selectAllCheckbox);
        if(selectAllCheckbox.getAttribute("class").contains("md-checked")){
            clickOn(selectAllCheckbox);
        }
        for(String templateName:templateNames){
            WebElement checkBox = findByXpath("//md-checkbox[@ng-click='lec.selectOption($index)'][contains(., '"+templateName+"')]");
            Assert.assertNotNull(checkBox, "Checkbox for template' "+templateName+"' does not exist in layout export dialogue");
            if (!checkBox.getAttribute("class").contains("md-checked")){
                clickOn(checkBox);
            }
        }

        for(String templateName:templateNames){
            WebElement checkBox = findByXpath("//md-checkbox[@ng-click='lec.selectOption($index)'][contains(., '"+templateName+"')]");
            softAssert.assertTrue(
                    checkBox.getAttribute("class").contains("md-checked"),
                    "Checkbox for template '"+templateName+"' is not selected in layout export dialogue"
            );
        }

        WebElement exportBtn = findByXpath("//button[@ng-click='lec.export()']");
        clickOn(exportBtn);
        waitForElementToBeInvisible(exportBtn);

        return waitForFile(new File(getDownloadsFolder()).getAbsolutePath(), projectName, "zip", 5000L);
    }
}
