/*
 * Copyright 2014 Open Networking Laboratory
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
import org.apache.felix.scr.annotations.Service;
import org.onosproject.net.HostId;
import org.onosproject.net.intent.HostToHostIntent;
import org.onosproject.net.intent.Intent;
import org.onosproject.store.AbstractStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.onos.byon.NetworkEvent.Type.NETWORK_ADDED;
import static org.onos.byon.NetworkEvent.Type.NETWORK_REMOVED;
import static org.onos.byon.NetworkEvent.Type.NETWORK_UPDATED;

/**
 * Trivial in-memory network store.
 */
@Component(immediate = false, enabled = false)
@Service
public class SimpleNetworkStore
        extends AbstractStore<NetworkEvent, NetworkStoreDelegate>
        implements NetworkStore {

    private static Logger log = LoggerFactory.getLogger(SimpleNetworkStore.class);

    private final Map<String, Set<HostId>> networks = Maps.newHashMap();
    private final Map<String, Set<Intent>> intentsPerNet = Maps.newHashMap();

    @Activate
    protected void activate() {
        log.info("Started");
    }

    @Deactivate
    protected void deactivate() {
        log.info("Stopped");
    }

    @Override
    public void putNetwork(String network) {
        intentsPerNet.putIfAbsent(network, Sets.<Intent>newHashSet());
        if (networks.putIfAbsent(network, Sets.<HostId>newHashSet()) == null) {
            notifyDelegate(new NetworkEvent(NETWORK_ADDED, network));
        }
    }

    @Override
    public void removeNetwork(String network) {
        if (networks.remove(network) != null) {
            notifyDelegate(new NetworkEvent(NETWORK_REMOVED, network));
        }
        intentsPerNet.remove(network);
    }

    @Override
    public Set<String> getNetworks() {
        return ImmutableSet.copyOf(networks.keySet());
    }

    @Override
    public Set<HostId> addHost(String network, HostId hostId) {
        Set<HostId> hosts = checkNotNull(networks.get(network),
                                         "Please create the network first");
        boolean added = hosts.add(hostId);
        if (added) {
            notifyDelegate(new NetworkEvent(NETWORK_UPDATED, network));
        }
        return added ? ImmutableSet.copyOf(hosts) : Collections.emptySet();
    }

    @Override
    public void removeHost(String network, HostId hostId) {
        Set<HostId> hosts = checkNotNull(networks.get(network),
                                         "Please create the network first");
        if (hosts.remove(hostId)) {
            notifyDelegate(new NetworkEvent(NETWORK_UPDATED, network));
        }
    }

    @Override
    public Set<HostId> getHosts(String network) {
        Set<HostId> hosts = checkNotNull(networks.get(network),
                                         "Please create the network first");
        return ImmutableSet.copyOf(hosts);
    }

    @Override
    public void addIntents(String network, Set<Intent> intents) {
        intents.forEach(intent -> checkArgument(intent instanceof HostToHostIntent,
                                                "Intent should be a host to host intent."));
        intentsPerNet.get(network).addAll(intents);
    }

    @Override
    public Set<Intent> removeIntents(String network, HostId hostId) {
        Set<Intent> intents = checkNotNull(intentsPerNet.get(network)).stream()
                .map(intent -> (HostToHostIntent) intent)
                .filter(intent -> intent.one().equals(hostId) || intent.two().equals(hostId))
                .collect(Collectors.toSet());
        intentsPerNet.get(network).removeAll(intents);
        return intents;
    }

    @Override
    public Set<Intent> removeIntents(String network) {
        Collection<Intent> intents = checkNotNull(intentsPerNet.get(network));
        intentsPerNet.get(network).clear();
        return ImmutableSet.copyOf(intents);
    }
}
