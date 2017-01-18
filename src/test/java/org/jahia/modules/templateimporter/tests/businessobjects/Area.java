package org.jahia.modules.templateimporter.tests.businessobjects;

/**
 * Created by sergey on 2016-08-22.
 */
public class Area extends Selection{
    public Area(String name, String xPath, int xOffset, int yOffset, String templateName){
        super(name, xPath, xOffset, yOffset, templateName);
    }

    public boolean isArea(){
        return true;
    }
}
