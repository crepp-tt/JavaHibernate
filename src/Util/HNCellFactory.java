/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util;

import entity.DsHoiNghi;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 *
 * @author Crepp
 */
public class HNCellFactory implements Callback<ListView<DsHoiNghi>, ListCell<DsHoiNghi>>{

    @Override
    public ListCell<DsHoiNghi> call(ListView<DsHoiNghi> param) {
        return new HNCell();
    }
}
