package org.jahia.modules.templateimporter.tests;

import org.apache.commons.io.FileUtils;
import org.jahia.modules.templateimporter.tests.businessobjects.Area;
import org.jahia.modules.templateimporter.tests.businessobjects.Component;
import org.jahia.modules.templateimporter.tests.businessobjects.Selection;
import org.jahia.modules.tests.core.ModuleTest;
import org.jahia.modules.tests.utils.CustomExpectedConditions;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.HashMap;

/**
 * Created by sergey on 2016-06-16.
 */
public class TemplateImporterRepository extends ModuleTest {
    protected static final String SELECTED_AREA_MARK = "AreaSelection";
    protected static final String SELECTED_COMPONENT_MARK = "ComponentSelection";
    protected static final String CURRENT_TEMPLATE_MARK = "md-raised";
    protected static final String DOM_OUTLINE_MARK = "DomOutlined";

    /**
     * Open list of projects. (Just iframe)
     * @param locale Is used as part of url to projects list.
     */
    protected void goToProjectsList(String locale){
        getDriver().get(getPath("/cms/adminframe/default/" + locale + "/settings.demo-starter.html"));
        waitForGlobalSpinner(2, 45);
    }

    /**
     * Opens projects list and imports a new project, asserts that new project is listed in 'projects library'
     * @param locale Is used as part of url to projects list
     * @param projectName String. Name of the project you are creating
     * @param projectDescription String, Description of project you are creating
     * @param testProjectFileName String, name of .zip file to import. Zip file should be inside testData.testProjects folder.
     * @return 'Success' if form created successfully, otherwise null.
     */
    protected void importProject(String locale,
                                 String projectName,
                                 String projectDescription,
                                 String testProjectFileName){
        String zipFilePath = new File("src/test/resources/testData/testProjects/"+testProjectFileName).getAbsolutePath();
        String jsToEnableInput = "function getElementByXpath(path) {" +
                "return document.evaluate(path, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;" +
                "}" +
                "fileInput = getElementByXpath(\"//label[input[@type='file']]\");" +
                "fileInput.setAttribute(\"style\", \"\");";

        goToProjectsList(locale);
        waitForGlobalSpinner(1, 45);
        WebElement importProjectButton = findByXpath("//button[@ng-click='pc.importProject($event)']");
        waitForElementToStopMoving(importProjectButton);
        clickOn(importProjectButton);
        WebElement projectNameField = findByXpath("//input[@name='projectName']");
        WebElement projectDescriptionField = findByXpath("//textarea[@ng-model='ipc.projectDescription']");
        WebElement projectZipFileField = findByXpath("//input[@type='file']");
        WebElement importButton = findByXpath("//button[@ng-click='ipc.submit()']");
        WebElement dialogueBox = findByXpath("//div[@class='md-dialog-container ng-scope']");
        try {
            createWaitDriver(5, 500).until(CustomExpectedConditions.javascriptWithoutException(jsToEnableInput));
        }catch(TimeoutException e){
            getLogger().error("Hidden Input field might be not activated. JavaScript execution still produces errors even after 5 - 7 attempts.");
        }
        typeInto(projectNameField, projectName);
        typeInto(projectDescriptionField, projectDescription);
        projectZipFileField.sendKeys(zipFilePath);

        waitForElementToBeEnabled(importButton, 7);
        Assert.assertEquals(
                importButton.isEnabled(),
                true,
                "All fields are filled, but 'Import' button is disabled. Cannot import a project. Check if project name is unique.");
        clickOn(importButton);
        //Increase second parameter here if import of large project fails
        waitForGlobalSpinner(1, 45);
        waitForElementToDisappear(dialogueBox, 7);
        waitForElementToDisappear(importButton, 7);
        Assert.assertEquals(
                isVisible(By.xpath("//md-card-title-text/span[contains(., '"+projectName+"')]"), 20),
                true,
                "New project name ("+projectName+")is not found in projects list.");
    }

    protected String returnJavascriptResponse(String javascript){
        String jsResponse = null;
        String javascriptToExecute = "return "+javascript;
        try {
            jsResponse = (String) ((JavascriptExecutor) driver).executeScript(javascriptToExecute);
        } catch (Exception e) {
            e.getMessage();
            e.printStackTrace();
        }
        return jsResponse;
    }

