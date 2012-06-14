/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.pump;

import com.mahn42.framework.BlockPosition;
import com.mahn42.framework.Building;
import java.util.ArrayList;

/**
 *
 * @author andre
 */
public class PumpBuilding extends Building {
    public boolean flooded = false;
    public ArrayList<BlockPosition> floodedBlocks = new ArrayList<BlockPosition>();
    
    @Override
    protected void toCSVInternal(ArrayList aCols) {
        super.toCSVInternal(aCols);
        aCols.add(flooded);
        String lValue = null;
        for(BlockPosition lPos : floodedBlocks) {
            if (lValue == null) {
                lValue = lPos.toCSV(",");
            } else {
                lValue += "|" + lPos.toCSV(",");
            }
        }
        aCols.add(lValue);
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
    }
}
