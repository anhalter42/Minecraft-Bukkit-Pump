/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.pump;

import com.mahn42.framework.BuildingDescription;
import com.mahn42.framework.Framework;
import com.mahn42.framework.WorldDBList;
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
        lRel = lBDesc.newRelatedTo(new Vector(0, 0, 3), "Furnance");
        lRel.materials.add(Material.BRICK);
        lBDesc = lDesc.newBlockDescription("PipeDown");
        lBDesc.material = Material.LAPIS_BLOCK;
        lBDesc = lDesc.newBlockDescription("Pump");
        lBDesc.material = Material.PISTON_BASE;
        lBDesc = lDesc.newBlockDescription("Furnance");
        lBDesc.material = Material.FURNACE;
        lDesc.activate();

        lDesc = framework.getBuildingDetector().newDescription("Pump.2.X");
        lDesc.typeName = "Pump";
        lDesc.handler = lHandler;
        lBDesc = lDesc.newBlockDescription("PipeUp");
        lBDesc.material = Material.LAPIS_BLOCK;
        lRel = lBDesc.newRelatedTo(new Vector(0,-10, 0), "PipeDown");
        lRel.materials.add(Material.BRICK);
        lRel.minDistance = 1;
        lRel = lBDesc.newRelatedTo(new Vector(3, 0, 0), "Pump");
        lRel.materials.add(Material.BRICK);
        lRel = lBDesc.newRelatedTo(new Vector(0, 0, -3), "Furnance");
        lRel.materials.add(Material.BRICK);
        lBDesc = lDesc.newBlockDescription("PipeDown");
        lBDesc.material = Material.LAPIS_BLOCK;
        lBDesc = lDesc.newBlockDescription("Pump");
        lBDesc.material = Material.PISTON_BASE;
        lBDesc = lDesc.newBlockDescription("Furnance");
        lBDesc.material = Material.FURNACE;
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
        lRel = lBDesc.newRelatedTo(new Vector(3, 0, 0), "Furnance");
        lRel.materials.add(Material.BRICK);
        lBDesc = lDesc.newBlockDescription("PipeDown");
        lBDesc.material = Material.LAPIS_BLOCK;
        lBDesc = lDesc.newBlockDescription("Pump");
        lBDesc.material = Material.PISTON_BASE;
        lBDesc = lDesc.newBlockDescription("Furnance");
        lBDesc.material = Material.FURNACE;
        lDesc.activate();

        lDesc = framework.getBuildingDetector().newDescription("Pump.2.Z");
        lDesc.typeName = "Pump";
        lDesc.handler = lHandler;
        lBDesc = lDesc.newBlockDescription("PipeUp");
        lBDesc.material = Material.LAPIS_BLOCK;
        lRel = lBDesc.newRelatedTo(new Vector(0,-10, 0), "PipeDown");
        lRel.materials.add(Material.BRICK);
        lRel.minDistance = 1;
        lRel = lBDesc.newRelatedTo(new Vector(0, 0, 3), "Pump");
        lRel.materials.add(Material.BRICK);
        lRel = lBDesc.newRelatedTo(new Vector(-3, 0, 0), "Furnance");
        lRel.materials.add(Material.BRICK);
        lBDesc = lDesc.newBlockDescription("PipeDown");
        lBDesc.material = Material.LAPIS_BLOCK;
        lBDesc = lDesc.newBlockDescription("Pump");
        lBDesc.material = Material.PISTON_BASE;
        lBDesc = lDesc.newBlockDescription("Furnance");
        lBDesc.material = Material.FURNACE;
        lDesc.activate();
}

    @Override
    public void onDisable() { 
    }
}
