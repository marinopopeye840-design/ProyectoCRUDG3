module ni.edu.uam.proyectocrudg3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens ni.edu.uam.proyectocrudg3 to javafx.fxml;
    opens ni.edu.uam.proyectocrudg3.controller to javafx.fxml;
    opens ni.edu.uam.proyectocrudg3.model to javafx.base;

    exports ni.edu.uam.proyectocrudg3;
    exports ni.edu.uam.proyectocrudg3.controller;
    exports ni.edu.uam.proyectocrudg3.model;
}