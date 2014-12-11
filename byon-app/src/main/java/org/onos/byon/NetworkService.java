package org.onos.byon;

import org.onosproject.net.HostId;

import java.util.Set;

/**
 * Service that allows to create virtual named networks
 * which provide mesh connectivity between hosts of a
 * given network.
 */
public interface NetworkService {

    /**
     * Create a named network.
     *
     * @param network network name
     */
    void createNetwork(String network);

    /**
     * Deletes a named network.
     *
     * @param network network name
     */
    void deleteNetwork(String network);

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
     * @param hostId host id
     */
    void addHost(String network, HostId hostId);

    /**
     * Removes a host from the given network.
     *
     * @param network network name
     * @param hostId host id
     */
    void removeHost(String network, HostId hostId);

    /**
     * Returns all the hosts in a network.
     *
     * @param network network name
     * @return set of host ids
     */
    Set<HostId> getHosts(String network);

}
