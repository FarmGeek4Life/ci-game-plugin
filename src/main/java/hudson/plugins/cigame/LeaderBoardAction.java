package hudson.plugins.cigame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import hudson.Extension;
import hudson.model.Api;
import hudson.model.RootAction;
import hudson.model.User;
import hudson.security.ACL;
import hudson.security.AccessControlled;
import hudson.security.Permission;
import hudson.util.VersionNumber;
import jenkins.model.Jenkins;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * Leader board for users participating in the game.
 * 
 * @author Erik Ramfelt
 */
@ExportedBean(defaultVisibility = 999)
@Extension
public class LeaderBoardAction implements RootAction, AccessControlled {

    private static final long serialVersionUID = 1L;

    @Override
    public String getDisplayName() {
        return Messages.Leaderboard_Title();
    }

    @Override
    public String getIconFileName() {
        return GameDescriptor.ACTION_LOGO_MEDIUM;
    }

    @Override
    public String getUrlName() {
        return "/cigame"; //$NON-NLS-1$
    }

    /**
     * Returns the user that are participants in the ci game
     * 
     * @return list containing users.
     */
    @Exported
    public List<UserScore> getUserScores() {
        return getUserScores(User.getAll(), Jenkins.getInstance().getDescriptorByType(GameDescriptor.class).getNamesAreCaseSensitive());
    }
    
    @Exported
    public boolean isUserAvatarSupported() {
        return new VersionNumber(Jenkins.VERSION).isNewerThan(new VersionNumber("1.433"));
    }

    List<UserScore> getUserScores(Collection<User> users, boolean usernameIsCasesensitive) {
        ArrayList<UserScore> list = new ArrayList<UserScore>();

        Collection<User> players;
        if (usernameIsCasesensitive) {
            players = users;
        } else {
            List<User> playerList = new ArrayList<User>();
            CaseInsensitiveUserIdComparator caseInsensitiveUserIdComparator = new CaseInsensitiveUserIdComparator();
            for (User user : users) {
                if (Collections.binarySearch(playerList, user, caseInsensitiveUserIdComparator) < 0) {
                    playerList.add(user);
                }
            }
            players = playerList;
        }
        
        for (User user : players) {
            UserScoreProperty property = user.getProperty(UserScoreProperty.class);
            if ((property != null) && property.isParticipatingInGame()) {
                list.add(new UserScore(user, property.getScore(), user.getDescription()));
            }
        }

        Collections.sort(list, new Comparator<UserScore>() {
            @Override
            public int compare(UserScore o1, UserScore o2) {
                return Double.compare(o2.score, o1.score);
            }
        });

        return list;
    }

    public void doResetScores( StaplerRequest req, StaplerResponse rsp ) throws IOException {
        if (Jenkins.getInstance().getACL().hasPermission(Jenkins.ADMINISTER)) {
            doResetScores(User.getAll());
        }
        rsp.sendRedirect2(req.getContextPath());
    }

    void doResetScores(Collection<User> users) throws IOException {
        for (User user : users) {
            UserScoreProperty property = user.getProperty(UserScoreProperty.class);
            if (property != null) {
                property.setScore(0);
                user.save();
            }
        }
    }

    
    @ExportedBean(defaultVisibility = 999)
    public static class UserScore {
        private final User user;
        private final double score;
        private final String description;

        public UserScore(User user, double score, String description) {
            super();
            this.user = user;
            this.score = score;
            this.description = description;
        }

        @Exported
        public User getUser() {
            return user;
        }

        @Exported
        public double getScore() {
            return score;
        }

        @Exported
        public String getDescription() {
            return description;
        }
    }

    @Override
    public ACL getACL() {
        return Jenkins.getInstance().getACL();
    }

    @Override
    public void checkPermission(Permission p) {
        getACL().checkPermission(p);
    }

    @Override
    public boolean hasPermission(Permission p) {
        return getACL().hasPermission(p);
    }

    public Api getApi() {
        return  new Api(this);
    }
}
