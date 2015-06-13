/*
 * Copyright 2015 Open Networking Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onos.byon;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.event.EventDeliveryService;
import org.onosproject.event.ListenerRegistry;
import org.onosproject.net.HostId;
import org.onosproject.net.intent.HostToHostIntent;
import org.onosproject.net.intent.Intent;
import org.onosproject.net.intent.IntentService;
import org.onosproject.net.intent.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;

/**
 * BYON Application component.
 */
@Component(immediate = true)
@Service
public class NetworkManager implements NetworkService {

    private static Logger log = LoggerFactory.getLogger(NetworkManager.class);

    public static final String HOST_FORMAT = "%s~%s";
    public static final String KEY_FORMAT = "%s,%s";

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected NetworkStore store;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected CoreService coreService;

    /*
     * TODO Lab 3: Get a reference to the intent service
     *
     * All you need to do is uncomment the following two lines.
     */
    //@Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    //protected IntentService intentService;


    /*
     * TODO Lab 6: Get a reference to the event delivery service
     *
     * All you need to do is uncomment the following two lines.
     */
    //@Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    //protected EventDeliveryService eventDispatcher;

    /*
     * TODO Lab 6: Construct a ListenerRegistry<NetworkEvent, NetworkListener>
     *
     * This will be used to keep track of external listeners.
     */

    /*
     * TODO Lab 6: Instantiate a NetworkStoreDelegate
     *
     * You will first need to implement the class (at the bottom of the file).
     */

    protected ApplicationId appId;

    @Activate
    protected void activate() {
        appId = coreService.registerApplication("org.onos.byon");
        /*
         * TODO Lab 6: Remove delegate and event sink
         *
         * 1. Add the listener registry to the event dispatcher using eventDispatcher.addSink()
         * 2. Set the delegate in the store
         */
        log.info("Started");
    }

    @Deactivate
    protected void deactivate() {
        /*
         * TODO Lab 6: Remove delegate and event sink
         *
         * 1. Remove the event class from the event dispatcher using eventDispatcher.removeSink()
         * 2. Unset the delegate from the store
         */
        log.info("Stopped");
    }

    @Override
    public void createNetwork(String network) {
        checkNotNull(network, "Network name cannot be null");
        checkState(!network.contains(","), "Network names cannot contain commas");
        /*
         * TODO Lab 2: Add the network to the store
         */
    }

    @Override
    public void deleteNetwork(String network) {
        checkNotNull(network, "Network name cannot be null");
        /*
         * TODO Lab 2: Remove the network from the store
         */
        /*
         * TODO Lab 4: Remove the intents when the network is deleted
         */
    }

    @Override
    public Set<String> getNetworks() {
        /*
         * TODO Lab 2: Get the networks from the store and return them
         */
        return ImmutableSet.of("my-network"); // TODO remove this line before starting lab 2
    }

    @Override
    public void addHost(String network, HostId hostId) {
        checkNotNull(network, "Network name cannot be null");
        checkNotNull(hostId, "HostId cannot be null");
        /*
         * TODO Lab 2: Add the host to the network in the store
         *
         * TODO Lab 3: Connect the host to the network using intents -- addIntents()
         *     You only need to add the intents if this is the first time that
         *     the host is added. (Check the store's return value)
         */
    }

    @Override
    public void removeHost(String network, HostId hostId) {
        checkNotNull(network, "Network name cannot be null");
        checkNotNull(hostId, "HostId cannot be null");
        /*
         * TODO Lab 2: Remove the host from the network in the store
         *
         * TODO Lab 4: Remove the host's intents from the network
         */
    }

    @Override
    public Set<HostId> getHosts(String network) {
        checkNotNull(network, "Network name cannot be null");
        /*
         * TODO Lab 2: Retrieve the hosts from the store and return them
         */
        return ImmutableSet.of(); // TODO remove this line before starting lab 2
    }

    /**
     * Adds an intent between a new host and all others in the network.
     *
     * @param network network name
     * @param src the new host
     * @param hostsInNet all hosts in the network
     */
    private void addIntents(String network, HostId src, Set<HostId> hostsInNet) {
        /*
         * TODO Lab 3: Implement add intents
         *
         * 1. Create a HostToHostIntent intent between src and every other host in
         *    the network using HostToHostIntent.builder()
         * 2. Generate the intent key using generateKey(), so they can be removed later
         * 3. Submit the intents using intentService.submit()
         */
    }

    /**
     * Removes intents that involve the specified host in a network.
     *
     * @param network network name
     * @param hostId host to remove; all hosts if empty
     */
    private void removeIntents(String network, Optional<HostId> hostId) {
        /*
         * TODO Lab 4: Implement remove intents
         *
         * 1. Get the intents from the intent service using intentService.getIntents()
         * 2. Using matches() to filter intents for this network and hostId
         * 3. Withdrawn intentService.withdraw()
         */
    }

    /**
     * Returns ordered intent key from network and two hosts.
     *
     * @param network network name
     * @param one host one
     * @param two host two
     * @return canonical intent string key
     */
    protected Key generateKey(String network, HostId one, HostId two) {
        String hosts = one.toString().compareTo(two.toString()) < 0 ?
                format(HOST_FORMAT, one, two) : format(HOST_FORMAT, two, one);
        return Key.of(format(KEY_FORMAT, network, hosts), appId);
    }

    /**
     * Matches an intent to a network and optional host.
     *
     * @param network network name
     * @param id optional host id, wildcard if missing
     * @param intent intent to match
     * @return true if intent matches, false otherwise
     */
    protected boolean matches(String network, Optional<HostId> id, Intent intent) {
        if (!Objects.equals(appId, intent.appId())) {
            // different app ids
            return false;
        }

        String key = intent.key().toString();
        if (!key.startsWith(network)) {
            // different network
            return false;
        }

        if (!id.isPresent()) {
            // no host id specified; wildcard match
            return true;
        }

        HostId hostId = id.get();
        String[] fields = key.split(",");
        // return result of id match in host portion of key
        return fields.length > 1 && fields[1].contains(hostId.toString());
    }

    /*
     * TODO Lab 6: Implement an InternalStoreDelegate class
     *
     * The class should implement the NetworkStoreDelegate interface and
     * its notify method.
     */
}
