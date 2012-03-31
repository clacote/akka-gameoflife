package akka.tutorial.first.java;

import org.junit.Test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;


public class CellTest {
	
	@Test
	public void voisinNe(){
		//...
		//...
		//...
		ActorSystem system = ActorSystem.create("game");
		ActorRef cell00 =  system.actorOf(new Props(Cell.class), "00");
		ActorRef cell01 =  system.actorOf(new Props(Cell.class), "01");
		cell00.tell(cell01);
	}
	
}
