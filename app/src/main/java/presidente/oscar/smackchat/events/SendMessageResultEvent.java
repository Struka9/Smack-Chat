package presidente.oscar.smackchat.events;

import presidente.oscar.smackchat.models.MessageModel;

/**
 * Created by oscarr on 8/19/16.
 */
public class SendMessageResultEvent {
    public boolean success;
    public MessageModel message;
}