    /**
     * Waits for several layers of global spinner to appear and than disappear
     * @param secondsToWaitSpinnerAppear int, amount of seconds to wait for global spinner elements to appear
     * @param secondsToWaitSpinnerDisappear int, amount of seconds to wait for global spinner elements to disappear
     */
    protected void waitForGlobalSpinner(int secondsToWaitSpinnerAppear,
                                        int secondsToWaitSpinnerDisappear) {
        List<WebElement> spinners = new LinkedList<WebElement>();

        try {
            WebElement spinner = null;
            WebElement tiOverlay;
            WebElement tiOverlayContent;
            try {
                spinner = createWaitDriver(secondsToWaitSpinnerAppear, 300).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='ti-global-spinner']")));

            } catch (TimeoutException ee) {
            }
            tiOverlay = noWaitingFindBy(By.xpath("//div[@class='ti-overlay']"));
            tiOverlayContent = noWaitingFindBy(By.xpath("//div[@class='ti-overlay-content']"));
            spinners.add(spinner);
            spinners.add(tiOverlay);
            spinners.add(tiOverlayContent);

            for (WebElement elementToWait : spinners) {
                if (elementToWait != null) {
                    waitForElementToBeInvisible(elementToWait, secondsToWaitSpinnerDisappear);
                }
            }
        } catch (TimeoutException e) {
        }
    }

    /**
     * Create new template by clicking "Create new template" button
     * @param templateName Name of the new template
     * @param pageFileName Page filename to associate with template
     */
    protected void createNewTemplate(String templateName,
                                     String pageFileName){
        WebElement layoutBtn = findByXpath("//md-icon[text()='view_quilt']/ancestor::button");
        clickOn(layoutBtn);
        WebElement createNewTemplateBtn = findByXpath("//button[@ng-click='project.createNewTemplate($event)']");
        waitForElementToStopMoving(createNewTemplateBtn);
        clickOn(createNewTemplateBtn);

        WebElement templateNameField = findByXpath("//input[@name='pageName']");
        WebElement pageSelectDropdown = findByXpath("//md-select[@ng-model='cpc.selectedTemplate']");
        WebElement createBtn = findByXpath("//button[@ng-click='cpc.create()']");

        waitForElementToStopMoving(pageSelectDropdown);
        clickOn(pageSelectDropdown);
        WebElement pageOption = findByXpath("//md-option[@value='"+pageFileName+"']");
        waitForElementToBeEnabled(pageOption, 5);
        waitForElementToStopMoving(pageOption);
        clickOn(pageOption);
        typeInto(templateNameField, templateName);
        waitForElementToBeEnabled(createBtn, 5);
        clickOn(createBtn);
        waitForGlobalSpinner(1, 30);
        waitForElementToBeInvisible(createBtn);
        shortSleep();

        WebElement newTemplateTab = findByXpath("//ti-tab[contains(., '"+templateName+"')]");
        Assert.assertNotNull(newTemplateTab, "Cannot find tab with new template name '"+templateName+"' after creating new template.");
        switchToProjectFrame();
        WebElement body = findByXpath("//body");
        waitForElementToStopMoving(body);
        String iFrameUtl = returnJavascriptResponse("document.location.pathname");
        switchToDefaultContent();

        boolean sourceContainsPageName = iFrameUtl.contains(pageFileName);
        Assert.assertTrue(sourceContainsPageName, "Iframe's SRC attribute does not contain page filename ("+pageFileName+"). SRC is: "+iFrameUtl);
    }

    /**
     * Delete all projects
     * @return Amount of deleted projects
     */
    protected int deleteAllProjects() {
        int projectsRemoved = 0;
        List<String> projectNames = new LinkedList<String>();

        try {
            List<WebElement> projectsBeforeDeletion = createWaitDriver(2, 300)
                    .until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//md-card-title-text/span[@ng-click='pc.seeProject($index)']")));
            for(WebElement projectTitle:projectsBeforeDeletion){
                projectNames.add(projectTitle.getText());
            }
        } catch (TimeoutException e) {
            return projectsRemoved;
        }

        for(String name:projectNames){
            if(deleteProject(name)){
                projectsRemoved++;
            }
        }

        return projectsRemoved;
    }

    private void deleteAllProjectsFast(){
        String jsToDeleteProjects = "function removeAllProjects() {\n" +
                "  var trashcans = $('button[aria-label=\"Remove\"]');\n" +
                "  var projects = []\n" +
                "  trashcans.each(function(index, item){\n" +
                "    item.onclick = function (event) {\n" +
                "      var confirmRemovalButton = $('.md-confirm-button');\n" +
                "      if (confirmRemovalButton.length > 0) {\n" +
                "        confirmRemovalButton.click();\n" +
                "      }\n" +
                "      if (projects.length > 0) {\n" +
                "        projects.pop().click();\n" +
                "      }\n" +
                "    };\n" +
                "    projects.push(item);\n" +
                "  });\n" +
                "  if (projects.length > 0) {\n" +
                "    projects.pop().click();\n" +
                "  }\n" +
                "}\n" +
                "removeAllProjects();";

        executeScriptWithJavascript(jsToDeleteProjects);
        waitForGlobalSpinner(2, 60);
        shortSleep();
    }

    /**
     * Deletes project with given name. Throws assertion error if you try to delete project that does not exist
     * @param projectName String, name of project to delete
     * @return True if project was deleted, false if still visible after deletion.
     */
    protected boolean deleteProject(String projectName){
        boolean isProjectDeleted;
        WebElement proectToDelete = findByXpath("//md-card[./*/md-card-title-text[contains(., '"+projectName+"')]]//button[@ng-click='pc.removeProject($event, p)']");

        Assert.assertNotNull(proectToDelete, "Remove button to delete a project '"+projectName+"' not found. Does project exist?");
        try{
            clickOn(proectToDelete);
        }catch (WebDriverException e){
            while(!isVisible(By.xpath("//button[@ng-click='dialog.hide()']"), 1)) {
                try {
                    new Actions(getDriver()).sendKeys(Keys.ARROW_UP).click(proectToDelete).build().perform();
                }catch(WebDriverException ee){}
            }
        }

        WebElement confirmRemovalBtn = findByXpath("//button[@ng-click='dialog.hide()']");
        waitForElementToStopMoving(confirmRemovalBtn);
        clickOn(confirmRemovalBtn);
        waitForElementToDisappear(confirmRemovalBtn, 10);
        waitForGlobalSpinner(1, 45);
        isProjectDeleted = waitForElementToBeInvisible(proectToDelete, 3);

        return isProjectDeleted;
    }

    /**
     * This method generates a random word for a given length.
     *
     * @param length Desired word length
     * @return String. word.
     */
    protected String randomWord(int length) {
        Random random = new Random();
        StringBuilder word = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            word.append((char) ('a' + random.nextInt(26)));
        }
        return word.toString();
    }

    /**
     * Click on 'Open Project' button and select page for base template
     * @param projectName String, name of the project
     * @param baseTemplatePageName String, filename of desired page. Example: Index.html
     */
    protected void openProjectFirstTime(String  projectName,
                                        String  baseTemplatePageName){
        WebElement editProjectBtn = findByXpath("//md-card-title-text[contains(., '"+projectName+"')]/ancestor::md-card//button[@ng-click='pc.seeProject($index)']");

        clickOn(editProjectBtn);
        waitForGlobalSpinner(1, 30);
        WebElement importBtn = findByXpath("//button[@ng-click='sbtc.submit()']");
        WebElement baseTemplateSelector = findByXpath("//md-select[@ng-model='sbtc.project.baseTemplate']");
        waitForElementToStopMoving(baseTemplateSelector);
        clickOn(baseTemplateSelector);
        WebElement baseTemplateOption = findByXpath("//md-option[@value='"+baseTemplatePageName+"']");
        waitForElementToBeEnabled(baseTemplateOption, 3);
        waitForElementToStopMoving(baseTemplateOption);
        clickOn(baseTemplateOption);
        waitForElementToBeEnabled(importBtn, 7);
        clickOn(importBtn);
        waitForElementToBeInvisible(importBtn);
        waitForGlobalSpinner(2, 45);
        switchToProjectFrame();
        WebElement body = findByXpath("//body");
        waitForElementToStopMoving(body);
        switchToDefaultContent();
    }

    /**
     * Selects an area in current template with given parameters. Will switch to the iFrame before selection and switch
     * back after. Checks that selected area has 'AreaSelection' class after selection.
     * @param areaName String, name of the area
     * @param xPath String, XPath to the element you want to select
     * @param xOffset int, Horizontal offset in pixels, from the <u>left</u> border of element.
     *                Pass negative value to move left. Pass 0 to use calculated center of the element (Default click behaviour).
     * @param yOffset int, Vertical offset in pixels, from the <u>top</u> border of element.
     *                Pass negative value to move left. Pass 0 to use calculated center of the element (Default click behaviour).
     */
    protected void selectArea(String    areaName,
                              String    xPath,
                              int       xOffset,
                              int       yOffset){
        rightMouseClick(xPath, xOffset, yOffset);
        WebElement menuAreaBtn = findByXpath("//div[@ng-click='rmc.canBeArea && rmc.showArea()']");
        waitForElementToStopMoving(menuAreaBtn);
        clickOn(menuAreaBtn);
        WebElement areaNameField = findByXpath("//input[@name='areaName']");
        WebElement selectBtn = findByXpath("//button[@ng-click='sac.area.ok()']");

        waitForElementToStopMoving(areaNameField);
        typeInto(areaNameField, areaName);
        waitForElementToBeEnabled(selectBtn, 5);
        clickOn(selectBtn);
        waitForElementToBeInvisible(selectBtn);

        Assert.assertTrue(
                checkIfAreaSelected(xPath),
                "Area was not selected. Target element does not have '"+SELECTED_AREA_MARK+"' class." + " XPath: "+xPath);
    }

    protected void selectArea(Area area){
        selectArea(area.getName(), area.getXpath(), area.getxOffset(), area.getyOffset());
    }

