/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.pump;

import com.mahn42.framework.BuildingDB;
import java.io.File;
import org.bukkit.World;

/**
 *
 * @author andre
 */
public class PumpBuildingDB extends BuildingDB<PumpBuilding> {
    
    public PumpBuildingDB() {
        super(PumpBuilding.class);
    }

    public PumpBuildingDB(World aWorld, File aFile) {
        super(PumpBuilding.class, aWorld, aFile);
    }
}
