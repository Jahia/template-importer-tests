package org.jahia.modules.templateimporter.tests.selection.tests;

import org.jahia.modules.templateimporter.tests.TemplateImporterRepository;
import org.jahia.modules.templateimporter.tests.businessobjects.Component;
import org.jahia.modules.tests.utils.SoftAssertWithScreenshot;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.List;

/**
 * Created by sergey on 2016-10-06.
 */
public class TreeExplorerTest extends TemplateImporterRepository {
    private static final String DOM_OUTLINE_MARK = "DomOutlined";

    @Test
    public void treeHighlightingTest(){
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "TreeExplorerTest.treeHighlightingTest");
        String projectName = randomWord(8);
        String treeFirstDiv = "(//md-sidenav//dom-viewer//div[@class='tiDomViewerHeader' and .//strong[normalize-space(.) = 'DIV']])[1]";
        String pageFirstDiv = "/html/body/div[1]";
        String treeFourthDiv = "(//md-sidenav//dom-viewer//div[@class='tiDomViewerHeader' and .//strong[normalize-space(.) = 'DIV']])[4]";
        String pageSecondDiv = "/html/body/div[2]";
        String treeFirstA = "(//md-sidenav//dom-viewer//div[@class='tiDomViewerHeader' and .//strong[normalize-space(.) = 'A']])[1]";
        String treeFifthDiv = "(//md-sidenav//dom-viewer//div[@class='tiDomViewerHeader' and .//strong[normalize-space(.) = 'DIV']])[5]";
        String pageThirdDiv = "/html/body/div[3]";
        String treeSecondDiv = "(//md-sidenav//dom-viewer//div[@class='tiDomViewerHeader' and .//strong[normalize-space(.) = 'DIV']])[2]";
        String pageFirstDivFirstDiv = "/html/body/div[1]/div[1]";
        String treeFirstH2 = "(//md-sidenav//dom-viewer//div[@class='tiDomViewerHeader' and .//strong[normalize-space(.) = 'H2']])[1]";

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        openTreeExplorer();
        checkElementHighlighting(softAssert, treeFirstDiv, pageFirstDiv);
        checkElementHighlighting(softAssert, treeFourthDiv, pageSecondDiv);
        //Because 'a' tag is not selectable by default, a's parent should be highlighted
        checkElementHighlighting(softAssert, treeFirstA, pageFirstDiv);
        checkElementHighlighting(softAssert, treeFifthDiv, pageThirdDiv);
        checkElementHighlighting(softAssert, treeSecondDiv, pageFirstDivFirstDiv);
        checkElementHighlighting(softAssert, treeFirstH2, pageFirstDiv);
        zoomIn(treeFirstDiv);
        checkElementHighlighting(softAssert, treeFirstDiv, pageFirstDiv);
        checkElementHighlighting(softAssert, treeFirstA, pageFirstDiv);
        checkElementHighlighting(softAssert, treeSecondDiv, pageFirstDivFirstDiv);

