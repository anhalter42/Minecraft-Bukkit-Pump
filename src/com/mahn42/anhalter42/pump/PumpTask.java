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
    public int maxBlocks = 42000;
    
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
    protected BlockPosition fSwitchPump;
    protected BlockPosition fSwitch;
    
    @Override
    public void run() {
        if (!fInRun) {
            fInRun = true;
            try {
                if (!fInit) {
                    fInit = init();;
                }
                if (fInit) {
                    if (pump.emergencyStop) {
                        rollback();
                    } else {
                        if (flood) {
                            flood();
                        } else {
                            unflood();
                        }
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
    
    protected int fPumpDelay = 8;
    
    private void flood() {
        SyncBlockList lList = new SyncBlockList(pump.world);
        //Block lPump = fPump.getBlock(pump.world);
        //lList.add(fPump, lPump.getType(), (byte)(lPump.getData() | (byte)0x8), false);
        Block lSwitchPump = fSwitchPump.getBlock(pump.world);
        if (fPumpDelay > 0) {
            fPumpDelay--;
        } else {
            lList.add(fSwitchPump, lSwitchPump.getType(), (byte)(lSwitchPump.getData() ^ (byte)0x8), true);
            fPumpDelay = 8;
        }
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
                runsOut();
                rollback();
            }
            plugin.getLogger().info("Pump BlockCount:" + fSettedBlocks);
            pump.flooded = true;
            plugin.framework.setTypeAndData(lSwitchPump.getLocation(), lSwitchPump.getType(), (byte)(lSwitchPump.getData() & (byte)0xF7), true);
            plugin.stopPumpTask(this);
        }
    }

    protected int fDummyWait = 0;
    
    private void unflood() {
        //Block lPump = fPump.getBlock(pump.world);
        Block lSwitchPump = fSwitchPump.getBlock(pump.world);
        if (fDummyWait > 0) {
            //plugin.framework.setTypeAndData(lPump.getLocation(), Material.PISTON_BASE /*lPump.getType()*/, (byte)(lPump.getData() | (byte)0x8), false);
            if (fPumpDelay > 0) {
                fPumpDelay--;
            } else {
                fPumpDelay = 8;
                plugin.framework.setTypeAndData(lSwitchPump.getLocation(), lSwitchPump.getType(), (byte)(lSwitchPump.getData() ^ (byte)0x8), true);
            }
            fDummyWait--;
        } else {
            if (!pump.floodedBlocks.isEmpty()) {
                SyncBlockList lList = new SyncBlockList(pump.world);
                //lList.add(fPump, Material.PISTON_BASE /*lPump.getType()*/, (byte)(lPump.getData() | (byte)0x8), false);
                lList.add(fSwitchPump, lSwitchPump.getType(), (byte)(lSwitchPump.getData() ^ (byte)0x8), true);
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
                    fDummyWait = 16;
                }
                lList.execute();
                if (pump.emergencyStop) {
                    rollback();
                }
            } else {
                pump.flooded = false;
                plugin.framework.setTypeAndData(lSwitchPump.getLocation(), lSwitchPump.getType(), (byte)(lSwitchPump.getData() & (byte)0xF7), true);
                plugin.stopPumpTask(this);
            }
        }
    }

    private boolean init() {
        fSettedBlocks = 0;
        fItems = new ArrayList<BlockPosition>();
        fAllItems = new ArrayList<BlockPosition>();
        fLiquids = new ArrayList<Material>();
        fLiquids.add(Material.AIR);
        fLiquids.add(Material.WATER);
        fLiquids.add(Material.STATIONARY_WATER);
        fLiquids.add(Material.REDSTONE_WIRE);
        fLiquids.add(Material.REDSTONE_TORCH_ON);
        fLiquids.add(Material.REDSTONE_TORCH_OFF);
        fLiquids.add(Material.TORCH);
        fLiquids.add(Material.CACTUS);
        fLiquids.add(Material.DEAD_BUSH);
        fLiquids.add(Material.DETECTOR_RAIL);
        fLiquids.add(Material.GRASS);
        fLiquids.add(Material.RAILS);
        fLiquids.add(Material.SAPLING);
        fLiquids.add(Material.VINE);
        fLiquids.add(Material.WHEAT);
        fLiquids.add(Material.SEEDS);
        fLiquids.add(Material.SUGAR_CANE_BLOCK);
        fLiquids.add(Material.WEB);
        fLiquids.add(Material.WATER_LILY);
        fLiquids.add(Material.getMaterial(31)); // tall grass
        fLiquids.add(Material.YELLOW_FLOWER); // yellow flower
        fLiquids.add(Material.RED_ROSE); // red flower
        fLiquids.add(Material.BROWN_MUSHROOM); // mushroom
        fLiquids.add(Material.RED_MUSHROOM); // mushroom
        fTop = pump.getBlock("PipeUp").position.clone();
        fBottom = pump.getBlock("PipeDown").position.clone();
        fPump = pump.getBlock("Pump").position.clone();
        fSwitchPump = pump.getBlock("PumpSwitch").position.clone();
        fSwitch = pump.getBlock("Switch").position.clone();
        if (fSwitch.x < fTop.x) {
            fTop.x++;
            fBottom.x++;
        } else if (fSwitch.x > fTop.x) {
            fTop.x--;
            fBottom.x--;
        } else if (fSwitch.z < fTop.z) {
            fTop.z++;
            fBottom.z++;
        } else {
            fTop.z--;
            fBottom.z--;
        }
        if (flood) {
            if (!check()) {
                runsOut();
                plugin.stopPumpTask(this);
                return false;
            } else {
                return true;
            }
        } else {
            fDummyWait = 16;
            return true;
        }
    }
    
    private void runsOut() {
        Player lPlayer = plugin.getServer().getPlayer(pump.playerName);
        if (lPlayer != null) {
            lPlayer.sendMessage("Pump runs out of bounds... please correct it!");
        }
        plugin.getLogger().info("Pump " + pump.getName() + " runs out of bounds... please correct it!");
    }

    private void rollback() {
        SyncBlockList lList = new SyncBlockList(pump.world);
        for(BlockPosition lPos : pump.floodedBlocks) {
            Block lBlock = lPos.getBlock(pump.world);
            if (fLiquids.contains(lBlock.getType())) {
                lList.add(lPos, Material.AIR, (byte)0, false);
            }
        }
        Block lSwitchPump = fSwitchPump.getBlock(pump.world);
        lList.add(fSwitchPump, lSwitchPump.getType(), (byte)(lSwitchPump.getData() & (byte)0xF7), true);
        lList.execute();
        lList = new SyncBlockList(pump.world);
        for(BlockPosition lPos : pump.floodedBlocks) {
            Block lBlock = lPos.getBlock(pump.world);
            if (fLiquids.contains(lBlock.getType())) {
                lList.add(lPos, Material.AIR, (byte)0, true);
            }
        }
        lList.execute();
        pump.floodedBlocks.clear();
    }
    
    private boolean check() {
        boolean lResult = true;
        ArrayList<BlockPosition> lAllItems = new ArrayList<BlockPosition>();
        ArrayList<BlockPosition> lGlobItems = new ArrayList<BlockPosition>();
        BlockPosition lBottom = fBottom.clone();
        int lSettedBlocks = 0;
        lGlobItems.addAll(fItems);
        do {
            ArrayList<BlockPosition> lItems = lGlobItems;
            lGlobItems = new ArrayList<BlockPosition>();
            for(BlockPosition lPos : lItems) {
                Material lMat = lPos.getBlockType(pump.world);
                if (fLiquids.contains(lMat)) {
                    lSettedBlocks++;
                    for(BlockPosition lNext : new BlockPositionWalkAround(lPos, BlockPositionDelta.Horizontal)) {
                        if (!lAllItems.contains(lNext)
                                && !lItems.contains(lNext)
                                && !lGlobItems.contains(lNext)) {
                            lGlobItems.add(lNext);
                        }
                    }
                }
            }
            if (lGlobItems.isEmpty() && lBottom.y < fTop.y) {
                lBottom.y++;
                for(BlockPosition lNext : new BlockPositionWalkAround(lBottom, BlockPositionDelta.Horizontal)) {
                    if (!lAllItems.contains(lNext)
                            && !lGlobItems.contains(lNext)) {
                        lGlobItems.add(lNext);
                    }
                }
            }
            lAllItems.addAll(lGlobItems);
        } while (lSettedBlocks < maxBlocks && !lGlobItems.isEmpty());
        lResult = lSettedBlocks < maxBlocks;
        return lResult;
    }
    
}
