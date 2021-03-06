package org.jahia.modules.templateimporter.tests.selection.tests;

import org.jahia.modules.templateimporter.tests.TemplateImporterRepository;
import org.jahia.modules.templateimporter.tests.businessobjects.Area;
import org.jahia.modules.templateimporter.tests.businessobjects.Component;
import org.jahia.modules.templateimporter.tests.businessobjects.Selection;
import org.jahia.modules.tests.utils.SoftAssertWithScreenshot;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by sergey on 2016-08-24.
 */
public class SelectionColorTests extends TemplateImporterRepository{
    private static final String AREA_BORDER_TYPE_KEY = "borderType";
    private static final String AREA_BORDER_COLOR_KEY = "borderColor";

    @Test
    public void colorSettingsTest(){
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "SelectionColorTests.colorSettingsTest");
        String projectName = randomWord(8);
        Area homeA1 = new Area(randomWord(4), "//body/div[1]//div[contains(., 'Level 2-1')]", 2, 0, "home");
//        View homeV1 = new View(randomWord(10), "jnt:html", homeA1.getXpath()+"/div[1]", 2, 0, homeA1.getTemplateName());
        Component homeC1 = new Component(randomWord(2), randomWord(6), randomWord(6), "/html/body/div[3]", 2, 0, homeA1.getTemplateName());
        Map<String, Map<String, String>> originalBorderColors;
        Map<String, String> expectedNewBorderColors;
        Map<String, Map<String, String>> actualNewBorderColors;
        Map<String, Map<String, String>> resetedBorderColors;

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        selectArea(homeA1);
        selectComponent(homeC1, "");

        createNewTemplate(randomWord(5), "page1.html");
        switchToTemplate(homeA1.getTemplateName());

        Selection[] allAreas = new Selection[]{homeA1, homeC1};
        originalBorderColors = getBorderColors(allAreas);
        expectedNewBorderColors = changeColors(allAreas);
        actualNewBorderColors = getBorderColors(allAreas);
        resetColors();
        resetedBorderColors = getBorderColors(allAreas);

        for(Selection sel:allAreas){
            String areaName = sel.getName();
            String expectedBorderType = "solid";
            String actualBorderType = actualNewBorderColors.get(areaName).get(AREA_BORDER_TYPE_KEY);
            String expectedNewBorderColor = expectedNewBorderColors.get(areaName);
            String actualNewBorderColor = actualNewBorderColors.get(areaName).get(AREA_BORDER_COLOR_KEY);
            String expectedBorderTypeAfterReset = originalBorderColors.get(areaName).get(AREA_BORDER_TYPE_KEY);
            String actualBorderTypeAfterReset = resetedBorderColors.get(areaName).get(AREA_BORDER_TYPE_KEY);
            String expectedBorderColorAfterReset = originalBorderColors.get(areaName).get(AREA_BORDER_COLOR_KEY);
            String actualBorderColorAfterReset = resetedBorderColors.get(areaName).get(AREA_BORDER_COLOR_KEY);

            softAssert.assertEquals(
                    actualBorderType,
                    expectedBorderType,
                    "Unexpected border type for selection '"+areaName+"' ("+sel.getXpath()+") after color change."
            );
            softAssert.assertEquals(
                    actualNewBorderColor,
                    expectedNewBorderColor,
                    "Unexpected border color for selection '"+areaName+"' ("+sel.getXpath()+") after color change."
            );
            softAssert.assertEquals(
                    actualBorderTypeAfterReset,
                    expectedBorderTypeAfterReset,
                    "Unexpected border type for selection '"+areaName+"' ("+sel.getXpath()+") after color reset."
            );
            softAssert.assertEquals(
                    actualBorderColorAfterReset,
                    expectedBorderColorAfterReset,
                    "Unexpected border color for selection '"+areaName+"' ("+sel.getXpath()+") after color reset."
            );
        }


        softAssert.assertAll();
    }

    @Test
    public void areaVisibilityTest(){
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "SelectionColorTests.areaVisibilityTest");
        String projectName = randomWord(8);
        Area homeA1 = new Area(randomWord(4), "//body/div[1]//div[contains(., 'Level 2-1')]", 2, 0, "home");
