package actors;

import akka.actor.UntypedActor;
import play.cache.Cache;
import play.mvc.WebSocket;

import java.util.List;

/**
 * @author Daniel Hinojosa
 * @since 11/30/13 11:46 PM
 *        url: <a href="http://www.evolutionnext.com">http://www.evolutionnext.com</a>
 *        email: <a href="mailto:dhinojosa@evolutionnext.com">dhinojosa@evolutionnext.com</a>
 *        tel: 505.363.5832
 */
public class ReceiverActor extends UntypedActor {
    /**
     * To be implemented by concrete UntypedActor, this defines the behavior of the
     * UntypedActor.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void onReceive(Object message) throws Exception {
        for (WebSocket.Out out : (List<WebSocket.Out>) Cache.get("channels")) {
            out.write("Message received:" + message);
        }
    }
}
