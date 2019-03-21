package hudson.plugins.cigame;

import hudson.model.User;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Comparator that ignores casing on the User's ID
 */
class CaseInsensitiveUserIdComparator implements Comparator<User>, Serializable {
    @Override
    public int compare(User arg0, User arg1) {
        return arg0.getId().compareToIgnoreCase(arg1.getId());
    }            
}