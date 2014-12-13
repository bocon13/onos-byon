package org.onos.byon;

import org.onosproject.net.HostId;
import org.onosproject.net.intent.Intent;
import org.onosproject.store.Store;

import java.util.Set;

/**
 * Network Store Interface
 */
public interface NetworkStore extends Store<NetworkEvent, NetworkStoreDelegate> {
    /**
     * Create a named network.
     *
     * @param network network name
     */
    void putNetwork(String network);

    /**
     * Removes a named network.
     *
     * @param network network name
     */
    void removeNetwork(String network);

    /**
     * Returns a set of network names.
     *
     * @return a set of network names
     */
    Set<String> getNetworks();

    /**
     * Adds a host to the given network.
     *
     * @param network network name
     * @param hostId  host id
     * @return updated set of hosts in the network (or an empty set if the host
     * has already been added to the network)
     */
    Set<HostId> addHost(String network, HostId hostId);

    /**
     * Removes a host from the given network.
     *
     * @param network network name
     * @param hostId  host id
     */
    void removeHost(String network, HostId hostId);

    /**
     * Returns all the hosts in a network.
     *
     * @param network network name
     * @return set of host ids
     */
    Set<HostId> getHosts(String network);

    /**
     * Adds a set of intents to a network
     *
     * @param network network name
     * @param intents set of intents
     */
    void addIntents(String network, Set<Intent> intents);

    /**
     * Returns a set of intents given a network and a host.
     *
     * @param network network name
     * @param hostId host id
     * @return set of intents
     */
    Set<Intent> removeIntents(String network, HostId hostId);

    /**
     * Returns a set of intents given a network.
     * @param network network name
     * @return set of intents
     */
    Set<Intent> removeIntents(String network);
}
