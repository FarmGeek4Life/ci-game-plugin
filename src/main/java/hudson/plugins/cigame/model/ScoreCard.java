package hudson.plugins.cigame.model;

import hudson.model.BuildListener;
import hudson.model.AbstractBuild;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import jenkins.model.Jenkins;

import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * Score card containing the results of evaluating the rules against a build.
 * 
 * 
 */
@ExportedBean(defaultVisibility=999)
public class ScoreCard {

    private List<Score> scores;

    /**
     * Record points for the rules in the rule set
     * 
     * @param build build to evaluate
     * @param ruleset rule set to use for evaluation
     * @param listener 
     */
    public void record(AbstractBuild<?, ?> build, RuleSet ruleset, BuildListener listener) {
        
        List<Score> scoresForBuild = new LinkedList<Score>();
        for (Rule rule : ruleset.getRules()) {
        	if (null != rule){
	        	if (listener != null) {
	        		listener.getLogger().append("[ci-game] evaluating rule: " + rule.getName() + "\n");
	        	}
	            RuleResult<?> result = evaluate(build, rule);
	            if ((result != null) && (result.getPoints() != 0)) {
	                Score score = new Score(ruleset.getName(), rule.getName(), result.getPoints(), result.getDescription());
	                scoresForBuild.add(score);
	                if (listener != null) {
	            		listener.getLogger().append("[ci-game] scored: " + score.getValue() + "\n");
	            	}
	            }
			} else {
				if (listener != null) {
					listener.getLogger().append("[ci-game] null rule encountered\n");
				}
			}
        }
        
        // prevent ConcurrentModificationExceptions for e.g. matrix builds (see JENKINS-11498):
        synchronized(this) {
            if (scores == null) {
                scores = new LinkedList<Score>();
            }
            scores.addAll(scoresForBuild);
            Collections.sort(scores);
        }
    }

    private boolean isMavenPluginAvailable() {
        return Jenkins.getInstance().getPlugin("maven-plugin") != null;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    RuleResult<?> evaluate(AbstractBuild<?, ?> build, Rule rule) {
        if (rule instanceof AggregatableRule<?>) {
            AggregatableRule aRule = (AggregatableRule<?>)rule;
            RuleResult<?> mavenResult = null;
            
            if (isMavenPluginAvailable()) {
                mavenResult = ScoreCardMaven.evaluate(build, aRule);
            }
            
            if (mavenResult != null) {
                // Not Null = Maven Project Build
                return mavenResult;
            } else {
                // mvnResult == null: can't load maven-plugin, or not a Maven Project Build
                return aRule.evaluate(build.getPreviousBuild(), build);
            }
        } else {
            return rule.evaluate(build);
        }
    }
    

    /**
     * Record points for the rules in the rule book
     * 
     * @param build build to evaluate
     * @param ruleBook rule book to use for evaluation
     * @param listener 
     */
    public void record(AbstractBuild<?, ?> build, RuleBook ruleBook, BuildListener listener) {
        if (scores == null) {
            scores = new LinkedList<Score>();
        }
        for (RuleSet set : ruleBook.getRuleSets()) {
            record(build, set, listener);
        }
    }

    /**
     * Returns a collection of scores. May not be called before the score has
     * been recorded.
     * 
     * @return a collection of scores.
     * @throws IllegalStateException thrown if the method is called before the scores has been recorded.
     */
    @Exported
    public Collection<Score> getScores() throws IllegalStateException {
        if (scores == null) {
            throw new IllegalStateException("No scores are available"); //$NON-NLS-1$
        }
        return scores;
    }

    /**
     * Returns the total points for this score card
     * 
     * @return the total points for this score card
     * @throws IllegalStateException thrown if the method is called before scores has been calculated
     */
    @Exported
    public double getTotalPoints() throws IllegalStateException {
        if (scores == null) {
            throw new IllegalStateException("No scores are available"); //$NON-NLS-1$
        }
        double value = 0;
        for (Score score : scores) {
            value += score.getValue();
        }
        return value;
    }
}
