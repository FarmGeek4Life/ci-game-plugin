package hudson.plugins.cigame;

import hudson.model.User;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import org.junit.Rule;
import org.jvnet.hudson.test.JenkinsRule;

public class UserScorePropertyDescriptorIntegrationTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();
    
    public void testConfiguringWorksForNewUser() throws Exception {
        HtmlForm userConfigurationForm = j.createWebClient().goTo(User.get("test").getUrl() + "/configure").getFormByName("config");
        j.submit(userConfigurationForm);
    }
}
