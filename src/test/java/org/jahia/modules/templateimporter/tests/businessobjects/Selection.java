package org.jahia.modules.templateimporter.tests.businessobjects;

/**
 * Created by Sergey Mokhov (sergey) on 2017-01-17.
 */
public class Selection {
    private String name;
    private String xPath;
    private String templateName;
    private int xOffset;
    private int yOffset;

    public Selection(String name, String xPath, int xOffset, int yOffset, String templateName){
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

    public boolean isArea(){
        return false;
    }

    public boolean isComponent(){
        return false;
    }
}
