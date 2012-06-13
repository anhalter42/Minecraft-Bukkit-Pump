/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.pump;

import com.mahn42.framework.BuildingDescription;
import com.mahn42.framework.Framework;
import com.mahn42.framework.WorldDBList;
import java.util.HashMap;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

/**
 *
 * @author andre
 */
public class Pump extends JavaPlugin {

    public Framework framework;
    public WorldDBList<PumpBuildingDB> DBs;
    
    protected HashMap<PumpBuilding, PumpTask> fPumpTasks = new HashMap<PumpBuilding, PumpTask>();

    public static void main(String[] args) {
    }
    
    @Override
    public void onEnable() { 
        framework = Framework.plugin;
        DBs = new WorldDBList<PumpBuildingDB>(PumpBuildingDB.class, this);
        
        framework.registerSaver(DBs);
        
        PumpHandler lHandler = new PumpHandler(this);
        
        BuildingDescription lDesc;
        BuildingDescription.BlockDescription lBDesc;
        BuildingDescription.RelatedTo lRel;
        
        lDesc = framework.getBuildingDetector().newDescription("Pump.1.X");
        lDesc.typeName = "Pump";
        lDesc.handler = lHandler;
        lBDesc = lDesc.newBlockDescription("PipeUp");
        lBDesc.material = Material.LAPIS_BLOCK;
        lRel = lBDesc.newRelatedTo(new Vector(0,-10, 0), "PipeDown");
        lRel.materials.add(Material.BRICK);
        lRel.minDistance = 1;
        lRel = lBDesc.newRelatedTo(new Vector(3, 0, 0), "Pump");
        lRel.materials.add(Material.BRICK);
        lBDesc = lDesc.newBlockDescription("PipeDown");
        lBDesc.material = Material.LAPIS_BLOCK;
        lBDesc = lDesc.newBlockDescription("Pump");
        lBDesc.material = Material.PISTON_BASE;
        lBDesc.redstoneSensible = true;
        lDesc.activate();

        lDesc = framework.getBuildingDetector().newDescription("Pump.2.X");
        lDesc.typeName = "Pump";
        lDesc.handler = lHandler;
        lBDesc = lDesc.newBlockDescription("PipeUp");
        lBDesc.material = Material.LAPIS_BLOCK;
        lRel = lBDesc.newRelatedTo(new Vector(0,-10, 0), "PipeDown");
        lRel.materials.add(Material.BRICK);
        lRel.minDistance = 1;
        lRel = lBDesc.newRelatedTo(new Vector(-3, 0, 0), "Pump");
        lRel.materials.add(Material.BRICK);
        lBDesc = lDesc.newBlockDescription("PipeDown");
        lBDesc.material = Material.LAPIS_BLOCK;
        lBDesc = lDesc.newBlockDescription("Pump");
        lBDesc.material = Material.PISTON_BASE;
        lBDesc.redstoneSensible = true;
        lDesc.activate();

        lDesc = framework.getBuildingDetector().newDescription("Pump.1.Z");
        lDesc.typeName = "Pump";
        lDesc.handler = lHandler;
        lBDesc = lDesc.newBlockDescription("PipeUp");
        lBDesc.material = Material.LAPIS_BLOCK;
        lRel = lBDesc.newRelatedTo(new Vector(0,-10, 0), "PipeDown");
        lRel.materials.add(Material.BRICK);
        lRel.minDistance = 1;
        lRel = lBDesc.newRelatedTo(new Vector(0, 0, 3), "Pump");
        lRel.materials.add(Material.BRICK);
        lBDesc = lDesc.newBlockDescription("PipeDown");
        lBDesc.material = Material.LAPIS_BLOCK;
        lBDesc = lDesc.newBlockDescription("Pump");
        lBDesc.material = Material.PISTON_BASE;
        lBDesc.redstoneSensible = true;
        lDesc.activate();

        lDesc = framework.getBuildingDetector().newDescription("Pump.2.Z");
        lDesc.typeName = "Pump";
        lDesc.handler = lHandler;
        lBDesc = lDesc.newBlockDescription("PipeUp");
        lBDesc.material = Material.LAPIS_BLOCK;
        lRel = lBDesc.newRelatedTo(new Vector(0,-10, 0), "PipeDown");
        lRel.materials.add(Material.BRICK);
        lRel.minDistance = 1;
        lRel = lBDesc.newRelatedTo(new Vector(0, 0, -3), "Pump");
        lRel.materials.add(Material.BRICK);
        lBDesc = lDesc.newBlockDescription("PipeDown");
        lBDesc.material = Material.LAPIS_BLOCK;
        lBDesc = lDesc.newBlockDescription("Pump");
        lBDesc.material = Material.PISTON_BASE;
        lBDesc.redstoneSensible = true;
        lDesc.activate();
}

    @Override
    public void onDisable() { 
        getServer().getScheduler().cancelTasks(this);
    }

    public boolean existsPumpTask(PumpBuilding aPump) {
        return fPumpTasks.containsKey(aPump);
    }
    
    public void startGateTask(PumpTask aTask) {
        aTask.taskId = getServer().getScheduler().scheduleAsyncRepeatingTask(this, aTask, 1, 15);
        fPumpTasks.put(aTask.pump, aTask);
        getLogger().info("start task " + new Integer(aTask.taskId));
    }
    
    public void stopGateTask(PumpTask aTask) {
        getServer().getScheduler().cancelTask(aTask.taskId);
        fPumpTasks.remove(aTask.pump);
        getLogger().info("stop task " + new Integer(aTask.taskId));
    }
    
}
