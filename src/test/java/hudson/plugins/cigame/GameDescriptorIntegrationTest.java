package hudson.plugins.cigame;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import org.junit.Rule;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.JenkinsRule.WebClient;
import org.jvnet.hudson.test.recipes.LocalData;

public class GameDescriptorIntegrationTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();
    
    public void testThatSettingCaseInsensitiveFlagWorks() throws Exception {
        GameDescriptor descriptor = j.jenkins.getDescriptorByType(GameDescriptor.class);
        WebClient webClient = j.createWebClient();
        webClient.setThrowExceptionOnFailingStatusCode(false);
        
        HtmlForm form = webClient.goTo("configure").getFormByName("config");
        assertThat(form.getInputByName("_.namesAreCaseSensitive").isChecked(), is(true));
        form.getInputByName("_.namesAreCaseSensitive").setChecked(false);
        j.submit(form);
        assertThat(descriptor.getNamesAreCaseSensitive(), is(false));
        
        form = webClient.goTo("configure").getFormByName("config");
        assertThat(form.getInputByName("_.namesAreCaseSensitive").isChecked(), is(false));
        form.getInputByName("_.namesAreCaseSensitive").setChecked(true);
        j.submit(form);
        assertThat(descriptor.getNamesAreCaseSensitive(), is(true));
    }

    @LocalData
    public void testLoadingCaseInsensitiveFlagWorks() throws Exception {
        GameDescriptor descriptor = j.jenkins.getDescriptorByType(GameDescriptor.class);
        assertThat(descriptor.getNamesAreCaseSensitive(), is(false));
        HtmlForm form = j.createWebClient().goTo("configure").getFormByName("config");
        assertThat(form.getInputByName("_.namesAreCaseSensitive").isChecked(), is(false));
    }
}
