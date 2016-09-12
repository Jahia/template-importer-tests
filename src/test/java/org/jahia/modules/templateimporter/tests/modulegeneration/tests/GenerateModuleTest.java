package org.jahia.modules.templateimporter.tests.modulegeneration.tests;

import org.jahia.modules.templateimporter.tests.TemplateImporterRepository;
import org.jahia.modules.templateimporter.tests.businessobjects.Area;
import org.jahia.modules.templateimporter.tests.businessobjects.View;
import org.jahia.modules.tests.utils.SoftAssertWithScreenshot;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.io.File;

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
        Area baseA1 = new Area(randomWord(5), "//body/div[1]", 1, 0, "base");
        Area baseA2 = new Area(randomWord(3), "//body/div[3]", 1, 0, "base");
        Area homeA1 = new Area(randomWord(4), "//body/div[1]//div[contains(., 'Level 2-1')]", 1, 0, "home");
        View baseV1 = new View(randomWord(6), "jnt:bootstrapMainContent", baseA2.getXpath()+"/div[1]", 1, 0, baseA2.getTemplateName());
        View homeV1 = new View(randomWord(10), "jnt:html", homeA1.getXpath()+"/div[1]", 1, 0, homeA1.getTemplateName());

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        selectArea(baseA1);
        selectArea(baseA2);
        selectView(baseV1);
        switchToTemplate("home");
        selectArea(homeA1);
        selectView(homeV1);
        generateModule(moduleName, definitionNameSpace, sourceFolderPath, true);
        softAssert.assertTrue(
                isModuleStarted(moduleName),
                "Module '"+moduleName+"' did not start after generation.");
        checkJntTemplateFile(softAssert, sourceFolderPath, moduleName);
        checkAreaFile(softAssert, sourceFolderPath, baseA1, definitionNameSpace, moduleName);
        checkAreaFile(softAssert, sourceFolderPath, baseA2, definitionNameSpace, moduleName);
        checkAreaFile(softAssert, sourceFolderPath, homeA1, definitionNameSpace, moduleName);
        checkViewFile(softAssert, sourceFolderPath, baseV1, moduleName);
        checkViewFile(softAssert, sourceFolderPath, homeV1, moduleName);
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
        Area baseA1 = new Area(randomWord(5), "//body/div[1]", 1, 0, "base");
        Area baseA2 = new Area(randomWord(3), "//body/div[3]", 1, 0, "base");
        Area userA1 = new Area(randomWord(4), "//body/div[1]//div[contains(., 'Level 2-1')]", 1, 0, userCreatedTemplateName);
        View baseV1 = new View(randomWord(6), "jnt:bootstrapMainContent", baseA2.getXpath()+"/div[1]", 1, 0, baseA2.getTemplateName());
        View userV1 = new View(randomWord(10), "jnt:html", userA1.getXpath()+"/div[1]", 1, 0, userA1.getTemplateName());

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        selectArea(baseA1);
        selectArea(baseA2);
        selectView(baseV1);
        createNewTemplate(userCreatedTemplateName, pageForUserCreatedTemplate);
        selectArea(userA1);
        selectView(userV1);
        generateModule(moduleName, definitionNameSpace, sourceFolderPath, true);
        softAssert.assertTrue(
                isModuleStarted(moduleName),
                "Module '"+moduleName+"' did not start after generation.");
        checkJntTemplateFile(softAssert, sourceFolderPath, moduleName);
        checkAreaFile(softAssert, sourceFolderPath, baseA1, definitionNameSpace, moduleName);
        checkAreaFile(softAssert, sourceFolderPath, baseA2, definitionNameSpace, moduleName);
        checkAreaFile(softAssert, sourceFolderPath, userA1, definitionNameSpace, moduleName);
        checkViewFile(softAssert, sourceFolderPath, baseV1, moduleName);
        checkViewFile(softAssert, sourceFolderPath, userV1, moduleName);
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
        Area baseA1 = new Area(randomWord(5), "//body/div[1]", 1, 0, "base");
        Area baseA2 = new Area(randomWord(3), "//body/div[3]", 1, 0, "base");
        Area userA1 = new Area(randomWord(4), "//body/div[1]//div[contains(., 'Level 2-1')]", 1, 0, userCreatedTemplateName);
        View baseV1 = new View(randomWord(6), "jnt:bootstrapMainContent", baseA2.getXpath()+"/div[1]", 1, 0, baseA2.getTemplateName());
        View userV1 = new View(randomWord(10), "jnt:html", userA1.getXpath()+"/div[1]", 1, 0, userA1.getTemplateName());

        importProject("en", projectName, "", "Assets.zip");
        openProjectFirstTime(projectName, "index.html");
        selectArea(baseA1);
        selectArea(baseA2);
        selectView(baseV1);
        createNewTemplate(userCreatedTemplateName, pageForUserCreatedTemplate);
        selectArea(userA1);
        selectView(userV1);
        generateModule(moduleName, definitionNameSpace, sourceFolderPath, true);
        softAssert.assertTrue(
                isModuleStarted(moduleName),
                "Module '"+moduleName+"' did not start after generation.");
        checkFolderInModulesResources(softAssert, sourceFolderPath, folderWithAssetsName, expectedItemsInAssetsFolder, moduleName);
        checkJntTemplateFile(softAssert, sourceFolderPath, moduleName);
        checkAreaFile(softAssert, sourceFolderPath, baseA1, definitionNameSpace, moduleName);
        checkAreaFile(softAssert, sourceFolderPath, baseA2, definitionNameSpace, moduleName);
        checkAreaFile(softAssert, sourceFolderPath, userA1, definitionNameSpace, moduleName);
        checkViewFile(softAssert, sourceFolderPath, baseV1, moduleName);
        checkViewFile(softAssert, sourceFolderPath, userV1, moduleName);
        softAssert.assertAll();
    }

    @Test //MOD-1271
    public void moduleNameWithSpaceOrCapital(){
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "GenerateModuleTest.moduleNameWithSpaceOrCapital");
        String projectName = randomWord(8);
        String moduleName = randomWord(10)+" "+"A";
        String definitionNameSpace = randomWord(3);
        String sourceFolderPath = new File(getDownloadsFolder()).getAbsolutePath()+"/"+randomWord(10);
        Area baseA1 = new Area(randomWord(5), "//body/div[1]", 1, 0, "base");
        Area baseA2 = new Area(randomWord(3), "//body/div[3]", 1, 0, "base");
        Area homeA1 = new Area(randomWord(4), "//body/div[1]//div[contains(., 'Level 2-1')]", 1, 0, "home");
        View baseV1 = new View(randomWord(6), "jnt:bootstrapMainContent", baseA2.getXpath()+"/div[1]", 1, 0, baseA2.getTemplateName());
        View homeV1 = new View(randomWord(10), "jnt:html", homeA1.getXpath()+"/div[1]", 1, 0, homeA1.getTemplateName());

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        selectArea(baseA1);
        selectArea(baseA2);
        selectView(baseV1);
        switchToTemplate("home");
        selectArea(homeA1);
        selectView(homeV1);
        generateModule(moduleName, definitionNameSpace, sourceFolderPath, true);
        softAssert.assertTrue(
                isModuleStarted(moduleName.toLowerCase().replace(" ", "-")),
                "Module '"+moduleName+"' did not start after generation.");
        checkJntTemplateFile(softAssert, sourceFolderPath, moduleName);
        checkAreaFile(softAssert, sourceFolderPath, baseA1, definitionNameSpace, moduleName);
        checkAreaFile(softAssert, sourceFolderPath, baseA2, definitionNameSpace, moduleName);
        checkAreaFile(softAssert, sourceFolderPath, homeA1, definitionNameSpace, moduleName);
        checkViewFile(softAssert, sourceFolderPath, baseV1, moduleName);
        checkViewFile(softAssert, sourceFolderPath, homeV1, moduleName);
        softAssert.assertAll();
    }

    @Test(enabled = false)
    public void manageAssetsAndGenerate(){

    }

    protected void checkJntTemplateFile(SoftAssert   softAssert,
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
    }

    protected void checkAreaFile(SoftAssert    softAssert,
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
    }

    protected void checkViewFile(SoftAssert    softAssert,
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
    }
}
