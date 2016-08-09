package org.jahia.modules.templateimporter.tests.assetmanagement.tests;

import org.jahia.modules.templateimporter.tests.TemplateImporterRepository;
import org.jahia.modules.tests.utils.SoftAssertWithScreenshot;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
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
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "GenerateModuleTest.generateModuleTest");
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
        WebElement assetManagementBtn = findByXpath("//button[@ng-click='project.mapAssets()']");
        clickOn(assetManagementBtn);
        softAssert.assertTrue(
                isVisible(By.xpath("//span[contains(., 'Template Importer - Assets Management')]"), 7),
                "Title 'Template Importer - Assets Management' is not visible after clicking asset management button."
        );
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
        checkFolderInModulesResources(softAssert, sourceFolderPath, "css", cssFolderExpectedContent);
        checkFolderInModulesResources(softAssert, sourceFolderPath, "img", imgFolderExpectedContent);
        checkFolderInModulesResources(softAssert, sourceFolderPath, "javascript", javascriptFolderExpectedContent);

        softAssert.assertAll();
    }

    protected void checkItemVisibility(SoftAssert   softAssert,
                                       String       columnName,
                                       String       itemName,
                                       boolean      expectedVisibiity,
                                       String       errorMsg){
        softAssert.assertEquals(
                isVisible(By.xpath("//div[./md-toolbar[contains(., '"+columnName+"')]]//strong[text()='"+itemName+"']"), 3),
                expectedVisibiity,
                errorMsg+"Item visibility error during asset management. Column: "+columnName+", Item: "+itemName+"."
        );
    }
}
