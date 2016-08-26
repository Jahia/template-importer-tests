package org.jahia.modules.templateimporter.tests.selection.tests;

import org.jahia.modules.templateimporter.tests.TemplateImporterRepository;
import org.jahia.modules.templateimporter.tests.businessobjects.Area;
import org.jahia.modules.templateimporter.tests.businessobjects.View;
import org.jahia.modules.tests.utils.SoftAssertWithScreenshot;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.HashMap;
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
        Area baseA1 = new Area(randomWord(5), "//body/div[1]", 1, 0, "base");
        Area baseA2 = new Area(randomWord(3), "//body/div[3]", 1, 0, "base");
        Area homeA1 = new Area(randomWord(4), "//body/div[1]//div[contains(., 'Level 2-1')]", 1, 0, "home");
        View baseV1 = new View(randomWord(6), "jnt:bootstrapMainContent", baseA2.getXpath()+"/div[1]", 1, 0, baseA2.getTemplateName());
        View homeV1 = new View(randomWord(10), "jnt:html", homeA1.getXpath()+"/div[1]", 1, 0, homeA1.getTemplateName());
        Map<String, Map<String, String>> originalBorderColors;
        Map<String, String> expectedNewBorderColors;
        Map<String, Map<String, String>> actualNewBorderColors;
        Map<String, Map<String, String>> resetedBorderColors;

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        selectArea(baseA1);
        selectArea(baseA2);
        selectView(baseV1);
        switchToTemplate("home");
        selectArea(homeA1);
        selectView(homeV1);

        switchToTemplate(baseA1.getTemplateName());
        switchToTemplate(homeA1.getTemplateName());

        Area[] allAreas = new Area[]{baseA1, baseA2, baseV1, homeA1, homeV1};
        originalBorderColors = getBorderColors(allAreas);
        expectedNewBorderColors = changeColors(allAreas);
        actualNewBorderColors = getBorderColors(allAreas);
        resetColors();
        resetedBorderColors = getBorderColors(allAreas);

        for(Area area:allAreas){
            String areaName = area.getName();
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


        softAssert.assertAll();
    }

    private void resetColors(){
        WebElement adjustColorsBtn = findByXpath("//button[@ng-click='project.setUpColors($event)']");
        clickOn(adjustColorsBtn);
        WebElement resetBtn = findByXpath("//button[@ng-click='sdoc.reset()']");
        waitForElementToStopMoving(resetBtn);
        clickOn(resetBtn);
        waitForElementToBeInvisible(resetBtn);
        switchToProjectFrame();
        WebElement body = findByXpath("//body");
        waitForElementToStopMoving(body);
        switchToDefaultContent();
    }

    private Map<String, String> changeColors(Area[] areas){
        Map<String, String> newColors = new HashMap<String, String>();
        String baColor = generateRGB();
        String bvColor = generateRGB();
        String haColor = generateRGB();
        String hvColor = generateRGB();

        WebElement adjustColorsBtn = findByXpath("//button[@ng-click='project.setUpColors($event)']");
        clickOn(adjustColorsBtn);
        WebElement applyBtn = findByXpath("//button[@ng-click='sdoc.apply()']");
        waitForElementToStopMoving(applyBtn);

        for(Area area:areas){
            WebElement changerBtn;
            WebElement colorInput;
            WebElement chooseBtn;
            String newColor;

            if(area.getTemplateName().equals("base")){
                //Base selections here
                if(area.isArea()){
                    //Areas here
                    changerBtn = findByXpath("(//md-dialog-content//div[contains(., 'Base area')]/span[@ng-model='type.color'])[2]");
                    colorInput = findByXpath("(//input[@class='sp-input'])[2]");
                    chooseBtn = findByXpath("(//button[@class='sp-choose'])[2]");
                    newColor = baColor;
                }else{
                    //Views here
                    changerBtn = findByXpath("//md-dialog-content//div[contains(., 'Base view')]/span[@ng-model='type.color']");
                    colorInput = findByXpath("(//input[@class='sp-input'])[3]");
                    chooseBtn = findByXpath("(//button[@class='sp-choose'])[3]");
                    newColor = bvColor;
                }
            }else{
                //Home and other non-base selections here
                if(area.isArea()){
                    //Areas here
                    changerBtn = findByXpath("(//md-dialog-content//div[contains(., 'Area')]/span[@ng-model='type.color'])[2]");
                    colorInput = findByXpath("(//input[@class='sp-input'])[5]");
                    chooseBtn = findByXpath("(//button[@class='sp-choose'])[5]");
                    newColor = haColor;
                }else{
                    //Views here
                    changerBtn = findByXpath("//md-dialog-content//div[contains(., 'View')]/span[@ng-model='type.color']");
                    colorInput = findByXpath("(//input[@class='sp-input'])[6]");
                    chooseBtn = findByXpath("(//button[@class='sp-choose'])[6]");
                    newColor = hvColor;
                }
            }

            clickOn(changerBtn);
            if(getBrowser().equals(FIREFOX)){
                //FirefoxDriver bug workaround
                for (int i = 0; i<20; i++){
                    colorInput.sendKeys(Keys.BACK_SPACE);
                }
                colorInput.sendKeys(newColor);
                colorInput.sendKeys(Keys.ENTER);
            }else{
                typeInto(colorInput, newColor);
            }
            waitForElementToBeEnabled(chooseBtn, 3);
            clickOn(chooseBtn);
            waitForElementToBeInvisible(chooseBtn);
            newColors.put(area.getName(), newColor);
        }

        applyBtn = findByXpath("//button[@ng-click='sdoc.apply()']");
        clickOn(applyBtn);
        waitForElementToBeInvisible(applyBtn);
        switchToProjectFrame();
        WebElement body = findByXpath("//body");
        waitForElementToStopMoving(body);
        switchToDefaultContent();

        return newColors;
    }

    private Map<String, Map<String, String>> getBorderColors(Area[] areas){
        Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();

        switchToProjectFrame();
        for(Area area:areas){
            WebElement selection = findByXpath(area.getXpath());
            Map<String, String> borderTypeAndColor = new HashMap<String, String>();

            if(getBrowser().equals(CHROME)) {
                String[] borderCssAttributes = selection.getCssValue("border").split(" ", 3);
                borderTypeAndColor.put(AREA_BORDER_TYPE_KEY, borderCssAttributes[1]);
                borderTypeAndColor.put(AREA_BORDER_COLOR_KEY, borderCssAttributes[2]);

            }else{
                borderTypeAndColor.put(AREA_BORDER_TYPE_KEY, selection.getCssValue("border-bottom-style"));
                borderTypeAndColor.put(AREA_BORDER_COLOR_KEY, selection.getCssValue("border-bottom-color"));
            }
            map.put(area.getName(), borderTypeAndColor);
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
