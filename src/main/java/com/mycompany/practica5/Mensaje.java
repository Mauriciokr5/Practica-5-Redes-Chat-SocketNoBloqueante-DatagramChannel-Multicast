/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.practica5;

import java.io.Serializable;

/**
 *
 * @author mauri
 */
public class Mensaje implements Serializable{
    int tipo;//0.-Conexion //1.- mensaje publico//2.- mensaje privado
    String autor;
    String destinatario;
    String mensaje;

    public Mensaje(int tipo, String autor, String mensaje) {
        this.tipo = tipo;
        this.autor = autor;
        this.mensaje = mensaje;
    }
    
    //Mensaje privado
    public Mensaje(String autor, String destinatario, String mensaje) throws IllegalArgumentException{ 
        //if (tipo != 2) {throw new IllegalArgumentException("El mensaje debe de ser privado");}
        this.tipo = 2;
        this.autor = autor;
        this.destinatario = destinatario;
        this.mensaje = mensaje;
    }
    
    
    public void desplegar(String usuario){
        String formatoPrint = " %10s | %-256s %n";
        switch (this.tipo){
            case 0:
                System.out.format(formatoPrint,"Sistema","El usuario "+ this.autor +" se ha conectado");
            break;
            case 1:
                System.out.format(formatoPrint, this.autor, mensaje);
            break;    
            case 2:
                if(usuario.equals(this.destinatario))
                    System.out.format(" %10s |[privado] %-256s %n", this.autor, mensaje);
            break;    
        }
    }
    
    
    
    
}