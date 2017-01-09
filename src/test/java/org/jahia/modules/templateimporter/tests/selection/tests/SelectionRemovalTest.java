package org.jahia.modules.templateimporter.tests.selection.tests;

import org.jahia.modules.templateimporter.tests.TemplateImporterRepository;
import org.jahia.modules.templateimporter.tests.businessobjects.Component;
import org.jahia.modules.tests.utils.SoftAssertWithScreenshot;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

/**
 * Created by sergey on 2016-07-13.
 */
public class SelectionRemovalTest extends TemplateImporterRepository{
    @Test //TI_S2C22
    public void areaRemovalTest(){
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "SelectionRemovalTest.areaRemovalTest");
        String projectName = randomWord(8);
        String xPathToSelectInBase = "//body/div[1]";
        String areaNameA1 = randomWord(5);
        int xOffsetA1Area = 1;
        int yOffsetA1Area = 0;

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        selectArea(areaNameA1, xPathToSelectInBase, xOffsetA1Area, yOffsetA1Area);
        removeArea(xPathToSelectInBase, xOffsetA1Area, yOffsetA1Area);
        softAssert.assertAll();
    }

    @Test //TI_S2C23
    public void viewRemovalTest(){
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "SelectionRemovalTest.viewRemovalTest");
        String projectName = randomWord(8);
        String xPathAreaA1 = "//body/div[1]/div[1]";
        String xPathViewV1 = "//body/div[1]/div[1]/div[1]";
        String nameAreaA1 = randomWord(5);
        String nameViewV1 = randomWord(5);
        String nodeTypeViewV1 = "jnt:bigText";
        int xOffsetA1Area = 2;
        int xOffsetViewV1 = 1;
        int yOffsetA1Area = 0;
        int yOffsetViewV1 = 0;

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        selectArea(nameAreaA1, xPathAreaA1, xOffsetA1Area, yOffsetA1Area);
        selectView(nameViewV1, nodeTypeViewV1, xPathViewV1, xOffsetViewV1, yOffsetViewV1);
        removeView(xPathViewV1, xOffsetViewV1, yOffsetViewV1);
        checkIfAreaSelected(xPathAreaA1, softAssert, true, "Removed view from that area on base");
        checkIfViewSelected(xPathViewV1, softAssert, false, "Removed that view from an area on base");
        selectView(nameViewV1, nodeTypeViewV1, xPathViewV1, xOffsetViewV1, yOffsetViewV1, "Re-selecting same element with the same name as a view, after removing it");
        checkIfAreaSelected(xPathAreaA1, softAssert, true, "After re-selecting removed view");
        softAssert.assertAll();
    }

    @Test
    public void componentRemovalTest(){
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "SelectionRemovalTest.componentRemovalTest");
        String projectName = randomWord(15);
        Component homeC1 = new Component(randomWord(2), randomWord(6), randomWord(6), "jnt:html", "/html/body/div[3]", 2, 0, "home");

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        selectComponent(homeC1, "Selecting component");
        removeView(homeC1.getXpath(), homeC1.getxOffset(), homeC1.getyOffset());
        checkIfComponentSelected(homeC1, softAssert, false, "Component after removal");
        selectComponent(homeC1, "Selecting same component after removal");
        checkIfComponentSelected(homeC1, softAssert, true, "Component after removal and re-selecting");

        softAssert.assertAll();
    }

    @Test //TI_S2C24
    public void areaWithViewRemovalTest(){
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "SelectionRemovalTest.areaWithViewRemovalTest");
        String projectName = randomWord(8);
        String xPathAreaA1 = "//body/div[1]";
        String xPathViewV1 = "//body/div[1]/div[1]";
        String nameAreaA1 = randomWord(5);
        String nameViewV1 = randomWord(5);
        String nodeTypeViewV1 = "jnt:bigText";
        int xOffsetA1Area = 1;
        int xOffsetViewV1 = 2;
        int yOffsetA1Area = 0;
        int yOffsetViewV1 = 0;

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        selectArea(nameAreaA1, xPathAreaA1, xOffsetA1Area, yOffsetA1Area);
        selectView(nameViewV1, nodeTypeViewV1, xPathViewV1, xOffsetViewV1, yOffsetViewV1);
        removeArea(xPathAreaA1, xOffsetA1Area, yOffsetA1Area);
        checkIfAreaSelected(xPathAreaA1, softAssert, false);
        checkIfViewSelected(xPathViewV1, softAssert, false);
        softAssert.assertAll();
    }

    @Test (enabled = false)//TI_S2C25 //Test disabled due to functionality change. There is no mo area inheritance.
    public void viewRemovalCrossTemplateTest(){
        SoftAssert softAssert = new SoftAssertWithScreenshot(getDriver(), "SelectionRemovalTest.viewRemovalCrossTemplateTest");
        String projectName = randomWord(8);
        String xPathAreaA1 = "//body/div[1]";
        String xPathAreaA2 = "//body/div[1]/div[1]";
        String xPathViewV1 = "//body/div[1]/div[1]/div[1]";
        String nameAreaA1 = randomWord(5);
        String nameAreaA2 = randomWord(8);
        String nameViewV1 = randomWord(5);
        String nodeTypeViewV1 = "jnt:bootstrapMainContent";
        int xOffsetA1Area = 1;
        int xOffsetA2Area = 2;
        int xOffsetViewV1 = 1;
        int yOffsetA1Area = 0;
        int yOffsetA2Area = 0;
        int yOffsetViewV1 = 0;

        importProject("en", projectName, "", "AlexLevels.zip");
        openProjectFirstTime(projectName, "index.html");
        selectArea(nameAreaA1, xPathAreaA1, xOffsetA1Area, yOffsetA1Area);
        switchToTemplate("home");
        selectArea(nameAreaA2, xPathAreaA2, xOffsetA2Area, yOffsetA2Area);
        selectView(nameViewV1, nodeTypeViewV1, xPathViewV1, xOffsetViewV1, yOffsetViewV1);
        switchToTemplate("base");
        removeArea(xPathAreaA1, xOffsetA1Area, yOffsetA1Area, "Removing area from base that has view on home");
        switchToTemplate("home");
        checkIfAreaSelected(xPathAreaA2, softAssert, false, "That area is in home. Her parent was removed from base");
        checkIfViewSelected(xPathViewV1, softAssert, false, "Parent area of that view on home,  was remover from base");
        softAssert.assertAll();
    }
}
