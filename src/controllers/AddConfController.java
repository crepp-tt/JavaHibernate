/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import entity.DiaDiem;
import entity.DsHoiNghi;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * FXML Controller class
 *
 * @author Crepp
 */
public class AddConfController implements Initializable {
    
    @FXML
    private DatePicker dateStart;

    @FXML
    private ComboBox address;
    
    @FXML
    private ImageView imageView;
    

    
    private Image image = null;
    
    @FXML
    private TextField name;

    @FXML
    private TextField desc;

    @FXML
    private TextField longDesc;

    @FXML
    private ComboBox hStart;

    @FXML
    private ComboBox hEnd;

    @FXML
    private ComboBox mStart;

    @FXML
    private ComboBox mEnd;
    
    File file;
    
    List<DiaDiem> addressList= new ArrayList<DiaDiem>();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //set up time picker
        hStart.getItems().addAll("7","8","9","10","11","13", "14", "15", "16");
        hEnd.getItems().addAll("7","8","9","10","11","1", "13", "14", "15", "16");
        hStart.getSelectionModel().select("7");
        hEnd.getSelectionModel().select("8");
        
        mStart.getItems().addAll("00", "15", "30", "45");
        mEnd.getItems().addAll("00", "15", "30", "45");
        mStart.getSelectionModel().select("00");
        mEnd.getSelectionModel().select("00");
        

        
        
        // TODO
        try {
            Configuration cfg = new Configuration();
            cfg.configure("hibernate.cfg.xml");
            SessionFactory factory = cfg.buildSessionFactory();
            Session session = factory.openSession();
            session.beginTransaction();
            List result = session.createQuery("from DiaDiem").list();
            Iterator it = result.iterator();
            while(it.hasNext()){
                Object o = (Object)it.next();
                DiaDiem diaDiem = (DiaDiem)o;
                address.getItems().add(diaDiem.getTen());
                addressList.add(diaDiem);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        
        
    }

    public void chooseImage(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn hình ảnh");
        file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            image = new Image(file.toURI().toString());
            imageView.setImage(image);
        }
    }
    
    public void cancel(ActionEvent event){
        MainController.deleteChildrenNode();
    }
    
    public void add(ActionEvent event) throws IOException{
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Configuration cfg = new Configuration();
        cfg.configure("hibernate.cfg.xml");
        SessionFactory factory = cfg.buildSessionFactory();
        Session session = factory.openSession();
        session.beginTransaction();
        
        String date = dateStart.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String start = date+" "+hStart.getValue().toString()+":" + mStart.getValue().toString()+":00";
        String end = date+" "+hEnd.getValue().toString()+":" + mEnd.getValue().toString()+":00";
        Timestamp startTime = Timestamp.valueOf(start);
        Timestamp endTime = Timestamp.valueOf(end);
        if(startTime.before(now)){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Lỗi");
            alert.setHeaderText(null);
            alert.setContentText("Thời gian không phù hợp!");
            alert.showAndWait();
            return;
        }
        if(startTime.after(endTime)){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Lỗi");
            alert.setHeaderText(null);
            alert.setContentText("Thời gian không phù hợp!");
            alert.showAndWait();
            return;
        }
        int temp = 0;
        for(DiaDiem d: addressList){
            if(d.getTen().equals(address.getValue())){
                temp = d.getIdDiaDiem();
            }
        }
        List result = session.createQuery("from DsHoiNghi where dia_diem = "+ temp).list();
        Iterator it = result.iterator();
        while(it.hasNext()){
            Object o = (Object)it.next();
            DsHoiNghi conf = (DsHoiNghi)o;
            if(startTime.equals(conf.getThoiGian()) || startTime.equals(conf.getThoiGian()) || startTime.equals(conf.getThoiGianKt()) || startTime.equals(conf.getThoiGianKt())){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Lỗi");
                alert.setHeaderText(null);
                alert.setContentText("Thời gian có hội nghị khác diễn ra!");
                alert.showAndWait();
                return;
            }
            if(endTime.equals(conf.getThoiGian()) || endTime.equals(conf.getThoiGian()) || endTime.equals(conf.getThoiGianKt()) || endTime.equals(conf.getThoiGianKt())){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Lỗi");
                alert.setHeaderText(null);
                alert.setContentText("Thời gian có hội nghị khác diễn ra!");
                alert.showAndWait();
                return;
            }
            if((startTime.after(conf.getThoiGian()) && startTime.before(conf.getThoiGianKt())) || (endTime.after(conf.getThoiGian()) && endTime.before(conf.getThoiGianKt()))){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Lỗi");
                alert.setHeaderText(null);
                alert.setContentText("Thời gian có hội nghị khác diễn ra!");
                alert.showAndWait();
                return;
            }
        }
        byte[] imageData = null;
            try {
                imageData = new byte[(int) file.length()];
                FileInputStream fileInputStream = new FileInputStream(file);
                fileInputStream.read(imageData);
                fileInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        
        for(DiaDiem a : addressList){
            if(a.getTen().equals(address.getValue().toString())){
                try {
                    DsHoiNghi nConf = new DsHoiNghi(a, name.getText(), desc.getText(), longDesc.getText(),startTime,endTime, imageData);
                    session.save(nConf);
                    session.getTransaction().commit();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Thành công");
                    alert.setHeaderText(null);
                    alert.setContentText("Tạo hội nghị thành công!");
                    alert.showAndWait();
                    ManageConfAdminController.reload();
                    MainController.deleteChildrenNode();
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Lỗi");
                    alert.setHeaderText(null);
                    alert.setContentText("Hình ảnh quá kích thước!");
                    alert.showAndWait();
                }
                
            }
        }
        
        
    }
    
}
