package hudson.plugins.cigame;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import com.gargoylesoftware.htmlunit.html.HtmlTable;
import org.junit.Rule;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.recipes.LocalData;

public class ScoreCardActionIntegrationTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();

    @LocalData
    public void testThatUsernameWithDifferentCasingIsDisplayedAsOne() throws Exception {
        j.jenkins.getDescriptorByType(GameDescriptor.class).setNamesAreCaseSensitive(false);
        HtmlTable table = (HtmlTable) j.createWebClient().goTo("job/multiple-culprits/4/cigame/").getHtmlElementById("game.culprits");
        assertThat(table.getRowCount(), is(2));
    }
    
    @LocalData
    public void testThatUsernameWithDifferentCasingIsNotDisplayedAsOne() throws Exception {
        HtmlTable table = (HtmlTable) j.createWebClient().goTo("job/multiple-culprits/4/cigame/").getHtmlElementById("game.culprits");
        assertThat(table.getRowCount(), is(3));        
    }
}
