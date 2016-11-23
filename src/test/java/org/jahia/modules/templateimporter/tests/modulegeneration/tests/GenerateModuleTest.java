package org.jahia.modules.templateimporter.tests.modulegeneration.tests;

import org.jahia.modules.templateimporter.tests.TemplateImporterRepository;
import org.jahia.modules.templateimporter.tests.businessobjects.Area;
import org.jahia.modules.templateimporter.tests.businessobjects.Component;
import org.jahia.modules.templateimporter.tests.businessobjects.View;
import org.jahia.modules.tests.utils.SoftAssertWithScreenshot;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

/**
 * Created by sergey on 2016-07-25.
 */
public class GenerateModuleTest extends TemplateImporterRepository{
    @Test
    public void generateModuleTest(){
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "GenerateModuleTest.generateModuleTest");
        String projectName = randomWord(8);
        String moduleName = randomWord(10);
        String definitionNameSpace = randomWord(3);
        String sourceFolderPath = new File(getDownloadsFolder()).getAbsolutePath()+"/"+randomWord(10);
        Area homeA1 = new Area(randomWord(4), "//body/div[1]//div[contains(., 'Level 2-1')]", 2, 0, "home");
        View homeV1 = new View(randomWord(10), "jnt:html", homeA1.getXpath()+"/div[1]", 2, 0, homeA1.getTemplateName());
        Component homeC1 = new Component(randomWord(2), randomWord(6), randomWord(6), "jnt:html", "/html/body/div[3]", 2, 0, homeA1.getTemplateName());
        String expectedAreFileContentForComponent = "<template:area path=\""+homeC1.getAreaName()+"\" />";
        String expectedViewFileContentForComponent = "<div  class=\"\"  >\n" +
                "\t\t\tLevel 1-3\n" +
                "\t\t\t<div   class=\"marginLeft \" >\n" +
                "\t\t\t\tLevel 2-1\n" +
                "\t\t\t\t<div   class=\"marginLeft\" >\n" +
                "\t\t\t\t\tLevel 3-1\n" +
                "\t\t\t\t\t<div   class=\"marginLeft\" >Level 4-1</div>\n" +
                "\t\t\t\t</div>\n" +
                "\t\t\t</div>\n" +
                "\t\t</div>";
        boolean homeA1FileContainsArea = false;
        boolean homeV1FileContainsView = false;

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        selectArea(homeA1);
        selectView(homeV1);
        selectComponent(homeC1, "");
        generateModule(moduleName, definitionNameSpace, sourceFolderPath, true);
        softAssert.assertTrue(
                isModuleStarted(moduleName),
                "Module '"+moduleName+"' did not start after generation.");
        File templateFile = checkJntTemplateFileExist(softAssert, sourceFolderPath, moduleName);
        boolean baseAreaOneFoundInTemplate = findTextInFile(templateFile, "<template:area path='baseContent'></template:area>");
        softAssert.assertTrue(
                baseAreaOneFoundInTemplate,
                "Base area not found in template JSP."
        );

        File homeA1File = checkAreaFile(softAssert, sourceFolderPath, homeA1, definitionNameSpace, moduleName);
        if(homeA1File != null) {
            homeA1FileContainsArea = findTextInFile(homeA1File, "<template:area path='" + homeA1.getName() + "'/>");
        }
        softAssert.assertTrue(
                homeA1FileContainsArea,
                "Home area JSP file misses area tag. Name: " + homeA1.getName() + ", XPath: " + homeA1.getXpath()
        );
        File homeV1File = checkViewFile(softAssert, sourceFolderPath, homeV1, moduleName);
        if (homeV1File != null) {
            homeV1FileContainsView = findTextInFile(homeV1File, "<div class=\"marginLeft \"   >\n" +
                    "\t\t\t\t\tLevel 3-1\n" +
                    "\t\t\t\t\t<div class=\"marginLeft \"  >\n" +
                    "\t\t\t\t\t\tLevel 4-1\n" +
                    "\t\t\t\t\t\t<div class=\"marginLeft\"  >Level 5-1</div>\n" +
                    "\t\t\t\t\t</div>\n" +
                    "\t\t\t\t</div>");
        }
        softAssert.assertTrue(
                homeV1FileContainsView,
                "Home view JSP file misses html tags. Name: " + homeV1.getName() + ", XPath: " + homeV1.getXpath()
        );

        checkComponentFiles(
                softAssert,
                sourceFolderPath,
                homeC1,
                definitionNameSpace,
                moduleName,
                expectedAreFileContentForComponent,
                expectedViewFileContentForComponent);

