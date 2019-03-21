package hudson.plugins.cigame;

import hudson.model.Action;

/**
 * Score card for a job.
 * 
 * @author Erik Ramfelt
 */
public class ScoreBoardAction implements Action {

    private static final long serialVersionUID = 1L;

    @Override
    public String getDisplayName() {
        return Messages.Scorecard_Title();
    }

    @Override
    public String getIconFileName() {
        return "Scorecard.gif"; //$NON-NLS-1$
    }

    @Override
    public String getUrlName() {
        return "cigame"; //$NON-NLS-1$
    }

}
