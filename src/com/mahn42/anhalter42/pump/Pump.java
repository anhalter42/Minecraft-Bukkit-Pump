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
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

/**
 *
 * @author andre
 */
public class Pump extends JavaPlugin {

    public Framework framework;
    public static Pump plugin;
    public WorldDBList<PumpBuildingDB> DBs;
    public int configMaxBlocks = 42000;
    public int configMaxHeight = 10;
    public int configTicks = 4;
    
    protected HashMap<PumpBuilding, PumpTask> fPumpTasks = new HashMap<PumpBuilding, PumpTask>();

    public static void main(String[] args) {
    }
    
    @Override
    public void onEnable() { 
        framework = Framework.plugin;
        plugin = this;
        readPumpConfig();
        DBs = new WorldDBList<PumpBuildingDB>(PumpBuildingDB.class, this);
        
        framework.registerSaver(DBs);
        
        PumpHandler lHandler = new PumpHandler(this);
        
        BuildingDescription lDesc;
        BuildingDescription.BlockDescription lBDesc;
        BuildingDescription.RelatedTo lRel;
        
        lDesc = framework.getBuildingDetector().newDescription("Pump");
        lDesc.typeName = "Pump";
        lDesc.handler = lHandler;
        lBDesc = lDesc.newBlockDescription("PipeUp");
        lBDesc.materials.add(Material.LAPIS_BLOCK);
        lBDesc.detectSensible = true;
        lRel = lBDesc.newRelatedTo(new Vector(0,-configMaxHeight, 0), "PipeDown");
        lRel.materials.add(Material.BRICK);
        lRel.materials.add(Material.SMOOTH_BRICK);
        lRel.materials.add(Material.SANDSTONE);
        lRel = lBDesc.newRelatedTo(new Vector(0, configMaxHeight, 0), "Pump");
        lRel.materials.add(Material.BRICK);
        lRel.materials.add(Material.SMOOTH_BRICK);
        lRel.materials.add(Material.SANDSTONE);
        lBDesc = lDesc.newBlockDescription("PipeDown");
        lBDesc.materials.add(Material.LAPIS_BLOCK);
        lBDesc = lDesc.newBlockDescription("Pump");
        lBDesc.materials.add(Material.PISTON_BASE);
        lRel = lBDesc.newRelatedTo(new Vector(3, 0, 0), "Switch");
        lRel.materials.add(Material.BRICK);
        lRel.materials.add(Material.SMOOTH_BRICK);
        lRel.materials.add(Material.SANDSTONE);
        lRel = lBDesc.newRelatedTo("PumpSwitch", BuildingDescription.RelatedPosition.Nearby, 1);
        lBDesc = lDesc.newBlockDescription("Switch");
        lBDesc.materials.add(Material.LAPIS_BLOCK);
        lBDesc.redstoneSensible = true;
        lBDesc = lDesc.newBlockDescription("PumpSwitch");
        lBDesc.materials.add(Material.LEVER);

        lDesc.createAndActivateXZ();
    }

    @Override
    public void onDisable() { 
        getServer().getScheduler().cancelTasks(this);
    }

    public boolean existsPumpTask(PumpBuilding aPump) {
        return fPumpTasks.containsKey(aPump);
    }
    
    public void startPumpTask(PumpTask aTask) {
        aTask.taskId = getServer().getScheduler().scheduleAsyncRepeatingTask(this, aTask, 1, configTicks);
        fPumpTasks.put(aTask.pump, aTask);
        //getLogger().info("start task " + new Integer(aTask.taskId));
    }
    
    public void stopPumpTask(PumpTask aTask) {
        getServer().getScheduler().cancelTask(aTask.taskId);
        fPumpTasks.remove(aTask.pump);
        //getLogger().info("stop task " + new Integer(aTask.taskId));
    }

    private void readPumpConfig() {
        FileConfiguration lConfig = getConfig();
        configMaxBlocks = lConfig.getInt("Pump.maxBlocks");
        configMaxHeight = lConfig.getInt("Pump.maxHeight");
        configTicks = lConfig.getInt("Pump.ticks");
    }
    
    public String getText(String aText, Object... aObjects) {
        return getText((String)null, aText, aObjects);
    }
    
    public String getText(CommandSender aPlayer, String aText, Object... aObjects) {
        return getText(Framework.plugin.getPlayerLanguage(aPlayer.getName()), aText, aObjects);
    }
    
    public String getText(String aLanguage, String aText, Object... aObjects) {
        return Framework.plugin.getText(this, aLanguage, aText, aObjects);
    }
}
