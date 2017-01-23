package org.jahia.modules.templateimporter.tests.modulegeneration.tests;

import org.jahia.modules.templateimporter.tests.TemplateImporterRepository;
import org.jahia.modules.templateimporter.tests.businessobjects.Area;
import org.jahia.modules.templateimporter.tests.businessobjects.Component;
import org.jahia.modules.tests.utils.SoftAssertWithScreenshot;
import org.openqa.selenium.By;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

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
        Area nestedArea = new Area(randomWord(1), homeA1.getXpath()+"/div[1]", 2, 0, homeA1.getTemplateName());
        Component homeC1 = new Component(randomWord(2), randomWord(6), randomWord(6), "/html/body/div[3]", 2, 0, homeA1.getTemplateName());
        String expectedPageFileContent = "\t\t<div >\n" +
                "\t\t\t<a href=\"index.html\" >index</a>\n" +
                "\t\t\t<a href=\"page1.html\" >Page 1</a>\n" +
                "\t\t\t<a href=\"page2.html\" >Page 2</a>\n" +
                "\t\t\t<a href=\"page3.html\" >Page 3</a>\n" +
                "\t\t\t<h2 >Index</h2>\n" +
                "\t\t\tLevel 1-1\n" +
                "\t\t\t<div class=\"marginLeft \" ><template:module path=\""+homeA1.getName()+"\" /></div>\n" +
                "\t\t\t<div class=\"marginLeft\" >\n" +
                "\t\t\t\tLevel 2-2\n" +
                "\t\t\t\t<div class=\"marginLeft\" >Level 3-1</div>\n" +
                "\t\t\t\t<div class=\"marginLeft\" >Level 3-2</div>\n" +
                "\t\t\t</div>\n" +
                "\t\t</div>\n" +
                "\t\t<div >Level 1-2</div>\n" +
                "\t\t<div class=\"\" ><template:module path=\""+homeC1.getAreaName()+"\"></template:module></div>\n" +
                "\t\t<div >Level 1-4</div>\n";
        String expectedViewFileContentForComponent = "\t\t\tLevel 1-3\n" +
                "\t\t\t<div class=\"marginLeft \" >\n" +
                "\t\t\t\tLevel 2-1\n" +
                "\t\t\t\t<div class=\"marginLeft\" >\n" +
                "\t\t\t\t\tLevel 3-1\n" +
                "\t\t\t\t\t<div class=\"marginLeft\" >Level 4-1</div>\n" +
                "\t\t\t\t</div>\n" +
                "\t\t\t</div>";
        String expectedFileContentForArea = "<div class=\"marginLeft \"><template:module path=\""+nestedArea.getName()+"\" /></div>";

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        selectArea(homeA1);
        selectArea(nestedArea);
        selectComponent(homeC1, "");
        generateModule(moduleName, definitionNameSpace, sourceFolderPath, true);
        softAssert.assertTrue(
                isModuleStarted(moduleName),
                "Module '"+moduleName+"' did not start after generation.");
        File templateFile = checkJntTemplateFileExist(softAssert, sourceFolderPath, moduleName);
        File areaFile = checkAreaFile(softAssert, sourceFolderPath, homeA1, moduleName, definitionNameSpace, nestedArea);
        boolean baseAreaOneFoundInTemplate = findTextInFile(templateFile, "<template:area path='baseContent'></template:area>");
        boolean templateFileContainsBodyTag = findTextInFile(templateFile, "</body>");
        templateFileContainsBodyTag = (templateFileContainsBodyTag && findTextInFile(templateFile, "<body >"));

        if (areaFile != null) {
            boolean nestedAreaFileContainsAreaTag = findTextInFile(areaFile, expectedFileContentForArea);
            softAssert.assertTrue(
                    nestedAreaFileContainsAreaTag,
                    "JSP file for nested area is missing area tag (or contains unexpected characters)."
            );
        }else{
            // No need to fail here, it is already failed in checkAreaFile() method.
        }
        softAssert.assertTrue(
                baseAreaOneFoundInTemplate,
                "Base area not found in template JSP."
        );
        softAssert.assertTrue(
                templateFileContainsBodyTag,
                "Template.jsp does not contain <body> tag!"
        );
        checkPageFile(
                softAssert,
                sourceFolderPath,
                homeA1.getTemplateName(),
                moduleName,
                definitionNameSpace,
                expectedPageFileContent,
                false
        );
        checkComponentFiles(
                softAssert,
                sourceFolderPath,
                homeC1,
                moduleName,
                definitionNameSpace,
                expectedViewFileContentForComponent);
        checkPageInPreview(
                moduleName,
                homeA1.getTemplateName(),
                homeA1.getXpath(),
                false,
                softAssert,
                "Empty area"
        );
        checkPageInPreview(
                moduleName,
                homeC1.getTemplateName(),
                "/html/body/div[3][contains(., 'Level 1-3')]",
                true,
                softAssert,
                "Component"
        );

        softAssert.assertAll();
    }

    /*@Test
    public void generateModuleWithAutoContinueTest(){
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "GenerateModuleTest.generateModuleWithAutoContinueTest");
        String projectName = randomWord(8);
        String moduleName = randomWord(10);
        String definitionNameSpace = randomWord(3);
        String sourceFolderPath = new File(getDownloadsFolder()).getAbsolutePath()+"/"+randomWord(10);
        Area homeA1 = new Area(randomWord(4), "//body/div[1]//div[contains(., 'Level 2-1')]", 2, 0, "home");
        Component homeC1 = new Component(randomWord(2), randomWord(6), randomWord(6), "/html/body/div[3]", 2, 0, homeA1.getTemplateName());
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

        File homeA1File = checkAreaFile(softAssert, sourceFolderPath, homeA1, moduleName, definitionNameSpace);
        if(homeA1File != null) {
            homeA1FileContainsArea = findTextInFile(homeA1File, "<template:area path='" + homeA1.getName() + "'/>");
        }
        softAssert.assertTrue(
                homeA1FileContainsArea,
                "Home area JSP file misses area tag. Name: " + homeA1.getName() + ", XPath: " + homeA1.getXpath()
        );

        checkComponentFiles(
                softAssert,
                sourceFolderPath,
                homeC1,
                moduleName, definitionNameSpace,
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
        Component homeC1 = new Component(randomWord(2), randomWord(6), randomWord(6), "/html/body/div[3]", 2, 0, homeA1.getTemplateName());
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

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        selectArea(homeA1);
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

        File homeA1File = checkAreaFile(softAssert, sourceFolderPath, homeA1, moduleName, definitionNameSpace);
        if(homeA1File != null) {
            homeA1FileContainsArea = findTextInFile(homeA1File, "<template:area path='" + homeA1.getName() + "'/>");
        }
        softAssert.assertTrue(
                homeA1FileContainsArea,
                "Home area JSP file misses area tag. Name: " + homeA1.getName() + ", XPath: " + homeA1.getXpath()
        );

        checkComponentFiles(
                softAssert,
                sourceFolderPath,
                homeC1,
                moduleName, definitionNameSpace,
                expectedAreFileContentForComponent,
                expectedViewFileContentForComponent);

        softAssert.assertAll();
    }*/

    @Test
    public void moduleWithUserCreatedTemplates(){
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "GenerateModuleTest.moduleWithUserCreatedTemplates");
        String projectName = randomWord(8);
        String moduleName = randomWord(10);
        String definitionNameSpace = randomWord(3);
        String userCreatedTemplateName = randomWord(9);
        String pageForUserCreatedTemplate = "page3.html";
        String sourceFolderPath = new File(getDownloadsFolder()).getAbsolutePath()+"/"+randomWord(10);
        Area userA1 = new Area(randomWord(4), "//body/div[1]//div[contains(., 'Level 2-1')]", 2, 0, userCreatedTemplateName);
        Area userNestedArea = new Area(randomWord(1), userA1.getXpath()+"/div[1]", 2, 0, userA1.getTemplateName());
        Component userC1 = new Component(randomWord(2), randomWord(6), randomWord(6), "/html/body/div[3]", 2, 0, userA1.getTemplateName());
        String expectedUsersPageFileContent = "\t\t<div >\n" +
                "\t\t\t<a href=\"index.html\" >index</a>\n" +
                "\t\t\t<a href=\"page1.html\" >Page 1</a>\n" +
                "\t\t\t<a href=\"page2.html\" >Page 2</a>\n" +
                "\t\t\t<a href=\"page3.html\" >Page 3</a>\n" +
                "\t\t\t<h2 >Page 3</h2>\n" +
                "\t\t\tLevel 1-1\n" +
                "\t\t\t<div class=\"marginLeft \" ><template:module path=\""+userA1.getName()+"\" /></div>\n" +
                "\t\t\t<div class=\"marginLeft\" >\n" +
                "\t\t\t\tLevel 2-2\n" +
                "\t\t\t\t<div class=\"marginLeft\" >Level 3-1</div>\n" +
                "\t\t\t</div>\n" +
                "\t\t</div>\n" +
                "\t\t<div >Level 1-2</div>\n" +
                "\t\t<div class=\"\" ><template:module path=\""+userC1.getAreaName()+"\"></template:module></div>\n" +
                "\t\t<div >Level 1-4</div>\n";
        String expectedHomePageContent = "\t\t<div >\n" +
                "\t\t\t<a href=\"index.html\" >index</a>\n" +
                "\t\t\t<a href=\"page1.html\" >Page 1</a>\n" +
                "\t\t\t<a href=\"page2.html\" >Page 2</a>\n" +
                "\t\t\t<a href=\"page3.html\" >Page 3</a>\n" +
                "\t\t\t<h2 >Index</h2>\n" +
                "\t\t\tLevel 1-1\n" +
                "\t\t\t<div class=\"marginLeft\" >\n" +
                "\t\t\t\tLevel 2-1\n" +
                "\t\t\t\t<div class=\"marginLeft\" >\n" +
                "\t\t\t\t\tLevel 3-1\n" +
                "\t\t\t\t\t<div class=\"marginLeft\" >\n" +
                "\t\t\t\t\t\tLevel 4-1\n" +
                "\t\t\t\t\t\t<div class=\"marginLeft\" >Level 5-1</div>\n" +
                "\t\t\t\t\t</div>\n" +
                "\t\t\t\t</div>\n" +
                "\t\t\t\t<div class=\"marginLeft\" >Level 3-2</div>\n" +
                "\t\t\t</div>\n" +
                "\t\t\t<div class=\"marginLeft\" >\n" +
                "\t\t\t\tLevel 2-2\n" +
                "\t\t\t\t<div class=\"marginLeft\" >Level 3-1</div>\n" +
                "\t\t\t\t<div class=\"marginLeft\" >Level 3-2</div>\n" +
                "\t\t\t</div>\n" +
                "\t\t</div>\n" +
                "\t\t<div >Level 1-2</div>\n" +
                "\t\t<div >\n" +
                "\t\t\tLevel 1-3\n" +
                "\t\t\t<div class=\"marginLeft\" >\n" +
                "\t\t\t\tLevel 2-1\n" +
                "\t\t\t\t<div class=\"marginLeft\" >\n" +
                "\t\t\t\t\tLevel 3-1\n" +
                "\t\t\t\t\t<div class=\"marginLeft\" >Level 4-1</div>\n" +
                "\t\t\t\t</div>\n" +
                "\t\t\t</div>\n" +
                "\t\t</div>\n" +
                "\t\t<div >Level 1-4</div>\n";
        String expectedViewFileContentForComponent = "Level 1-3";
        String expectedFileContentForArea = "<div class=\"marginLeft \"><template:module path=\""+userNestedArea.getName()+"\" /></div>";

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        createNewTemplate(userCreatedTemplateName, pageForUserCreatedTemplate);
        selectArea(userA1);
        selectArea(userNestedArea);
        selectComponent(userC1, "");
        generateModule(moduleName, definitionNameSpace, sourceFolderPath, true);
        softAssert.assertTrue(
                isModuleStarted(moduleName),
                "Module '"+moduleName+"' did not start after generation.");
        File templateFile = checkJntTemplateFileExist(softAssert, sourceFolderPath, moduleName);
        File areaFile = checkAreaFile(softAssert, sourceFolderPath, userA1, moduleName, definitionNameSpace, userNestedArea);
        boolean baseAreaOneFoundInTemplate = findTextInFile(templateFile, "<template:area path='baseContent'></template:area>");
        boolean templateFileContainsBodyTag = (findTextInFile(templateFile, "</body>") && findTextInFile(templateFile, "<body >"));

        if (areaFile != null) {
            boolean nestedAreaFileContainsAreaTag = findTextInFile(areaFile, expectedFileContentForArea);
            softAssert.assertTrue(
                    nestedAreaFileContainsAreaTag,
                    "JSP file for nested area is missing area tag (or contains unexpected characters)."
            );
        }else{
            // No need to fail here, it is already failed in checkAreaFile() method.
        }
        softAssert.assertTrue(
                baseAreaOneFoundInTemplate,
                "Base area not found in template JSP."
        );
        softAssert.assertTrue(
                templateFileContainsBodyTag,
                "Template.jsp does not contain <body> tag! Reopen MOD-1664."
        );
        checkPageFile(
                softAssert,
                sourceFolderPath,
                userA1.getTemplateName(),
                moduleName,
                definitionNameSpace,
                expectedUsersPageFileContent,
                false
        );
        checkPageFile(
                softAssert,
                sourceFolderPath,
                "home",
                moduleName,
                definitionNameSpace,
                expectedHomePageContent,
                false
        );
        checkComponentFiles(
                softAssert,
                sourceFolderPath,
                userC1,
                moduleName,
                definitionNameSpace,
                expectedViewFileContentForComponent);
        checkPageInPreview(
                moduleName,
                userA1.getTemplateName(),
                userA1.getXpath(),
                false,
                softAssert,
                "Empty area"
        );
        checkPageInPreview(
                moduleName,
                userC1.getTemplateName(),
                "/html/body/div[3][contains(., 'Level 1-3')]",
                true,
                softAssert,
                "Component"
        );

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
        Component userC1 = new Component(randomWord(2), randomWord(6), randomWord(6), "/html/body/div[3]", 2, 0, userA1.getTemplateName());

        importProject("en", projectName, "", "Assets.zip");
        openProjectFirstTime(projectName, "index.html");
        createNewTemplate(userCreatedTemplateName, pageForUserCreatedTemplate);
        selectArea(userA1);
        selectComponent(userC1, "");
        generateModule(moduleName, definitionNameSpace, sourceFolderPath, true);
        softAssert.assertTrue(
                isModuleStarted(moduleName),
                "Module '"+moduleName+"' did not start after generation.");
        checkFolderInModulesResources(softAssert, sourceFolderPath, folderWithAssetsName, false, expectedItemsInAssetsFolder, moduleName);
        checkJntTemplateFileExist(softAssert, sourceFolderPath, moduleName);
        checkViewFileForComponent(softAssert, sourceFolderPath, userC1, moduleName, definitionNameSpace);

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
        Component homeC1 = new Component(randomWord(2), randomWord(6), randomWord(6), "/html/body/div[3]", 2, 0, homeA1.getTemplateName());

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        selectArea(homeA1);
        selectComponent(homeC1, "");
        generateModule(moduleName, definitionNameSpace, sourceFolderPath, true);
        softAssert.assertTrue(
                isModuleStarted(moduleName.toLowerCase().replace(" ", "-")),
                "Module '"+moduleName+"' did not start after generation.");
        checkJntTemplateFileExist(softAssert, sourceFolderPath, moduleName);
