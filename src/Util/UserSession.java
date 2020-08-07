/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util;

import entity.User;

/**
 *
 * @author Crepp
 */
public final  class UserSession {   
    private static UserSession instance;

    private User user; 

    private UserSession(User user) {
        this.user = user;
    }

    public static UserSession getInstace() {
        return instance;
    }
    
    public static UserSession setInstace(User user){
        if(instance == null) {
            instance = new UserSession(user);
        }
        return instance;
    }

    public User getUser(){
        return this.user;
    }

    public void cleanUserSession() {
        instance = null;
    }

    
}
