/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import entity.DiaDiem;
import entity.DsHoiNghi;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * FXML Controller class
 *
 * @author Crepp
 */
public class ChangeConfController{
    
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
    
    DsHoiNghi conf;
    
    File file = null;
    
    List<DiaDiem> addressList= new ArrayList<DiaDiem>();
    @FXML
    public void backButton(ActionEvent event) throws IOException{
        MainController.deleteChildrenNode();
    }
    
    
    public void initConf(DsHoiNghi conf) throws IOException{

        name.setText(conf.getTenHoiNghi());
        desc.setText(conf.getMoTa());
        longDesc.setText(conf.getMoTaChiTiet());
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(conf.getHinhAnh()));
        Image image = SwingFXUtils.toFXImage(img, null);
        imageView.setImage(image);
        this.conf = new DsHoiNghi(conf.getIdHoiNghi(), conf.getDiaDiem(), conf.getTenHoiNghi(), conf.getMoTa(),conf.getMoTaChiTiet(),conf.getThoiGian(),conf.getThoiGianKt(),conf.getHinhAnh());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = dateFormat.format(conf.getThoiGian());
        System.out.println(strDate);
        LocalDate dt = LocalDate.parse(strDate);
        
        dateStart.setValue(dt);
        for(DiaDiem d: addressList){
            if(d.getTen().equals(this.conf.getDiaDiem().getTen())){
                address.getSelectionModel().select(d.getTen());
            }
        }
        
        
        
    }
    
    public void initialize() {
        
        
        hStart.getItems().addAll("7","8","9","10","11", "12","13", "14", "15", "16");
        hEnd.getItems().addAll("7","8","9","10","11", "12", "13", "14", "15", "16");
        hStart.getSelectionModel().select("7");
        hEnd.getSelectionModel().select("12");

        
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
    
    public void change(ActionEvent event){
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
        if(file != null){
            try {
                imageData = new byte[(int) file.length()];
                FileInputStream fileInputStream = new FileInputStream(file);
                fileInputStream.read(imageData);
                fileInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            imageData = this.conf.getHinhAnh();
        }

        for(DiaDiem a : addressList){
            if(a.getTen().equals(address.getValue().toString())){
                try {
                    DsHoiNghi nConf = new DsHoiNghi(conf.getIdHoiNghi(), a, name.getText(), desc.getText(), longDesc.getText(),startTime,endTime, imageData);
                    session.update(nConf);
                    session.getTransaction().commit();
                    session.close();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Thành công");
                    alert.setHeaderText(null);
                    alert.setContentText("Thay đổi hội nghị thành công!");
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
