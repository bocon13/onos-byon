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

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logs network events.
 */
@Component(immediate = true)
public class NetworkEventMonitor {
    private static Logger log = LoggerFactory.getLogger(NetworkEventMonitor.class);

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected NetworkService service;

    private final Listener listener = new Listener();

    @Activate
    protected void activate() {
        service.addListener(listener);
        log.info("Started");
    }

    @Deactivate
     protected void deactivate() {
        service.removeListener(listener);
        log.info("Stopped");
    }

    private class Listener implements NetworkListener {
        @Override
        public void event(NetworkEvent event) {
            log.info("{}", event);
        }
    }
}

