package org.onos.byon.cli;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.onos.byon.NetworkService;
import org.onosproject.cli.AbstractShellCommand;

/**
 * CLI to create network
 */
@Command(scope = "byon", name = "create-network", description = "Create a network")
public class CreateNetworkCommand extends AbstractShellCommand {

    @Argument(index = 0, name = "network", description = "Network name",
            required = true, multiValued = false)
    String network = null;

    @Override
    protected void execute() {
        NetworkService networkService = get(NetworkService.class);

        networkService.createNetwork(network);

        print("Created network %s", network);

    }
}