//    /**
//     * Selects a view in current template with given parameters. Will switch to the iFrame before selection and switch
//     * back after. Checks that selected area has 'ViewSelection' class after selection.
//     * @param viewName String, name of the area
//     * @param xPath String, XPath to the element you want to select
//     * @param xOffset int, Horizontal offset in pixels, from the <u>left</u> border of element.
//     *                Pass negative value to move left. Pass 0 to use calculated center of the element (Default click behaviour).
//     * @param yOffset int, Vertical offset in pixels, from the <u>top</u> border of element.
//     *                Pass negative value to move left. Pass 0 to use calculated center of the element (Default click behaviour).
//     * @param errorMsg Error message that will be prepended to the assertion error message. Describe use case here. Example:
//     *                Reselecting view after deletion
//     */
//    protected void selectView(String    viewName,
//                              String    nodeType,
//                              String    xPath,
//                              int       xOffset,
//                              int       yOffset,
//                              String    errorMsg){
//        rightMouseClick(xPath, xOffset, yOffset);
//        WebElement menuViewBtn = findByXpath("//div[@ng-click='rmc.canBeView && rmc.showView()']");
//        waitForElementToStopMoving(menuViewBtn);
//        clickOn(menuViewBtn);
//
//        WebElement viewNameField = findByXpath("//input[@name='viewName']");
//        WebElement selectViewBtn = findByXpath("//button[@ng-click='svc.view.ok()']");
//        WebElement advsncedCheckbox = findByXpath("//md-checkbox[@ng-model='svc.view.advancedNodeTypeSelection']");
//
//        waitForElementToStopMoving(viewNameField);
//        typeInto(viewNameField, viewName);
//        clickOn(advsncedCheckbox);
//        WebElement nodeTypeField = findByName("nodeTypeSelection");
//        waitForElementToBeVisible(nodeTypeField);
//        waitForElementToStopMoving(nodeTypeField);
//        nodeTypeField.click();
//        nodeTypeField.clear();
//        typeInto(nodeTypeField, nodeType);
//        clickOn(By.xpath("//div[@ng-click='tc.select(item)'][contains(., '"+nodeType+"')]"));
//        waitForElementToBeEnabled(selectViewBtn, 5);
//        clickOn(selectViewBtn);
//        waitForElementToBeInvisible(selectViewBtn);
//
//        Assert.assertTrue(
//                checkIfViewSelected(xPath),
//                errorMsg+". View was not selected. Target element does not have '"+SELECTED_VIEW_MARK+"' class. Target element  XPath: "+
//                        xPath+". Are you sure Node Type " +nodeType+" exist?");
//    }
//
//    /**
//     * Selects a view in current template with given parameters. Will switch to the iFrame before selection and switch
//     * back after. Checks that selected area has 'ViewSelection' class after selection.
//     * @param viewName String, name of the area
//     * @param xPath String, XPath to the element you want to select
//     * @param xOffset int, Horizontal offset in pixels, from the <u>left</u> border of element.
//     *                Pass negative value to move left. Pass 0 to use calculated center of the element (Default click behaviour).
//     * @param yOffset int, Vertical offset in pixels, from the <u>top</u> border of element.
//     *                Pass negative value to move left. Pass 0 to use calculated center of the element (Default click behaviour).
//     */
//    protected void selectView(String    viewName,
//                              String    nodeType,
//                              String    xPath,
//                              int       xOffset,
//                              int       yOffset){
//        selectView(viewName, nodeType, xPath, xOffset, yOffset, "");
//    }
//
//    protected void selectView(View view) {
//        selectView(view.getName(), view.getNodeType(), view.getXpath(), view.getxOffset(), view.getyOffset());
//    }

    protected void selectComponent(Component    component,
                                   String       errorMsg){
        String xPath = component.getXpath();
        int xOffset = component.getxOffset();
        int yOffset = component.getyOffset();
        String areaName = component.getAreaName();
        String viewName = component.getViewName();
        String componentName = component.getName();

        rightMouseClick(xPath, xOffset, yOffset);
        WebElement menuComponentBtn = findByXpath("//div[@ng-click='rmc.canBeComponent && rmc.showComponent()']");
        waitForElementToStopMoving(menuComponentBtn);
        clickOn(menuComponentBtn);

        WebElement areaNameField = findByName("areaName");
        WebElement viewNameField = findByName("viewName");
        WebElement componentNameField = findByName("componentName");

        WebElement okBtn = findByXpath("//button[@ng-click='scc.ok()']");

        waitForElementToStopMoving(areaNameField);
        typeInto(areaNameField, areaName);
        typeInto(componentNameField, componentName);
        typeInto(viewNameField, viewName);

        clickOn(okBtn);
        waitForElementToBeInvisible(okBtn);

        Assert.assertTrue(
                checkIfComponentSelected(component, new SoftAssert(), true, ""),
                errorMsg+". Component was not selected. Target element does not have '"+SELECTED_COMPONENT_MARK+"' class." + " XPath: "+xPath);
    }

    protected void selectComponent(Component    component,
                                   Component    componentWhosNameWeReuse,
                                   String       errorMsg){
        String xPath = component.getXpath();
        int xOffset = component.getxOffset();
        int yOffset = component.getyOffset();
        String areaName = component.getAreaName();
        String viewName = component.getViewName();
        String componentName = componentWhosNameWeReuse.getName();

        rightMouseClick(xPath, xOffset, yOffset);
        WebElement menuComponentBtn = findByXpath("//div[@ng-click='rmc.canBeComponent && rmc.showComponent()']");
        waitForElementToStopMoving(menuComponentBtn);
        clickOn(menuComponentBtn);

        WebElement areaNameField = findByName("areaName");
        WebElement viewNameField = findByName("viewName");
        WebElement componentNameSelector = findByXpath("//md-select[@ng-model='scc.view.componentType']");

        WebElement okBtn = findByXpath("//button[@ng-click='scc.ok()']");

        waitForElementToStopMoving(areaNameField);
        typeInto(areaNameField, areaName);
        typeInto(viewNameField, viewName);
        clickOn(componentNameSelector);
        WebElement componentNameOption = findByXpath("//md-option[@value='"+componentName+"']");
        clickOn(componentNameOption);

        clickOn(okBtn);
        waitForElementToBeInvisible(okBtn);

        Assert.assertTrue(
                checkIfComponentSelected(component, new SoftAssert(), true, ""),
                errorMsg+". Component was not selected. Target element does not have '"+SELECTED_COMPONENT_MARK+"' class." + " XPath: "+xPath);
    }

    protected void switchToProjectFrame(){
        createWaitDriver(10, 300).until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.xpath("//iframe[@id='tiProjectFrame']")));
    }

    protected void switchToDefaultContent(){
        getDriver().switchTo().defaultContent();
    }

    protected void switchToTemplate(String templateName) {
        WebElement templateTabToClick = findByXpath("//ti-tab[contains(., '"+templateName+"')]/div[@ng-click='updateNavState();']");
        WebElement templateTabToCheck = findByXpath("//ti-tab[contains(., '"+templateName+"')]");
        boolean isCurrentlyOpen = templateTabToCheck.getAttribute("class").contains(CURRENT_TEMPLATE_MARK);

        if (!isCurrentlyOpen) {
            clickOn(templateTabToClick);
            waitForGlobalSpinner(1, 30);
            waitForElementAttributeToContainValue(templateTabToCheck, 3, "class", CURRENT_TEMPLATE_MARK);

            templateTabToCheck = findByXpath("//ti-tab[contains(., '" + templateName + "')]");
            Assert.assertNotNull(templateName, "Template with name " + templateName + " not found after clicking on it.");
            Assert.assertTrue(templateTabToCheck.getAttribute("class").contains(CURRENT_TEMPLATE_MARK),
                    "Switching to template '" + templateName + "' failed. After clicking on template tab it did not become active." +
                            "(Does not have tiRaised class.). Attempts: ");
            switchToProjectFrame();
            WebElement body = findByXpath("//body");
            waitForElementToStopMoving(body);
            switchToDefaultContent();
        }
    }

    /**
     * Performs right click on element with given xPath and offsets. Will do all the iFrame switching for you.
     * @param xPath String, xPath to your target element
     * @param xOffset int, Horizontal offset in pixels, from the <u>left</u> border of element.
     *                Pass negative value to move left. Pass 0 to use calculated center of the element (Default click behaviour).
     * @param yOffset int, Vertical offset in pixels, from the <u>top</u> border of element.
     *                Pass negative value to move left. Pass 0 to use calculated center of the element (Default click behaviour).
     */
    protected void rightMouseClick(String   xPath,
                                   int      xOffset,
                                   int      yOffset){
        switchToProjectFrame();
        WebElement area = findByXpath(xPath);
        Assert.assertNotNull(area, "Cannot find an element that you are trying to right click on. XPath: '"+xPath+"'.");

        if(xOffset == 0){
            xOffset = area.getSize().getWidth()/2;
        }
        if(yOffset == 0){
            yOffset =  area.getSize().getHeight()/2;
        }
        waitForElementToBeVisible(area);
        waitForElementToStopMoving(area);
        new Actions(getDriver()).moveToElement(area, xOffset, yOffset).contextClick().build().perform();
        switchToDefaultContent();
    }

    /**
     * Removes area. Click on area than click 'Remove' and assert that area removed.
     * @param xPath String, xPath to your target element
     * @param xOffset int, Horizontal offset in pixels, from the <u>left</u> border of element.
     *                Pass negative value to move left. Pass 0 to use calculated center of the element (Default click behaviour).
     * @param yOffset int, Vertical offset in pixels, from the <u>top</u> border of element.
     *                Pass negative value to move left. Pass 0 to use calculated center of the element (Default click behaviour).
     * @param errorMsg Error message that will be passed to assert
     */
    protected  void removeArea(String   xPath,
                               int      xOffset,
                               int      yOffset,
                               String   errorMsg){
        boolean isSelected = checkIfAreaSelected(xPath, new SoftAssert(), true);

        Assert.assertTrue(isSelected, "Area that you are trying to remove is not selected.");
        rightMouseClick(xPath, xOffset, yOffset);
        WebElement removeBtn = findByXpath("//div[@ng-click='rmc.removable && rmc.remove()']");
        waitForElementToStopMoving(removeBtn);
        clickOn(removeBtn);
        waitForElementToBeInvisible(removeBtn);
        Assert.assertFalse(
                checkIfAreaSelected(xPath),
                errorMsg+". Area was not removed. XPath:'"+xPath+"'. Horizontal offset:"+xOffset+", Vertical offset:"+yOffset);
    }

    protected  void removeArea(String   xPath,
                               int      xOffset,
                               int      yOffset){
        removeArea(xPath, xOffset, yOffset, "");
    }

    /**
     *
     * @param selection
     */
    protected  void removeSelection(Selection   selection){
        boolean isSelected;
        String xPath = selection.getXpath();

        if(selection.isArea()){
            isSelected = checkIfAreaSelected(xPath);
        }else{
            isSelected = checkIfComponentSelected((Component)selection, new SoftAssert(), true, "");
        }

        Assert.assertTrue(isSelected, "Selection that you are trying to remove is not selected.");
        rightMouseClick(xPath, selection.getxOffset(), selection.getyOffset());
        WebElement removeBtn = findByXpath("//div[@ng-click='rmc.removable && rmc.remove()']");
        clickOn(removeBtn);
        waitForElementToBeInvisible(removeBtn);
        boolean isStillVisible;
        if(selection.isArea()){
            isStillVisible = checkIfAreaSelected(xPath);
        }else{
            isStillVisible = checkIfComponentSelected((Component)selection, new SoftAssert(), true, "");
        }
        Assert.assertFalse(
                isStillVisible,
                "Selection was not removed. XPath:'"+xPath+"'. Horizontal offset:"+selection.getxOffset()+", Vertical offset:"+selection.getyOffset());
    }

    /**
     * Check if area is selected (has AreaSelection class)
     * @param xPath String, XPath to the area
     * @param softAssert Instance of SoftAssert you are working with. Will fail if visibility result is not expected.
     * @param expectedResult boolean, your expectation if area should be selected.
     * @return True if area selected, otherwise false
     */
    protected boolean checkIfAreaSelected(String        xPath,
                                          SoftAssert    softAssert,
                                          boolean       expectedResult,
                                          String        errorMsg) {
        switchToProjectFrame();
        WebElement area = findByXpath(xPath);
        softAssert.assertNotNull(area, errorMsg+". Cannot find an element that you are trying to check if selected as area. XPath: '" + xPath + "'.");

        boolean isAreaSelected = area.getAttribute("class").contains(SELECTED_AREA_MARK);
        switchToDefaultContent();
        softAssert.assertEquals(
                isAreaSelected,
                expectedResult,
                errorMsg+". Assertion if element: '" + xPath + "' has class '" + SELECTED_AREA_MARK + "' (is selected) Failed");
        return isAreaSelected;
    }

    protected boolean checkIfComponentSelected(Component    component,
                                               SoftAssert   softAssert,
                                               boolean      expectedResult,
                                               String       errorMsg){
        String xPath = component.getXpath();

        switchToProjectFrame();
        WebElement selection = findByXpath(xPath);
        softAssert.assertNotNull(selection, errorMsg+". Cannot find an element that you are trying to check if selected as component. XPath: '" + xPath + "'.");

        boolean isSelectionSelected = selection.getAttribute("class").contains(SELECTED_COMPONENT_MARK);
        switchToDefaultContent();
        softAssert.assertEquals(
                isSelectionSelected,
                expectedResult,
                errorMsg+". Assertion if element: '" + xPath + "' has class '" + SELECTED_COMPONENT_MARK + "' (is selected) Failed");
        return isSelectionSelected;
    }

    protected boolean checkIfAreaSelected(String        xPath,
                                          SoftAssert    softAssert,
                                          boolean       expectedResult) {
        return checkIfAreaSelected(xPath, softAssert, expectedResult, "");
    }

    /**
     * Check if area is selected (has ViewSelection class)
     * @param xPath String, XPath to the area
     * @return True if area selected, otherwise false
     */
    protected boolean checkIfAreaSelected(String xPath){
        return checkIfAreaSelected(xPath, new SoftAssert(), false);
    }


    protected void removeTemplate(String templateName){
        WebElement templateTab = findByXpath("//ti-tab[contains(., '"+templateName+"')]");
        Assert.assertNotNull(templateTab, "Cannot find template tab you are trying to remove: '"+templateName+"'");
        WebElement trashBinIcon = findByXpath("//ti-tab[contains(., '"+templateName+"')]/div[@ng-click='removePage()']");
        clickOn(trashBinIcon);
        WebElement confirmDeletionBtn = findByXpath("//button[@ng-click='dialog.hide()']");
        clickOn(confirmDeletionBtn);
        waitForElementToBeInvisible(confirmDeletionBtn);
        Assert.assertTrue(waitForElementToBeInvisible(templateTab, 5), "Template '"+templateName+"' was not deleted. Template's tab is still visible.");
        switchToProjectFrame();
        WebElement body = findByXpath("//body");
        waitForElementToStopMoving(body);
        switchToDefaultContent();
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
            Long moduleGenerationStart = new Date().getTime();
            Long phaseStart = new Date().getTime();
            WebElement continueBtn = findByXpath("//button[@ng-click='esc.nextStep()']");
            Assert.assertTrue(
                    isVisible(By.xpath("//md-list-item[contains(., 'Module created')]"), 150),
                    "Module generation failed at module creation stage. Time spent on stage: "+(new Date().getTime() - phaseStart)+" milliseconds\n" +
                            "Time spent on module generation overall:"+(new Date().getTime() - moduleGenerationStart)+" milliseconds"
            );
            waitForElementToBeEnabled(continueBtn, 5);
            clickOn(continueBtn);
            phaseStart = new Date().getTime();
            Assert.assertTrue(
                    isVisible(By.xpath("//md-list-item[contains(., 'Module deployed')]"), 60),
                    "Module generation failed at deployment stage. Time spent on stage: "+(new Date().getTime() - phaseStart)+" milliseconds\n" +
                            "Time spent on module generation overall:"+(new Date().getTime() - moduleGenerationStart)+" milliseconds"
            );
            waitForElementToBeEnabled(continueBtn, 5);
            clickOn(continueBtn);
            phaseStart = new Date().getTime();
            Assert.assertTrue(
                    isVisible(By.xpath("//md-list-item[contains(., 'Base template generated')]"), 30),
                    "Module generation failed at base template generation stage. Time spent on stage: "+(new Date().getTime() - phaseStart)+" milliseconds\n" +
                            "\"Time spent on module generation overall:\"+(new Date().getTime() - moduleGenerationStart)+\" milliseconds\""
            );
            waitForElementToBeEnabled(continueBtn, 5);
            clickOn(continueBtn);
            phaseStart = new Date().getTime();
            Assert.assertTrue(
                    isVisible(By.xpath("//md-list-item[contains(., 'Assets copied')]"), 30),
                    "Module generation failed at assets copying stage. Time spent on stage: "+(new Date().getTime() - phaseStart)+" milliseconds\n" +
                            "Time spent on module generation overall:"+(new Date().getTime() - moduleGenerationStart)+" milliseconds"
            );
            waitForElementToBeEnabled(continueBtn, 5);
            clickOn(continueBtn);
            phaseStart = new Date().getTime();
            Assert.assertTrue(
                    isVisible(By.xpath("//md-list-item[contains(., 'Templates created')]"), 60),
                    "Module generation failed at templates creation stage. Time spent on stage: "+(new Date().getTime() - phaseStart)+" milliseconds\n" +
                            "Time spent on module generation overall:"+(new Date().getTime() - moduleGenerationStart)+" milliseconds"
            );
            waitForElementToBeInvisible(continueBtn);

            Assert.assertTrue(
                    isVisible(By.xpath("//span[@message-key='angular.dsCreateModuleDirective.message.allDone']"), 5),
                    "All stages of Module generation passed, but module generation failed anyway." +
                            " Final message that module successfully generated is not visible.\n" +
                            "Module generation took "+(new Date().getTime() - moduleGenerationStart)+" milliseconds");
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
     * After module generation check that module's sources folder contains desired folder or file
     * @param softAssert instance of soft assert you are working with
     * @param sourceFolderPath String, absolute path to the module's sources folder. You specified that folder in module generation dialogue.
     * @param folderName String, folder to look into in sourceFolderPath/src/main/resources/
     * @param isMappedAsset boolean, true if looking for folder under .../resources, false if under .../resources/unmapped_assets
     * @param expectedFolderContent String[], Array of filenames you expect to find in folderName
     */
    protected void checkFolderInModulesResources(SoftAssert    softAssert,
                                                 String        sourceFolderPath,
                                                 String        folderName,
                                                 boolean       isMappedAsset,
                                                 String[]      expectedFolderContent,
                                                 String        moduleName){
        boolean folderExist = false;
        HashMap<String, Boolean> filesFoundInAssetsFolder = new HashMap<String, Boolean>();

        for(String filename:expectedFolderContent){
            filesFoundInAssetsFolder.put(filename, false);
        }

        File[] files;
        if(isMappedAsset){
            files = findFilesOrDirectories(sourceFolderPath+"/"+moduleName+"/src/main/resources", folderName, "");
        }else{
            files = findFilesOrDirectories(sourceFolderPath+"/"+moduleName+"/src/main/resources/unmapped_assets", folderName, "");
        }
        if(files != null &&
                files.length > 0 &&
                files[0].exists() &&
                files[0].isDirectory()){
            folderExist = true;
            for(String fileFound:files[0].list()){
                filesFoundInAssetsFolder.put(fileFound, true);
            }
            for (String filename:filesFoundInAssetsFolder.keySet()){
                softAssert.assertTrue(
                        filesFoundInAssetsFolder.get(filename),
                        "Folder with name '"+folderName+"' does not contain file '"+filename+"'."
                );
            }
        }
        softAssert.assertTrue(
                folderExist,
                "Folder with name '"+folderName+"' not found in generated module's src folder."
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

    protected String getCurrentIframeSrc(){
        return findByXpath("//iframe[@id='tiProjectFrame']").getAttribute("src");
    }

    protected String getCurrentIframeUrl(){
        String url;
        switchToProjectFrame();
        url = returnJavascriptResponse("document.location.pathname");
        switchToDefaultContent();
        return url;
    }

    protected void cleanDownloadsFolder() {
        String downloadsFolderPath = new File(getDownloadsFolder()).getAbsolutePath();

        try {
            FileUtils.cleanDirectory(new File(downloadsFolderPath));
        } catch (IOException e) {
            getLogger().error(e.getMessage());
        } catch (IllegalArgumentException ee){
            getLogger().error(ee.getMessage());
        }
    }

    /**
     * AfterClass method, deletes all projects, clean Downloads folder.
     */
    protected void customTestCleanUp(){
//        goToProjectsList("en");
//        deleteAllProjectsFast();
//        cleanDownloadsFolder();
    }
}