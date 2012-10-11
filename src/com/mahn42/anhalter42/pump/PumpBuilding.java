/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.pump;

import com.mahn42.framework.BlockPosition;
import com.mahn42.framework.Building;
import java.util.ArrayList;
import org.bukkit.Material;

/**
 *
 * @author andre
 */
public class PumpBuilding extends Building {
    public boolean flooded = false;
    public ArrayList<BlockPosition> floodedBlocks = new ArrayList<BlockPosition>();
    public Material floodMaterial = Material.STATIONARY_WATER;
    boolean emergencyStop = false;
    
    @Override
    protected void toCSVInternal(ArrayList aCols) {
        super.toCSVInternal(aCols);
        aCols.add(flooded);
        boolean lFirst = true;
        StringBuilder lBuilder = new StringBuilder();
        for(BlockPosition lPos : floodedBlocks) {
            if (lFirst) {
                lFirst = false;
            } else {
                lBuilder.append("|");
            }
            lPos.toCSV(lBuilder, ",");
        }
        aCols.add(lBuilder.toString());
        aCols.add(floodMaterial);
    }

    @Override
    protected void fromCSVInternal(DBRecordCSVArray aCols) {
        super.fromCSVInternal(aCols);
        flooded = Boolean.parseBoolean(aCols.pop());
        String lValue = aCols.pop();
        if (!lValue.isEmpty()) {
            String[] lVals = lValue.split("\\|");
            for(String lVal :  lVals) {
                BlockPosition lPos = new BlockPosition();
                lPos.fromCSV(lVal, "\\,");
                floodedBlocks.add(lPos);
            }
        }
        floodMaterial = Material.getMaterial(aCols.pop());
        if (floodMaterial == null) {
            floodMaterial = Material.STATIONARY_WATER;
        }
    }
}
