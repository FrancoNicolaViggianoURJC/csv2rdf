package com.fnvigg.csv2rdf;

import java.io.*;

public class Gestor_usuarios {
    private String usuario;
    private String password;

    public Gestor_usuarios() {

        if(!comprobarArchivo()){
          //Si no existe el archivo, se crea
            crearArchivo();
        }

    }
    public String getUsuario() {
        return usuario;
    }
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    private boolean comprobarArchivo(){
        File f = new File("src/main/resources/usersDB.txt");
        return f.exists() && !f.isDirectory();
    }
    private void crearArchivo(){
        try {
            //Creacion del objeto
            File archivo = new File("src/main/resources/usersDB.txt");
            FileWriter escritor = new FileWriter(archivo);
            BufferedWriter bufferedWriter = new BufferedWriter(escritor);
            bufferedWriter.write(" ");
            bufferedWriter.close();
            escritor.close();
            System.out.println("Archivo creado exitosamente.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public boolean validar_Usuario(String user, String pass){
        this.setUsuario(user);
        this.setPassword(pass);
        String linea = "";

        //Iterar sobre cada linea del txt comprobando que exista los pares "user","pass"
        try {
            FileReader lectorArchivo = new FileReader("src/main/resources/usersDB.txt");
            BufferedReader bufferedLector = new BufferedReader(lectorArchivo);
            while((linea = bufferedLector.readLine()) != null){
                //procesar linea
                String[] partes = linea.split(String.valueOf(","));
                if(partes[0].equals(user) && partes[1].equals(pass)){
                    bufferedLector.close();
                    lectorArchivo.close();
                    return true;
                }
            }

            bufferedLector.close();
            return false;

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public boolean registrarUsuario(String user, String pass, String rol){
        try {
            //Creacion del objeto para escribir con el archivo
            File archivo = new File("src/main/resources/usersDB.txt");
            FileWriter escritor = new FileWriter(archivo, true);
            BufferedWriter bufferedWriter = new BufferedWriter(escritor);
            bufferedWriter.write(user + "," + pass + "," + rol + '\n');
            bufferedWriter.close();
            escritor.close();
            System.out.println("Usuario escrito exitosamente.");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String obtenerRoles(String user) {
        try {
            //Creacion del objeto para escribir con el archivo
            File archivo = new File("src/main/resources/usersDB.txt");
            FileReader lector = new FileReader(archivo);
            BufferedReader br = new BufferedReader(lector);
            String linea = br.readLine();
            while(linea !=null){
                String[] partes = linea.split(String.valueOf(","));
                if(partes[0].equals(user)){
                    br.close();
                    lector.close();
                    return partes[2];
                }else{
                    linea = br.readLine();
                }
            }
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
