/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */
package com.mycompany.practica5;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketOptions;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Scanner;

/**
 *
 * @author brauu
 */
public class Practica5 {

    public static void main(String[] args) {
        try {
            NetworkInterface ni = NetworkInterface.getByIndex(14);//Cambiar el indice de la interfaz si es necesario
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            Mensaje mensaje;
            boolean inicio = true;
            
            //Creacion del socket no bloqueante
            DatagramChannel channel = DatagramChannel.open();
            channel.configureBlocking(false);
            channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            channel.setOption(StandardSocketOptions.IP_MULTICAST_IF, ni);
            channel.bind(new InetSocketAddress(1235));

            //Ingreso de nombre de usuario
            System.out.println("---------------------------------");
            System.out.println("Ingrese su usuario (Sin espacios)");
            System.out.println("---------------------------------");
            String autor = (new Scanner(System.in).nextLine());
            if (autor.length() > 10) {
                autor = autor.substring(0, 10);
            }
            System.out.println("---------------------------------");

            // Se une al grupo 
            InetAddress group = InetAddress.getByName("230.0.0.1");
            channel.join(group, ni);

            HiloEscritura he = new HiloEscritura();
            he.start();

            Selector sel = Selector.open();
            channel.register(sel, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            while (true) {
                sel.select();
                Iterator<SelectionKey> it = sel.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey k = (SelectionKey) it.next();
                    it.remove();
                    if (k.isReadable()) {
                        DatagramChannel dc = (DatagramChannel) k.channel();
                        ByteBuffer b = ByteBuffer.allocate(65535);
                        b.clear();
                        SocketAddress emisor = dc.receive(b);
                        b.flip();
                        InetSocketAddress e = (InetSocketAddress) emisor;
                        //System.out.println("Datagram recibido desde: " + e.getAddress() + ":" + e.getPort());
                        if (b.hasArray()) {
                            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(b.array()));
                            Mensaje m = (Mensaje) ois.readObject();
                            m.desplegar(autor);
                        }//if

                    } else if (k.isWritable()) {
                        DatagramChannel dc = (DatagramChannel) k.channel();
                        //mensaje de conexion
                        if (inicio) {
                            mensaje = new Mensaje(0, autor, "inicio");
                            inicio = false;
                            //Envio de mensaje
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            ObjectOutputStream oos = new ObjectOutputStream(baos);
                            oos.writeObject(mensaje);
                            oos.flush();
                            ByteBuffer b = ByteBuffer.wrap(baos.toByteArray());
                            InetSocketAddress dst = new InetSocketAddress("230.0.0.1", 1235);
                            dc.send(b, dst);
                            //Fin envio del mensaje
                        } else if (!he.getTexto().equals("")) {//Cualquier otro mensaje
                            String texto = he.getTexto();//Obtiene lo escrito por el usuario mediante un hilo 
                            he.setTexto("");
                            if (texto.charAt(0) == '@') {
                                mensaje = new Mensaje(autor, texto.substring(1, texto.indexOf(' ')), texto);//mensaje privado
                            } else {
                                mensaje = new Mensaje(1, autor, texto);
                            }
                            //Envio de mensaje
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            ObjectOutputStream oos = new ObjectOutputStream(baos);
                            oos.writeObject(mensaje);
                            oos.flush();
                            ByteBuffer b = ByteBuffer.wrap(baos.toByteArray());
                            InetSocketAddress dst = new InetSocketAddress("230.0.0.1", 1235);
                            dc.send(b, dst);
                            //Fin envio del mensaje
                        }

                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }
}
