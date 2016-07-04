package org.jahia.modules.templateimporter.tests;

import org.apache.commons.io.FileUtils;
import org.jahia.modules.tests.core.ModuleTest;
import org.jahia.modules.tests.utils.CustomExpectedConditions;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by sergey on 2016-06-16.
 */
public class TemplateImporterRepository extends ModuleTest {
    /**
     * Open list of projects. (Just iframe)
     * @param locale Is used as part of url to projects list.
     */
    protected void goToProjectsList(String locale){
        getDriver().get(getPath("/cms/adminframe/default/" + locale + "/settings.template-importer.html"));
        waitForGlobalSpinner(1, 10);
    }

    /**
     * Opens projects list and imports a new project, asserts that new project is listed in 'projects library'
     * @param locale Is used as part of url to projects list
     * @param projectName String. Name of the project you are creating
     * @param projectDescription String, Description of project you are creating
     * @param testProjectFileName String, name of .zip file to import. Zip file should be inside testData.testProjects folder.
     * @return 'Success' if form created successfully, otherwise null.
     */
    protected void importProject(String locale, String projectName, String projectDescription, String testProjectFileName){
        String zipFilePath = new File("src/test/resources/testData/testProjects/"+testProjectFileName).getAbsolutePath();
        String jsToEnableInput = "function getElementByXpath(path) {" +
                "return document.evaluate(path, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;" +
                "}" +
                "fileInput = getElementByXpath(\"//label[input[@type='file']]\");" +
                "fileInput.setAttribute(\"style\", \"\");";

        goToProjectsList(locale);
        WebElement importProjectButton = findByXpath("//button[contains(., 'Import Project')]");
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
                "New project name is not found in projects list.");
    }

    /**
     * Click on field, clear it and type text into it.
     * @param field WebElement, filed to send keys into it
     * @param text String, text to send into input field
     */
    protected void typeInto(WebElement field, String text){
        clickOn(field);
        field.clear();
        field.sendKeys(text);
    }

    /**
     * Waits for several layers of global spinner to appear and than disappear
     * @param secondsToWaitSpinnerAppear int, amount of seconds to wait for global spinner elements to appear
     * @param secondsToWaitSpinnerDisappear int, amount of seconds to wait for global spinner elements to disappear
     */
    protected void waitForGlobalSpinner(int secondsToWaitSpinnerAppear,  int secondsToWaitSpinnerDisappear) {
        List<WebElement> spinners = new LinkedList<WebElement>();

        try {
            WebElement tiOverlay = createWaitDriver(secondsToWaitSpinnerAppear, 300).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='ti-overlay']")));
            WebElement tiOverlayContent = createWaitDriver(secondsToWaitSpinnerAppear, 300).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='ti-overlay-content'']")));
            WebElement spinner = createWaitDriver(secondsToWaitSpinnerAppear, 300).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='ti-global-spinner']")));
            spinners.add(spinner);
            spinners.add(tiOverlay);
            spinners.add(tiOverlayContent);

            for (WebElement elementToWait : spinners) {
                waitForElementToBeInvisible(elementToWait, secondsToWaitSpinnerDisappear);
            }
        } catch (TimeoutException e) {
        }
    }

    /**
     * Delete all projects
     * @return amount of deleted projects
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
            waitForElementToBeEnabled(removeSelectedBtn, 7);
            clickOn(removeSelectedBtn);

            WebElement confirmRemovalBtn = findByXpath("//button[@aria-label='Remove']");
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