package org.jahia.modules.templateimporter.tests.businessobjects;

/**
 * Created by sergey on 2016-08-22.
 */
public class Area {
    private String name;
    private String xPath;
    private String templateName;
    private String xPathToInternalArea;
    private int xOffset;
    private int yOffset;

    public Area(String name, String xPath, int xOffset, int yOffset, String templateName){
        this.name = name;
        this.xPath = xPath;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.templateName = templateName;
        this.xPathToInternalArea = null;
    }

    public Area(String name, String xPath, String xPathToInternalArea, int xOffset, int yOffset, String templateName){
        this.name = name;
        this.xPath = xPath;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.templateName = templateName;
        this.xPathToInternalArea = xPathToInternalArea;
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

    public boolean isArea(){
        return true;
    }

    public boolean isComponent(){
        return false;
    }

    public String getxPathToInternalArea() {
        return xPathToInternalArea;
    }
}