//        View homeV1 = new View(randomWord(10), "jnt:html", homeA1.getXpath()+"/div[1]", 2, 0, homeA1.getTemplateName());
        Component homeC1 = new Component(randomWord(2), randomWord(6), randomWord(6), "/html/body/div[3]", 2, 0, homeA1.getTemplateName());
        Map<String, Map<String, String>> originalBorderColors;
        Map<String, Map<String, String>> actualNewBorderColors;
        Map<String, Map<String, String>> resetedBorderColors;

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        selectArea(homeA1);
        selectComponent(homeC1, "");

        createNewTemplate(randomWord(5), "page1.html");
        switchToTemplate(homeA1.getTemplateName());

        Selection[] allSelections = new Selection[]{homeC1, homeA1};
        originalBorderColors = getBorderColors(allSelections);
        turnOffVisibility();
        actualNewBorderColors = getBorderColors(allSelections);
        resetColors();
        resetedBorderColors = getBorderColors(allSelections);

        for(Selection sel:allSelections){
            String areaName = sel.getName();
            String expectedBorderType = "none";
            String actualBorderType = actualNewBorderColors.get(areaName).get(AREA_BORDER_TYPE_KEY);
            String expectedBorderTypeAfterReset = originalBorderColors.get(areaName).get(AREA_BORDER_TYPE_KEY);
            String actualBorderTypeAfterReset = resetedBorderColors.get(areaName).get(AREA_BORDER_TYPE_KEY);
            String expectedBorderColorAfterReset = originalBorderColors.get(areaName).get(AREA_BORDER_COLOR_KEY);
            String actualBorderColorAfterReset = resetedBorderColors.get(areaName).get(AREA_BORDER_COLOR_KEY);

            softAssert.assertEquals(
                    actualBorderType,
                    expectedBorderType,
                    "Unexpected border type for selection '"+areaName+"' ("+sel.getXpath()+") after visibility change."
            );
            softAssert.assertEquals(
                    actualBorderTypeAfterReset,
                    expectedBorderTypeAfterReset,
                    "Unexpected border type for selection '"+areaName+"' ("+sel.getXpath()+") after visibility reset."
            );
            softAssert.assertEquals(
                    actualBorderColorAfterReset,
                    expectedBorderColorAfterReset,
                    "Unexpected border color for selection '"+areaName+"' ("+sel.getXpath()+") after visibility reset."
            );
        }
        softAssert.assertAll();
    }

    @Test(enabled = false) //Test disabled due to functionality removal
    public void areaWithLimitColorTest(){
      /*  SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "SelectionColorTests.areaWithLimitColorTest");
        String projectName = randomWord(8);
        Area homeA1 = new Area(randomWord(4), "//body/div[1]//div[contains(., 'Level 2-1')]/div[1]", "//body/div[1]//div[contains(., 'Level 2-1')]/div[1]", 2, 0, "home");
        Map<String, Map<String, String>> originalBorderColors;
        Map<String, String> expectedNewBorderColors;
        Map<String, Map<String, String>> actualNewBorderColors;
        Map<String, Map<String, String>> resetedBorderColors;

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        selectArea(homeA1, 2);

        createNewTemplate(randomWord(5), "page1.html");
        switchToTemplate(homeA1.getTemplateName());

        Area[] allAreas = new Area[]{homeA1};
        originalBorderColors = getBorderColors(allAreas);
        expectedNewBorderColors = changeColors(allAreas);
        actualNewBorderColors = getBorderColors(allAreas);
        resetColors();
        resetedBorderColors = getBorderColors(allAreas);

        for(Area area:allAreas){
            String areaName = area.getName();
            String expectedBorderType = "dotted";
            String actualBorderType = actualNewBorderColors.get(areaName).get(AREA_BORDER_TYPE_KEY);
            String expectedNewBorderColor = expectedNewBorderColors.get(areaName);
            String actualNewBorderColor = actualNewBorderColors.get(areaName).get(AREA_BORDER_COLOR_KEY);
            String expectedBorderTypeAfterReset = originalBorderColors.get(areaName).get(AREA_BORDER_TYPE_KEY);
            String actualBorderTypeAfterReset = resetedBorderColors.get(areaName).get(AREA_BORDER_TYPE_KEY);
            String expectedBorderColorAfterReset = originalBorderColors.get(areaName).get(AREA_BORDER_COLOR_KEY);
            String actualBorderColorAfterReset = resetedBorderColors.get(areaName).get(AREA_BORDER_COLOR_KEY);

            softAssert.assertEquals(
                    actualBorderType,
                    expectedBorderType,
                    "Unexpected border type for selection '"+areaName+"' ("+area.getXpath()+") after color change."
            );
            softAssert.assertEquals(
                    actualNewBorderColor,
                    expectedNewBorderColor,
                    "Unexpected border color for selection '"+areaName+"' ("+area.getXpath()+") after color change."
            );
            softAssert.assertEquals(
                    actualBorderTypeAfterReset,
                    expectedBorderTypeAfterReset,
                    "Unexpected border type for selection '"+areaName+"' ("+area.getXpath()+") after color reset."
            );
            softAssert.assertEquals(
                    actualBorderColorAfterReset,
                    expectedBorderColorAfterReset,
                    "Unexpected border color for selection '"+areaName+"' ("+area.getXpath()+") after color reset."
            );
        }

        softAssert.assertAll();*/
    }

    @Test(enabled = false) //Test disabled due to functionality removal
    public void areaWithLimitVisibilityTest(){
      /*  SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "SelectionColorTests.areaWithLimitVisibilityTest");
        String projectName = randomWord(8);
        Area homeA1 = new Area(randomWord(4), "//body/div[1]//div[contains(., 'Level 2-1')]/div[1]", "//body/div[1]//div[contains(., 'Level 2-1')]/div[1]", 2, 0, "home");
        Map<String, Map<String, String>> originalBorderColors;
        Map<String, Map<String, String>> actualNewBorderColors;
        Map<String, Map<String, String>> resetedBorderColors;

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        selectArea(homeA1, 2);

        createNewTemplate(randomWord(5), "page1.html");
        switchToTemplate(homeA1.getTemplateName());

        Area[] allAreas = new Area[]{homeA1};
        originalBorderColors = getBorderColors(allAreas);
        turnOffVisibility();
        actualNewBorderColors = getBorderColors(allAreas);
        resetColors();
        resetedBorderColors = getBorderColors(allAreas);

        for(Area area:allAreas){
            String areaName = area.getName();
            String expectedBorderType = "none";
            String actualBorderType = actualNewBorderColors.get(areaName).get(AREA_BORDER_TYPE_KEY);
            String expectedBorderTypeAfterReset = originalBorderColors.get(areaName).get(AREA_BORDER_TYPE_KEY);
            String actualBorderTypeAfterReset = resetedBorderColors.get(areaName).get(AREA_BORDER_TYPE_KEY);
            String expectedBorderColorAfterReset = originalBorderColors.get(areaName).get(AREA_BORDER_COLOR_KEY);
            String actualBorderColorAfterReset = resetedBorderColors.get(areaName).get(AREA_BORDER_COLOR_KEY);

            softAssert.assertEquals(
                    actualBorderType,
                    expectedBorderType,
                    "Unexpected border type for selection '"+areaName+"' ("+area.getXpath()+") after visibility change."
            );
            softAssert.assertEquals(
                    actualBorderTypeAfterReset,
                    expectedBorderTypeAfterReset,
                    "Unexpected border type for selection '"+areaName+"' ("+area.getXpath()+") after visibility reset."
            );
            softAssert.assertEquals(
                    actualBorderColorAfterReset,
                    expectedBorderColorAfterReset,
                    "Unexpected border color for selection '"+areaName+"' ("+area.getXpath()+") after visibility reset."
            );
        }

        softAssert.assertAll();*/
    }

    private void turnOffVisibility(){
        WebElement menuBtn = findByXpath("//md-icon[text()='settings']/ancestor::button");
        clickOn(menuBtn);
        WebElement adjustColorsBtn = findByXpath("//button[@ng-click='project.setUpColors($event)']");
        waitForElementToStopMoving(adjustColorsBtn);
        clickOn(adjustColorsBtn);
        WebElement applyBtn = findByXpath("//button[@ng-click='sdoc.apply()']");
        waitForElementToStopMoving(applyBtn);
        List<WebElement> toggles = findElementsByXpath("//md-dialog-content//md-switch");

        for(WebElement toggle:toggles){
            clickOn(toggle);
            Assert.assertTrue(toggle.getAttribute("aria-checked").equals("false"),
                    "Selection visibility toggle did not change position to 'Off' after click.");
        }
        clickOn(applyBtn);
        waitForElementToBeInvisible(applyBtn);
        switchToProjectFrame();
        WebElement body = findByXpath("//body");
        waitForElementToStopMoving(body);
        switchToDefaultContent();
    }

    private void resetColors(){
        WebElement menuBtn = findByXpath("//md-icon[text()='settings']/ancestor::button");
        clickOn(menuBtn);
        WebElement adjustColorsBtn = findByXpath("//button[@ng-click='project.setUpColors($event)']");
        waitForElementToStopMoving(adjustColorsBtn);
        clickOn(adjustColorsBtn);
        WebElement resetBtn = findByXpath("//button[@ng-click='sdoc.reset()']");
        waitForElementToStopMoving(resetBtn);
        clickOn(resetBtn);
        waitForElementToBeInvisible(resetBtn);
        shortSleep();
        switchToProjectFrame();
        WebElement body = findByXpath("//body");
        waitForElementToStopMoving(body);
        switchToDefaultContent();
    }

    private Map<String, String> changeColors(Selection[] selections){
        Map<String, String> newColors = new HashMap<String, String>();
        String haColor = generateRGB();
        String hcColor = generateRGB();

        WebElement menuBtn = findByXpath("//md-icon[text()='settings']/ancestor::button");
        clickOn(menuBtn);
        WebElement adjustColorsBtn = findByXpath("//button[@ng-click='project.setUpColors($event)']");
        waitForElementToStopMoving(adjustColorsBtn);
        clickOn(adjustColorsBtn);
        WebElement applyBtn = findByXpath("//button[@ng-click='sdoc.apply()']");
        waitForElementToStopMoving(applyBtn);

        for (Selection selection : selections) {
            WebElement changerBtn;
            WebElement colorInput;
            WebElement chooseBtn;
            String newColor;

            //Home and other non-base selections here
            if (selection.isArea()) {
                //Areas here
                    //No internal area, means it is not dotted area (not expanded twice)
                    changerBtn = findByXpath("(//div[contains(@class, 'sp-replacer')])[2]");
                    colorInput = findByXpath("(//input[@class='sp-input'])[2]");
                    chooseBtn = findByXpath("(//button[@class='sp-choose'])[2]");
                newColor = haColor;
            } else {
                    //Components are here
                    changerBtn = findByXpath("(//div[contains(@class, 'sp-replacer')])[1]");
                    colorInput = findByXpath("(//input[@class='sp-input'])[1]");
                    chooseBtn = findByXpath("(//button[@class='sp-choose'])[1]");
                    newColor = hcColor;
            }

            clickOn(changerBtn);
            if (getBrowser().equals(FIREFOX)) {
                //FirefoxDriver bug workaround
                for (int i = 0; i < 20; i++) {
                    colorInput.sendKeys(Keys.BACK_SPACE);
                }
                colorInput.sendKeys(newColor);
                colorInput.sendKeys(Keys.ENTER);
            } else {
                typeInto(colorInput, newColor);
            }
            waitForElementToBeEnabled(chooseBtn, 3);
            clickOn(chooseBtn);
            waitForElementToBeInvisible(chooseBtn);
            newColors.put(selection.getName(), newColor);
        }

        applyBtn = findByXpath("//button[@ng-click='sdoc.apply()']");
        clickOn(applyBtn);
        waitForElementToBeInvisible(applyBtn);
        shortSleep();
        switchToProjectFrame();
        WebElement body = findByXpath("//body");
        waitForElementToStopMoving(body);
        switchToDefaultContent();

        return newColors;
    }

    private Map<String, Map<String, String>> getBorderColors(Selection[] selections){
        Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();

        switchToProjectFrame();
        for(Selection sel:selections){
            WebElement selection = findByXpath(sel.getXpath());
            Map<String, String> borderTypeAndColor = new HashMap<String, String>();

            if(getBrowser().equals(CHROME)) {
                String[] borderCssAttributes = selection.getCssValue("border").split(" ", 3);
                borderTypeAndColor.put(AREA_BORDER_TYPE_KEY, borderCssAttributes[1]);
                borderTypeAndColor.put(AREA_BORDER_COLOR_KEY, borderCssAttributes[2]);

            }else{
                borderTypeAndColor.put(AREA_BORDER_TYPE_KEY, selection.getCssValue("border-bottom-style"));
                borderTypeAndColor.put(AREA_BORDER_COLOR_KEY, selection.getCssValue("border-bottom-color"));
            }
            map.put(sel.getName(), borderTypeAndColor);
        }
        switchToDefaultContent();

        return map;
    }

    private String generateRGB(){
        int r = randInt(0, 255);
        int g = randInt(0, 255);
        int b = randInt(0, 255);
        String opacity = "0."+randInt(1, 9);

        if(getBrowser().equals(CHROME)){
            return "rgb("+r+", "+g+", "+b+")";
        }else{
            return "rgba("+r+", "+g+", "+b+", "+opacity+")";
        }
    }

    public static int randInt(int min, int max) {
        Random rand = new Random();

        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }
}
