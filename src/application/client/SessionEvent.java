package application.client;

import application.User;

class SessionEvent extends GUIEvent {
	
	final User user;
	
	SessionEvent(User user) {
		this.user = user;
	}

}
