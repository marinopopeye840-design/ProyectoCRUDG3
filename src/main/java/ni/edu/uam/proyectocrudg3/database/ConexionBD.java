package ni.edu.uam.proyectocrudg3.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static final String URL = "jdbc:postgresql://localhost:5432/bdventas_1";
    private static final String USER = "postgres";
    private static final String PASSWORD = "TU_CONTRASEÑA";

    public static Connection getConnection() {
        Connection con = null;

        try {
            con = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("¡Conexión exitosa a PostgreSQL!");
        } catch (SQLException e) {
            System.err.println("Error al conectar: " + e.getMessage());
        }

        return con;
    }
}