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

package org.onos.byon.cli;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.onos.byon.NetworkService;
import org.onosproject.cli.AbstractShellCommand;
import org.onosproject.net.HostId;

/**
 * CLI to add a host to a network.
 */
@Command(scope = "byon", name = "add-host", description = "Add a host to a network")
public class AddHostCommand extends AbstractShellCommand {

    @Argument(index = 0, name = "network", description = "Network name",
            required = true, multiValued = false)
    String network = null;

    @Argument(index = 1, name = "hostId", description = "Host Id",
            required = true, multiValued = false)
    String hostId = null;

    @Override
    protected void execute() {
        NetworkService networkService = get(NetworkService.class);
        networkService.addHost(network, HostId.hostId(hostId));
        print("Added host %s to %s", hostId, network);
    }
}
