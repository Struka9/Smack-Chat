package presidente.oscar.smackchat.events;

import org.jivesoftware.smack.roster.RosterEntry;

import java.util.Set;

/**
 * Created by oscarr on 8/17/16.
 */
public class RosterEntriesUpdatedEvent {
    public Set<RosterEntry> mContactList;
}
