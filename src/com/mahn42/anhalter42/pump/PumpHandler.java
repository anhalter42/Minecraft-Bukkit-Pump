/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.pump;

import com.mahn42.framework.Building;
import com.mahn42.framework.BuildingDB;
import com.mahn42.framework.BuildingHandler;
import org.bukkit.World;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author andre
 */
class PumpHandler implements BuildingHandler {

    public Pump plugin;
    
    public PumpHandler(Pump aPlugin) {
        plugin = aPlugin;
    }

    @Override
    public boolean breakBlock(BlockBreakEvent aEvent, Building aBuilding) {
        return false;
    }

    @Override
    public boolean redstoneChanged(BlockRedstoneEvent aEvent, Building aBuilding) {
        return false;
    }

    @Override
    public boolean playerInteract(PlayerInteractEvent aEvent, Building aBuilding) {
        return false;
    }

    @Override
    public BuildingDB getDB(World aWorld) {
        return plugin.DBs.getDB(aWorld);
    }
}