//        checkAreaFile(softAssert, sourceFolderPath, homeA1, moduleName, definitionNameSpace);
//        checkAreaFileForComponent(softAssert, sourceFolderPath, homeC1, moduleName, definitionNameSpace);
        checkViewFileForComponent(softAssert, sourceFolderPath, homeC1, moduleName, definitionNameSpace);

        softAssert.assertAll();
    }


//    /**
//     * Click on 'Generate module', fill in all the fields, click create or cancel
//     * @param moduleName String, Module name
//     * @param definitionNameSpace String Definition name space
//     * @param sourcesFolder String, absolute path to folder where you want your modulu's sources
//     * @param reallyGenerate true to click 'Create', false to click 'Cancel' after all fields are filled.
//     */
 /*   protected void generateModuleAutoContinue(String    moduleName,
                                              String    definitionNameSpace,
                                              String    sourcesFolder,
                                              boolean   reallyGenerate){
        WebElement menuBtn = findByXpath("//md-icon[text()='desktop_windows']/ancestor::button");
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
            WebElement continueBtn = findByXpath("//button[@ng-click='esc.nextStep()']");
            Long moduleGenerationStart = new Date().getTime();
            Long phaseStart = new Date().getTime();
            //Module creation
            Assert.assertTrue(
                    isVisible(By.xpath("//md-list-item[contains(., 'Module created')]"), 120),
                    "Module generation failed at module creation stage. Time spent on stage: "+(new Date().getTime() - phaseStart)+" milliseconds"
            );
            //Deployment
            WebElement autoContinueCheckbox = findByXpath("//md-checkbox[@ng-model='esc.autoContinue']/div");
            waitForElementToBeEnabled(continueBtn, 5);
            clickOn(autoContinueCheckbox);
            phaseStart = new Date().getTime();
            Assert.assertTrue(
                    isVisible(By.xpath("//span[@ng-if='!esc.loading && esc.autoContinue' and contains(text(), 'Deploying module in')]"), 5),
                    "Automatic Module generation failed at deployment stage. Timer not started. Time spent on stage: "+(new Date().getTime() - phaseStart)+" milliseconds"
            );
            Assert.assertTrue(
                    isVisible(By.xpath("//md-list-item[contains(., 'Module deployed')]"), 60), //30 sec for the time and 30 for deployment itself
                    "Automatic Module generation failed at deployment stage. Module not deployed. Time spent on stage: "+(new Date().getTime() - phaseStart)+" milliseconds"
            );
            //Base template generation
            waitForElementToBeEnabled(continueBtn, 5);
            phaseStart = new Date().getTime();
            Assert.assertTrue(
                    isVisible(By.xpath("//span[@ng-if='!esc.loading && esc.autoContinue' and contains(text(), 'Generate base template in')]"), 5),
                    "Automatic Module generation failed at base template generation stage. Timer not started. Time spent on stage: "+(new Date().getTime() - phaseStart)+" milliseconds"
            );
            Assert.assertTrue(
                    isVisible(By.xpath("//md-list-item[contains(., 'Base template generated')]"), 60),
                    "Automatic Module generation failed at base template generation stage. Time spent on stage: "+(new Date().getTime() - phaseStart)+" milliseconds"
            );
            //Asset files copying
            waitForElementToBeEnabled(continueBtn, 5);
            phaseStart = new Date().getTime();
            Assert.assertTrue(
                    isVisible(By.xpath("//span[@ng-if='!esc.loading && esc.autoContinue' and contains(text(), 'Copying asset files in')]"), 5),
                    "Automatic Module generation failed at assets copying stage. Timer not started. Time spent on stage: "+(new Date().getTime() - phaseStart)+" milliseconds"
            );
            Assert.assertTrue(
                    isVisible(By.xpath("//md-list-item[contains(., 'Assets copied')]"), 60),
                    "Automatic Module generation failed at assets copying stage. Time spent on generation: "+(new Date().getTime() - phaseStart)+" milliseconds"
            );
            //Other templates creation
            waitForElementToBeEnabled(continueBtn, 5);
            phaseStart = new Date().getTime();
            Assert.assertTrue(
                    isVisible(By.xpath("//span[@ng-if='!esc.loading && esc.autoContinue' and contains(text(), 'Creating templates in')]"), 5),
                    "Automatic Module generation failed at templates creation stage. Timer not started. Time spent on stage: "+(new Date().getTime() - phaseStart)+" milliseconds"
            );
            Assert.assertTrue(
                    isVisible(By.xpath("//md-list-item[contains(., 'Templates created')]"), 90),
                    "Automatic Module generation failed at templates creation stage. Time spent on stage: "+(new Date().getTime() - phaseStart)+" milliseconds"
            );
            waitForElementToBeInvisible(continueBtn);

            Assert.assertTrue(
                    isVisible(By.xpath("//span[@message-key='angular.dsCreateModuleDirective.message.allDone']"), 5),
                    "All stages of Automatic Module generation passed, but module generation failed anyway." +
                            "Final message that module successfully generated is not visible.\n" +
                            "Module generation took "+(new Date().getTime() - moduleGenerationStart)+" milliseconds");
            WebElement okBtn = findByXpath("//button[@ng-click='esc.close()']");
            clickOn(okBtn);
            waitForElementToBeInvisible(okBtn);
        }else{
            waitForElementToBeEnabled(cancelBtn, 5);
            clickOn(cancelBtn);
            waitForElementToBeInvisible(cancelBtn);
        }
    }*/

