package org.onos.byon.cli;

import org.apache.karaf.shell.commands.Command;
import org.onos.byon.NetworkService;
import org.onosproject.cli.AbstractShellCommand;
import org.onosproject.net.HostId;

/**
 * CLI to create network
 */
@Command(scope="byon", name="list-networks", description = "Lists all the networks")
public class ListNetworkCommand extends AbstractShellCommand {


    @Override
    protected void execute() {
        NetworkService networkService = get(NetworkService.class);

        for (String net : networkService.getNetworks()) {
            print("%s", net);
            for (HostId hostId : networkService.getHosts(net)) {
                print("\t%s", hostId);
            }
        }

    }
}
