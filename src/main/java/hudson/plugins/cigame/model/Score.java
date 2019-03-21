package hudson.plugins.cigame.model;

import java.util.Objects;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * Recorded score for a rule and build.
 * 
 */
@ExportedBean(defaultVisibility=999)
public class Score implements Comparable<Score> {
    private final String rulesetName;
    private final String ruleName;
    private final double value;
    private final String description;

    public Score(String rulesetName, String ruleName, double points, String pointDescription) {
        this.rulesetName = rulesetName;
        this.ruleName = ruleName;
        this.value = points;
        description = pointDescription;
    }

    @Exported
    public String getDescription() {
        if (description == null) {
            return rulesetName + " - " + ruleName; //$NON-NLS-1$
        }
        return description;
    }

    @Exported
    public String getRulesetName() {
        return rulesetName;
    }

    @Exported
    public String getRuleName() {
        return ruleName;
    }

    @Exported
    public double getValue() {
        return value;
    }

    @Override
    public int compareTo(Score o) {
        int byValue = Double.compare(o.value, value);
        if (byValue == 0) {
            return description.compareToIgnoreCase(o.description);
        }
        return byValue;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + (int) (Double.doubleToLongBits(this.value) ^ (Double.doubleToLongBits(this.value) >>> 32));
        hash = 41 * hash + Objects.hashCode(this.description);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Score other = (Score) obj;
        if (Double.doubleToLongBits(this.value) != Double.doubleToLongBits(other.value)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        return true;
    }
}
