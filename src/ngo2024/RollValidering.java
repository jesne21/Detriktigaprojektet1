/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ngo2024;

/**
 *
 * @author Filip
 */
public class RollValidering {
    
    public static boolean ärAdmin(String roll){
        return roll.equalsIgnoreCase("admin");
    }
    
    public static boolean ärProjektledare(String roll){
        return roll.equalsIgnoreCase("projektledare");
    }
    
    public static boolean ärHandläggare(String roll){
        return roll.equalsIgnoreCase("handläggare");
    }
    
    public static boolean harBehörighetAttÄndraProjekt(String roll){
        return ärAdmin(roll)|| ärProjektledare(roll);
    }
    
}