        softAssert.assertAll();
    }

    @Test
    public void treeNodeControlsEnableabilityTest(){
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "TreeExplorerTest.treeNodeControlsEnableabilityTest");
        String projectName = randomWord(8);
        String body = "//md-sidenav//dom-viewer//div[contains(@class, 'tiDomViewerHeader') and .//strong[normalize-space(.) = 'BODY']]";
        String firstDiv = "(//md-sidenav//dom-viewer//div[@class='tiDomViewerHeader' and .//strong[normalize-space(.) = 'DIV']])[1]";
        String fourthDiv = "(//md-sidenav//dom-viewer//div[@class='tiDomViewerHeader' and .//strong[normalize-space(.) = 'DIV']])[4]";
        String firstA = "(//md-sidenav//dom-viewer//div[@class='tiDomViewerHeader' and .//strong[normalize-space(.) = 'A']])[1]";
        String fifsDiv = "(//md-sidenav//dom-viewer//div[@class='tiDomViewerHeader' and .//strong[normalize-space(.) = 'DIV']])[5]";
        String secondDiv = "(//md-sidenav//dom-viewer//div[@class='tiDomViewerHeader' and .//strong[normalize-space(.) = 'DIV']])[2]";
        String firstH2 = "(//md-sidenav//dom-viewer//div[@class='tiDomViewerHeader' and .//strong[normalize-space(.) = 'H2']])[1]";

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        openTreeExplorer();
        checkIfButtonsEnabled(softAssert, body, false, false, false, "Default selector settings, BODY tag.");
        checkIfButtonsEnabled(softAssert, firstDiv, true, false, true, "Default selector settings, expandable DIV.");
        checkIfButtonsEnabled(softAssert, fourthDiv, false, false, true, "Default selector settings, non expandable DIV.");
        checkIfButtonsEnabled(softAssert, firstA, false, false, false, "Default selector settings, A tag");
        checkIfButtonsEnabled(softAssert, fifsDiv, true, false, true, "Default selector settings, expandable DIV.");
        checkIfButtonsEnabled(softAssert, secondDiv, true, false, true, "Default selector settings, expandable DIV.");
        checkIfButtonsEnabled(softAssert, firstH2, false, false, false, "Default selector settings, non expandable H2.");
        closeTreeExplorer();
        turnOnSelectabilityForAll();
        openTreeExplorer();
        checkIfButtonsEnabled(softAssert, body, false, false, false, "All enabled in selector settings, BODY tag.");
        checkIfButtonsEnabled(softAssert, firstDiv, true, false, true, "All enabled in selector settings, expandable DIV.");
        checkIfButtonsEnabled(softAssert, fourthDiv, false, false, true, "All enabled in selector settings, non expandable DIV.");
        checkIfButtonsEnabled(softAssert, firstA, false, false, true, "All enabled in selector settings, A tag");
        checkIfButtonsEnabled(softAssert, fifsDiv, true, false, true, "All enabled in selector settings, expandable DIV.");
        checkIfButtonsEnabled(softAssert, secondDiv, true, false, true, "All enabled in selector settings, expandable DIV.");
        checkIfButtonsEnabled(softAssert, firstH2, false, false, true, "All enabled in selector settings, non expandable H2.");
        zoomIn(firstDiv);
        checkIfButtonsEnabled(softAssert, firstDiv, false, true, true, "All enabled in selector settings, zoommed into first DIV");
        checkIfButtonsEnabled(softAssert, firstA, false, true, true, "All enabled in selector settings, zoommed into first DIV");
        checkIfButtonsEnabled(softAssert, secondDiv, true, true, true, "All enabled in selector settings, zoommed into first DIV");
        checkIfButtonsEnabled(softAssert, firstH2, false, true, true, "All enabled in selector settings, zoommed into first DIV");
        checkIfButtonsEnabled(softAssert, fourthDiv, false, true, true, "All enabled in selector settings, zoommed into first DIV");
        checkIfButtonsEnabled(softAssert, fifsDiv, true, true, true, "All enabled in selector settings, zoommed into first DIV");

        softAssert.assertAll();
    }

    @Test
    public void sausageMenuTest(){
        String projectName = randomWord(8);
        String fifsDiv = "(//md-sidenav//dom-viewer//div[@class='tiDomViewerHeader' and .//strong[normalize-space(.) = 'DIV']])[5]";
        String fourthDiv = "(//md-sidenav//dom-viewer//div[@class='tiDomViewerHeader' and .//strong[normalize-space(.) = 'DIV']])[4]";
        String firstDiv = "(//md-sidenav//dom-viewer//div[@class='tiDomViewerHeader' and .//strong[normalize-space(.) = 'DIV']])[1]";
        Component homeC1 = new Component(randomWord(5), randomWord(4), randomWord(6), "jnt:html", "/html/body/div[3]", 2, 0, "home");

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        sausageSelectComponent(homeC1, fifsDiv, "Selecting component from sausage menu");
        sausageSelectView(randomWord(4), "jnt:html", "/html/body/div[1]", firstDiv, "Selecting view from sausage menu");
        sausageSelectArea(randomWord(10), fourthDiv, "/html/body/div[2]");
    }

    @Test
    public void zoomOutTest(){
        String projectName = randomWord(12);
        String secondDiv = "(//md-sidenav//dom-viewer//div[@class='tiDomViewerHeader' and .//strong[normalize-space(.) = 'DIV']])[2]";
        String thirdDiv = "(//md-sidenav//dom-viewer//div[@class='tiDomViewerHeader' and .//strong[normalize-space(.) = 'DIV']])[3]";
        String firstDiv = "(//md-sidenav//dom-viewer//div[@class='tiDomViewerHeader' and .//strong[normalize-space(.) = 'DIV']])[1]";
        String forthDiv = "(//md-sidenav//dom-viewer//div[@class='tiDomViewerHeader' and .//strong[normalize-space(.) = 'DIV']])[4]";
        String body = "//md-sidenav//dom-viewer//div[contains(@class, 'tiDomViewerHeader') and .//strong[normalize-space(.) = 'BODY']]";

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        openTreeExplorer();
        zoomIn(firstDiv);
        zoomIn(thirdDiv);
        zoomIn(secondDiv);
        zoomOut(secondDiv);
        zoomOut(secondDiv);
        zoomOut(forthDiv);
        zoomOut(firstDiv);
        Assert.assertTrue(
                isVisible(By.xpath(body), 2),
                "After all that zooming in and out, we should be at root level again, but bodybtag is not visible in th tree!"
        );
    }

    private void sausageSelectView(String    viewName,
                                     String    nodeType,
                                     String    pageXpath,
                                     String    treeXpath,
                                     String    errorMsg){
        openTreeExplorer();
        clickSausageMenu(treeXpath);
        WebElement menuViewBtn = findByXpath("//div[@ng-click='snmc.canBeView && snmc.showView()']");
        waitForElementToStopMoving(menuViewBtn);
        clickOn(menuViewBtn);

        WebElement viewNameField = findByXpath("//input[@name='viewName']");
        WebElement selectViewBtn = findByXpath("//button[@ng-click='svc.view.ok()']");
        WebElement advsncedCheckbox = findByXpath("//md-checkbox[@ng-model='svc.view.advancedNodeTypeSelection']");

        waitForElementToStopMoving(viewNameField);
        typeInto(viewNameField, viewName);
        clickOn(advsncedCheckbox);
        WebElement nodeTypeField = findByName("nodeTypeSelection");
        waitForElementToBeVisible(nodeTypeField);
        waitForElementToStopMoving(nodeTypeField);
        nodeTypeField.click();
        nodeTypeField.clear();
        typeInto(nodeTypeField, nodeType);
        clickOn(By.xpath("//div[@ng-click='tc.select(item)'][contains(., '"+nodeType+"')]"));
        waitForElementToBeEnabled(selectViewBtn, 5);
        clickOn(selectViewBtn);
        waitForElementToBeInvisible(selectViewBtn);

        Assert.assertTrue(
                checkIfViewSelected(pageXpath),
                errorMsg+". View was not selected. Target element does not have '"+SELECTED_VIEW_MARK+"' class." + " XPath: "+pageXpath);
    }

    private void sausageSelectComponent(Component   component,
                                        String      treeXpath,
                                        String      errorMsg){
        String xPath = component.getXpath();
        String areaName = component.getAreaName();
        String viewName = component.getViewName();
        String nodeType = component.getNodeType();

        openTreeExplorer();
        clickSausageMenu(treeXpath);
        WebElement menuComponentBtn = findByXpath("//div[@ng-click='snmc.canBeAreaAndView && snmc.showAreaView()']");
        waitForElementToStopMoving(menuComponentBtn);
        clickOn(menuComponentBtn);

        WebElement areaNameField = findByName("areaName");
        WebElement viewNameField = findByName("viewName");
        WebElement advsncedCheckbox = findByXpath("//md-checkbox[@ng-model='savc.view.advancedNodeTypeSelection']");
        WebElement okBtn = findByXpath("//button[@ng-click='savc.ok()']");

        waitForElementToStopMoving(areaNameField);
        typeInto(areaNameField, areaName);
        typeInto(viewNameField, viewName);
        clickOn(advsncedCheckbox);
        WebElement nodeTypeField = findByName("nodeTypeSelection");
        waitForElementToBeVisible(nodeTypeField);
        nodeTypeField.click();
        nodeTypeField.clear();
        typeInto(nodeTypeField, nodeType);
        clickOn(By.xpath("//div[@ng-click='tc.select(item)'][contains(., '"+nodeType+"')]"));
        waitForElementToBeEnabled(okBtn, 5);
        clickOn(okBtn);
        waitForElementToBeInvisible(okBtn);

        Assert.assertTrue(
                checkIfViewSelected(xPath),
                errorMsg+". Component was not selected. Target element does not have '"+SELECTED_VIEW_MARK+"' class." + " XPath: "+xPath);
        Assert.assertTrue(
                checkIfAreaSelected(xPath),
                errorMsg+". Component was not selected. Target element does not have '"+SELECTED_AREA_MARK+"' class." + " XPath: "+xPath);
    }

    private void sausageSelectArea(String    areaName,
                                     String    treeXpath,
                                     String    pageXpth){
        openTreeExplorer();
        clickSausageMenu(treeXpath);
        WebElement menuAreaBtn = findByXpath("//div[@ng-click='snmc.canBeArea && snmc.showArea()']");
        waitForElementToStopMoving(menuAreaBtn);
        clickOn(menuAreaBtn);
        WebElement areaNameField = findByXpath("//input[@name='areaName']");
        WebElement selectBtn = findByXpath("//button[@ng-click='sac.area.ok()']");
        WebElement expandArea = findByXpath("//button[@ng-click='sac.area.expandSelection()']");

        waitForElementToStopMoving(areaNameField);
        typeInto(areaNameField, areaName);
        clickOn(expandArea);
        waitForElementToBeEnabled(selectBtn, 5);
        clickOn(selectBtn);
        waitForElementToBeInvisible(selectBtn);

        Assert.assertTrue(
                checkIfAreaSelected(pageXpth),
                "Area was not selected. Target element does not have '"+SELECTED_AREA_MARK+"' class." + " XPath: "+pageXpth);
    }

    private void checkIfButtonsEnabled(SoftAssert   softAssert,
                                       String       xPathToTree,
                                       boolean      expectedZoomInEnabled,
                                       boolean      expectedZoomOutEnabled,
                                       boolean      expectedMenuEnabled,
                                       String       errorMsg){
        String zoomInEnabledErrorMsg = "enabled";
        String zomOutEnabledErrorMsg = "enabled";
        String menuEnabledErrorMsg = "enabled";

        if(expectedZoomInEnabled){
            zoomInEnabledErrorMsg = "disabled";
        }
        if(expectedZoomOutEnabled){
            zomOutEnabledErrorMsg = "disabled";
        }
        if(expectedMenuEnabled){
            menuEnabledErrorMsg = "disabled";
        }

        WebElement treeElement = findByXpath(xPathToTree);
        Assert.assertNotNull(treeElement, "Element in the tee explorer not found. XPath: "+xPathToTree);
        new Actions(getDriver()).moveToElement(treeElement).build().perform();
        WebElement zoomInBtn = treeElement.findElement(By.xpath(".//button[@ng-click='zoomIn()']"));
        WebElement zoomOutBtn = treeElement.findElement(By.xpath(".//button[@ng-click='zoomOut()']"));
        WebElement menuBtn = treeElement.findElement(By.xpath(".//button[@ng-click='showMenu()($event)']"));
        softAssert.assertTrue(
                isVisible(zoomInBtn, 1),
                errorMsg+". Zoom in button is not visible for tree node: "+xPathToTree
        );
        softAssert.assertTrue(
                isVisible(zoomOutBtn, 1),
                errorMsg+". Zoom out button is not visible for tree node: "+xPathToTree
        );
        softAssert.assertTrue(
                isVisible(menuBtn, 1),
                errorMsg+". Menu button is not visible for tree node: "+xPathToTree
        );
        softAssert.assertEquals(
                zoomInBtn.isEnabled(),
                expectedZoomInEnabled,
                errorMsg+". Zoom in button is "+zoomInEnabledErrorMsg+" for tree node: "+xPathToTree
        );
        softAssert.assertEquals(
                zoomOutBtn.isEnabled(),
                expectedZoomOutEnabled,
                errorMsg+". Zoom out button is "+zomOutEnabledErrorMsg+" for tree node: "+xPathToTree
        );
        softAssert.assertEquals(
                menuBtn.isEnabled(),
                expectedMenuEnabled,
                errorMsg+". Menu button is "+menuEnabledErrorMsg+" for tree node: "+xPathToTree
        );
    }

    private void zoomIn(String  xPathToTree){
        WebElement treeElement = findByXpath(xPathToTree);
        Assert.assertNotNull(treeElement, "Element in the tee explorer not found. XPath: "+xPathToTree);
        new Actions(getDriver()).moveToElement(treeElement).build().perform();
        WebElement zoomInBtn = treeElement.findElement(By.xpath(".//button[@ng-click='zoomIn()']"));
        waitForElementToBeEnabled(zoomInBtn, 1);
        Assert.assertTrue(zoomInBtn.isEnabled(), "Zoom in button disabled for element: "+xPathToTree);
        clickOn(zoomInBtn);
        waitForElementToDisappear(zoomInBtn, 1);
    }

    private void clickSausageMenu(String  xPathToTree){
        WebElement treeElement = findByXpath(xPathToTree);
        Assert.assertNotNull(treeElement, "Element in the tee explorer not found. XPath: "+xPathToTree);
        new Actions(getDriver()).moveToElement(treeElement).build().perform();
        WebElement menuBtn = treeElement.findElement(By.xpath(".//button[@ng-click='showMenu()($event)']"));
        waitForElementToBeEnabled(menuBtn, 1);
        Assert.assertTrue(menuBtn.isEnabled(), "Menu button disabled for element: "+xPathToTree);
        clickOn(menuBtn);
    }

    private void zoomOut(String  xPathToTree){
        WebElement treeElement = findByXpath(xPathToTree);
        Assert.assertNotNull(treeElement, "Element in the tee explorer not found. XPath: "+xPathToTree);
        new Actions(getDriver()).moveToElement(treeElement).build().perform();
        WebElement zoomOutBtn = treeElement.findElement(By.xpath(".//button[@ng-click='zoomOut()']"));
        waitForElementToBeEnabled(zoomOutBtn, 1);
        Assert.assertTrue(zoomOutBtn.isEnabled(), "Zoom out button disabled for element: "+xPathToTree);
        clickOn(zoomOutBtn);
        waitForElementToDisappear(zoomOutBtn, 1);
    }

    private void openTreeExplorer(){
        WebElement menuBtn = findByXpath("//md-icon[text()='view_quilt']/ancestor::button");
        clickOn(menuBtn);
        WebElement treeExplorerBtn = findByXpath("//button[@ng-click='project.showTreeExplorer($event)']");
        waitForElementToStopMoving(treeExplorerBtn);
        clickOn(treeExplorerBtn);
        waitForElementToBeInvisible(treeExplorerBtn);
        WebElement treeViewHeader = findByXpath("//md-toolbar/h1[text()='Tree View']");
        waitForElementToStopMoving(treeViewHeader);
    }

    private void closeTreeExplorer(){
        WebElement closeBtn = findByXpath("//button[@ng-click='tc.close()']");
        clickOn(closeBtn);
        waitForElementToBeInvisible(closeBtn);
    }

    private boolean checkElementHighlighting(SoftAssert     softAssert,
                                             String         xPathForTreeExplorer,
                                             String         xPathForPage){
        boolean isHighlighted;

        WebElement treeElement = findByXpath(xPathForTreeExplorer);
        Assert.assertNotNull(treeElement, "Element in the tee explorer not found. XPath: "+xPathForTreeExplorer);
        new Actions(getDriver()).moveToElement(treeElement).build().perform();
        switchToProjectFrame();
        WebElement pageElement = findByXpath(xPathForPage);
        Assert.assertNotNull(pageElement, "Element in the page not found. XPath: "+pageElement);
        isHighlighted = pageElement.getAttribute("class").contains(DOM_OUTLINE_MARK);
        softAssert.assertTrue(
                isHighlighted,
                "Element is not highlighted on the page, after hovering related node in tree explorer. Tree node: "
                        +xPathForTreeExplorer+", Page  node: "+xPathForPage);
        switchToDefaultContent();

        return isHighlighted;
    }

    private void resetSelectability(){
        WebElement menuBtn = findByXpath("//md-icon[text()='settings']/ancestor::button");
        clickOn(menuBtn);
        WebElement configureSelectorBtn = findByXpath("//button[@ng-click='project.configureSelector($event)']");

        clickOn(configureSelectorBtn);
        WebElement resetBtn = findByXpath("//button[@ng-click='soc.reset()']");
        WebElement applyBtn = findByXpath("//button[@ng-click='soc.apply()']");

        waitForElementToStopMoving(resetBtn);
        clickOn(resetBtn);
        clickOn(applyBtn);
        waitForElementToBeInvisible(applyBtn);
    }

    private void turnOnSelectabilityForAll(){
        WebElement menuBtn = findByXpath("//md-icon[text()='settings']/ancestor::button");
        clickOn(menuBtn);
        WebElement configureSelectorBtn = findByXpath("//button[@ng-click='project.configureSelector($event)']");
        waitForElementToStopMoving(configureSelectorBtn);
        clickOn(configureSelectorBtn);
        WebElement applyBtn = findByXpath("//button[@ng-click='soc.apply()']");
        waitForElementToStopMoving(applyBtn);

        List<WebElement> categories = findElementsByXpath("//md-checkbox[@ng-click='soc.selectCategory(category)']");
        for(WebElement checkbox:categories){
            if(checkbox.getAttribute("aria-checked").contains("false")){
                clickOn(checkbox);
            }
            Assert.assertTrue(
                    checkbox.getAttribute("aria-checked").contains("true"),
                    "Turning selectability of all elements On - failed. One or more caregory is not activated."
            );
        }
        clickOn(applyBtn);
        waitForElementToBeInvisible(applyBtn);
    }
}
