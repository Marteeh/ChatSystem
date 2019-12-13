package application.client;

public class RenamePseudoEvent extends GUIEvent {
	
	public final String pseudo;
	
	RenamePseudoEvent(String pseudo) {
		this.pseudo = pseudo;
	}
}
