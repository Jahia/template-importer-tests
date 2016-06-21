package org.jahia.modules.templateimporter.tests.projectmanagement.tests;

import org.jahia.modules.templateimporter.tests.TemplateImporterRepository;
import org.testng.annotations.Test;

/**
 * Created by sergey on 2016-06-16.
 */
public class ImportTest extends TemplateImporterRepository {
    @Test
    public void importTest(){
        String projectName = randomWord(10);
        String projectDescription = randomWord(300);

        importProject("en", projectName, projectDescription, "AlexLevels.zip");
    }
}
