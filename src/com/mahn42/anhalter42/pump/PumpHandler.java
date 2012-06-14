/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.pump;

import com.mahn42.framework.Building;
import com.mahn42.framework.BuildingDB;
import com.mahn42.framework.BuildingHandler;
import org.bukkit.World;
import org.bukkit.entity.Player;
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
        World lWorld = aEvent.getBlock().getWorld();
        PumpBuildingDB lDB = plugin.DBs.getDB(lWorld);
        lDB.remove(aBuilding);
        return true;
    }

    @Override
    public boolean redstoneChanged(BlockRedstoneEvent aEvent, Building aBuilding) {
        PumpBuilding lPump = (PumpBuilding)aBuilding;
        boolean lFlood = aEvent.getNewCurrent() > 0;
        if (!plugin.existsPumpTask(lPump)) {
                //&& ((lFlood && !lPump.flooded) || (!lFlood && lPump.flooded))) {
            PumpTask aTask = new PumpTask(plugin);
            aTask.pump = (PumpBuilding)aBuilding;
            aTask.flood = lFlood;
            plugin.startPumpTask(aTask);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean playerInteract(PlayerInteractEvent aEvent, Building aBuilding) {
        Player lPlayer = aEvent.getPlayer();
        World lWorld = lPlayer.getWorld();
        boolean lFound = false;
        PumpBuildingDB lDB = plugin.DBs.getDB(lWorld);
        if (lDB.getBuildings(aBuilding.edge1).isEmpty()
                && lDB.getBuildings(aBuilding.edge2).isEmpty()) {
            PumpBuilding lPump = new PumpBuilding();
            lPump.cloneFrom(aBuilding);
            lPump.playerName = lPlayer.getName();
            lDB.addRecord(lPump);
            lPlayer.sendMessage("Building " + lPump.getName() + " found.");
            lFound = true;
        }
        return lFound;
    }

    @Override
    public BuildingDB getDB(World aWorld) {
        return plugin.DBs.getDB(aWorld);
    }
}
