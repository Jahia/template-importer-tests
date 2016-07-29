package org.jahia.modules.templateimporter.tests;

import org.apache.commons.io.FileUtils;
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

/**
 * Created by sergey on 2016-06-16.
 */
public class TemplateImporterRepository extends ModuleTest {
    protected static final String SELECTED_AREA_MARK = "AreaSelection";
    protected static final String SELECTED_VIEW_MARK = "ViewSelection";

    /**
     * Open list of projects. (Just iframe)
     * @param locale Is used as part of url to projects list.
     */
    protected void goToProjectsList(String locale){
        getDriver().get(getPath("/cms/adminframe/default/" + locale + "/settings.template-importer.html"));
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
        WebElement importProjectButton = findByXpath("//button[contains(., 'Import Project')]");
        waitForElementToStopMoving(importProjectButton);
        clickOn(importProjectButton);
        WebElement projectNameField = findByXpath("//input[@name='projectName']");
        WebElement projectDescriptionField = findByXpath("//textarea[@ng-model='ic.projectDescription']");
        WebElement projectZipFileField = findByXpath("//input[@type='file']");
        WebElement importButton = findByXpath("//button[@aria-label='Import']");
        WebElement dialogueBox = findByXpath("//div[@class='md-dialog-container ng-scope']");

        createWaitDriver(5, 500).until(CustomExpectedConditions.javascriptWithoutException(jsToEnableInput));
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
                isVisible(By.xpath("//md-card-title-text/span[contains(text(), '"+projectName+"')]"), 5),
                true,
                "New project name ("+projectName+")is not found in projects list.");
    }

    /**
     * Click on field, clear it and type text into it.
     * @param field WebElement, filed to send keys into it
     * @param text String, text to send into input field
     */
    protected void typeInto(WebElement  field,
                            String      text){
        Long maxMilliSecondsToWait = 5000L;
        String xPath = generateUglyXpath(field, "");
        Long start = new Date().getTime();

        while(!getValueFromInput(xPath).equals(text)){
            try{
                clickOn(field);
                field.clear();
                field.sendKeys(text);
                Thread.sleep(200L);
            }catch (InterruptedException e){}
            catch (StaleElementReferenceException ee){}
            if(new Date().getTime() - start >= maxMilliSecondsToWait){
                getLogger().error("Text you are tried to type did not reach target's value in 5 sec.");
                break;
            }
        }
    }

    private String generateUglyXpath(WebElement childElement, String current) {
        String childTag = childElement.getTagName();
        if(childTag.equals("html")) {
            return "/html[1]"+current;
        }
        WebElement parentElement = childElement.findElement(By.xpath(".."));
        List<WebElement> childrenElements = parentElement.findElements(By.xpath("*"));
        int count = 0;
        for(int i=0;i<childrenElements.size(); i++) {
            WebElement childrenElement = childrenElements.get(i);
            String childrenElementTag = childrenElement.getTagName();
            if(childTag.equals(childrenElementTag)) {
                count++;
            }
            if(childElement.equals(childrenElement)) {
                return generateUglyXpath(parentElement, "/" + childTag + "[" + count + "]"+current);
            }
        }
        return null;
    }

    /**
     * Returns input's value, even if it is hidden input.
     * @param xPathToInput String. xPath to input element
     * @return String. Input's value
     */
    protected String getValueFromInput(String xPathToInput){
        String jsResponse = null;
        String javascriptToExecute = "" +
                "function getElementByXpath(path) {" +
                "    return document.evaluate(path, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;" +
                "}" +

                "node = getElementByXpath(\""+xPathToInput+"\");" +
                "return node.value;";
        try {
            jsResponse = (String) ((JavascriptExecutor) driver).executeScript(javascriptToExecute);
        } catch (Exception e) {
            e.getMessage();
            e.printStackTrace();
        }
        return jsResponse;
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
        WebElement createNewTemplateBtn = findByXpath("//button[@ng-click='cpc.showCreatePageDialog($event)']");

        clickOn(createNewTemplateBtn);
        WebElement templateNameField = findByXpath("//input[@name='pageName']");
        WebElement pageSelectDropdown = findByXpath("//md-select[@ng-model='selectedTemplate']");
        WebElement createBtn = findByXpath("//button[@ng-click='create()']");

        waitForElementToStopMoving(pageSelectDropdown);
        clickOn(pageSelectDropdown);
        WebElement pageOption = findByXpath("//md-option[@value='"+pageFileName+"']");
        waitForElementToBeEnabled(pageOption, 5);
        clickOn(pageOption);
        typeInto(templateNameField, templateName);
        waitForElementToBeEnabled(createBtn, 5);
        clickOn(createBtn);
        waitForElementToBeInvisible(createBtn);

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
        List<WebElement> projectsBeforeDeletion = null;

        try {
            projectsBeforeDeletion = createWaitDriver(2, 300).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//md-card")));
        }catch(TimeoutException e){}

        if (projectsBeforeDeletion != null && projectsBeforeDeletion.size() > 0) {
            WebElement selectAllCheckbox = findByXpath("//md-checkbox[@aria-label='Select all']/div");
            WebElement removeSelectedBtn = findByXpath("//button[@aria-label='Remove Selected Project']");

            clickOn(selectAllCheckbox);
            waitForElementToStopMoving(removeSelectedBtn);
            waitForElementToBeEnabled(removeSelectedBtn, 7);
            clickOn(removeSelectedBtn);

            WebElement confirmRemovalBtn = findByXpath("//button[@aria-label='Remove']");
            waitForElementToStopMoving(confirmRemovalBtn);
            clickOn(confirmRemovalBtn);
            waitForElementToDisappear(confirmRemovalBtn, 10);
            waitForGlobalSpinner(1, 45);

            for (WebElement project : projectsBeforeDeletion) {
                boolean isDeleted = waitForElementToBeInvisible(project);
                if (isDeleted) {
                    projectsRemoved++;
                }
            }
        }
        return projectsRemoved;
    }

    /**
     * Deletes project with given name. Throws assertion error if you try to delete project that does not exist
     * @param projectName String, name of project to delete
     * @return True if project was deleted, false if still visible after deletion.
     */
    protected boolean deleteProject(String projectName){
        boolean isProjectDeleted;
        WebElement removeSelectedBtn = findByXpath("//button[@aria-label='Remove Selected Project']");
        WebElement proectToDelete = findByXpath("//md-card-title-text[contains(., '"+projectName+"')]/ancestor::md-card");
        WebElement checkboxToSelectProjectToDelete = findByXpath("//md-card-title-text[contains(., '"+projectName+"')]/ancestor::md-card//md-checkbox");

        Assert.assertNotNull(checkboxToSelectProjectToDelete, "Checkbox to delete a project '"+projectName+"' not found. Does project exist?");
        clickOn(checkboxToSelectProjectToDelete);
        waitForElementToStopMoving(removeSelectedBtn);
        waitForElementToBeEnabled(removeSelectedBtn, 7);
        clickOn(removeSelectedBtn);
        WebElement confirmRemovalBtn = findByXpath("//button[@aria-label='Remove']");
        clickOn(confirmRemovalBtn);
        waitForElementToDisappear(confirmRemovalBtn, 10);
        waitForGlobalSpinner(1, 45);
        isProjectDeleted = waitForElementToBeInvisible(proectToDelete);

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
        WebElement areaNameField = findByXpath("//input[@name='areaName']");
        WebElement okButton = findByXpath("//button[@ng-click='hdc.area.ok()']");
        WebElement expandArea = findByXpath("//button[@ng-click='hdc.area.expandSelection()']");
        waitForElementToStopMoving(areaNameField);
        typeInto(areaNameField, areaName);
        clickOn(expandArea);
        waitForElementToBeEnabled(okButton, 5);
        clickOn(okButton);
        waitForElementToBeInvisible(okButton);

        Assert.assertTrue(
                checkIfAreaSelected(xPath),
                "Area was not selected. Target element does not have '"+SELECTED_AREA_MARK+"' class." + " XPath: "+xPath);
    }

    /**
     * Selects a view in current template with given parameters. Will switch to the iFrame before selection and switch
     * back after. Checks that selected area has 'ViewSelection' class after selection.
     * @param viewName String, name of the area
     * @param xPath String, XPath to the element you want to select
     * @param xOffset int, Horizontal offset in pixels, from the <u>left</u> border of element.
     *                Pass negative value to move left. Pass 0 to use calculated center of the element (Default click behaviour).
     * @param yOffset int, Vertical offset in pixels, from the <u>top</u> border of element.
     *                Pass negative value to move left. Pass 0 to use calculated center of the element (Default click behaviour).
     * @param errorMsg Error message that will be prepended to the assertion error message. Describe use case here. Example:
     *                Reselecting view after deletion
     */
    protected void selectView(String    viewName,
                              String    nodeType,
                              String    xPath,
                              int       xOffset,
                              int       yOffset,
                              String    errorMsg){
        rightMouseClick(xPath, xOffset, yOffset);
        WebElement viewTab = findByXpath("//md-tab-item[@ng-click='$mdTabsCtrl.select(tab.getIndex())'][contains(., 'view')]");
        WebElement viewNameField = findByXpath("//input[@name='viewName']");
        WebElement nodeTypeField = findByXpath("//input[@id='nodeTypeSelection-typeahead-input']");
        WebElement okButton = findByXpath("//button[@ng-click='hdc.view.ok()']");
        waitForElementToStopMoving(viewTab);
        clickOn(viewTab);
        waitForElementToStopMoving(viewNameField);
        typeInto(viewNameField, viewName);
        typeInto(nodeTypeField, nodeType);
        clickOn(By.xpath("//div[@ng-click='tc.select(item)'][contains(., '"+nodeType+"')]"));
        waitForElementToBeEnabled(okButton, 5);
        clickOn(okButton);
        waitForElementToBeInvisible(okButton);

        Assert.assertTrue(
                checkIfViewSelected(xPath),
                errorMsg+". View was not selected. Target element does not have '"+SELECTED_VIEW_MARK+"' class." + " XPath: "+xPath);
    }

    /**
     * Selects a view in current template with given parameters. Will switch to the iFrame before selection and switch
     * back after. Checks that selected area has 'ViewSelection' class after selection.
     * @param viewName String, name of the area
     * @param xPath String, XPath to the element you want to select
     * @param xOffset int, Horizontal offset in pixels, from the <u>left</u> border of element.
     *                Pass negative value to move left. Pass 0 to use calculated center of the element (Default click behaviour).
     * @param yOffset int, Vertical offset in pixels, from the <u>top</u> border of element.
     *                Pass negative value to move left. Pass 0 to use calculated center of the element (Default click behaviour).
     */
    protected void selectView(String    viewName,
                              String    nodeType,
                              String    xPath,
                              int       xOffset,
                              int       yOffset){
        selectView(viewName, nodeType, xPath, xOffset, yOffset, "");
    }

    protected void switchToProjectFrame(){
        createWaitDriver(10, 300).until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.xpath("//iframe[@id='tiProjectFrame']")));
    }

    protected void switchToDefaultContent(){
        getDriver().switchTo().defaultContent();
    }

    protected void switchToTemplate(String templateName){
        WebElement templateTab = findByXpath("//ti-tab[contains(., '"+templateName+"')]");
        clickOn(templateTab);
        switchToProjectFrame();
        WebElement body = findByXpath("//body");
        waitForElementToStopMoving(body);
        switchToDefaultContent();
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
        WebElement removeBtn = findByXpath("//button[@ng-click='hdc.area.remove()']");
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
     * Removes area. Click on area than click 'Remove' and assert that area removed.
     * @param xPath String, xPath to your target element
     * @param xOffset int, Horizontal offset in pixels, from the <u>left</u> border of element.
     *                Pass negative value to move left. Pass 0 to use calculated center of the element (Default click behaviour).
     * @param yOffset int, Vertical offset in pixels, from the <u>top</u> border of element.
     *                Pass negative value to move left. Pass 0 to use calculated center of the element (Default click behaviour).
     */
    protected  void removeView(String   xPath,
                               int      xOffset,
                               int      yOffset){
        boolean isSelected = checkIfViewSelected(xPath, new SoftAssert(), true);

        Assert.assertTrue(isSelected, "View that you are trying to remove is not selected.");
        rightMouseClick(xPath, xOffset, yOffset);

        WebElement viewTab = findByXpath("//md-tab-item[@ng-click='$mdTabsCtrl.select(tab.getIndex())'][contains(., 'view')]");
        WebElement removeBtn = findByXpath("//button[@ng-click='hdc.view.remove()']");
        waitForElementToStopMoving(viewTab);
        clickOn(viewTab);
        waitForElementToStopMoving(removeBtn);
        clickOn(removeBtn);
        waitForElementToBeInvisible(removeBtn);
        Assert.assertFalse(
                checkIfViewSelected(xPath),
                "View was not removed. XPath:'"+xPath+"'. Horizontal offset:"+xOffset+", Vertical offset:"+yOffset);
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
        softAssert.assertNotNull(area, "Cannot find an element that you are trying to check if selected as area. XPath: '" + xPath + "'.");

        boolean isAreaSelected = area.getAttribute("class").contains(SELECTED_AREA_MARK);
        switchToDefaultContent();
        softAssert.assertEquals(
                isAreaSelected,
                expectedResult,
                errorMsg+". Assertion if element: '" + xPath + "' has class '" + SELECTED_AREA_MARK + "' (is selected) Failed");
        return isAreaSelected;
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

    /**
     * Check if area is selected (has ViewSelection class)
     * @param xPath String, XPath to the view
     * @param softAssert Instance of SoftAssert you are working with. Will fail if visibility result is not expected.
     * @param expectedResult boolean, your expectation if area should be selected.
     * @return True if area selected, otherwise false
     */
    protected boolean checkIfViewSelected(String        xPath,
                                          SoftAssert    softAssert,
                                          boolean       expectedResult,
                                          String        errorMsg) {
        switchToProjectFrame();
        WebElement area = findByXpath(xPath);
        softAssert.assertNotNull(area, "Cannot find an element that you are trying to check if selected as view. XPath: '" + xPath + "'.");

        boolean isViewSelected = area.getAttribute("class").contains(SELECTED_VIEW_MARK);
        switchToDefaultContent();
        softAssert.assertEquals(
                isViewSelected,
                expectedResult,
                errorMsg+". Assertion if element: '" + xPath + "' has class '" + SELECTED_VIEW_MARK + "' (is selected) Failed");
        return isViewSelected;
    }

    protected boolean checkIfViewSelected(String        xPath,
                                          SoftAssert    softAssert,
                                          boolean       expectedResult) {
        return checkIfViewSelected(xPath, softAssert, expectedResult, "");
    }

    protected void removeTemplate(String templateName){
        WebElement templateTab = findByXpath("//ti-tab[contains(., '"+templateName+"')]");
        Assert.assertNotNull(templateTab, "Cannot find template tab you are trying to remove: '"+templateName+"'");
        WebElement trashBinIcon = findByXpath("//ti-tab[contains(., '"+templateName+"')]/div[@ng-click='removePage()']");
        clickOn(trashBinIcon);
        WebElement confirmDeletionBtn = findByXpath("//button[@ng-click='dialog.hide()']");
        clickOn(confirmDeletionBtn);
        waitForElementToBeInvisible(confirmDeletionBtn);
        switchToProjectFrame();
        WebElement body = findByXpath("//body");
        waitForElementToStopMoving(body);
        switchToDefaultContent();
        Assert.assertTrue(waitForElementToBeInvisible(templateTab, 5), "Template '"+templateName+"' was not deleted. Template's tab is still visible.");
    }

    protected String getCurrentIframeSrc(){
        return findByXpath("//iframe[@id='tiProjectFrame']").getAttribute("src");
    }

    /**
     * Check if area is selected (has AreaSelection class)
     * @param xPath String, XPath to the view
     * @return True if area selected, otherwise false
     */
    protected boolean checkIfViewSelected(String xPath){
        return checkIfViewSelected(xPath, new SoftAssert(), false);
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
        goToProjectsList("en");
        deleteAllProjects();
        cleanDownloadsFolder();
    }
}