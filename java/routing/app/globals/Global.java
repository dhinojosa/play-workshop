package globals;

import actors.ReceiverActor;
import akka.actor.Props;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.libs.Akka;

/**
 * @author Daniel Hinojosa
 * @since 12/2/13 10:38 AM
 *        url: <a href="http://www.evolutionnext.com">http://www.evolutionnext.com</a>
 *        email: <a href="mailto:dhinojosa@evolutionnext.com">dhinojosa@evolutionnext.com</a>
 *        tel: 505.363.5832
 */
public class Global extends GlobalSettings {

    @Override
    public void onStart(Application app) {
        Logger.info("Application has started");
        Akka.system().actorOf(new Props(ReceiverActor.class), "receiverActor");
    }

    @Override
    public void onStop(Application app) {
        Logger.info("Application shutdown...");
    }
}
