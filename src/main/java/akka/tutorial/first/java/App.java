package akka.tutorial.first.java;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.dispatch.Future;
import akka.dispatch.Futures;
import akka.dispatch.Mapper;

/**
 * Hello world!
 * 
 */
public class App {
	static ActorSystem system = ActorSystem.create("game");
	static ActorRef cells[][] = new ActorRef[3][3];
	
	public static void main(String[] args) {
		System.out.println("Hello World!");
		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[i].length; j++) {
				cells[i][j] = system.actorOf(new Props(Cell.class), ""+i+j);
			}
		}
		
		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[i].length; j++) {
				
				if (exists(i-1, j)) {
					cells[i][j].tell(cells[i-1][j]);
				}
				if (exists(i-1, j-1)) {
					cells[i][j].tell(cells[i-1][j-1]);
				}
				if (exists(i, j-1)) {
					cells[i][j].tell(cells[i][j-1]);
				}
				if (exists(i+1, j)) {
					cells[i][j].tell(cells[i+1][j]);
				}
				if (exists(i+1, j+1)) {
					cells[i][j].tell(cells[i+1][j+1]);
				}
				if (exists(i, j+1)) {
					cells[i][j].tell(cells[i][j+1]);
				}
				if (exists(i-1, j+1)) {
					cells[i][j].tell(cells[i-1][j+1]);
				}
				if (exists(i+1, j-1)) {
					cells[i][j].tell(cells[i+1][j-1]);
				}
			}
		}
		
		//oscillo
		cells[0][1].tell("alive"); 
		cells[1][1].tell("alive"); 
		cells[2][1].tell("alive"); 
		
		//tick
		List<Future<Object>> futures = new ArrayList<Future<Object>>();
		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[i].length; j++) {
				futures.add(akka.pattern.Patterns.ask(cells[i][j], "tick", 1000));
			}
		}
		
		Future<Iterable<Object>> aggregate = Futures.sequence(futures, system.dispatcher());
		Future<Boolean> results = aggregate.map(new Mapper<Iterable<Object>, Boolean>() {
			public Boolean apply(Iterable<Object> col) {
				final Iterator<Object> it = col.iterator();
				return (Boolean) it.next();
			}
		});
		
		akka.pattern.Patterns.pipe(results).to(cells[0][0]);
		
	}
	
	private static boolean exists(int i, int j) {
		return (i >= 0 && i < cells.length) && (j >= 0 && j < cells[i].length);
	}
}
