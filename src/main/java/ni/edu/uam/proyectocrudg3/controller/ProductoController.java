package ni.edu.uam.proyectocrudg3.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ni.edu.uam.proyectocrudg3.database.ConexionBD;
import ni.edu.uam.proyectocrudg3.model.Producto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class ProductoController {

    @FXML
    private TextField txtCodigo;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtPrecio;
    @FXML
    private TextField txtStock;
    @FXML
    private ComboBox<String> cmbCategoria;
    @FXML
    private ComboBox<String> cmbMarca;
    @FXML
    private DatePicker dtpFecha;
    @FXML
    private TextField txtDescripcion;

    @FXML
    private TextField txtCodigoNombre;
    @FXML
    private ComboBox<String> cmbCategoriaFiltro;
    @FXML
    private DatePicker dtpDesde;
    @FXML
    private DatePicker dtpHasta;
    @FXML
    private TextField txtPrecioMinimo;
    @FXML
    private TextField txtPrecioMaximo;
    @FXML
    private CheckBox chkProductosExistencia;
    @FXML
    private ComboBox<String> cmbOrdenar;

    @FXML
    private TableView<Producto> tblProductos;
    @FXML
    private TableColumn<Producto, String> colCodigo;
    @FXML
    private TableColumn<Producto, String> colNombre;
    @FXML
    private TableColumn<Producto, String> colCategoria;
    @FXML
    private TableColumn<Producto, String> colMarca;
    @FXML
    private TableColumn<Producto, Double> colPrecio;
    @FXML
    private TableColumn<Producto, Integer> colStock;
    @FXML
    private TableColumn<Producto, java.util.Date> colFechaCreacion;

    private final ObservableList<Producto> listaProductos = FXCollections.observableArrayList();

    private final Map<Integer, String> nombresCategorias = new HashMap<>();
    private final Map<Integer, String> nombresMarcas = new HashMap<>();

    @FXML
    private void initialize() {
        configurarTabla();
        cargarCategorias();
        cargarMarcas();
        configurarOrdenamiento();
        cargarProductos();

        dtpFecha.setValue(LocalDate.now());

        tblProductos.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                cargarProductoSeleccionado(newValue);
            }
        });
    }

    private void cargarProductoSeleccionado(Producto producto) {
        txtCodigo.setText(producto.getCodigo());
        txtNombre.setText(producto.getNombre());
        txtDescripcion.setText(producto.getDescripcion());
        txtPrecio.setText(String.valueOf(producto.getPrecio()));
        txtStock.setText(String.valueOf(producto.getStock()));

        cmbCategoria.setValue(nombresCategorias.get(producto.getId_categoria()));
        cmbMarca.setValue(nombresMarcas.get(producto.getId_marca()));

        if (producto.getFecha_creacion() != null) {
            Date fecha = new Date(producto.getFecha_creacion().getTime());
            dtpFecha.setValue(fecha.toLocalDate());
        }
    }

    private void configurarTabla() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colFechaCreacion.setCellValueFactory(new PropertyValueFactory<>("fecha_creacion"));

        colCategoria.setCellValueFactory(datos -> {
            Producto producto = datos.getValue();
            String nombreCategoria = nombresCategorias.get(producto.getId_categoria());
            return new SimpleStringProperty(nombreCategoria);
        });

        colMarca.setCellValueFactory(datos -> {
            Producto producto = datos.getValue();
            String nombreMarca = nombresMarcas.get(producto.getId_marca());
            return new SimpleStringProperty(nombreMarca);
        });
    }

    private void cargarCategorias() {
        cmbCategoria.getItems().clear();
        cmbCategoriaFiltro.getItems().clear();

        String sql = "SELECT nombre FROM catalogos.categorias ORDER BY nombre";

        try (
                Connection connection = ConexionBD.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery();
        ) {
            cmbCategoriaFiltro.getItems().add("Todas");

            while (resultSet.next()) {
                String nombre = resultSet.getString("nombre");

                cmbCategoria.getItems().add(nombre);
                cmbCategoriaFiltro.getItems().add(nombre);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void cargarMarcas() {
        cmbMarca.getItems().clear();

        String sql = "SELECT nombre FROM catalogos.marcas ORDER BY nombre";

        try (
                Connection connection = ConexionBD.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery();
        ) {
            while (resultSet.next()) {
                cmbMarca.getItems().add(resultSet.getString("nombre"));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void configurarOrdenamiento() {
        cmbOrdenar.getItems().clear();

        cmbOrdenar.getItems().addAll(
                "Nombre A-Z",
                "Nombre Z-A",
                "Precio menor a mayor",
                "Precio mayor a menor",
                "Más recientes",
                "Más antiguos"
        );
    }

    private void cargarProductos() {
        listaProductos.clear();
        nombresCategorias.clear();
        nombresMarcas.clear();

        String sql = "SELECT P.*, C.nombre AS categoria_nombre, M.nombre AS marca_nombre " +
                "FROM catalogos.productos P " +
                "INNER JOIN catalogos.categorias C ON P.id_categoria = C.id_categoria " +
                "INNER JOIN catalogos.marcas M ON P.id_marca = M.id_marca " +
                "ORDER BY P.id_producto";

        try (
                Connection connection = ConexionBD.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery();
        ) {
            while (resultSet.next()) {
                Producto producto = new Producto();

                producto.setId_producto(resultSet.getInt("id_producto"));
                producto.setCodigo(resultSet.getString("codigo"));
                producto.setNombre(resultSet.getString("nombre"));
                producto.setDescripcion(resultSet.getString("descripcion"));
                producto.setPrecio(resultSet.getDouble("precio"));
                producto.setStock(resultSet.getInt("stock"));
                producto.setFecha_creacion(resultSet.getDate("fecha_creacion"));
                producto.setId_categoria(resultSet.getInt("id_categoria"));
                producto.setId_marca(resultSet.getInt("id_marca"));
                producto.setEstado(resultSet.getInt("estado"));

                nombresCategorias.put(
                        producto.getId_categoria(),
                        resultSet.getString("categoria_nombre")
                );

                nombresMarcas.put(
                        producto.getId_marca(),
                        resultSet.getString("marca_nombre")
                );

                listaProductos.add(producto);
            }

            tblProductos.setItems(listaProductos);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private int obtenerIdCategoria(String nombreCategoria) {
        int idCategoria = -1;

        String sql = "SELECT id_categoria FROM catalogos.categorias WHERE nombre = ?";

        try (
                Connection connection = ConexionBD.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            statement.setString(1, nombreCategoria);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                idCategoria = resultSet.getInt("id_categoria");
            }

            resultSet.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return idCategoria;
    }

    private int obtenerIdMarca(String nombreMarca) {
        int idMarca = -1;

        String sql = "SELECT id_marca FROM catalogos.marcas WHERE nombre = ?";

        try (
                Connection connection = ConexionBD.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            statement.setString(1, nombreMarca);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                idMarca = resultSet.getInt("id_marca");
            }

            resultSet.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return idMarca;
    }

    @FXML
    private void guardarRegistro() {
        if (!validarCampos()) {
            return;
        }

        int idCategoria = obtenerIdCategoria(cmbCategoria.getValue());
        int idMarca = obtenerIdMarca(cmbMarca.getValue());

        String sql = "INSERT INTO catalogos.productos " +
                "(codigo, nombre, descripcion, precio, stock, fecha_creacion, id_categoria, id_marca) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (
                Connection connection = ConexionBD.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            statement.setString(1, txtCodigo.getText());
            statement.setString(2, txtNombre.getText());
            statement.setString(3, txtDescripcion.getText());
            statement.setDouble(4, Double.parseDouble(txtPrecio.getText()));
            statement.setInt(5, Integer.parseInt(txtStock.getText()));
            statement.setDate(6, Date.valueOf(dtpFecha.getValue()));
            statement.setInt(7, idCategoria);
            statement.setInt(8, idMarca);

            statement.executeUpdate();

            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Registro almacenado",
                    "Producto registrado",
                    "El producto se ha almacenado exitosamente"
            );

            crearRegistro();
            cargarProductos();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void actualizarRegistro() {
        Producto productoSeleccionado = tblProductos.getSelectionModel().getSelectedItem();

        if (productoSeleccionado == null) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Selección requerida",
                    "No hay producto seleccionado",
                    "Seleccione un producto de la lista"
            );
            return;
        }

        if (!validarCampos()) {
            return;
        }

        int idCategoria = obtenerIdCategoria(cmbCategoria.getValue());
        int idMarca = obtenerIdMarca(cmbMarca.getValue());

        String sql = "UPDATE catalogos.productos SET " +
                "codigo = ?, nombre = ?, descripcion = ?, precio = ?, stock = ?, " +
                "fecha_creacion = ?, id_categoria = ?, id_marca = ? " +
                "WHERE id_producto = ?";

        try (
                Connection connection = ConexionBD.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            statement.setString(1, txtCodigo.getText());
            statement.setString(2, txtNombre.getText());
            statement.setString(3, txtDescripcion.getText());
            statement.setDouble(4, Double.parseDouble(txtPrecio.getText()));
            statement.setInt(5, Integer.parseInt(txtStock.getText()));
            statement.setDate(6, Date.valueOf(dtpFecha.getValue()));
            statement.setInt(7, idCategoria);
            statement.setInt(8, idMarca);
            statement.setInt(9, productoSeleccionado.getId_producto());

            int filasActualizadas = statement.executeUpdate();

            if (filasActualizadas > 0) {
                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Registro actualizado",
                        "Actualización completada",
                        "El producto fue actualizado correctamente"
                );

                crearRegistro();
                cargarProductos();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void eliminarRegistro() {
        Producto productoSeleccionado = tblProductos.getSelectionModel().getSelectedItem();

        if (productoSeleccionado == null) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Selección requerida",
                    "No hay producto seleccionado",
                    "Seleccione un producto de la lista"
            );
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Está seguro que desea eliminar el registro del producto?");

        if (confirmacion.showAndWait().isEmpty() || confirmacion.getResult() != ButtonType.OK) {
            return;
        }

        String sql = "DELETE FROM catalogos.productos WHERE id_producto = ?";

        try (
                Connection connection = ConexionBD.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            statement.setInt(1, productoSeleccionado.getId_producto());

            int filasEliminadas = statement.executeUpdate();

            if (filasEliminadas > 0) {
                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Registro eliminado",
                        "Eliminación completada",
                        "El producto fue eliminado exitosamente"
                );

                crearRegistro();
                cargarProductos();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void crearRegistro() {
        txtCodigo.clear();
        txtNombre.clear();
        txtPrecio.clear();
        txtStock.clear();
        txtDescripcion.clear();

        cmbCategoria.getSelectionModel().clearSelection();
        cmbMarca.getSelectionModel().clearSelection();

        dtpFecha.setValue(LocalDate.now());

        tblProductos.getSelectionModel().clearSelection();

        txtCodigo.requestFocus();
    }

    @FXML
    private void buscarProducto() {
        listaProductos.clear();
        nombresCategorias.clear();
        nombresMarcas.clear();

        String sql = "SELECT P.*, C.nombre AS categoria_nombre, M.nombre AS marca_nombre " +
                "FROM catalogos.productos P " +
                "INNER JOIN catalogos.categorias C ON P.id_categoria = C.id_categoria " +
                "INNER JOIN catalogos.marcas M ON P.id_marca = M.id_marca " +
                "WHERE 1 = 1 ";

        if (!txtCodigoNombre.getText().trim().isEmpty()) {
            sql += "AND (P.codigo ILIKE ? OR P.nombre ILIKE ?) ";
        }

        if (cmbCategoriaFiltro.getValue() != null
                && !cmbCategoriaFiltro.getValue().equals("Todas")) {

            sql += "AND C.nombre = ? ";
        }

        if (dtpDesde.getValue() != null) {
            sql += "AND P.fecha_creacion >= ? ";
        }

        if (dtpHasta.getValue() != null) {
            sql += "AND P.fecha_creacion <= ? ";
        }

        if (!txtPrecioMinimo.getText().trim().isEmpty()) {
            sql += "AND P.precio >= ? ";
        }

        if (!txtPrecioMaximo.getText().trim().isEmpty()) {
            sql += "AND P.precio <= ? ";
        }

        if (chkProductosExistencia.isSelected()) {
            sql += "AND P.stock > 0 ";
        }

        if (cmbOrdenar.getValue() != null) {

            switch (cmbOrdenar.getValue()) {
                case "Nombre A-Z":
                    sql += "ORDER BY P.nombre ASC";
                    break;

                case "Nombre Z-A":
                    sql += "ORDER BY P.nombre DESC";
                    break;

                case "Precio menor a mayor":
                    sql += "ORDER BY P.precio ASC";
                    break;

                case "Precio mayor a menor":
                    sql += "ORDER BY P.precio DESC";
                    break;

                case "Más recientes":
                    sql += "ORDER BY P.fecha_creacion DESC";
                    break;

                case "Más antiguos":
                    sql += "ORDER BY P.fecha_creacion ASC";
                    break;

                default:
                    sql += "ORDER BY P.id_producto";
                    break;
            }

        } else {
            sql += "ORDER BY P.id_producto";
        }

        try (
                Connection connection = ConexionBD.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            int parametro = 1;

            if (!txtCodigoNombre.getText().trim().isEmpty()) {
                String busqueda = "%" + txtCodigoNombre.getText().trim() + "%";

                statement.setString(parametro++, busqueda);
                statement.setString(parametro++, busqueda);
            }

            if (cmbCategoriaFiltro.getValue() != null
                    && !cmbCategoriaFiltro.getValue().equals("Todas")) {

                statement.setString(parametro++, cmbCategoriaFiltro.getValue());
            }

            if (dtpDesde.getValue() != null) {
                statement.setDate(parametro++, Date.valueOf(dtpDesde.getValue()));
            }

            if (dtpHasta.getValue() != null) {
                statement.setDate(parametro++, Date.valueOf(dtpHasta.getValue()));
            }

            if (!txtPrecioMinimo.getText().trim().isEmpty()) {
                statement.setDouble(
                        parametro++,
                        Double.parseDouble(txtPrecioMinimo.getText())
                );
            }

            if (!txtPrecioMaximo.getText().trim().isEmpty()) {
                statement.setDouble(
                        parametro++,
                        Double.parseDouble(txtPrecioMaximo.getText())
                );
            }

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Producto producto = new Producto();
                producto.setId_producto(resultSet.getInt("id_producto"));
                producto.setCodigo(resultSet.getString("codigo"));
                producto.setNombre(resultSet.getString("nombre"));
                producto.setDescripcion(resultSet.getString("descripcion"));
                producto.setPrecio(resultSet.getDouble("precio"));
                producto.setStock(resultSet.getInt("stock"));
                producto.setFecha_creacion(resultSet.getDate("fecha_creacion"));
                producto.setId_categoria(resultSet.getInt("id_categoria"));
                producto.setId_marca(resultSet.getInt("id_marca"));
                producto.setEstado(resultSet.getInt("estado"));

                nombresCategorias.put(producto.getId_categoria(), resultSet.getString("categoria_nombre")
                );

                nombresMarcas.put(producto.getId_marca(), resultSet.getString("marca_nombre")
                );
                listaProductos.add(producto);
            }
            resultSet.close();
            tblProductos.setItems(listaProductos);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (NumberFormatException ex) {
            mostrarAlerta(Alert.AlertType.WARNING, "Datos incorrectos", "Precio incorrecto", "Los precios de búsqueda deben ser números"
            );
        }
    }
    @FXML
    private void limpiarFiltro() {
        txtCodigoNombre.clear();
        cmbCategoriaFiltro.getSelectionModel().clearSelection();
        dtpDesde.setValue(null);
        dtpHasta.setValue(null);
        txtPrecioMinimo.clear();
        txtPrecioMaximo.clear();
        chkProductosExistencia.setSelected(false);
        cmbOrdenar.getSelectionModel().clearSelection();
        cargarProductos();
    }
    private boolean validarCampos() {
        if (txtCodigo.getText().trim().isEmpty() || txtNombre.getText().trim().isEmpty()
                || txtPrecio.getText().trim().isEmpty() || txtStock.getText().trim().isEmpty()
                || cmbCategoria.getValue() == null || cmbMarca.getValue() == null || dtpFecha.getValue() == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos requeridos", "Datos incompletos", "Complete todos los campos obligatorios"
            );
            return false;
        }
        try {
            double precio = Double.parseDouble(txtPrecio.getText());
            if (precio < 0) {
                mostrarAlerta(Alert.AlertType.WARNING, "Precio incorrecto", "Precio no válido", "El precio no puede ser negativo"
                );

                return false;
            }
        } catch (NumberFormatException ex) {
            mostrarAlerta(Alert.AlertType.WARNING, "Precio incorrecto", "Precio no válido", "Ingrese un número válido en el precio"
            );
            return false;
        }
        try {
            int stock = Integer.parseInt(txtStock.getText());
            if (stock < 0) {
                mostrarAlerta(Alert.AlertType.WARNING, "Stock incorrecto", "Stock no válido", "El stock no puede ser negativo"
                );
                return false;
            }

        } catch (NumberFormatException ex) {
            mostrarAlerta(Alert.AlertType.WARNING, "Stock incorrecto", "Stock no válido", "Ingrese un número entero en el stock"
            );
            return false;
        }
        return true;
    }
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String encabezado, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(encabezado);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}