/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.pump;

import com.mahn42.framework.BlockPosition;
import com.mahn42.framework.Building;
import com.mahn42.framework.BuildingDB;
import com.mahn42.framework.BuildingHandlerBase;
import com.mahn42.framework.Framework;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author andre
 */
class PumpHandler extends BuildingHandlerBase {

    public Pump plugin;
    
    public PumpHandler(Pump aPlugin) {
        plugin = aPlugin;
    }

    @Override
    public JavaPlugin getPlugin() {
        return plugin;
    }
    
    @Override
    public boolean redstoneChanged(BlockRedstoneEvent aEvent, Building aBuilding) {
        PumpBuilding lPump = (PumpBuilding)aBuilding;
        boolean lFlood = aEvent.getNewCurrent() > 0;
        if (!plugin.existsPumpTask(lPump)//) {
                && ((lFlood && !lPump.flooded) || (!lFlood && lPump.flooded))) {
            PumpTask aTask = new PumpTask(plugin);
            aTask.pump = lPump;
            aTask.flood = lFlood;
            plugin.startPumpTask(aTask);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Building insert(Building aBuilding) {
        PumpBuildingDB lDB = (PumpBuildingDB)getDB(aBuilding.world);
        PumpBuilding lPump = new PumpBuilding();
        lPump.cloneFrom(aBuilding);
        lDB.addRecord(lPump);
        return lPump;
    }

    @Override
    public boolean remove(Building aBuilding) {
        PumpBuilding lPump = (PumpBuilding)aBuilding;
        lPump.emergencyStop = true;
        if (!plugin.existsPumpTask(lPump) && lPump.flooded) {
            PumpTask lTask = new PumpTask(plugin);
            lTask.pump = lPump;
            lTask.flood = false;
            plugin.startPumpTask(lTask);
        }
        super.remove(aBuilding);
        return true;
    }

    @Override
    public BuildingDB getDB(World aWorld) {
        return plugin.DBs.getDB(aWorld);
    }

    @Override
    public void nextConfiguration(Building aBuilding, BlockPosition position, Player aPlayer) {
        super.nextConfiguration(aBuilding, position, aPlayer);
        if (aBuilding instanceof PumpBuilding) {
            PumpBuilding lPump = (PumpBuilding)aBuilding;
            if (lPump.floodMaterial.equals(Material.STATIONARY_WATER)) {
                lPump.floodMaterial = Material.STATIONARY_LAVA;
            } else if (lPump.floodMaterial.equals(Material.STATIONARY_LAVA)) {
                lPump.floodMaterial = Material.STATIONARY_WATER;
            }
            if (aPlayer != null) {
                aPlayer.sendMessage(Pump.plugin.getText(aPlayer, "Pump will flood with %s now.", Framework.plugin.getText(aPlayer, lPump.floodMaterial.toString())));
            }
        }
    }
}