        softAssert.assertAll();
    }

    @Test
    public void generateModuleWithAutoContinueTest(){
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "GenerateModuleTest.generateModuleWithAutoContinueTest");
        String projectName = randomWord(8);
        String moduleName = randomWord(10);
        String definitionNameSpace = randomWord(3);
        String sourceFolderPath = new File(getDownloadsFolder()).getAbsolutePath()+"/"+randomWord(10);
        Area homeA1 = new Area(randomWord(4), "//body/div[1]//div[contains(., 'Level 2-1')]", 2, 0, "home");
        View homeV1 = new View(randomWord(10), "jnt:html", homeA1.getXpath()+"/div[1]", 2, 0, homeA1.getTemplateName());
        Component homeC1 = new Component(randomWord(2), randomWord(6), randomWord(6), "jnt:html", "/html/body/div[3]", 2, 0, homeA1.getTemplateName());
        String expectedAreFileContentForComponent = "<template:area path=\""+homeC1.getAreaName()+"\" />";
        String expectedViewFileContentForComponent = "<div  class=\"\"  >\n" +
                "\t\t\tLevel 1-3\n" +
                "\t\t\t<div   class=\"marginLeft \" >\n" +
                "\t\t\t\tLevel 2-1\n" +
                "\t\t\t\t<div   class=\"marginLeft\" >\n" +
                "\t\t\t\t\tLevel 3-1\n" +
                "\t\t\t\t\t<div   class=\"marginLeft\" >Level 4-1</div>\n" +
                "\t\t\t\t</div>\n" +
                "\t\t\t</div>\n" +
                "\t\t</div>";
        boolean homeA1FileContainsArea = false;
        boolean homeV1FileContainsView = false;

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        selectArea(homeA1);
        selectView(homeV1);
        selectComponent(homeC1, "");
        generateModuleAutoContinue(moduleName, definitionNameSpace, sourceFolderPath, true);
        softAssert.assertTrue(
                isModuleStarted(moduleName),
                "Module '"+moduleName+"' did not start after generation.");
        File templateFile = checkJntTemplateFileExist(softAssert, sourceFolderPath, moduleName);
        boolean baseAreaOneFoundInTemplate = findTextInFile(templateFile, "<template:area path='baseContent'></template:area>");
        softAssert.assertTrue(
                baseAreaOneFoundInTemplate,
                "Base area not found in template JSP."
        );

        File homeA1File = checkAreaFile(softAssert, sourceFolderPath, homeA1, definitionNameSpace, moduleName);
        if(homeA1File != null) {
            homeA1FileContainsArea = findTextInFile(homeA1File, "<template:area path='" + homeA1.getName() + "'/>");
        }
        softAssert.assertTrue(
                homeA1FileContainsArea,
                "Home area JSP file misses area tag. Name: " + homeA1.getName() + ", XPath: " + homeA1.getXpath()
        );
        File homeV1File = checkViewFile(softAssert, sourceFolderPath, homeV1, moduleName);
        if (homeV1File != null) {
            homeV1FileContainsView = findTextInFile(homeV1File, "<div class=\"marginLeft \"   >\n" +
                    "\t\t\t\t\tLevel 3-1\n" +
                    "\t\t\t\t\t<div class=\"marginLeft \"  >\n" +
                    "\t\t\t\t\t\tLevel 4-1\n" +
                    "\t\t\t\t\t\t<div class=\"marginLeft\"  >Level 5-1</div>\n" +
                    "\t\t\t\t\t</div>\n" +
                    "\t\t\t\t</div>");
        }
        softAssert.assertTrue(
                homeV1FileContainsView,
                "Home view JSP file misses html tags. Name: " + homeV1.getName() + ", XPath: " + homeV1.getXpath()
        );

        checkComponentFiles(
                softAssert,
                sourceFolderPath,
                homeC1,
                definitionNameSpace,
                moduleName,
                expectedAreFileContentForComponent,
                expectedViewFileContentForComponent);

        softAssert.assertAll();
    }

    @Test
    public void generateModuleMixedMode(){
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "GenerateModuleTest.generateModuleMixedMode");
        String projectName = randomWord(8);
        String moduleName = randomWord(10);
        String definitionNameSpace = randomWord(3);
        String sourceFolderPath = new File(getDownloadsFolder()).getAbsolutePath()+"/"+randomWord(10);
        Area homeA1 = new Area(randomWord(4), "//body/div[1]//div[contains(., 'Level 2-1')]", 2, 0, "home");
        View homeV1 = new View(randomWord(10), "jnt:html", homeA1.getXpath()+"/div[1]", 2, 0, homeA1.getTemplateName());
        Component homeC1 = new Component(randomWord(2), randomWord(6), randomWord(6), "jnt:html", "/html/body/div[3]", 2, 0, homeA1.getTemplateName());
        String expectedAreFileContentForComponent = "<template:area path=\""+homeC1.getAreaName()+"\" />";
        String expectedViewFileContentForComponent = "<div  class=\"\"  >\n" +
                "\t\t\tLevel 1-3\n" +
                "\t\t\t<div   class=\"marginLeft \" >\n" +
                "\t\t\t\tLevel 2-1\n" +
                "\t\t\t\t<div   class=\"marginLeft\" >\n" +
                "\t\t\t\t\tLevel 3-1\n" +
                "\t\t\t\t\t<div   class=\"marginLeft\" >Level 4-1</div>\n" +
                "\t\t\t\t</div>\n" +
                "\t\t\t</div>\n" +
                "\t\t</div>";
        boolean homeA1FileContainsArea = false;
        boolean homeV1FileContainsView = false;

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        selectArea(homeA1);
        selectView(homeV1);
        selectComponent(homeC1, "");
        generateModuleManualAndAutoContinue(moduleName, definitionNameSpace, sourceFolderPath, true);
        softAssert.assertTrue(
                isModuleStarted(moduleName),
                "Module '"+moduleName+"' did not start after generation.");
        File templateFile = checkJntTemplateFileExist(softAssert, sourceFolderPath, moduleName);
        boolean baseAreaOneFoundInTemplate = findTextInFile(templateFile, "<template:area path='baseContent'></template:area>");
        softAssert.assertTrue(
                baseAreaOneFoundInTemplate,
                "Base area not found in template JSP."
        );

        File homeA1File = checkAreaFile(softAssert, sourceFolderPath, homeA1, definitionNameSpace, moduleName);
        if(homeA1File != null) {
            homeA1FileContainsArea = findTextInFile(homeA1File, "<template:area path='" + homeA1.getName() + "'/>");
        }
        softAssert.assertTrue(
                homeA1FileContainsArea,
                "Home area JSP file misses area tag. Name: " + homeA1.getName() + ", XPath: " + homeA1.getXpath()
        );
        File homeV1File = checkViewFile(softAssert, sourceFolderPath, homeV1, moduleName);
        if (homeV1File != null) {
            homeV1FileContainsView = findTextInFile(homeV1File, "<div class=\"marginLeft \"   >\n" +
                    "\t\t\t\t\tLevel 3-1\n" +
                    "\t\t\t\t\t<div class=\"marginLeft \"  >\n" +
                    "\t\t\t\t\t\tLevel 4-1\n" +
                    "\t\t\t\t\t\t<div class=\"marginLeft\"  >Level 5-1</div>\n" +
                    "\t\t\t\t\t</div>\n" +
                    "\t\t\t\t</div>");
        }
        softAssert.assertTrue(
                homeV1FileContainsView,
                "Home view JSP file misses html tags. Name: " + homeV1.getName() + ", XPath: " + homeV1.getXpath()
        );

        checkComponentFiles(
                softAssert,
                sourceFolderPath,
                homeC1,
                definitionNameSpace,
                moduleName,
                expectedAreFileContentForComponent,
                expectedViewFileContentForComponent);

        softAssert.assertAll();
    }

    @Test
    public void moduleWithUserCreatedTemplates(){
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "GenerateModuleTest.moduleWithUserCreatedTemplates");
        String projectName = randomWord(8);
        String moduleName = randomWord(10);
        String definitionNameSpace = randomWord(5);
        String sourceFolderPath = new File(getDownloadsFolder()).getAbsolutePath()+"/"+randomWord(10);
        String userCreatedTemplateName = randomWord(10);
        String pageForUserCreatedTemplate = "page3.html";
        Area userA1 = new Area(randomWord(4), "//body/div[1]//div[contains(., 'Level 2-1')]", 2, 0, userCreatedTemplateName);
        View userV1 = new View(randomWord(10), "jnt:html", userA1.getXpath()+"/div[1]", 2, 0, userA1.getTemplateName());
        Component userC1 = new Component(randomWord(2), randomWord(6), randomWord(6), "jnt:html", "/html/body/div[3]", 2, 0, userA1.getTemplateName());
        String expectedAreFileContentForComponent = "<template:area path=\""+userC1.getAreaName()+"\" />";
        String expectedViewFileContentForComponent = "<div  class=\"\"  >\n" +
                "\t\t\tLevel 1-3\n" +
                "\t\t</div>";
        boolean userA1FileContainsArea = false;
        boolean userV1FileContainsView = false;

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");

        createNewTemplate(userCreatedTemplateName, pageForUserCreatedTemplate);
        selectArea(userA1);
        selectView(userV1);
        selectComponent(userC1, "");
        generateModule(moduleName, definitionNameSpace, sourceFolderPath, true);
        softAssert.assertTrue(
                isModuleStarted(moduleName),
                "Module '"+moduleName+"' did not start after generation.");
        checkJntTemplateFileExist(softAssert, sourceFolderPath, moduleName);
        File userA1File = checkAreaFile(softAssert, sourceFolderPath, userA1, definitionNameSpace, moduleName);
        if(userA1File != null) {
            userA1FileContainsArea = findTextInFile(userA1File, "<template:area path='" + userA1.getName() + "'/>");
        }
        softAssert.assertTrue(
                userA1FileContainsArea,
                "User template area JSP file misses area tag. Name: " + userA1.getName() + ", XPath: " + userA1.getXpath()
        );
        File userV1File = checkViewFile(softAssert, sourceFolderPath, userV1, moduleName);
        if (userV1File != null) {
            userV1FileContainsView = findTextInFile(userV1File, "<div   class=\"marginLeft \" >Level 3-2</div>");
        }
        softAssert.assertTrue(
                userV1FileContainsView,
                "User template view JSP file misses html tags. Name: " + userV1.getName() + ", XPath: " + userV1.getXpath()
        );
        checkComponentFiles(
                softAssert,
                sourceFolderPath,
                userC1,
                definitionNameSpace,
                moduleName,
                expectedAreFileContentForComponent,
                expectedViewFileContentForComponent);

        softAssert.assertAll();
    }

    @Test
    public void generateModuleWithAssetFiles(){
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "GenerateModuleTest.generateModuleWithAssetFiles");
        String projectName = randomWord(8);
        String moduleName = randomWord(10);
        String definitionNameSpace = randomWord(5);
        String sourceFolderPath = new File(getDownloadsFolder()).getAbsolutePath()+"/"+randomWord(10);
        String userCreatedTemplateName = randomWord(10);
        String pageForUserCreatedTemplate = "page3.html";
        String folderWithAssetsName = "AnotherFolder";
        String[] expectedItemsInAssetsFolder = {
                "1.css",
                "1.js",
                "css subfolder",
                "img subfolder",
                "js subfolder",
                "subfolder"
        };
        Area userA1 = new Area(randomWord(4), "//body/div[1]//div[contains(., 'Level 2-1')]", 2, 0, userCreatedTemplateName);
        View userV1 = new View(randomWord(10), "jnt:html", userA1.getXpath()+"/div[1]", 2, 0, userA1.getTemplateName());
        Component userC1 = new Component(randomWord(2), randomWord(6), randomWord(6), "jnt:html", "/html/body/div[3]", 2, 0, userA1.getTemplateName());

        importProject("en", projectName, "", "Assets.zip");
        openProjectFirstTime(projectName, "index.html");
        createNewTemplate(userCreatedTemplateName, pageForUserCreatedTemplate);
        selectArea(userA1);
        selectView(userV1);
        selectComponent(userC1, "");
        generateModule(moduleName, definitionNameSpace, sourceFolderPath, true);
        softAssert.assertTrue(
                isModuleStarted(moduleName),
                "Module '"+moduleName+"' did not start after generation.");
        checkFolderInModulesResources(softAssert, sourceFolderPath, folderWithAssetsName, false, expectedItemsInAssetsFolder, moduleName);
        checkJntTemplateFileExist(softAssert, sourceFolderPath, moduleName);
        checkAreaFile(softAssert, sourceFolderPath, userA1, definitionNameSpace, moduleName);
        checkViewFile(softAssert, sourceFolderPath, userV1, moduleName);
        checkAreaFileForComponent(softAssert, sourceFolderPath, userC1, definitionNameSpace, moduleName);
        checkViewFileForComponent(softAssert, sourceFolderPath, userC1, moduleName);

        softAssert.assertAll();
    }

    @Test //MOD-1271
    public void moduleNameWithSpaceOrCapital(){
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "GenerateModuleTest.moduleNameWithSpaceOrCapital");
        String projectName = randomWord(8);
        String moduleName = randomWord(10)+" "+"A";
        String definitionNameSpace = randomWord(3);
        String sourceFolderPath = new File(getDownloadsFolder()).getAbsolutePath()+"/"+randomWord(10);
        Area homeA1 = new Area(randomWord(4), "//body/div[1]//div[contains(., 'Level 2-1')]", 2, 0, "home");
        View homeV1 = new View(randomWord(10), "jnt:html", homeA1.getXpath()+"/div[1]", 2, 0, homeA1.getTemplateName());
        Component homeC1 = new Component(randomWord(2), randomWord(6), randomWord(6), "jnt:html", "/html/body/div[3]", 2, 0, homeA1.getTemplateName());

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        selectArea(homeA1);
        selectView(homeV1);
        selectComponent(homeC1, "");
        generateModule(moduleName, definitionNameSpace, sourceFolderPath, true);
        softAssert.assertTrue(
                isModuleStarted(moduleName.toLowerCase().replace(" ", "-")),
                "Module '"+moduleName+"' did not start after generation.");
        checkJntTemplateFileExist(softAssert, sourceFolderPath, moduleName);
        checkAreaFile(softAssert, sourceFolderPath, homeA1, definitionNameSpace, moduleName);
        checkViewFile(softAssert, sourceFolderPath, homeV1, moduleName);
        checkAreaFileForComponent(softAssert, sourceFolderPath, homeC1, definitionNameSpace, moduleName);
        checkViewFileForComponent(softAssert, sourceFolderPath, homeC1, moduleName);

        softAssert.assertAll();
    }


    /**
     * Click on 'Generate module', fill in all the fields, click create or cancel
     * @param moduleName String, Module name
     * @param definitionNameSpace String Definition name space
     * @param sourcesFolder String, absolute path to folder where you want your modulu's sources
     * @param reallyGenerate true to click 'Create', false to click 'Cancel' after all fields are filled.
     */
    protected void generateModuleAutoContinue(String    moduleName,
                                              String    definitionNameSpace,
                                              String    sourcesFolder,
                                              boolean   reallyGenerate){
        WebElement menuBtn = findByXpath("//i[text()='dvr']/ancestor::button");
        clickOn(menuBtn);
        WebElement generateModuleBtn = findByXpath("//button[@ng-click='project.generateModule()']");
        waitForElementToStopMoving(generateModuleBtn);
        clickOn(generateModuleBtn);
        WebElement confirmBtn = findByXpath("//button[@ng-click=\"awc.choice('generate')\"]");
        waitForElementToStopMoving(confirmBtn);
        clickOn(confirmBtn);
        WebElement moduleNameField = findByName("moduleName");
        WebElement definitionNameSpaceField = findByName("nameSpace");
        WebElement sourcesFolderField = findByName("sources");
        WebElement createBtn = findByXpath("//button[@ng-click='cmc.create()']");
        WebElement cancelBtn = findByXpath("//button[@ng-click='cmc.cancel()']");
        waitForElementToStopMoving(moduleNameField);
        typeInto(moduleNameField, moduleName);
        typeInto(definitionNameSpaceField, definitionNameSpace);
        typeInto(sourcesFolderField, sourcesFolder);
        if(reallyGenerate){
            clickOn(createBtn);
            waitForElementToBeInvisible(createBtn);
            Long start = new Date().getTime();
            WebElement continueBtn = findByXpath("//button[@ng-click='esc.nextStep()']");
            //Module creation
            Assert.assertTrue(
                    isVisible(By.xpath("//md-list-item[contains(., 'Module created')]"), 120),
                    "Module generation failed at module creation stage. Time spent on generation: "+(new Date().getTime() - start - 120000L)+" milliseconds"
            );
            //Deployment
            WebElement autoContinueCheckbox = findByXpath("//md-checkbox[@ng-model='esc.autoContinue']/div");
            waitForElementToBeEnabled(continueBtn, 5);
            clickOn(autoContinueCheckbox);
            Assert.assertTrue(
                    isVisible(By.xpath("//span[@ng-if='!esc.loading && esc.autoContinue' and contains(text(), 'Deploying module in')]"), 5),
                    "Automatic Module generation failed at deployment stage. Timer not started. Time spent on generation: "+(new Date().getTime() - start - 5000)+" milliseconds"
            );
            Assert.assertTrue(
                    isVisible(By.xpath("//md-list-item[contains(., 'Module deployed')]"), 60), //30 sec for the time and 30 for deployment itself
                    "Automatic Module generation failed at deployment stage. Module not deployed. Time spent on generation: "+(new Date().getTime() - start - 60000L)+" milliseconds"
            );
            //Base template generation
            waitForElementToBeEnabled(continueBtn, 5);
            Assert.assertTrue(
                    isVisible(By.xpath("//span[@ng-if='!esc.loading && esc.autoContinue' and contains(text(), 'Generate base template in')]"), 5),
                    "Automatic Module generation failed at base template generation stage. Timer not started. Time spent on generation: "+(new Date().getTime() - start - 5000)+" milliseconds"
            );
            Assert.assertTrue(
                    isVisible(By.xpath("//md-list-item[contains(., 'Base template generated')]"), 60),
                    "Automatic Module generation failed at base template generation stage. Time spent on generation: "+(new Date().getTime() - start - 60000L)+" milliseconds"
            );
            //Asset files copying
            waitForElementToBeEnabled(continueBtn, 5);
            Assert.assertTrue(
                    isVisible(By.xpath("//span[@ng-if='!esc.loading && esc.autoContinue' and contains(text(), 'Copying asset files in')]"), 5),
                    "Automatic Module generation failed at assets copying stage. Timer not started. Time spent on generation: "+(new Date().getTime() - start - 5000)+" milliseconds"
            );
            Assert.assertTrue(
                    isVisible(By.xpath("//md-list-item[contains(., 'Assets copied')]"), 60),
                    "Automatic Module generation failed at assets copying stage. Time spent on generation: "+(new Date().getTime() - start - 60000L)+" milliseconds"
            );
            //Other templates creation
            waitForElementToBeEnabled(continueBtn, 5);
            Assert.assertTrue(
                    isVisible(By.xpath("//span[@ng-if='!esc.loading && esc.autoContinue' and contains(text(), 'Creating templates in')]"), 5),
                    "Automatic Module generation failed at templates creation stage. Timer not started. Time spent on generation: "+(new Date().getTime() - start - 5000)+" milliseconds"
            );
            Assert.assertTrue(
                    isVisible(By.xpath("//md-list-item[contains(., 'Templates created')]"), 60),
                    "Automatic Module generation failed at templates creation stage. Time spent on generation: "+(new Date().getTime() - start - 60000L)+" milliseconds"
            );
            waitForElementToBeInvisible(continueBtn);

            Assert.assertTrue(
                    isVisible(By.xpath("//span[@message-key='angular.tiCreateModuleDirective.message.allDone']"), 5),
                    "All stages of Automatic Module generation passed, but module generation failed anyway." +
                            "Final message that module successfully generated is not visible.\n" +
                            "Module generation took "+(new Date().getTime() - start - 5000L)+" milliseconds");
            WebElement okBtn = findByXpath("//button[@ng-click='esc.close()']");
            clickOn(okBtn);
            waitForElementToBeInvisible(okBtn);
        }else{
            waitForElementToBeEnabled(cancelBtn, 5);
            clickOn(cancelBtn);
            waitForElementToBeInvisible(cancelBtn);
        }
    }


    /**
     * Click on 'Generate module', fill in all the fields, click create or cancel
     * @param moduleName String, Module name
     * @param definitionNameSpace String Definition name space
     * @param sourcesFolder String, absolute path to folder where you want your modulu's sources
     * @param reallyGenerate true to click 'Create', false to click 'Cancel' after all fields are filled.
     */
    protected void generateModuleManualAndAutoContinue(String    moduleName,
                                              String    definitionNameSpace,
                                              String    sourcesFolder,
                                              boolean   reallyGenerate){
        WebElement menuBtn = findByXpath("//i[text()='dvr']/ancestor::button");
        clickOn(menuBtn);
        WebElement generateModuleBtn = findByXpath("//button[@ng-click='project.generateModule()']");
        waitForElementToStopMoving(generateModuleBtn);
        clickOn(generateModuleBtn);
        WebElement confirmBtn = findByXpath("//button[@ng-click=\"awc.choice('generate')\"]");
        waitForElementToStopMoving(confirmBtn);
        clickOn(confirmBtn);
        WebElement moduleNameField = findByName("moduleName");
        WebElement definitionNameSpaceField = findByName("nameSpace");
        WebElement sourcesFolderField = findByName("sources");
        WebElement createBtn = findByXpath("//button[@ng-click='cmc.create()']");
        WebElement cancelBtn = findByXpath("//button[@ng-click='cmc.cancel()']");
        waitForElementToStopMoving(moduleNameField);
        typeInto(moduleNameField, moduleName);
        typeInto(definitionNameSpaceField, definitionNameSpace);
        typeInto(sourcesFolderField, sourcesFolder);
        if(reallyGenerate){
            clickOn(createBtn);
            waitForElementToBeInvisible(createBtn);
            Long start = new Date().getTime();
            WebElement continueBtn = findByXpath("//button[@ng-click='esc.nextStep()']");
            //Module creation
            Assert.assertTrue(
                    isVisible(By.xpath("//md-list-item[contains(., 'Module created')]"), 120),
                    "Module generation failed at module creation stage. Time spent on generation: "+(new Date().getTime() - start - 120000L)+" milliseconds"
            );
            //Deployment
            WebElement autoContinueCheckbox = findByXpath("//md-checkbox[@ng-model='esc.autoContinue']/div");
            waitForElementToBeEnabled(continueBtn, 5);
            clickOn(autoContinueCheckbox);
            Assert.assertTrue(
                    isVisible(By.xpath("//span[@ng-if='!esc.loading && esc.autoContinue' and contains(text(), 'Deploying module in')]"), 5),
                    "Automatic Module generation failed at deployment stage. Timer not started. Time spent on generation: "+(new Date().getTime() - start - 5000)+" milliseconds"
            );
            Assert.assertTrue(
                    isVisible(By.xpath("//md-list-item[contains(., 'Module deployed')]"), 60), //30 sec for the time and 30 for deployment itself
                    "Automatic Module generation failed at deployment stage. Module not deployed. Time spent on generation: "+(new Date().getTime() - start - 60000L)+" milliseconds"
            );
            //Base template generation
            waitForElementToBeEnabled(continueBtn, 5);
            Assert.assertTrue(
                    isVisible(By.xpath("//span[@ng-if='!esc.loading && esc.autoContinue' and contains(text(), 'Generate base template in')]"), 5),
                    "Automatic Module generation failed at base template generation stage. Timer not started. Time spent on generation: "+(new Date().getTime() - start - 5000)+" milliseconds"
            );
            Assert.assertTrue(
                    isVisible(By.xpath("//md-list-item[contains(., 'Base template generated')]"), 60),
                    "Automatic Module generation failed at base template generation stage. Time spent on generation: "+(new Date().getTime() - start - 60000L)+" milliseconds"
            );
            //Asset files copying
            waitForElementToBeEnabled(continueBtn, 5);
            Assert.assertTrue(
                    isVisible(By.xpath("//span[@ng-if='!esc.loading && esc.autoContinue' and contains(text(), 'Copying asset files in')]"), 5),
                    "Automatic Module generation failed at assets copying stage. Timer not started. Time spent on generation: "+(new Date().getTime() - start - 5000)+" milliseconds"
            );
            new Actions(getDriver()).moveToElement(continueBtn, 100, 100).build().perform();
            clickOn(continueBtn);
            Assert.assertTrue(
                    isVisible(By.xpath("//md-list-item[contains(., 'Assets copied')]"), 36), //30 sec for copying and 5 for timer, 1 extra
                    "Automatic Module generation failed at assets copying stage. Time spent on generation: "+(new Date().getTime() - start - 36000L)+" milliseconds"
            );
            //Other templates creation
            waitForElementToBeEnabled(continueBtn, 5);
            Assert.assertTrue(
                    isVisible(By.xpath("//span[@ng-if='!esc.loading && esc.autoContinue' and contains(text(), 'Creating templates in')]"), 5),
                    "Automatic Module generation failed at templates creation stage. Timer not started. Time spent on generation: "+(new Date().getTime() - start - 5000)+" milliseconds"
            );
            Assert.assertTrue(
                    isVisible(By.xpath("//md-list-item[contains(., 'Templates created')]"), 60),
                    "Automatic Module generation failed at templates creation stage. Time spent on generation: "+(new Date().getTime() - start - 60000L)+" milliseconds"
            );
            waitForElementToBeInvisible(continueBtn);

            Assert.assertTrue(
                    isVisible(By.xpath("//span[@message-key='angular.tiCreateModuleDirective.message.allDone']"), 5),
                    "All stages of Automatic Module generation passed, but module generation failed anyway." +
                            "Final message that module successfully generated is not visible.\n" +
                            "Module generation took "+(new Date().getTime() - start - 5000L)+" milliseconds");
            WebElement okBtn = findByXpath("//button[@ng-click='esc.close()']");
            clickOn(okBtn);
            waitForElementToBeInvisible(okBtn);
        }else{
            waitForElementToBeEnabled(cancelBtn, 5);
            clickOn(cancelBtn);
            waitForElementToBeInvisible(cancelBtn);
        }
    }

    protected File checkJntTemplateFileExist(SoftAssert   softAssert,
                                             String       sourceFolderPath,
                                             String       moduleName){
        String jntTemplateFileName = "template."+moduleName.toLowerCase().replace(" ", "-")+".jsp";
        boolean jntTemplateExist = false;
        File[] files = findFilesOrDirectories(sourceFolderPath+"/"+moduleName.toLowerCase().replace(" ", "-")+"/src/main/resources/jnt_template/html", jntTemplateFileName, "jsp");
        if(files != null &&
                files.length > 0 &&
                files[0].exists() &&
                files[0].isFile()){
            jntTemplateExist = true;
        }

        softAssert.assertTrue(
                jntTemplateExist,
                "jnt_template file not found. Expected filename is: '"+jntTemplateFileName+"'."
        );
        if (jntTemplateExist) {
            return files[0];
        } else {
            return null;
        }
    }

    protected File checkAreaFile(SoftAssert    softAssert,
                                 String        sourceFolderPath,
                                 Area          area,
                                 String        definitionNameSpace,
                                 String        moduleName){
        String areaFileName = "tiLayoutComponent."+area.getTemplateName()+"_"+area.getName()+".jsp";
        String areaFolderPath = sourceFolderPath+"/"+moduleName.toLowerCase().replace(" ", "-")+"/src/main/resources/"+definitionNameSpace+"nt_tiLayoutComponent/html";
        boolean areaFileExist = false;

        File[] files = findFilesOrDirectories(areaFolderPath, areaFileName, "jsp");
        if(files != null &&
                files.length > 0 &&
                files[0].exists() &&
                files[0].isFile()){
            areaFileExist = true;
        }
        softAssert.assertTrue(
                areaFileExist,
                "jsp file for "+area.getName()+" area not found. Expected file name: '"+areaFileName+"'. Expected path: "+areaFolderPath
        );
        if (areaFileExist) {
            return files[0];
        } else {
            return null;
        }
    }

    protected File checkAreaFileForComponent(SoftAssert softAssert,
                                             String     sourceFolderPath,
                                             Component  component,
                                             String     definitionNameSpace,
                                             String     moduleName) {
        String areaFileName = "tiLayoutComponent." + component.getTemplateName() + "_" + component.getAreaName() + ".jsp";
        String areaFolderPath = sourceFolderPath + "/" + moduleName.toLowerCase().replace(" ", "-") + "/src/main/resources/" + definitionNameSpace + "nt_tiLayoutComponent/html";
        boolean areaFileExist = false;

        File[] files = findFilesOrDirectories(areaFolderPath, areaFileName, "jsp");
        if (files != null &&
                files.length > 0 &&
                files[0].exists() &&
                files[0].isFile()) {
            areaFileExist = true;
        }
        softAssert.assertTrue(
                areaFileExist,
                "jsp file for " + component.getName() + " component's area not found. Expected file name: '" + areaFileName + "'. Expected path: " + areaFolderPath
        );
        if (areaFileExist) {
            return files[0];
        } else {
            return null;
        }
    }

    protected void checkComponentFiles(SoftAssert   softAssert,
                                       String       sourceFolderPath,
                                       Component    component,
                                       String       definitionNameSpace,
                                       String       moduleName,
                                       String       expectedAreaFileContent,
                                       String       expectedViewFileContent){
        boolean componentsAreaFileContainsArea = false;
        boolean componentsViewFileContainsView = false;
        File componentsAreaFile = checkAreaFileForComponent(softAssert, sourceFolderPath, component, definitionNameSpace, moduleName);
        if(componentsAreaFile != null) {
            componentsAreaFileContainsArea = findTextInFile(componentsAreaFile, expectedAreaFileContent);
        }
        softAssert.assertTrue(
                componentsAreaFileContainsArea,
                "Component's area JSP file misses area tag. Component Name: " + component.getName() + ", Area name: '"+component.getAreaName()+"', XPath: " + component.getXpath()
        );
        File componentsViewFile = checkViewFileForComponent(softAssert, sourceFolderPath, component, moduleName);
        if (componentsViewFile != null) {
            componentsViewFileContainsView = findTextInFile(componentsViewFile, expectedViewFileContent);
        }
        softAssert.assertTrue(
                componentsViewFileContainsView,
                "Component's view JSP file misses html tags. Component Name: " + component.getName() + ", View name: '"+component.getViewName()+"', XPath: " + component.getXpath()
        );
    }

    protected File checkViewFile(SoftAssert    softAssert,
                                 String        sourceFolderPath,
                                 View          view,
                                 String        moduleName){
        String viewFileName = view.getNodeType().replace("jnt:", "")+"."+view.getTemplateName()+"_"+view.getName()+".jsp";
        String viewFolderPath = sourceFolderPath+"/"+moduleName.toLowerCase().replace(" ", "-")+"/src/main/resources/"+view.getNodeType().replace(":", "_")+"/html";
        boolean viewFileExist = false;

        File[] files = findFilesOrDirectories(viewFolderPath, viewFileName, "jsp");
        if(files != null &&
                files.length > 0 &&
                files[0].exists() &&
                files[0].isFile()){
            viewFileExist = true;
        }
        softAssert.assertTrue(
                viewFileExist,
                "jsp file for "+view.getName()+" view not found. Expected file name: '"+viewFileName+"'. Expected path: "+viewFolderPath
        );
        if (viewFileExist) {
            return files[0];
        } else {
            return null;
        }
    }

    protected File checkViewFileForComponent(SoftAssert softAssert,
                                             String     sourceFolderPath,
                                             Component  component,
                                             String     moduleName) {
        String viewFileName = component.getNodeType().replace("jnt:", "") + "." + component.getTemplateName() + "_" + component.getViewName() + ".jsp";
        String viewFolderPath = sourceFolderPath + "/" + moduleName.toLowerCase().replace(" ", "-") + "/src/main/resources/" + component.getNodeType().replace(":", "_") + "/html";
        boolean viewFileExist = false;

        File[] files = findFilesOrDirectories(viewFolderPath, viewFileName, "jsp");
        if (files != null &&
                files.length > 0 &&
                files[0].exists() &&
                files[0].isFile()) {
            viewFileExist = true;
        }
        softAssert.assertTrue(
                viewFileExist,
                "jsp file for " + component.getName() + " component's view not found. Expected file name: '" + viewFileName + "'. Expected path: " + viewFolderPath
        );
        if (viewFileExist) {
            return files[0];
        } else {
            return null;
        }
    }

    protected boolean findTextInFile(File file,
                                     String text) {
        boolean match = false;
        try {
            String content = readFile(file.getAbsolutePath(), StandardCharsets.UTF_8);
            if (content.replaceAll("\\s+", "").contains(text.replaceAll("\\s+", ""))) {
                match = true;
            }
        } catch (IOException e) {
        }
        return match;
    }

    private static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

}
