package com.tracki.ui.inventory;

import com.tracki.data.model.response.config.Inventory;

import java.util.Comparator;

/**
 * Created by Vikas Kesharvani on 18/11/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
class InventoryComparator implements Comparator<Inventory> {


    @Override
    public int compare(Inventory task, Inventory t1) {
        if (task.getQuantity() > t1.getQuantity()) return -1;
        if (task.getQuantity() < t1.getQuantity()) return 1;
        else return 0;
    }
}
