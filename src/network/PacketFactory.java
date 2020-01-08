package network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PacketFactory {
	private final Map<Class<? extends Packet>, Integer> typeMap = new HashMap<Class<? extends Packet>, Integer>();
	private final List<Class<? extends Packet>> typeList = new ArrayList<Class<? extends Packet>>();
	
	public void registerPacket(Class<? extends Packet> packetType) {
		typeMap.put(packetType, typeMap.size());
		typeList.add(packetType);
	}
	
	public Packet createPacket(int packetType) throws InstantiationException, IllegalAccessException {
		return typeList.get(packetType).newInstance();
	}
	
	public int getPacketType(Packet packet) {
		return typeMap.get(packet.getClass());
	}
}