//    /**
//     * Click on 'Generate module', fill in all the fields, click create or cancel
//     * @param moduleName String, Module name
//     * @param definitionNameSpace String Definition name space
//     * @param sourcesFolder String, absolute path to folder where you want your modulu's sources
//     * @param reallyGenerate true to click 'Create', false to click 'Cancel' after all fields are filled.
//     */
   /* protected void generateModuleManualAndAutoContinue(String    moduleName,
                                              String    definitionNameSpace,
                                              String    sourcesFolder,
                                              boolean   reallyGenerate){
        WebElement menuBtn = findByXpath("//md-icon[text()='desktop_windows']/ancestor::button");
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
            new Actions(getDriver()).moveToElement(continueBtn, 100, -100).build().perform();
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
                    isVisible(By.xpath("//md-list-item[contains(., 'Templates created')]"), 90),
                    "Automatic Module generation failed at templates creation stage. Time spent on generation: "+(new Date().getTime() - start - 60000L)+" milliseconds"
            );
            waitForElementToBeInvisible(continueBtn);

            Assert.assertTrue(
                    isVisible(By.xpath("//span[@message-key='angular.dsCreateModuleDirective.message.allDone']"), 5),
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
    }*/

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

    protected File checkAreaFile(SoftAssert softAssert,
                                 String sourceFolderPath,
                                 Area   area,
                                 String moduleName,
                                 String definitionNameSpace,
                                 Area   nestedArea){
        String areaFileName = "dsSiteLayoutComponent."+area.getName()+"_"+nestedArea.getName()+".jsp";
        String areaFolderPath = sourceFolderPath+"/"+moduleName.toLowerCase().replace(" ", "-")+"/src/main/resources/"+definitionNameSpace+"nt_dsSiteLayoutComponent/html";
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
                "jsp file for '"+area.getName()+"' area not found. Expected file name: '"+areaFileName+"'. Expected path: "+areaFolderPath
        );
        if (areaFileExist) {
            return files[0];
        } else {
            return null;
        }
    }

    /*protected File checkAreaFileForComponent(SoftAssert softAssert,
                                             String sourceFolderPath,
                                             Component component,
                                             String moduleName,
                                             String definitionNameSpace) {
        String areaFileName = "dsLayoutComponent." + component.getTemplateName() + "_" + component.getAreaName() + ".jsp";
        String areaFolderPath = sourceFolderPath + "/" + moduleName.toLowerCase().replace(" ", "-") + "/src/main/resources/" + definitionNameSpace + "nt_dsLayoutComponent/html";
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
    }*/

    protected void checkComponentFiles(SoftAssert softAssert,
                                       String sourceFolderPath,
                                       Component component,
                                       String moduleName,
                                       String definitionNameSpace,
                                       String expectedViewFileContent){
        boolean componentsViewFileContainsView = false;
        File componentsViewFile = checkViewFileForComponent(softAssert, sourceFolderPath, component, moduleName, definitionNameSpace);
        if (componentsViewFile != null) {
            componentsViewFileContainsView = findTextInFile(componentsViewFile, expectedViewFileContent);
        }
        softAssert.assertTrue(
                componentsViewFileContainsView,
                "Component's view JSP file misses html tags. Component Name: " + component.getName() + ", View name: '"+component.getViewName()+"', XPath: " + component.getXpath()
        );
    }

    protected File checkViewFileForComponent(SoftAssert softAssert,
                                             String     sourceFolderPath,
                                             Component  component,
                                             String     moduleName,
                                             String     definitioNameSpace) {
        String  viewFolderPath = sourceFolderPath+"/"+moduleName.toLowerCase().replace(" ", "-")+"/src/main/resources/"+definitioNameSpace+"nt_"+component.getName()+"/html";

        String viewFileName = component.getName()+"."+component.getViewName()+".jsp";
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
                "jsp file for "+component.getName()+" view not found. Expected file name: '"+viewFileName+"'. Expected path: "+viewFolderPath
        );
        if (viewFileExist) {
            return files[0];
        } else {
            return null;
        }
    }

    protected void checkPageFile(SoftAssert softAssert,
                                 String sourceFolderPath,
                                 String pageTemplateName,
                                 String moduleName,
                                 String definitioNameSpace,
                                 String expectedFileContent,
                                 boolean shouldContainBodyTag) {
        String pageFileFolderPath = sourceFolderPath + "/" + moduleName.toLowerCase().replace(" ", "-") + "/src/main/resources/" + definitioNameSpace + "nt_dsLayoutComponent/html";

        String pageFileFileName = "dsLayoutComponent." + pageTemplateName + "_pagehtml.jsp";
        boolean pageFileExist = false;

        File[] files = findFilesOrDirectories(pageFileFolderPath, pageFileFileName, "jsp");
        if (files != null &&
                files.length > 0 &&
                files[0].exists() &&
                files[0].isFile()) {
            pageFileExist = true;
        }
        softAssert.assertTrue(
                pageFileExist,
                "jsp file for " + pageTemplateName + " page file not found. Expected file name: '" + pageFileFileName + "'. Expected path: " + pageFileFolderPath
        );
        if (pageFileExist) {
            boolean pageFileContantIsOk = findTextInFile(files[0], expectedFileContent);
            softAssert.assertTrue(pageFileContantIsOk,
                    "jsp file for pagefile '" + pageTemplateName + "' is missing some content (or has unexpected characters).");
            if(shouldContainBodyTag){
                boolean pageFileContainsBodyTag = (findTextInFile(files[0], "</body>") && findTextInFile(files[0], "<body >"));
                softAssert.assertEquals(pageFileContainsBodyTag, shouldContainBodyTag, "Unexpected / missing body tag in jsp file for pagefile '" + pageTemplateName + "'.");
            }
        }
    }

    protected void checkPageInPreview(String    moduleName,
                                      String    pageName,
                                      String    xPath,
                                      boolean   expectedResult,
                                      SoftAssert    softAssert,
                                      String    errorMsg){
        String pagePath;
        if (pageName.equals("home")){
            pagePath = "home";
        }else{
            pagePath = "home/"+pageName;
        }
        getDriver().get(getBaseURL()+"/cms/render/default/en/sites/mySite_"+moduleName+"/"+pagePath+".html");
        softAssert.assertEquals(isVisible(By.xpath(xPath), 2),
                expectedResult,
                errorMsg+ ". In preview, element with xPath '"+xPath+"' has unexpected visibility on '"+pageName+"' after module generation.");
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
