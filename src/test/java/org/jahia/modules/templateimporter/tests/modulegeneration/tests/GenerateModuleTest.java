package org.jahia.modules.templateimporter.tests.modulegeneration.tests;

import org.jahia.modules.templateimporter.tests.TemplateImporterRepository;
import org.jahia.modules.tests.utils.SoftAssertWithScreenshot;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.io.File;
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
        checkAreaFile(softAssert, sourceFolderPath, baseA1, definitionNameSpace);
        checkAreaFile(softAssert, sourceFolderPath, baseA2, definitionNameSpace);
        checkAreaFile(softAssert, sourceFolderPath, homeA1, definitionNameSpace);
        checkViewFile(softAssert, sourceFolderPath, baseV1);
        checkViewFile(softAssert, sourceFolderPath, homeV1);
        softAssert.assertAll();
    }

    protected void checkJntTemplateFile(SoftAssert   softAssert,
                                        String       sourceFolderPath,
                                        String       moduleName){
        String jntTemplateFileName = "template."+moduleName+".jsp";
        boolean jntTemplateExist = false;

        File[] files = findFilesOrDirectories(sourceFolderPath+"/src/main/resources/jnt_template/html", jntTemplateFileName, "jsp");
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
                                 String        definitionNameSpace){
        String areaFileName = "tiLayoutComponent."+area.getTemplateName()+"_"+area.getName()+".jsp";
        String areaFolderPath = sourceFolderPath+"/src/main/resources/"+definitionNameSpace+"nt_tiLayoutComponent/html";
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
                                 View          view){
        String viewFileName = view.getNodeType().replace("jnt:", "")+"."+view.getTemplateName()+"_"+view.getName()+".jsp";
        String viewFolderPath = sourceFolderPath+"/src/main/resources/"+view.getNodeType().replace(":", "_")+"/html";
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

    /**
     * Check if module started
     * @param moduleID String, Module ID
     * @return True if module is started
     */
    protected boolean isModuleStarted(String moduleID){
        return checkModuleState(moduleID).contains("STARTED");
    }

    protected void selectView(View view) {
        selectView(view.getName(), view.getNodeType(), view.getXpath(), view.getxOffset(), view.getyOffset());
    }

    protected void selectArea(Area area){
        selectArea(area.getName(), area.getXpath(), area.getxOffset(), area.getyOffset());
    }

    /**
     * Click on 'Generate module', fill in all the fields, click create or cancel
     * @param moduleName String, Module name
     * @param definitionNameSpace String Definition name space
     * @param sourcesFolder String, absolute path to folder where you want your modulu's sources
     * @param reallyGenerate true to click 'Create', false to click 'Cancel' after all fields are filled.
     */
    protected void generateModule(String    moduleName,
                                  String    definitionNameSpace,
                                  String    sourcesFolder,
                                  boolean   reallyGenerate){
        WebElement generateModuleBtn = findByXpath("//button[@ng-click='cmc.showCreateDialog($event)']");
        clickOn(generateModuleBtn);
        WebElement moduleNameField = findByXpath("//input[@name='moduleName']");
        WebElement definitionNameSpaceField = findByXpath("//input[@name='nameSpace']");
        WebElement sourcesFolderField = findByXpath("//input[@name='sources']");
        WebElement createBtn = findByXpath("//button[@ng-click='create()']");
        WebElement cancelBtn = findByXpath("//button[@ng-click='cancel()']");
        waitForElementToStopMoving(moduleNameField);
        typeInto(moduleNameField, moduleName);
        typeInto(definitionNameSpaceField, definitionNameSpace);
        typeInto(sourcesFolderField, sourcesFolder);
        if(reallyGenerate){
            waitForElementToBeEnabled(createBtn, 5);
            clickOn(createBtn);
            waitForElementToBeInvisible(createBtn);
            Long start = new Date().getTime();
            waitForGlobalSpinner(2, 180);
            Long finish = new Date().getTime();
            WebElement successToast = findByXpath("//div[contains(@class, 'toast-title')][contains(., 'All done!!! Your module is ready!!!')]");
            Assert.assertNotNull(successToast, "Success toast not found after module generation. " +
                    "Global spinner was visible for at least "+(finish-start)+" milliseconds.");
            clickOn(successToast);
        }else{
            waitForElementToBeEnabled(cancelBtn, 5);
            clickOn(cancelBtn);
            waitForElementToBeInvisible(cancelBtn);
        }
    }

    /**
     * Business Object, representation of "Area selection"
     */
    private class Area{
        private String name;
        private String xPath;
        private String templateName;
        private int xOffset;
        private int yOffset;

        Area(String name, String xPath, int xOffset, int yOffset, String templateName){
            this.name = name;
            this.xPath = xPath;
            this.xOffset = xOffset;
            this.yOffset = yOffset;
            this.templateName = templateName;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getXpath() {
            return xPath;
        }

        public String getTemplateName() {
            return templateName;
        }

        public int getxOffset() {
            return xOffset;
        }

        public int getyOffset() {
            return yOffset;
        }
    }

    /**
     * Business Object, representation of "View selection"
     */
    private class View extends Area{
        private String nodeType;

        View(String viewName, String nodeType, String xPathToView, int xOffset, int yOffset, String templateName){
            super(viewName, xPathToView, xOffset, yOffset, templateName);
            this.nodeType = nodeType;
        }

        public String getNodeType() {
            return nodeType;
        }

        public void setNodeType(String nodeType) {
            this.nodeType = nodeType;
        }
    }
}
