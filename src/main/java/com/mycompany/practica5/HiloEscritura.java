/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.practica5;

import java.util.Scanner;

/**
 *
 * @author Mauricio Beltrán
 */

//Hilo de captura de mensaje escrito por el usuario
public class HiloEscritura extends Thread{
    public String texto = "";

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }
    
    @Override
    public void run(){
        Scanner sc = new Scanner(System.in);
        while (true) {
            texto = sc.nextLine();
        }
    
    }
    
    
}
