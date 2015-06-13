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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onlab.packet.MacAddress;
import org.onosproject.TestApplicationId;
import org.onosproject.core.IdGenerator;
import org.onosproject.net.HostId;
import org.onosproject.net.intent.HostToHostIntent;
import org.onosproject.net.intent.Intent;
import org.onosproject.net.intent.Key;
import org.onosproject.net.intent.MockIdGenerator;

import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Test network manager.
 */
public class NetworkManagerTest {

    protected NetworkManager manager;
    protected IdGenerator idGenerator = new MockIdGenerator();

    @Before
    public void setUp() {
        manager = new NetworkManager();
        manager.appId = new TestApplicationId("network-test");
        Intent.bindIdGenerator(idGenerator);

    }

    @After
    public void tearDown() {
        Intent.unbindIdGenerator(idGenerator);
    }

    public static final String NETWORK = "test";
    public static final String NETWORK_2 = "test2";
    public static final HostId HOST_1 = HostId.hostId(MacAddress.valueOf(1L));
    public static final HostId HOST_2 = HostId.hostId(MacAddress.valueOf(2L));
    public static final HostId HOST_3 = HostId.hostId(MacAddress.valueOf(3L));

    @Test
    public void testHostOrderEquality() {
        Key key         = manager.generateKey(NETWORK, HOST_1, HOST_2);
        Key reverse     = manager.generateKey(NETWORK, HOST_2, HOST_1);
        Key keyDiffHost = manager.generateKey(NETWORK, HOST_1, HOST_3);
        Key keyDiffNet  = manager.generateKey(NETWORK_2, HOST_1, HOST_2);

        assertEquals(key, reverse);
        assertNotEquals(key, keyDiffHost);
        assertNotEquals(key, keyDiffNet);
    }

    @Test
    public void testMatches() {
        Intent intent = HostToHostIntent.builder()
                .key(manager.generateKey(NETWORK, HOST_1, HOST_2))
                .appId(manager.appId)
                .one(HOST_1)
                .two(HOST_2)
                .build();

        assertTrue(manager.matches(NETWORK, Optional.of(HOST_1), intent));
        assertTrue(manager.matches(NETWORK, Optional.of(HOST_2), intent));
        assertTrue(manager.matches(NETWORK, Optional.empty(), intent));

        assertFalse(manager.matches(NETWORK, Optional.of(HOST_3), intent));
        assertFalse(manager.matches(NETWORK_2, Optional.of(HOST_1), intent));
        assertFalse(manager.matches(NETWORK_2, Optional.of(HOST_3), intent));
    }
}
