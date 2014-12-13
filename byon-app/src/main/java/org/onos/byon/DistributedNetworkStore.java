package org.onos.byon;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.hazelcast.core.EntryAdapter;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Service;
import org.onosproject.net.HostId;
import org.onosproject.net.intent.HostToHostIntent;
import org.onosproject.net.intent.Intent;
import org.onosproject.store.hz.AbstractHazelcastStore;
import org.onosproject.store.hz.SMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.onos.byon.NetworkEvent.Type.*;

/**
 * Hazelcast-backed Network Store implementation.
 */
@Component(immediate = true)
@Service
public class DistributedNetworkStore
        extends AbstractHazelcastStore<NetworkEvent, NetworkStoreDelegate>
        implements NetworkStore {
    // TODO implement that event delegate

    private static Logger log = LoggerFactory.getLogger(DistributedNetworkStore.class);

    private SMap<String, Set<HostId>> networks;
    private SMap<String, Set<Intent>> intentsPerNet;

    private String listenerId;

    @Activate
    public void activate() {
        super.activate();

        networks = new SMap<>(theInstance.<byte[], byte[]>getMap("byon-networks"), this.serializer);
        intentsPerNet = new SMap<>(theInstance.<byte[], byte[]>getMap("byon-network-intents"), this.serializer);
        EntryListener<String, Set<HostId>> listener = new RemoteListener();
        listenerId = networks.addEntryListener(listener, true);
        log.info("Started");
    }

    @Deactivate
    public void deactivate() {
        networks.removeEntryListener(listenerId);
        log.info("Stopped");
    }

    @Override
    public void putNetwork(String network) {
        try {
            networks.lock(network);
            networks.putIfAbsent(network, Sets.<HostId>newHashSet());
            intentsPerNet.putIfAbsent(network, Sets.<Intent>newHashSet());
        } finally {
            networks.unlock(network);
        }
    }

    @Override
    public void removeNetwork(String network) {
        try {
            networks.lock(network);
            networks.remove(network);
            intentsPerNet.remove(network);
        } finally {
            networks.unlock(network);
        }
    }

    @Override
    public Set<String> getNetworks() {
        return ImmutableSet.copyOf(networks.keySet());
    }

    @Override
    public Set<HostId> addHost(String network, HostId hostId) {
        try {
            networks.lock(network);
            Set<HostId> hosts = checkNotNull(networks.get(network),
                                             "Please create the network first");
            if (hosts.add(hostId)) {
                networks.put(network, hosts);
                return hosts;
            } else {
                return Collections.emptySet();
            }
        } finally {
            networks.unlock(network);
        }
    }

    @Override
    public void removeHost(String network, HostId hostId) {
        try {
            networks.lock(network);
            Set<HostId> hosts = checkNotNull(networks.get(network),
                                             "Please create the network first");
            if (hosts.remove(hostId)) {
                networks.put(network, hosts);
            }
        } finally {
            networks.unlock(network);
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
        try {
            intentsPerNet.lock(network);
            Set<Intent> existingIntents = intentsPerNet.get(network);
            existingIntents.addAll(intents);
            intentsPerNet.put(network, existingIntents);
        } finally {
            intentsPerNet.unlock(network);
        }
    }

    @Override
    public Set<Intent> removeIntents(String network, HostId hostId) {
        try {
            intentsPerNet.lock(network);
            Set<Intent> existingIntents = intentsPerNet.get(network);
            Set<Intent> intents = checkNotNull(existingIntents).stream()
                .map(intent -> (HostToHostIntent) intent)
                .filter(intent -> intent.one().equals(hostId) || intent.two().equals(hostId))
                .collect(Collectors.toSet());
            existingIntents.removeAll(intents);
            intentsPerNet.put(network, existingIntents);
            return intents;
        } finally {
            intentsPerNet.unlock(network);
        }
    }

    @Override
    public Set<Intent> removeIntents(String network) {
        try {
            intentsPerNet.lock(network);
            return intentsPerNet.put(network, Sets.<Intent>newHashSet());
        } finally {
            intentsPerNet.unlock(network);
        }
    }

    private class RemoteListener extends EntryAdapter<String, Set<HostId>> {
        @Override
        public void entryAdded(EntryEvent<String, Set<HostId>> event) {
            notifyDelegate(new NetworkEvent(NETWORK_ADDED, event.getKey()));
        }

        @Override
        public void entryUpdated(EntryEvent<String, Set<HostId>> event) {
            notifyDelegate(new NetworkEvent(NETWORK_UPDATED, event.getKey()));
        }

        @Override
        public void entryRemoved(EntryEvent<String, Set<HostId>> event) {
            notifyDelegate(new NetworkEvent(NETWORK_REMOVED, event.getKey()));
        }
    }
}
