package network;

import utils.Event;

public abstract class NetworkEvent implements Event {
	public final Client client;

	NetworkEvent(Client client) {
		this.client = client;
	}
}
