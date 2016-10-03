package org.jahia.modules.templateimporter.tests.modulegeneration.tests;

import org.jahia.modules.templateimporter.tests.TemplateImporterRepository;
import org.jahia.modules.templateimporter.tests.businessobjects.Area;
import org.jahia.modules.templateimporter.tests.businessobjects.Component;
import org.jahia.modules.templateimporter.tests.businessobjects.View;
import org.jahia.modules.tests.utils.SoftAssertWithScreenshot;
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
        boolean baseAreaOneFoundInTemplate = findTextInFile(templateFile, "<template:area path='pageContent'></template:area>");
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
        Area baseA1 = new Area(randomWord(5), "//body/div[1]", 1, 0, "base");
        Area baseA2 = new Area(randomWord(3), "//body/div[3]", 1, 0, "base");
        Area userA1 = new Area(randomWord(4), "//body/div[1]//div[contains(., 'Level 2-1')]", 1, 0, userCreatedTemplateName);
        View baseV1 = new View(randomWord(6), "jnt:bootstrapMainContent", baseA2.getXpath()+"/div[1]", 1, 0, baseA2.getTemplateName());
        View userV1 = new View(randomWord(10), "jnt:html", userA1.getXpath()+"/div[1]", 1, 0, userA1.getTemplateName());
        boolean userA1FileContainsArea = false;
        boolean userV1FileContainsView = false;

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
        checkJntTemplateFileExist(softAssert, sourceFolderPath, moduleName);
        checkAreaFile(softAssert, sourceFolderPath, baseA1, definitionNameSpace, moduleName);
        checkAreaFile(softAssert, sourceFolderPath, baseA2, definitionNameSpace, moduleName);
        File userA1File = checkAreaFile(softAssert, sourceFolderPath, userA1, definitionNameSpace, moduleName);
        if(userA1File != null) {
            userA1FileContainsArea = findTextInFile(userA1File, "<template:area path='" + userA1.getName() + "'/>");
        }
        softAssert.assertTrue(
                userA1FileContainsArea,
                "User template area JSP file misses area tag. Name: " + userA1.getName() + ", XPath: " + userA1.getXpath()
        );
        checkViewFile(softAssert, sourceFolderPath, baseV1, moduleName);
        File userV1File = checkViewFile(softAssert, sourceFolderPath, userV1, moduleName);
        if (userV1File != null) {
            userV1FileContainsView = findTextInFile(userV1File, "<div  class=\"\"  >Level 3-2</div>");
        }
        softAssert.assertTrue(
                userV1FileContainsView,
                "User template view JSP file misses html tags. Name: " + userV1.getName() + ", XPath: " + userV1.getXpath()
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
        checkJntTemplateFileExist(softAssert, sourceFolderPath, moduleName);
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
        checkJntTemplateFileExist(softAssert, sourceFolderPath, moduleName);
        checkAreaFile(softAssert, sourceFolderPath, baseA1, definitionNameSpace, moduleName);
        checkAreaFile(softAssert, sourceFolderPath, baseA2, definitionNameSpace, moduleName);
        checkAreaFile(softAssert, sourceFolderPath, homeA1, definitionNameSpace, moduleName);
        checkViewFile(softAssert, sourceFolderPath, baseV1, moduleName);
        checkViewFile(softAssert, sourceFolderPath, homeV1, moduleName);
        softAssert.assertAll();
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
