/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util;

import entity.DsHoiNghi;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author Crepp
 */
public class HNCell extends ListCell<DsHoiNghi>{
    @FXML
    private Label name;

    @FXML
    private Label desc;

    @FXML
    private Label address;
    
    @FXML
    private Label time;
    
    @FXML
    private Label capacity;
    
    @FXML
    private ImageView imgID;
    
    private DsHoiNghi conf = null;
    
    public HNCell() {
        loadFXML();
    }

    private void loadFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Conference.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    
    
    

    @Override
    protected void updateItem(DsHoiNghi conf, boolean empty) {
        super.updateItem(conf, empty);

        if(empty) {
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }
        else {
            try {
                this.conf = conf;
                name.setText(conf.getTenHoiNghi());
                desc.setText(conf.getMoTa());
                address.setText(conf.getDiaDiem().getTen());
                DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                String strDate = dateFormat.format(conf.getThoiGian());  
                time.setText(strDate);
                capacity.setText(Integer.toString(conf.getDiaDiem().getSucChua()));
                Image image = new Image(new ByteArrayInputStream(conf.getHinhAnh()));
                imgID.setImage(image);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            } catch (Exception e) {
                System.out.println(e);
            }

        }
    }
}

