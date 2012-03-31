package akka.tutorial.first.java;

import java.util.HashSet;
import java.util.Set;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class Cell extends UntypedActor {

	LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	Set<ActorRef> voisins = new HashSet<ActorRef>();

	int vivants;
	int received;

	boolean state;

	@Override
	public void preStart() {
		log.info("prestart");
		super.preStart();
	}

	@Override
	public void onReceive(Object message) throws Exception {
		log.info("nouveau message {}", message);
		if (message instanceof Boolean) {
			received++;
			if ((Boolean) message) {
				vivants += 1;
			}
			if (received == voisins.size()) {
				if (state) {
					if (vivants < 2) {
						die();
					}
					if (vivants > 3) {
						die();
					}
				} else {
					if (vivants == 3) {
						born();
					}
				}
			}
		}
		if (message instanceof ActorRef) {
			voisins.add((ActorRef) message);
			log.info("nouveau voisin ! {}", message);
		}
		if (message instanceof String) {
			if ("alive".equals(message)) {
				state = true;
			}
			if ("areYouAlive".equals(message)) {
				getSender().tell(state);
			}
			if ("tick".equals(message)) {
				for (ActorRef voisin : voisins) {
					voisin.tell(state);
					vivants = 0;
					received = 0;
				}
			}
		}
	}

	private void born() {
		changeState(true);
	}

	private void die() {
		changeState(false);

	}

	private void changeState(boolean b) {
		state = b;
	}

}
