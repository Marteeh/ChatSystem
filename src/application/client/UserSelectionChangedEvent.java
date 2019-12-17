package application.client;

import application.User;

class UserSelectionChangedEvent extends GUIEvent {
	
	final User user;
	
	UserSelectionChangedEvent(User user) {
		this.user = user;
	}
	
}
