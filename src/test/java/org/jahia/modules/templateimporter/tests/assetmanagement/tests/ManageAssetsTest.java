package org.jahia.modules.templateimporter.tests.assetmanagement.tests;

import org.jahia.modules.templateimporter.tests.TemplateImporterRepository;
import org.jahia.modules.tests.utils.SoftAssertWithScreenshot;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.io.File;

/**
 * Created by sergey on 2016-07-20.
 */
public class ManageAssetsTest extends TemplateImporterRepository{
    private static final String AVAILABLE_ASSETS_COL_NAME = "Available Assets";
    private static final String CSS_COL_NAME = "CSS";
    private static final String JAVASCRIPT_COL_NAME = "JavaScript";
    private static final String IMG_COL_NAME = "IMG";

    @Test
    public void checkDefaultFoldersCopying() {
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "GenerateModuleTest.checkDefaultFoldersCopying");
        String projectName = randomWord(8);
        String moduleName = randomWord(10);
        String definitionNameSpace = randomWord(3);
        String sourceFolderPath = new File(getDownloadsFolder()).getAbsolutePath() + "/" + randomWord(10);
        String[] cssFolderExpectedContent = {
                "proper2.css",
                "proper.css"
        };
        String[] imgFolderExpectedContent = {
                "semi-transparent-cat.png",
                "cat.GIF",
                "acid.svg",
                "proper2.tif",
                "proper.jpg",
                "песочница-космонавт-котэ-165601 copy"
        };
        String[] javascriptFolderExpectedContent = {
                "filename with spaces.js",
                "proper.js"
        };

        importProject("en", projectName, "", "Assets.zip");
        openProjectFirstTime(projectName, "index.html");
        openAssetsManagement();
        //Check that css, img and javascript folders are not visible in assets
        checkItemVisibility(softAssert, AVAILABLE_ASSETS_COL_NAME, "AnotherFolder", true, "");
        checkItemVisibility(softAssert, CSS_COL_NAME, "css", false, "");
        checkItemVisibility(softAssert, IMG_COL_NAME, "img", false, "");
        checkItemVisibility(softAssert, JAVASCRIPT_COL_NAME, "javascript", false, "");

        WebElement backToProjectBtn = findByXpath("//button[@ng-click='amc.backToProject()']");
        clickOn(backToProjectBtn);
        waitForGlobalSpinner(2, 10);
        generateModule(moduleName, definitionNameSpace, sourceFolderPath, true);
        softAssert.assertTrue(
                isModuleStarted(moduleName),
                "Module '" + moduleName + "' did not start after generation.");
        checkFolderInModulesResources(softAssert, sourceFolderPath, "css", true, cssFolderExpectedContent, moduleName);
        checkFolderInModulesResources(softAssert, sourceFolderPath, "img", true, imgFolderExpectedContent,  moduleName);
        checkFolderInModulesResources(softAssert, sourceFolderPath, "javascript", true, javascriptFolderExpectedContent, moduleName);

        softAssert.assertAll();
    }

    private void expandFolder(String    column,
                              String    folderName){
        WebElement expandBtn = findByXpath("//div[./md-toolbar[.//h5[text()='"+column+"']]]//li[./strong[text()='"+folderName+"']]" +
                "/span[@ng-click='amc.toggleFolder(list, $index, item.assetPath, item.assetName)']");
        Assert.assertNotNull(expandBtn, "Expand button not found for item: '"+folderName+"' in clumn:'"+column+"'.");
        clickOn(expandBtn);
        waitForElementToBeInvisible(expandBtn, 5);
    }

    private void shrinkFolder(String    column,
                              String    folderName){
        WebElement shrinkBtn = findByXpath("//div[./md-toolbar[.//h5[text()='"+column+"']]]" +
                "//li[.//strong[text()='"+folderName+"']]/div/span[@ng-click='amc.toggleFolder(list, $index, item.assetPath, item.assetName)']");
        Assert.assertNotNull(shrinkBtn, "Expand button not found for item: '"+folderName+"' in clumn:'"+column+"'.");
        clickOn(shrinkBtn);
        waitForElementToBeInvisible(shrinkBtn, 5);
    }

    private void openAssetsManagement() {
        WebElement menuBtn = findByXpath("//md-icon[text()='settings']/ancestor::button");
        clickOn(menuBtn);
        WebElement assetManagementBtn = findByXpath("//button[@ng-click='project.mapAssets()']");
        waitForElementToStopMoving(assetManagementBtn);
        clickOn(assetManagementBtn);
        Assert.assertTrue(
                isVisible(By.xpath("//span[contains(., 'Template Importer - Assets Management')]"), 7),
                "Title 'Template Importer - Assets Management' is not visible after clicking asset management button."
        );
    }

    protected void checkItemVisibility(SoftAssert   softAssert,
                                       String       columnName,
                                       String       itemName,
                                       boolean      expectedVisibiity,
                                       String       errorMsg){
        softAssert.assertEquals(
                isVisible(By.xpath("//div[./md-toolbar[.//h5[text()='"+columnName+"']]]//strong[text()='"+itemName+"']"), 3),
                expectedVisibiity,
                errorMsg+"Item visibility error during asset management. Column: "+columnName+", Item: "+itemName+"."
        );
    }
}
