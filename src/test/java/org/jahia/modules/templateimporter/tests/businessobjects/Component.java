package org.jahia.modules.templateimporter.tests.businessobjects;

/**
 * Created by sergey on 2016-09-27.
 */
public class Component extends Selection {
    private String areaName;
    private String viewName;

    public Component(String componentName, String areaName, String viewName, String xPathToComponent, int xOffset, int yOffset, String templateName){
        super(componentName, xPathToComponent, xOffset, yOffset, templateName);
        this.areaName = areaName;
        this.viewName = viewName;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public boolean isComponent(){
        return true;
    }
}
