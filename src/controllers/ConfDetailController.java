/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import Util.UserSession;
import entity.DsHoiNghi;
import entity.ThamGiaHn;
import entity.ThamGiaHnId;
import entity.User;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javax.imageio.ImageIO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * FXML Controller class
 *
 * @author Crepp
 */
public class ConfDetailController{
    
   @FXML
    private AnchorPane anchorPane;

    @FXML
    private ImageView imageView;

    @FXML
    private Button btnJoin;

    @FXML
    private Label longDesc;

    @FXML
    private Label confName;

    @FXML
    private Label confDesc;

    @FXML
    private Label capacity;

    @FXML
    private Label timeStart;

    @FXML
    private Label timeEnd;
    
    private DsHoiNghi conf;

    @FXML
    public void backButton(ActionEvent event) throws IOException{
        MainController.deleteChildrenNode();
    }
    
    
    public void initConf(DsHoiNghi conf) throws IOException{

        confName.setText(conf.getTenHoiNghi());
        confDesc.setText(conf.getMoTaChiTiet());
        longDesc.setText(conf.getMoTaChiTiet());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String startTime = dateFormat.format(conf.getThoiGian());  
        String endTime = dateFormat.format(conf.getThoiGianKt());
        timeStart.setText(startTime);
        timeEnd.setText(endTime);
        capacity.setText(Integer.toString(conf.getDiaDiem().getSucChua()));
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(conf.getHinhAnh()));
        Image image = SwingFXUtils.toFXImage(img, null);
        imageView.setImage(image);
        this.conf = new DsHoiNghi(conf.getIdHoiNghi(), conf.getDiaDiem(), conf.getTenHoiNghi(), conf.getMoTa(),conf.getMoTaChiTiet(),conf.getThoiGian(), conf.getThoiGianKt(),conf.getHinhAnh());
        check();
    }
    
    public void initialize() {
  
    }
    
    public void check(){
        if(UserSession.getInstace() == null){
            Integer count = 0;
            Configuration cfg = new Configuration();
            cfg.configure("hibernate.cfg.xml");
            SessionFactory factory = cfg.buildSessionFactory();
            Session session = factory.openSession();
            session.beginTransaction();
            List result = session.createQuery("FROM ThamGiaHn WHERE id_hoi_nghi=" + conf.getIdHoiNghi()).list();
            Iterator it = result.iterator();
            
            while(it.hasNext()){
                count++;
                it.next();
            }
            if(count >= conf.getDiaDiem().getSucChua()){
                btnJoin.setDisable(true);
            }

        }
        else{
            Configuration cfg = new Configuration();
            cfg.configure("hibernate.cfg.xml");
            SessionFactory factory = cfg.buildSessionFactory();
            Session session = factory.openSession();
            session.beginTransaction();
            List result = session.createQuery("FROM ThamGiaHn WHERE id_user = " +UserSession.getInstace().getUser().getIdUser() +  " AND id_hoi_nghi=" + conf.getIdHoiNghi()).list();
            Iterator it = result.iterator();
            if(it.hasNext()){
                Object o = (Object)it.next();
                ThamGiaHn tg = (ThamGiaHn)o;
                System.out.println(tg.getTrangThai());
                if(tg.getTrangThai().equals("pending")){
                    btnJoin.setText("Đã đăng kí");
                }
                else if(tg.getTrangThai().equals("accepted")){
                    btnJoin.setText("Đã tham gia");
                }
                else{
                    btnJoin.setText("Đã bị từ chối");
                }
                btnJoin.setDisable(true);
            }
        }
    }

    @FXML
    public void joinConf(ActionEvent event) throws IOException{
        if(UserSession.getInstace() == null){
            Dialog<?> loginDialog = new Dialog<>();
            loginDialog.initOwner(anchorPane.getScene().getWindow());
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/login.fxml"));
            
            try {
                loginDialog.getDialogPane().setContent(loader.load());
                loginDialog.initStyle(StageStyle.DECORATED);
                loginDialog.setResizable(false);
                loginDialog.getDialogPane().setPrefSize(600, 400);
            } catch (Exception e) {
                System.out.println(e);
            }
            
            Window window = loginDialog.getDialogPane().getScene().getWindow();
            window.setOnCloseRequest(event1->{
                System.out.println("close");
            });
            loginDialog.showAndWait();
            check();
            MainController.reload();
            
            

        }
        else{

            Configuration cfg = new Configuration();
            cfg.configure("hibernate.cfg.xml");
            SessionFactory factory = cfg.buildSessionFactory();
            Session session = factory.openSession();
            session.beginTransaction();
            
            User user = UserSession.getInstace().getUser();
            ThamGiaHnId tgId = new ThamGiaHnId(conf.getIdHoiNghi(), user.getIdUser());
            ThamGiaHn tg = new ThamGiaHn(tgId, conf, user, "pending");
            session.save(tg);
            session.getTransaction().commit();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Đăng ký thành công");
            alert.setHeaderText(null);
            alert.setContentText("Đăng ký tham gia hội nghị thành công!");
            alert.showAndWait();
            check();
            
        }
    }
    
    @FXML
    public void getAttendList(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/AttendList.fxml"));
        Parent listConfParent = loader.load();
        AttendListController controller = loader.getController();
        controller.initConf(this.conf);
        MainController.addChildrenNode(listConfParent);
    }
    
}
