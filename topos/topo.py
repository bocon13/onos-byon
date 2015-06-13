#!/usr/bin/env python

from mininet.topo import Topo

class TowerTopo( Topo ):
    """Create a tower topology"""

    def build( self, k=4, h=6 ):
        spines = []
        leaves = []
        hosts = []

        # Create the two spine switches
        spines.append(self.addSwitch('s1'))
        spines.append(self.addSwitch('s2'))

        # Create two links between the spine switches
        self.addLink(spines[0], spines[1])
        #TODO add second link between spines when multi-link topos are supported
        #self.addLink(spines[0], spines[1])
        
        # Now create the leaf switches, their hosts and connect them together
        i = 1
        c = 0
        while i <= k:
            leaves.append(self.addSwitch('s1%d' % i))
            for spine in spines:
                self.addLink(leaves[i-1], spine)

            j = 1
            while j <= h:
                hosts.append(self.addHost('h%d%d' % (i, j)))
                self.addLink(hosts[c], leaves[i-1])
                j+=1
                c+=1

            i+=1

topos = { 'tower': TowerTopo }

if __name__ == '__main__':
    from onosnet import run
    run( TowerTopo() )

