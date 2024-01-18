import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectFile("src/test/java/equivalencePartitioning.feature")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "")
public class AppTest {

    /*@Test
    void myTest() throws InvocationTargetException, IllegalAccessException {
        //TODO
    }*/
}
