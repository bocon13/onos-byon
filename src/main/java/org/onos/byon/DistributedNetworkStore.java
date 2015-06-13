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
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;
import org.onosproject.net.HostId;
import org.onosproject.store.AbstractStore;
import org.onosproject.store.serializers.KryoNamespaces;
import org.onosproject.store.service.ConsistentMap;
import org.onosproject.store.service.MapEvent;
import org.onosproject.store.service.MapEventListener;
import org.onosproject.store.service.Serializer;
import org.onosproject.store.service.StorageService;
import org.onosproject.store.service.Versioned;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Network Store implementation backed by consistent map.
 */
@Component(immediate = true)
@Service
public class DistributedNetworkStore
        // TODO Lab 6: Extend the AbstractStore class for the store delegate
        implements NetworkStore {

    private static Logger log = LoggerFactory.getLogger(DistributedNetworkStore.class);

    /*
     * TODO Lab 5: Get a reference to the storage service
     *
     * All you need to do is uncomment the following two lines.
     */
    //@Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    //protected StorageService storageService;

    /*
     * TODO Lab 5: Replace the ConcurrentMap with ConsistentMap
     */
    private ConcurrentMap<String, Set<HostId>> networks;

    /*
     * TODO Lab 6: Create a listener instance of InternalListener
     *
     * You will first need to implement the class (at the bottom of the file).
     */

    @Activate
    public void activate() {
        /**
         * TODO Lab 5: Replace the ConcurrentHashMap with ConsistentMap
         *
         * You should use storageService.consistentMapBuilder(), and the
         * serializer: Serializer.using(KryoNamespaces.API)
         */
        networks = Maps.newConcurrentMap();

        /*
         * TODO Lab 6: Add the listener to the networks map
         *
         * Use networks.addListener()
         */
        log.info("Started");
    }

    @Deactivate
    public void deactivate() {
        /*
         * TODO Lab 6: Remove the listener from the networks map
         *
         * Use networks.removeListener()
         */
        log.info("Stopped");
    }

    @Override
    public void putNetwork(String network) {
        networks.putIfAbsent(network, Sets.<HostId>newHashSet());
    }

    @Override
    public void removeNetwork(String network) {
        networks.remove(network);
    }

    @Override
    public Set<String> getNetworks() {
        return ImmutableSet.copyOf(networks.keySet());
    }

    @Override
    public boolean addHost(String network, HostId hostId) {
        /*
         * TODO Lab 5: Update the Set to Versioned<Set<HostId>>
         *
         * You will also need to extract the value in the if statement.
         */
        Set<HostId> existingHosts = checkNotNull(networks.get(network),
                                                            "Network %s does not exist", network);
        if (existingHosts.contains(hostId)) {
            return false;
        }

        networks.computeIfPresent(network,
                                  (k, v) -> {
                                      Set<HostId> result = Sets.newHashSet(v);
                                      result.add(hostId);
                                      return result;
                                  });
        return true;
    }

    @Override
    public void removeHost(String network, HostId hostId) {
        /*
         * TODO Lab 5: Update the Set to Versioned<Set<HostId>>
         */
        Set<HostId> hosts =
                networks.computeIfPresent(network,
                                          (k, v) -> {
                                              Set<HostId> result = Sets.newHashSet(v);
                                              result.remove(hostId);
                                              return result;
                                          });
        checkNotNull(hosts, "Network %s does not exist", network);
    }

    @Override
    public Set<HostId> getHosts(String network) {
        /*
         * TODO Lab 5: Update return value
         *
         * ConsistentMap returns a Versioned<V>, so you need to extract the value
         */
        return checkNotNull(networks.get(network),
                            "Please create the network first");
    }

    /*
     * TODO Lab 6: Implement an InternalListener class for remote map events
     *
     * The class should implement the MapEventListener interface and
     * its event method.
     */

}
