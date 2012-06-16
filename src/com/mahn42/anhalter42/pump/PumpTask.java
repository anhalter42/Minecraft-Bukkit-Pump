/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.pump;

import com.mahn42.framework.*;
import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 *
 * @author andre
 */
public class PumpTask implements Runnable {

    public Pump plugin;
    public PumpBuilding pump;
    public int taskId;
    public int maxBlocks = 10000;
    
    public boolean flood = false;
    
    public PumpTask(Pump aPlugin) {
        plugin = aPlugin;
    }
    
    protected boolean fInRun = false;
    protected boolean fInit = false;
    protected int fSettedBlocks = 0;
    protected BlockPosition fTop;
    protected BlockPosition fBottom;
    protected BlockPosition fPump;
    protected BlockPosition fSwitch;
    
    @Override
    public void run() {
        if (!fInRun) {
            fInRun = true;
            try {
                if (!fInit) {
                    init();
                    fInit = true;
                }
                if (pump.emergencyStop) {
                    rollback();
                } else {
                    if (flood) {
                        flood();
                    } else {
                        unflood();
                    }
                }
            } finally {
                fInRun = false;
            }
        }
    }

    protected ArrayList<Material> fLiquids;
    protected ArrayList<BlockPosition> fItems;
    protected ArrayList<BlockPosition> fAllItems;
    
    private void flood() {
        SyncBlockList lList = new SyncBlockList(pump.world);
        Block lPump = fPump.getBlock(pump.world);
        lList.add(fPump, lPump.getType(), (byte)(lPump.getData() | (byte)0x8), false);
        if (fSettedBlocks == 0) {
            for(BlockPosition lPos : new WorldLineWalk(fTop, fBottom)) {
                pump.floodedBlocks.add(lPos);
                lList.add(lPos, Material.STATIONARY_WATER, (byte)0, true);
                fSettedBlocks++;
            }
            fItems.add(fBottom);
        } else {
            ArrayList<BlockPosition> lItems = fItems;
            fItems = new ArrayList<BlockPosition>();
            for(BlockPosition lPos : lItems) {
                Material lMat = lPos.getBlockType(pump.world);
                if (fLiquids.contains(lMat)) {
                    pump.floodedBlocks.add(lPos);
                    lList.add(lPos, Material.STATIONARY_WATER, (byte)0, true);
                    fSettedBlocks++;
                    for(BlockPosition lNext : new BlockPositionWalkAround(lPos, BlockPositionDelta.Horizontal)) {
                        if (!fAllItems.contains(lNext)
                                && !lItems.contains(lNext)
                                && !fItems.contains(lNext)) {
                            fItems.add(lNext);
                        }
                    }
                } else {
                    //plugin.getLogger().info("mat = " + lMat);
                }
            }
        }
        lList.execute();
        if (fItems.isEmpty() && fBottom.y < fTop.y) {
            fBottom.y++;
            for(BlockPosition lNext : new BlockPositionWalkAround(fBottom, BlockPositionDelta.Horizontal)) {
                if (!fAllItems.contains(lNext)
                        && !fItems.contains(lNext)) {
                    fItems.add(lNext);
                }
            }
        }
        fAllItems.addAll(fItems);
        //plugin.getLogger().info("count = " + fItems.size());
        if (pump.emergencyStop) {
            rollback();
        }
        if (fSettedBlocks > maxBlocks || fItems.isEmpty()) {
            if (fSettedBlocks > maxBlocks) {
                Player lPlayer = plugin.getServer().getPlayer(pump.playerName);
                if (lPlayer != null) {
                    lPlayer.sendMessage("Pump runs out of bounds... please correct it!");
                }
                plugin.getLogger().info("Pump " + pump.getName() + " runs out of bounds... please correct it!");
                rollback();
            }
            pump.flooded = true;
            plugin.stopPumpTask(this);
        }
    }

    protected int fDummyWait = 0;
    
    private void unflood() {
        Block lPump = fPump.getBlock(pump.world);
        if (fDummyWait > 0) {
            plugin.framework.setTypeAndData(lPump.getLocation(), lPump.getType(), (byte)(lPump.getData() | (byte)0x8), false);
            fDummyWait--;
        } else {
            if (!pump.floodedBlocks.isEmpty()) {
                SyncBlockList lList = new SyncBlockList(pump.world);
                lList.add(fPump, lPump.getType(), (byte)(lPump.getData() | (byte)0x8), false);
                ArrayList<BlockPosition> lItems = new ArrayList<BlockPosition>();
                for(BlockPosition lPos : pump.floodedBlocks) {
                    if (lPos.y == fTop.y) {
                        Block lBlock = lPos.getBlock(pump.world);
                        if (fLiquids.contains(lBlock.getType())) {
                            lList.add(lPos, Material.AIR, (byte)0, false);
                        }
                        lItems.add(lPos);
                    }
                }
                pump.floodedBlocks.removeAll(lItems);
                fTop.y--;
                if (fTop.y < fBottom.y) {
                    pump.floodedBlocks.clear();
                } else {
                    fDummyWait = 4;
                }
                lList.execute();
                if (pump.emergencyStop) {
                    rollback();
                }
            } else {
                pump.flooded = false;
                plugin.stopPumpTask(this);
            }
        }
    }

    private void init() {
        fSettedBlocks = 0;
        fItems = new ArrayList<BlockPosition>();
        fAllItems = new ArrayList<BlockPosition>();
        fLiquids = new ArrayList<Material>();
        fLiquids.add(Material.AIR);
        fLiquids.add(Material.WATER);
        fLiquids.add(Material.STATIONARY_WATER);
        fTop = pump.getBlock("PipeUp").position.clone();
        fBottom = pump.getBlock("PipeDown").position.clone();
        fPump = pump.getBlock("Pump").position.clone();
        fSwitch = pump.getBlock("Switch").position.clone();
        if (fSwitch.x < fTop.x) {
            fTop.x++;
            fBottom.x++;
        } else if (fSwitch.x > fTop.x) {
            fTop.x--;
            fBottom.x--;
        } else if (fSwitch.z > fTop.z) {
            fTop.z++;
            fBottom.z++;
        } else {
            fTop.z--;
            fBottom.z--;
        }
    }

    private void rollback() {
        SyncBlockList lList = new SyncBlockList(pump.world);
        for(BlockPosition lPos : pump.floodedBlocks) {
            Block lBlock = lPos.getBlock(pump.world);
            if (fLiquids.contains(lBlock.getType())) {
                lList.add(lPos, Material.AIR, (byte)0, false);
            }
        }
        pump.floodedBlocks.clear();
        lList.execute();
    }
    
}
