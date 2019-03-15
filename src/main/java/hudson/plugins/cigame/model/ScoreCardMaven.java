package hudson.plugins.cigame.model;

import hudson.maven.MavenBuild;
import hudson.maven.MavenModule;
import hudson.maven.MavenModuleSetBuild;
import hudson.model.AbstractBuild;
import hudson.plugins.cigame.util.BuildUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Secondary class to check Maven Project Build data
 *
 */
public class ScoreCardMaven {
    public static boolean isMavenProjectBuild(AbstractBuild<?, ?> build) {
        try {
            // If we can load the maven-plugin
            MavenModuleSetBuild.class.getClass();
            // Then we can check if it's a MavenModuleSetBuild
            return build instanceof hudson.maven.MavenModuleSetBuild;
        } catch (Throwable t) {
            // We can't load the plugin, so assume that it isn't a Maven Project build
            return false;
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static RuleResult<?> evaluate(AbstractBuild<?, ?> build, AggregatableRule aRule) {
        if (!isMavenProjectBuild(build)){
            return null;
        }

        return new ScoreCardMaven().evaluateMaven(build, aRule);
    }
    
    private ScoreCardMaven() {
        // Intentionally empty
    }
    
    public RuleResult<?> evaluateMaven(AbstractBuild<?, ?> build, AggregatableRule aRule) {
        MavenModuleSetBuild mavenModuleSetBuild = (MavenModuleSetBuild)build;

        List<RuleResult> results = new ArrayList<RuleResult>();

        for (Map.Entry<MavenModule, MavenBuild> e : mavenModuleSetBuild.getModuleLastBuilds().entrySet()) {
            AbstractBuild moduleBuild = e.getValue();
            if (moduleBuild != null) {
                AbstractBuild<?, ?> previousBuild = BuildUtil.getPreviousBuiltBuild(moduleBuild);
                results.add(aRule.evaluate(previousBuild, moduleBuild));
            } else {
                // module was probably removed from multimodule
                if (mavenModuleSetBuild.getPreviousBuild() != null) {
                    MavenModuleSetBuild prevBuild = mavenModuleSetBuild.getPreviousBuild();
                    AbstractBuild<?, ?> prevModuleBuild = prevBuild.getModuleLastBuilds().get(e.getKey());
                    if (prevModuleBuild.getResult() == null) {
                        prevModuleBuild = BuildUtil.getPreviousBuiltBuild(prevModuleBuild);
                    }
                    results.add(aRule.evaluate(prevModuleBuild, null));
                } else {
                    //results.add(aRule.evaluate(null, null));
                    return RuleResult.EMPTY_RESULT;
                }
            }
        }
        return aRule.aggregate(results);
    }
}
