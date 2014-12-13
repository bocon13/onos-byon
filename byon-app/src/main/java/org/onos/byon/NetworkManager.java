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

import com.google.common.collect.Sets;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.event.AbstractListenerRegistry;
import org.onosproject.event.EventDeliveryService;
import org.onosproject.net.HostId;
import org.onosproject.net.intent.HostToHostIntent;
import org.onosproject.net.intent.Intent;
import org.onosproject.net.intent.IntentEvent;
import org.onosproject.net.intent.IntentListener;
import org.onosproject.net.intent.IntentOperation;
import org.onosproject.net.intent.IntentOperations;
import org.onosproject.net.intent.IntentService;
import org.onosproject.net.intent.IntentStoreDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Skeletal ONOS application component.
 */
@Component(immediate = true)
@Service
public class NetworkManager implements NetworkService {

    private static Logger log = LoggerFactory.getLogger(NetworkManager.class);

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected CoreService coreService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected NetworkStore store;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected IntentService intentService;

    private ApplicationId appId;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected EventDeliveryService eventDispatcher;

    private final AbstractListenerRegistry<NetworkEvent, NetworkListener>
            listenerRegistry = new AbstractListenerRegistry<>();

    private final NetworkStoreDelegate delegate = new InternalStoreDelegate();

    @Activate
    protected void activate() {
        appId = coreService.registerApplication("org.onos.byon");
        eventDispatcher.addSink(NetworkEvent.class, listenerRegistry);
        store.setDelegate(delegate);
        log.info("Started");
    }

    @Deactivate
    protected void deactivate() {
        eventDispatcher.removeSink(NetworkEvent.class);
        store.unsetDelegate(delegate);
        log.info("Stopped");
    }

    @Override
    public void createNetwork(String network) {
        checkNotNull(network, "Network name cannot be null");
        store.putNetwork(network);
    }

    @Override
    public void deleteNetwork(String network) {
        checkNotNull(network, "Network name cannot be null");
        removeFromMesh(store.removeIntents(network));
        store.removeNetwork(network);
    }

    @Override
    public Set<String> getNetworks() {
        return store.getNetworks();
    }

    @Override
    public void addHost(String network, HostId hostId) {
        checkNotNull(network, "Network name cannot be null");
        checkNotNull(hostId, "HostId cannot be null");
        Set<HostId> hostIds = store.addHost(network, hostId);
        store.addIntents(network, addToMesh(hostId, hostIds));
    }

    @Override
    public void removeHost(String network, HostId hostId) {
        checkNotNull(network, "Network name cannot be null");
        checkNotNull(hostId, "HostId cannot be null");
        store.removeHost(network, hostId);
        removeFromMesh(store.removeIntents(network, hostId));
    }

    @Override
    public Set<HostId> getHosts(String network) {
        checkNotNull(network, "Network name cannot be null");
        return store.getHosts(network);
    }

    private Set<Intent> addToMesh(HostId src, Set<HostId> existing) {
        if (existing.isEmpty()) {
            return Collections.emptySet();
        }
        IntentOperations.Builder builder = IntentOperations.builder(appId);
        existing.forEach(dst -> {
            if (!src.equals(dst)) {
                builder.addSubmitOperation(new HostToHostIntent(appId, src, dst));
            }
        });
        IntentOperations ops = builder.build();
        intentService.execute(ops);

        return ops.operations().stream().map(IntentOperation::intent)
                .collect(Collectors.toSet());
    }

    private void removeFromMesh(Set<Intent> intents) {
        IntentOperations.Builder builder = IntentOperations.builder(appId);
        intents.forEach(intent -> builder.addWithdrawOperation(intent.id()));
        intentService.execute(builder.build());
    }

    @Override
    public void addListener(NetworkListener listener) {
        listenerRegistry.addListener(listener);
    }

    @Override
    public void removeListener(NetworkListener listener) {
        listenerRegistry.removeListener(listener);
    }

    private class InternalStoreDelegate implements NetworkStoreDelegate {
        @Override
        public void notify(NetworkEvent event) {
            eventDispatcher.post(event);
        }
    }

}
