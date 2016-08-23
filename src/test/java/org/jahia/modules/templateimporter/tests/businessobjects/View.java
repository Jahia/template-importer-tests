package org.jahia.modules.templateimporter.tests.businessobjects;

/**
 * Created by sergey on 2016-08-22.
 */
public class View extends Area{
    private String nodeType;

    public View(String viewName, String nodeType, String xPathToView, int xOffset, int yOffset, String templateName){
        super(viewName, xPathToView, xOffset, yOffset, templateName);
        this.nodeType = nodeType;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public boolean isArea(){
        return false;
    }
}
