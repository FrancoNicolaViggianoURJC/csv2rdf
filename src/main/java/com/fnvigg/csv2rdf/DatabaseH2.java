package com.fnvigg.csv2rdf;
import javafx.util.Pair;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
public class DatabaseH2 {

    private static String urlBBDD;
    private static String userBBDD;
    private static String passBBDD;

    //Creacion/inicializacion bbdd
    public DatabaseH2() throws SQLException {
        urlBBDD = "jdbc:h2:./database;DB_CLOSE_ON_EXIT=FALSE"; // '~/database' es la ubicación de la base de datos
        userBBDD = "admin";
        passBBDD = "1234";

        getConnection();
        //Quitar esto una vez probada la bbdd
        freshDB();
        createTables();
    }

    public static void createTables() {
        //Crear cada tabla
        createTableUsersDB();
        createTableProyecto();
        createTableArchivo();
        createTableAtributo();
        System.out.println("Base de datos inicializada");
    }

    public static Connection getConnection() throws SQLException {
        // Devolver la conexion
        return DriverManager.getConnection(urlBBDD, userBBDD, passBBDD);
    }
    public static void freshDB() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            String dropAllTables = "DROP ALL OBJECTS";
            statement.executeUpdate(dropAllTables);

            System.out.println("Base de datos borrada.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTableUsersDB() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            String createTableSQL = "CREATE TABLE IF NOT EXISTS UsersDB ("
                    + "idUsuario INT AUTO_INCREMENT UNIQUE,"
                    + "nombre VARCHAR(255) NOT NULL PRIMARY KEY,"
                    + "password VARCHAR(255) NOT NULL,"
                    + "rol VARCHAR(255))";
            statement.executeUpdate(createTableSQL);

            System.out.println("Tabla usersDB creada correctamente.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTableProyecto() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            //idProyecto INT AUTO_INCREMENT UNIQUE
            String createTableSQL = "CREATE TABLE IF NOT EXISTS Proyecto ("
                    + "nombreProyecto VARCHAR(255) PRIMARY KEY,"
                    + "ingenieroDatos VARCHAR(255),"
                    + "ingenieroOntologico VARCHAR(255),"
                    + "fase VARCHAR(10),"
                    + "idProyecto INT AUTO_INCREMENT UNIQUE,"
                    + "rutaEsquemaGrafico VARCHAR(255),"
                    + "requerimientosDatos VARCHAR(255),"
                    + "rutaModeloDominioObjetivo VARCHAR(255),"
                    + "rutaEsquemaOntologico VARCHAR(255),"
                    + "publisher VARCHAR(255),"
                    + "descripcionES VARCHAR(255),"
                    + "descripcionEN VARCHAR(255))";
            statement.executeUpdate(createTableSQL);

            System.out.println("Tabla proyecto creada correctamente.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTableArchivo() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            //idProyecto INT AUTO_INCREMENT UNIQUE
            String createTableSQL = "CREATE TABLE IF NOT EXISTS Archivo ("
                    + "idProyecto INT NOT NULL,"
                    + "rutaArchivo VARCHAR(255) PRIMARY KEY,"
                    + "nombreArchivo VARCHAR(255) UNIQUE,"
                    + "idAtributoPrimario INT UNIQUE,"
                    + "idArchivo INT AUTO_INCREMENT UNIQUE)";
            statement.executeUpdate(createTableSQL);

            System.out.println("Tabla Archivo creada correctamente.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTableAtributo() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            //idProyecto INT AUTO_INCREMENT UNIQUE
            String createTableSQL = "CREATE TABLE IF NOT EXISTS Atributo ("
                    + "idArchivo INT NOT NULL,"
                    + "idProyecto INT NOT NULL,"
                    + "idAtributo INT AUTO_INCREMENT PRIMARY KEY,"
                    + "nombre VARCHAR(255) NOT NULL,"
                    + "valor VARCHAR(255))";
            statement.executeUpdate(createTableSQL);

            System.out.println("Tabla Archivo creada correctamente.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Gestion de usuarios
    public static boolean insertUser(String user, String pass1, String rol) {
        String sql = "INSERT INTO UsersDB (nombre, password, rol) VALUES ('"+user+"','"+pass1+"','"+rol+"')";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Ejecutar la sentencia SQL de inserción
            int filasInsertadas = statement.executeUpdate();

            if (filasInsertadas > 0) {
                System.out.println("Datos insertados correctamente.");
                return true;
            } else {
                System.out.println("No se insertaron datos.");
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean getUser(String selectedUser, String pass1) {
        String sql = "SELECT * FROM UsersDB WHERE nombre = '"+selectedUser+"';";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            //Obtener la consulta
            try (ResultSet resultSet = statement.executeQuery()) {
                // Deberia haber como maximo una fila
                while (resultSet.next()) {
                    // Leer los valores de la fila
                    int id = resultSet.getInt("idUsuario");
                    String nombreDevuelto = resultSet.getString("nombre");
                    String rolDevuelto = resultSet.getString("rol");
                    String passDevuelta = resultSet.getString("password");

                    if(pass1.equals(passDevuelta)){
                        //Correcto
                        //Establecer el usuario en la sesion
                        AtributosSesion.setUser(nombreDevuelto);
                        AtributosSesion.setRol(rolDevuelto);
                        return true;

                    }else{
                        return false;
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    //Gestion de proyectos
    public static boolean insertProyecto(String ingDatos, String ingOntologico, String nombreProyecto) {
        String cim = "CIM";
        String sql = "INSERT INTO Proyecto (nombreProyecto, ingenieroDatos, ingenieroOntologico, fase)" +
                     " VALUES ('"+nombreProyecto+"','"+ingDatos+"','"+ingOntologico+"','"+cim+"')";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Ejecutar la sentencia SQL de inserción
            int filasInsertadas = statement.executeUpdate();

            if (filasInsertadas > 0) {
                System.out.println("Proyecto creado correctamente.");
                return true;
            } else {
                System.out.println("Fallo al crear el proyecto");
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<String> getProyectos() {
        //Obtencion de todos los proyectos creados en la base de datos
        List<String> proyectos = new LinkedList<>();
        String sql = "SELECT * FROM Proyecto;";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            //Obtener la consulta
            try (ResultSet resultSet = statement.executeQuery()) {
                // Rellenar la linkedList
                while (resultSet.next()) {
                    // Leer los valores de la fila
                    String nombreProyecto = resultSet.getString("nombreProyecto");
                    proyectos.add(nombreProyecto);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        //return false;
        return proyectos;
    }

    public static ArrayList<String> getProyectosDatos(String proyecto) {
        ArrayList<String> datos = new ArrayList<>();
        String sql = "SELECT nombreProyecto, ingenieroDatos, ingenieroOntologico FROM Proyecto WHERE nombreProyecto = '"+proyecto+"';";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            //Obtener la consulta
            try (ResultSet resultSet = statement.executeQuery()) {
                // Solo deberia haber un resultado
                while (resultSet.next()) {
                    // Leer los valores de la fila necesarios
                    String ingenieroDatos = resultSet.getString("ingenieroDatos");
                    String ingenieroOntologico = resultSet.getString("ingenieroOntologico");
                    datos.add(ingenieroDatos);
                    datos.add(ingenieroOntologico);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //return false;
        return datos;
    }

    public static String getProyectosFase(String nombreProyecto) {
        String sql = "SELECT fase FROM Proyecto WHERE nombreProyecto = '"+nombreProyecto+"';";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            //Obtener la consulta
            try (ResultSet resultSet = statement.executeQuery()) {
                // Solo deberia haber un resultado
                while (resultSet.next()) {
                    // Leer los valores de la fila necesarios
                   return resultSet.getString("fase");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //return false;
        return "";
    }

    public static String getProyectosID(String nombreProyecto) {
        String sql = "SELECT idProyecto FROM Proyecto WHERE nombreProyecto = '"+nombreProyecto+"';";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            //Obtener la consulta
            try (ResultSet resultSet = statement.executeQuery()) {
                // Solo deberia haber un resultado
                while (resultSet.next()) {
                    // Leer los valores de la fila necesarios
                    return resultSet.getString("idProyecto");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //return false;
        return "";
    }

    public static Boolean deleteProyecto(String nombreProyecto) {
        String sql = "DELETE FROM Proyecto WHERE nombreProyecto = '"+nombreProyecto+"';";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Ejecutar la sentencia SQL de borrado
            int filasEliminadas = statement.executeUpdate();

            if (filasEliminadas > 0) {
                System.out.println("El proyecto se eliminó correctamente.");
                return true;
            } else {
                System.out.println("No se eliminó el proyecto.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    //Fase CIM
    public static Boolean insertCimArchivo(String ruta, String nombre, String idProyecto) {
        String sql = "INSERT INTO Archivo (idProyecto, rutaArchivo, nombreArchivo)" +
                " VALUES ('"+idProyecto+"','"+ruta+"','"+nombre+"')";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            // Ejecutar la sentencia SQL de inserción
            int filasInsertadas = statement.executeUpdate();
            if(filasInsertadas>0){
                return true;
            }else
                return false;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static LinkedList<File> getCimRutasArchivos(String idProyecto) {
        LinkedList<File> ficheros = new LinkedList<>();
        File aux;
        String sql = "SELECT rutaArchivo FROM Archivo WHERE idProyecto = '"+idProyecto+"';";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            //Obtener la consulta
            try (ResultSet resultSet = statement.executeQuery()) {
                // Resultados
                while (resultSet.next()) {
                    // Leer los valores de la fila necesarios
                    String ruta = resultSet.getString("rutaArchivo");
                    aux = new File(ruta);
                    ficheros.add(aux);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //return false;
        return ficheros;
    }

    public static LinkedList<String> getCimNombresArchivos(String idProyecto) {
        LinkedList<String> nombres = new LinkedList<>();
        String sql = "SELECT nombreArchivo FROM Archivo WHERE idProyecto = '"+idProyecto+"';";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            //Obtener la consulta
            try (ResultSet resultSet = statement.executeQuery()) {
                // Resultados
                while (resultSet.next()) {
                    // Leer los valores de la fila necesarios
                    String nombre = resultSet.getString("nombreArchivo");
                    nombres.add(nombre);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //return false;
        return nombres;
    }

    public static String getCimRutaIndividual(String nombreArchivo) {
        String sql = "SELECT rutaArchivo FROM Archivo WHERE nombreArchivo = '"+nombreArchivo+"';";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            //Obtener la consulta
            try (ResultSet resultSet = statement.executeQuery()) {
                // Resultados
                while (resultSet.next()) {
                    // Leer los valores de la fila necesarios
                    return resultSet.getString("rutaArchivo");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //return false;
        return "";
    }

    public static String getCimIdArchivo(String ruta) {
        String sql = "SELECT idArchivo FROM Archivo WHERE rutaArchivo = '"+ruta+"';";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            //Obtener la consulta
            try (ResultSet resultSet = statement.executeQuery()) {
                // Resultados
                while (resultSet.next()) {
                    // Leer los valores de la fila necesarios
                    return resultSet.getString("idArchivo");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //return false;
        return "";
    }
    //Tabla atributos
    public static boolean insertCimAtributos(String[] camposArchivo, String idArchivo, String idProyecto) {
        int count = 0;
        for(String atributo : camposArchivo){
            String sql = "INSERT INTO Atributo (idArchivo, idProyecto, nombre)" +
                    " VALUES ('"+idArchivo+"','"+idProyecto+"','"+atributo+"')";
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {

                // Ejecutar la sentencia SQL de inserción
                int filasInsertadas = statement.executeUpdate();
                count += filasInsertadas; //Se incrementa de uno en uno
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return count == camposArchivo.length;
    }


    public static LinkedList<String> getCimAtributos(String idArchivo) {
        LinkedList<String> atributos = new LinkedList<>();
        String sql = "SELECT nombre FROM Atributo WHERE idArchivo = '"+idArchivo+"';";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            //Obtener la consulta
            try (ResultSet resultSet = statement.executeQuery()) {
                // Resultados
                while (resultSet.next()) {
                    // Leer los valores de la fila necesarios
                    atributos.add(resultSet.getString("nombre"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //return false;
        return atributos;
    }

    public static boolean deleteCimAtributo(String nombreAtributo, String idArchivo) {
        String sql = "DELETE FROM Atributo WHERE nombre = '"+nombreAtributo+"' AND idArchivo = '"+idArchivo+"';";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Ejecutar la sentencia SQL de inserción
            int filasEliminadas = statement.executeUpdate();
            return filasEliminadas != 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //return false;
        return false;
    }

    public static boolean deleteCimArchivo_and_Atributos(String idArchivo) {
        Boolean checkAtributos = false;
        Boolean checkArchivo = false;
        String sql = "DELETE FROM Atributo WHERE idArchivo = '"+idArchivo+"';";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Ejecutar la sentencia SQL de inserción
            int filasEliminadas = statement.executeUpdate();
            if(filasEliminadas>0)
                checkAtributos = true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        String sql0 = "DELETE FROM Archivo WHERE idArchivo = '"+idArchivo+"';";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql0)) {

            // Ejecutar la sentencia SQL de inserción
            int filasEliminadas = statement.executeUpdate();
            if(filasEliminadas>0)
                checkArchivo = true;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        //return false;
        return (checkAtributos && checkArchivo);
    }

    public static boolean insertCimEsquemaGrafico(String ruta, String idProyecto) {
        String sql = "UPDATE Proyecto SET rutaEsquemaGrafico = '"+ruta+"' WHERE idProyecto = '"+idProyecto+"'";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Ejecutar la sentencia SQL de inserción
            int filasInsertadas = statement.executeUpdate();
            if(filasInsertadas>0)
                return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    //PIM
    public static boolean insertPimMDO(String ruta, String idProyecto) {
        String sql = "UPDATE Proyecto SET rutaModeloDominioObjetivo = '"+ruta+"' WHERE idProyecto = '"+idProyecto+"'";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Ejecutar la sentencia SQL de inserción
            int filasInsertadas = statement.executeUpdate();
            if(filasInsertadas>0)
                return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean updateProyectosFase(String pim, String idProyecto) {
        String sql = "UPDATE Proyecto SET fase = '"+pim+"' WHERE idProyecto = '"+idProyecto+"'";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Ejecutar la sentencia SQL de inserción
            int filasInsertadas = statement.executeUpdate();
            if(filasInsertadas>0)
                return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean insertPsmEsquemaOntologico(String ruta, String idProyecto) {
        String sql = "UPDATE Proyecto SET rutaEsquemaOntologico = '"+ruta+"' WHERE idProyecto = '"+idProyecto+"'";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Ejecutar la sentencia SQL de inserción
            int filasInsertadas = statement.executeUpdate();
            if(filasInsertadas>0)
                return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<Pair<String, String>> getPsmArchivos(String idProyecto) {
        List<Pair<String, String>> archivos = new LinkedList<>();
        String sql = "SELECT idArchivo, nombreArchivo FROM Archivo WHERE idProyecto = '"+idProyecto+"'";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            try (ResultSet resultSet = statement.executeQuery()) {
                // Resultados
                while (resultSet.next()) {
                    // Leer los valores de la fila necesarios

                    Pair<String, String> par = new Pair<>(resultSet.getString("idArchivo"),resultSet.getString("nombreArchivo"));
                    archivos.add(par);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return archivos;
    }

    public static LinkedList<String> getPSMAtributos(String idArchivo) {
        LinkedList<String> atributos = new LinkedList<>();
        String sql = "SELECT nombre, valor FROM Atributo WHERE idArchivo = '"+idArchivo+"'";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            try (ResultSet resultSet = statement.executeQuery()) {
                // Resultados
                while (resultSet.next()) {
                    // Leer los valores de la fila necesarios

                    String nombre = resultSet.getString("nombre");
                    String valor = resultSet.getString("valor");
                    if(valor != null){
                        atributos.add(nombre+ " ; " + valor);
                    }else{
                        atributos.add(nombre);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return atributos;
    }

    public static boolean updateAtributo(String atributo, String tipo, String idArchivo) {
        String sql = "UPDATE Atributo SET valor = '"+tipo+"' WHERE nombre = '"+atributo+"' AND idArchivo = '"+idArchivo+"';";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Ejecutar la sentencia SQL de inserción
            int filasInsertadas = statement.executeUpdate();
            if(filasInsertadas>0)
                return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean updateMetadatos(String publisher, String descripcion, String description, String idProyecto) {
        String sql = "UPDATE Proyecto SET publisher = '"+publisher+"', descripcionES = '"+descripcion+"', descripcionEN = '"+description+"'" +
                " WHERE idProyecto = '"+idProyecto+"'";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Ejecutar la sentencia SQL de inserción
            int filasInsertadas = statement.executeUpdate();
            if(filasInsertadas>0)
                return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<String> getMetadatos(String idProyecto) {
        LinkedList<String> metadatos = new LinkedList<>();
        String sql = "SELECT publisher, descripcionES, descripcionEN FROM Proyecto WHERE idProyecto = '"+idProyecto+"'";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            try (ResultSet resultSet = statement.executeQuery()) {
                // Resultados
                while (resultSet.next()) {
                    // Leer los valores de la fila necesarios

                    String publisher = resultSet.getString("publisher");
                    String descripcionES = resultSet.getString("descripcionES");
                    String descripcionEN = resultSet.getString("descripcionEN");
                    if(publisher != null && descripcionES != null && descripcionEN != null){
                        metadatos.add(publisher);
                        metadatos.add(descripcionES);
                        metadatos.add(descripcionEN);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return metadatos;
    }

    public static String getIdArchivo_nombre(String archivo) {
        String idArchivo = "";
        String sql = "SELECT idArchivo FROM Archivo WHERE nombreArchivo = '"+archivo+"'";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            try (ResultSet resultSet = statement.executeQuery()) {
                // Resultados
                while (resultSet.next()) {
                    // Leer los valores de la fila necesarios
                    idArchivo = resultSet.getString("idArchivo");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return idArchivo;
    }

    public static List<String> getRoles(String nombreProyecto) {
        List<String> roles = new LinkedList<>();
        String sql = "SELECT ingenieroDatos, ingenieroOntologico FROM Proyecto WHERE nombreProyecto = '"+nombreProyecto+"'";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            try (ResultSet resultSet = statement.executeQuery()) {
                // Resultados
                while (resultSet.next()) {
                    // Leer los valores de la fila necesarios
                   String ingDatos = resultSet.getString("ingenieroDatos");
                   String ingOnt = resultSet.getString("ingenieroOntologico");
                   roles.add(ingDatos);
                   roles.add(ingOnt);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roles;
    }

    public static LinkedList<Pair<String,String>> getOntAtributos(String idArchivo) {
        LinkedList<Pair<String,String>> atributos = new LinkedList<>();
        String sql = "SELECT nombre, valor FROM Atributo WHERE idArchivo = '"+idArchivo+"';";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            //Obtener la consulta
            try (ResultSet resultSet = statement.executeQuery()) {
                // Resultados
                while (resultSet.next()) {
                    // Leer los valores de la fila necesarios
                    atributos.add(new Pair(resultSet.getString("nombre"), resultSet.getString("valor")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //return false;
        return atributos;
    }

    public static String getIdAtributo(String idArchivo, String nombreAtributo) {
        String sql = "SELECT idAtributo FROM Atributo WHERE idArchivo = '"+idArchivo+"' AND nombre = '"+nombreAtributo+ "';";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            //Obtener la consulta
            try (ResultSet resultSet = statement.executeQuery()) {
                // Resultados
                while (resultSet.next()) {
                    // Leer los valores de la fila necesarios
                    int id = resultSet.getInt("idAtributo");
                    return Integer.toString(id);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //return false;
        return "";
    }

    public static boolean updateAtributoPrimario(String idAtributo, String idArchivo) {
        String sql = "UPDATE Archivo SET idAtributoPrimario = '"+idAtributo+"' WHERE idArchivo = '"+idArchivo+"'";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Ejecutar la sentencia SQL de inserción
            int filasInsertadas = statement.executeUpdate();
            if(filasInsertadas>0)
                return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getAtributoPrimario(String clase) {
        String sql = "SELECT idAtributoPrimario FROM Archivo WHERE nombreArchivo = '"+clase+"';";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            //Obtener la consulta
            try (ResultSet resultSet = statement.executeQuery()) {
                // Resultados
                while (resultSet.next()) {
                    // Leer los valores de la fila necesarios
                    String idAtt = resultSet.getString("idAtributoPrimario");
                    return auxFunction(idAtt);  //obtener el nombre a partir del ID
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //return false;
        return "";
    }

    private static String auxFunction(String idAtt) {
        String sql = "SELECT nombre FROM Atributo WHERE idAtributo = '"+idAtt+"';";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            //Obtener la consulta
            try (ResultSet resultSet = statement.executeQuery()) {
                // Resultados
                while (resultSet.next()) {
                    // Leer los valores de la fila necesarios
                    return resultSet.getString("nombre");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //return false;
        return "";
    }

    public static void getAtributosTable() {
        String sql = "SELECT * FROM Atributo ;";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            //Obtener la consulta
            try (ResultSet resultSet = statement.executeQuery()) {
                // Resultados
                while (resultSet.next()) {
                    // Leer los valores de la fila necesarios
                    resultSet.getString("nombre");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //return false;
    }
}
