/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mahn42.anhalter42.pump;

/**
 *
 * @author andre
 */
public class PumpTask implements Runnable {

    public Pump plugin;
    public PumpBuilding pump;
    public int taskId;
    
    public boolean flood = false;
    
    public PumpTask(Pump aPlugin) {
        plugin = aPlugin;
    }
    
    protected boolean fInRun = false;
    
    @Override
    public void run() {
        if (!fInRun) {
            fInRun = true;
            try {
                if (flood) {
                    flood();
                } else {
                    unflood();
                }
            } finally {
                fInRun = false;
            }
        }
    }

    private void flood() {
        plugin.stopGateTask(this);
    }

    private void unflood() {
        plugin.stopGateTask(this);
    }
    
}
