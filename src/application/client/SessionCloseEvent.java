package application.client;

import application.User;

/* Classe identique à SessionEvent, mais nécessaire pour raison de sémantique */
class SessionCloseEvent extends GUIEvent {
	
	final User user;
	
	SessionCloseEvent(User user){
		this.user = user;
	}
}
