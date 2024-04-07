package com.fnvigg.csv2rdf;
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
        String sql = "INSERT INTO Proyecto (nombreProyecto, ingenieroDatos, ingenieroOntologico)" +
                     " VALUES ('"+nombreProyecto+"','"+ingDatos+"','"+ingOntologico+"')";
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
}
