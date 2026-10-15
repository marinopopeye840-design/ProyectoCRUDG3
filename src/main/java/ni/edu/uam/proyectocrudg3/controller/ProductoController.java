package ni.edu.uam.proyectocrudg3.controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ni.edu.uam.proyectocrudg3.database.ConexionBD;
import ni.edu.uam.proyectocrudg3.model.Categoria;
import ni.edu.uam.proyectocrudg3.model.Marca;
import ni.edu.uam.proyectocrudg3.model.Producto;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    private ComboBox<Categoria> cmbCategoria;
    @FXML
    private ComboBox<Marca> cmbMarca;
    @FXML
    private DatePicker dtpFecha;
    @FXML
    private TextField txtDescripcion;

    @FXML
    private TextField txtCodigoNombre;
    @FXML
    private ComboBox<Categoria> cmbCategoriaFiltro;
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
    private TableColumn<Producto, LocalDate> colFechaCreacion;

    private final ObservableList<Producto> listaProductos = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        configurarTabla();
        configurarSeleccionTableView();
        cargarCategorias();
        cargarMarcas();
        configurarOrdenamiento();
        cargarProductos();

        dtpFecha.setValue(LocalDate.now());
    }

    private void configurarSeleccionTableView() {
        // Se detecta cuando se selecciona un producto de la tabla
        tblProductos.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            // Si se seleccionó un producto, sus datos pasan al formulario
            if (newValue != null) {
                cargarProductoSeleccionado(newValue);
            }
        });
    }

    private void cargarProductoSeleccionado(Producto producto) {
        // Se cargan los datos básicos del producto seleccionado
        txtCodigo.setText(producto.getCodigo());
        txtNombre.setText(producto.getNombre());
        txtDescripcion.setText(producto.getDescripcion());
        txtPrecio.setText(String.valueOf(producto.getPrecio()));
        txtStock.setText(String.valueOf(producto.getStock()));

        // Se busca la categoría que corresponde al producto
        for (Categoria categoria : cmbCategoria.getItems()) {
            if (categoria.getIdCategoria() == producto.getIdCategoria()) {
                cmbCategoria.setValue(categoria);
                break;
            }
        }

        // Se busca la marca que corresponde al producto
        for (Marca marca : cmbMarca.getItems()) {
            if (marca.getIdMarca() == producto.getIdMarca()) {
                cmbMarca.setValue(marca);
                break;
            }
        }

        // Se carga la fecha si el producto tiene una registrada
        if (producto.getFechaCreacion() != null) {
            dtpFecha.setValue(producto.getFechaCreacion());
        }
    }

    private void configurarTabla() {
        // Cada columna obtiene el dato que le corresponde del producto
        colCodigo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCodigo()));
        colNombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombre()));
        colCategoria.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCategoria()));
        colMarca.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMarca()));
        colPrecio.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPrecio()).asObject());
        colStock.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getStock()).asObject());
        colFechaCreacion.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getFechaCreacion()));

        // La lista de productos se asigna al TableView
        tblProductos.setItems(listaProductos);
    }

    private void cargarCategorias() {
        cmbCategoria.getItems().clear();
        cmbCategoriaFiltro.getItems().clear();

        // Se consultan solamente las categorías que están activas
        String sql = "SELECT id_categoria, nombre " +
                "FROM catalogos.categorias " +
                "WHERE estado = 1 " +
                "ORDER BY nombre";

        try (
                Connection connection = ConexionBD.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery();
        ) {
            while (resultSet.next()) {

                // Se crea una categoría con los datos obtenidos
                Categoria categoria = new Categoria(
                        resultSet.getInt("id_categoria"),
                        resultSet.getString("nombre")
                );

                // Se agrega a los dos ComboBox
                cmbCategoria.getItems().add(categoria);
                cmbCategoriaFiltro.getItems().add(categoria);
            }

        } catch (SQLException ex) {
            mostrarErrorBaseDatos(ex);
        }
    }

    private void cargarMarcas() {
        cmbMarca.getItems().clear();

        // Se consultan solamente las marcas activas
        String sql = "SELECT id_marca, nombre " +
                "FROM catalogos.marcas " +
                "WHERE estado = 1 " +
                "ORDER BY nombre";

        try (
                Connection connection = ConexionBD.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery();
        ) {
            while (resultSet.next()) {

                // Se crea la marca con su id y nombre
                Marca marca = new Marca(
                        resultSet.getInt("id_marca"),
                        resultSet.getString("nombre")
                );

                cmbMarca.getItems().add(marca);
            }

        } catch (SQLException ex) {
            mostrarErrorBaseDatos(ex);
        }
    }

    private void configurarOrdenamiento() {
        cmbOrdenar.getItems().clear();

        // Opciones disponibles para ordenar los resultados
        cmbOrdenar.getItems().addAll(
                "Nombre",
                "Precio menor a mayor",
                "Precio mayor a menor",
                "Fecha más reciente",
                "Fecha más antigua",
                "Stock mayor a menor",
                "Stock menor a mayor"
        );

        cmbOrdenar.setValue("Nombre");
    }

    private void cargarProductos() {
        listaProductos.clear();

        // Se cargan solamente los productos activos
        String sql = "SELECT P.*, C.nombre AS categoria, M.nombre AS marca " +
                "FROM catalogos.productos P " +
                "INNER JOIN catalogos.categorias C ON P.id_categoria = C.id_categoria " +
                "INNER JOIN catalogos.marcas M ON P.id_marca = M.id_marca " +
                "WHERE P.estado = 1 " +
                "ORDER BY P.nombre";

        try (
                Connection connection = ConexionBD.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery();
        ) {
            while (resultSet.next()) {

                // Cada fila encontrada se convierte en un objeto Producto
                listaProductos.add(crearProductoDesdeResultSet(resultSet));
            }

            tblProductos.setItems(listaProductos);

        } catch (SQLException ex) {
            mostrarErrorBaseDatos(ex);
        }
    }

    private Producto crearProductoDesdeResultSet(ResultSet resultSet) throws SQLException {
        LocalDate fechaCreacion = null;

        // Se convierte la fecha de SQL a LocalDate
        if (resultSet.getDate("fecha_creacion") != null) {
            fechaCreacion = resultSet.getDate("fecha_creacion").toLocalDate();
        }

        // Se crea el producto con todos los datos obtenidos
        return new Producto(
                resultSet.getInt("id_producto"),
                resultSet.getString("codigo"),
                resultSet.getString("nombre"),
                resultSet.getString("descripcion"),
                resultSet.getDouble("precio"),
                resultSet.getInt("stock"),
                fechaCreacion,
                resultSet.getInt("id_categoria"),
                resultSet.getString("categoria"),
                resultSet.getInt("id_marca"),
                resultSet.getString("marca"),
                resultSet.getInt("estado")
        );
    }

    @FXML
    private void guardarRegistro() {

        // Primero se revisa que los datos ingresados sean correctos
        if (!validarCampos()) {
            return;
        }

        // Se obtiene el código quitando espacios al inicio y al final
        String codigo = txtCodigo.getText().trim();

        // Se revisa si el código ya pertenece a otro producto
        if (existeCodigo(codigo, null)) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Código duplicado",
                    "El código ya existe",
                    "Ya existe un producto registrado con ese código"
            );

            return;
        }

        // Se obtienen la categoría y la marca seleccionadas
        Categoria categoria = cmbCategoria.getValue();
        Marca marca = cmbMarca.getValue();

        // Se prepara el INSERT utilizando parámetros
        String sql = "INSERT INTO catalogos.productos " +
                "(codigo, nombre, descripcion, precio, stock, fecha_creacion, id_categoria, id_marca, estado) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 1)";

        try (
                // Se abre la conexión a la base de datos
                Connection connection = ConexionBD.getConnection();

                // Se prepara la sentencia SQL
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {

            // Se envía cada dato a su parámetro correspondiente
            statement.setString(1, codigo);
            statement.setString(2, txtNombre.getText().trim());
            statement.setString(3, txtDescripcion.getText().trim());

            // BigDecimal se utiliza para manejar correctamente el precio
            statement.setBigDecimal(4, new BigDecimal(txtPrecio.getText().trim()));

            statement.setInt(5, Integer.parseInt(txtStock.getText().trim()));

            // Se convierte la fecha seleccionada al tipo Date de SQL
            statement.setDate(6, Date.valueOf(dtpFecha.getValue()));

            // Se guardan los id de la categoría y marca seleccionadas
            statement.setInt(7, categoria.getIdCategoria());
            statement.setInt(8, marca.getIdMarca());

            // Se ejecuta el INSERT
            statement.executeUpdate();

            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Registro almacenado",
                    "Producto registrado",
                    "El producto se ha almacenado exitosamente"
            );

            // Se limpia el formulario y se actualiza la tabla
            crearRegistro();
            cargarProductos();

        } catch (SQLException ex) {

            // PostgreSQL utiliza 23505 cuando se intenta repetir un valor UNIQUE
            if ("23505".equals(ex.getSQLState())) {
                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Código duplicado",
                        "El código ya existe",
                        "Ya existe un producto registrado con ese código"
                );

            } else {
                mostrarErrorBaseDatos(ex);
            }
        }
    }

    @FXML
    private void actualizarRegistro() {

        // Se obtiene el producto que está seleccionado en la tabla
        Producto productoSeleccionado = tblProductos.getSelectionModel().getSelectedItem();

        // Si no se seleccionó ningún producto no se puede actualizar
        if (productoSeleccionado == null) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Selección requerida",
                    "No hay producto seleccionado",
                    "Seleccione un producto de la lista"
            );

            return;
        }

        // Se comprueba que los datos del formulario sean correctos
        if (!validarCampos()) {
            return;
        }

        String codigo = txtCodigo.getText().trim();

        // Se revisa que el código no pertenezca a otro producto
        // Se excluye el id del producto que actualmente se está editando
        if (existeCodigo(codigo, productoSeleccionado.getIdProducto())) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Código duplicado",
                    "El código ya existe",
                    "Ya existe otro producto registrado con ese código"
            );

            return;
        }

        Categoria categoria = cmbCategoria.getValue();
        Marca marca = cmbMarca.getValue();

        // Se prepara la actualización del producto
        String sql = "UPDATE catalogos.productos SET " +
                "codigo = ?, nombre = ?, descripcion = ?, precio = ?, stock = ?, " +
                "fecha_creacion = ?, id_categoria = ?, id_marca = ? " +
                "WHERE id_producto = ?";

        try (
                Connection connection = ConexionBD.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {

            // Se colocan los nuevos datos del producto
            statement.setString(1, codigo);
            statement.setString(2, txtNombre.getText().trim());
            statement.setString(3, txtDescripcion.getText().trim());
            statement.setBigDecimal(4, new BigDecimal(txtPrecio.getText().trim()));
            statement.setInt(5, Integer.parseInt(txtStock.getText().trim()));
            statement.setDate(6, Date.valueOf(dtpFecha.getValue()));
            statement.setInt(7, categoria.getIdCategoria());
            statement.setInt(8, marca.getIdMarca());

            // El id indica cuál producto se debe actualizar
            statement.setInt(9, productoSeleccionado.getIdProducto());

            // Se ejecuta el UPDATE
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

            // También se controla un posible código duplicado desde PostgreSQL
            if ("23505".equals(ex.getSQLState())) {
                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Código duplicado",
                        "El código ya existe",
                        "Ya existe otro producto registrado con ese código"
                );

            } else {
                mostrarErrorBaseDatos(ex);
            }
        }
    }

    @FXML
    private void eliminarRegistro() {

        // Se obtiene el producto seleccionado
        Producto productoSeleccionado = tblProductos.getSelectionModel().getSelectedItem();

        // Se comprueba que haya un producto seleccionado
        if (productoSeleccionado == null) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Selección requerida",
                    "No hay producto seleccionado",
                    "Seleccione un producto de la lista"
            );

            return;
        }

        // Se pide una confirmación antes de eliminar
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Está seguro que desea eliminar el registro del producto?");

        if (confirmacion.showAndWait().isEmpty() || confirmacion.getResult() != ButtonType.OK) {
            return;
        }

        // No se borra físicamente el registro.
        // Solamente se cambia su estado a 0.
        String sql = "UPDATE catalogos.productos SET estado = 0 WHERE id_producto = ?";

        try (
                Connection connection = ConexionBD.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {

            // Se envía el id del producto que se quiere desactivar
            statement.setInt(1, productoSeleccionado.getIdProducto());

            // Se ejecuta la eliminación lógica
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
            mostrarErrorBaseDatos(ex);
        }
    }

    @FXML
    private void crearRegistro() {
        // Se limpian los datos del formulario para ingresar un producto nuevo
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

        // Se eliminan los resultados anteriores
        listaProductos.clear();

        // Se comienza con una consulta base.
        // Después se van agregando las condiciones según los filtros utilizados.
        StringBuilder sql = new StringBuilder(
                "SELECT P.*, C.nombre AS categoria, M.nombre AS marca " +
                        "FROM catalogos.productos P " +
                        "INNER JOIN catalogos.categorias C ON P.id_categoria = C.id_categoria " +
                        "INNER JOIN catalogos.marcas M ON P.id_marca = M.id_marca " +
                        "WHERE P.estado = 1 "
        );

        // Aquí se guardan los valores que posteriormente se enviarán al PreparedStatement
        List<Object> parametros = new ArrayList<>();

        // Filtro para buscar por código o nombre
        if (!txtCodigoNombre.getText().trim().isEmpty()) {

            // Los símbolos % permiten buscar una parte del texto
            String busqueda = "%" + txtCodigoNombre.getText().trim() + "%";

            // ILIKE permite realizar la búsqueda sin importar mayúsculas o minúsculas
            sql.append("AND (P.codigo ILIKE ? OR P.nombre ILIKE ?) ");

            // El mismo texto se usa para código y nombre
            parametros.add(busqueda);
            parametros.add(busqueda);
        }

        // Filtro por categoría
        if (cmbCategoriaFiltro.getValue() != null) {

            // Se agrega la condición a la consulta
            sql.append("AND P.id_categoria = ? ");

            // Se guarda el id como parámetro
            parametros.add(cmbCategoriaFiltro.getValue().getIdCategoria());
        }

        // Se obtienen las fechas ingresadas por el usuario
        LocalDate fechaDesde = dtpDesde.getValue();
        LocalDate fechaHasta = dtpHasta.getValue();

        // Se evita que la fecha inicial sea mayor a la fecha final
        if (fechaDesde != null && fechaHasta != null && fechaDesde.isAfter(fechaHasta)) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Fechas incorrectas",
                    "Rango de fechas no válido",
                    "La fecha desde no puede ser mayor que la fecha hasta"
            );

            return;
        }

        // Si existe una fecha desde se agrega a la consulta
        if (fechaDesde != null) {
            sql.append("AND P.fecha_creacion >= ? ");
            parametros.add(fechaDesde);
        }

        // Si existe una fecha hasta también se agrega
        if (fechaHasta != null) {
            sql.append("AND P.fecha_creacion <= ? ");
            parametros.add(fechaHasta);
        }

        // Variables para guardar los precios escritos
        BigDecimal precioMinimo = null;
        BigDecimal precioMaximo = null;

        try {

            // Se revisa el precio mínimo si el campo tiene información
            if (!txtPrecioMinimo.getText().trim().isEmpty()) {
                precioMinimo = new BigDecimal(txtPrecioMinimo.getText().trim());

                // No se permiten precios negativos
                if (precioMinimo.compareTo(BigDecimal.ZERO) < 0) {
                    mostrarAlerta(
                            Alert.AlertType.WARNING,
                            "Precio incorrecto",
                            "Precio mínimo no válido",
                            "El precio mínimo no puede ser negativo"
                    );

                    return;
                }
            }

            // Se revisa el precio máximo
            if (!txtPrecioMaximo.getText().trim().isEmpty()) {
                precioMaximo = new BigDecimal(txtPrecioMaximo.getText().trim());

                if (precioMaximo.compareTo(BigDecimal.ZERO) < 0) {
                    mostrarAlerta(
                            Alert.AlertType.WARNING,
                            "Precio incorrecto",
                            "Precio máximo no válido",
                            "El precio máximo no puede ser negativo"
                    );

                    return;
                }
            }

        } catch (NumberFormatException ex) {

            // Si el usuario escribe letras u otro valor inválido se muestra una advertencia
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Datos incorrectos",
                    "Precio incorrecto",
                    "Los precios de búsqueda deben ser números"
            );

            return;
        }

        // Se comprueba que el precio mínimo no sea mayor al máximo
        if (precioMinimo != null && precioMaximo != null
                && precioMinimo.compareTo(precioMaximo) > 0) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Rango incorrecto",
                    "Precios no válidos",
                    "El precio mínimo no puede ser mayor que el precio máximo"
            );

            return;
        }

        // Se agrega el precio mínimo si fue utilizado
        if (precioMinimo != null) {
            sql.append("AND P.precio >= ? ");
            parametros.add(precioMinimo);
        }

        // Se agrega el precio máximo si fue utilizado
        if (precioMaximo != null) {
            sql.append("AND P.precio <= ? ");
            parametros.add(precioMaximo);
        }

        // Si está marcado, solo se muestran productos que tengan existencias
        if (chkProductosExistencia.isSelected()) {
            sql.append("AND P.stock > 0 ");
        }

        // El orden se toma solamente de las opciones que se encuentran en el ComboBox
        if (cmbOrdenar.getValue() != null) {

            switch (cmbOrdenar.getValue()) {

                case "Precio menor a mayor":
                    sql.append("ORDER BY P.precio ASC");
                    break;

                case "Precio mayor a menor":
                    sql.append("ORDER BY P.precio DESC");
                    break;

                case "Fecha más reciente":
                    sql.append("ORDER BY P.fecha_creacion DESC");
                    break;

                case "Fecha más antigua":
                    sql.append("ORDER BY P.fecha_creacion ASC");
                    break;

                case "Stock mayor a menor":
                    sql.append("ORDER BY P.stock DESC");
                    break;

                case "Stock menor a mayor":
                    sql.append("ORDER BY P.stock ASC");
                    break;

                default:
                    sql.append("ORDER BY P.nombre ASC");
                    break;
            }

        } else {
            sql.append("ORDER BY P.nombre ASC");
        }

        try (
                // Se abre la conexión
                Connection connection = ConexionBD.getConnection();

                // Se prepara la consulta que se construyó anteriormente
                PreparedStatement statement = connection.prepareStatement(sql.toString());
        ) {

            // Se recorren los parámetros para asignarlos al PreparedStatement
            for (int i = 0; i < parametros.size(); i++) {

                Object parametro = parametros.get(i);

                // Las fechas se convierten al tipo Date de SQL
                if (parametro instanceof LocalDate) {
                    statement.setDate(i + 1, Date.valueOf((LocalDate) parametro));

                    // Los precios se envían como BigDecimal
                } else if (parametro instanceof BigDecimal) {
                    statement.setBigDecimal(i + 1, (BigDecimal) parametro);

                    // Los otros valores se pueden enviar directamente
                } else {
                    statement.setObject(i + 1, parametro);
                }
            }

            // Se ejecuta la consulta
            ResultSet resultSet = statement.executeQuery();

            // Se recorren los productos encontrados
            while (resultSet.next()) {
                listaProductos.add(crearProductoDesdeResultSet(resultSet));
            }

            resultSet.close();

            // Se muestran los resultados encontrados
            tblProductos.setItems(listaProductos);

            // Se informa cuando la búsqueda no encuentra coincidencias
            if (listaProductos.isEmpty()) {
                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Búsqueda",
                        "Sin resultados",
                        "No se encontraron productos con los filtros seleccionados"
                );
            }

        } catch (SQLException ex) {
            mostrarErrorBaseDatos(ex);
        }
    }

    @FXML
    private void limpiarFiltro() {
        // Se regresan todos los filtros a su estado inicial
        txtCodigoNombre.clear();
        cmbCategoriaFiltro.getSelectionModel().clearSelection();
        dtpDesde.setValue(null);
        dtpHasta.setValue(null);
        txtPrecioMinimo.clear();
        txtPrecioMaximo.clear();
        chkProductosExistencia.setSelected(false);
        cmbOrdenar.setValue("Nombre");

        // Se vuelven a mostrar todos los productos activos
        cargarProductos();
    }

    private boolean validarCampos() {

        // Se revisa que los campos obligatorios no estén vacíos
        if (txtCodigo.getText().trim().isEmpty() || txtNombre.getText().trim().isEmpty()
                || txtPrecio.getText().trim().isEmpty() || txtStock.getText().trim().isEmpty()
                || cmbCategoria.getValue() == null || cmbMarca.getValue() == null
                || dtpFecha.getValue() == null) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Campos requeridos",
                    "Datos incompletos",
                    "Complete todos los campos obligatorios"
            );

            return false;
        }

        try {

            // El precio se convierte a BigDecimal
            BigDecimal precio = new BigDecimal(txtPrecio.getText().trim());

            // El precio tiene que ser mayor a cero
            if (precio.compareTo(BigDecimal.ZERO) <= 0) {
                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Precio incorrecto",
                        "Precio no válido",
                        "El precio debe ser mayor que cero"
                );

                return false;
            }

        } catch (NumberFormatException ex) {

            // Se muestra si el valor escrito no es un número
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Precio incorrecto",
                    "Precio no válido",
                    "Ingrese un número válido en el precio"
            );

            return false;
        }

        try {

            // Se convierte el stock a entero
            int stock = Integer.parseInt(txtStock.getText().trim());

            // El stock puede ser cero, pero no puede ser negativo
            if (stock < 0) {
                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Stock incorrecto",
                        "Stock no válido",
                        "El stock no puede ser negativo"
                );

                return false;
            }

        } catch (NumberFormatException ex) {

            // El stock debe ser un número entero
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Stock incorrecto",
                    "Stock no válido",
                    "Ingrese un número entero en el stock"
            );

            return false;
        }

        return true;
    }

    private boolean existeCodigo(String codigo, Integer idProductoExcluir) {

        // Se busca si existe un producto con el código recibido
        String sql = "SELECT id_producto FROM catalogos.productos WHERE codigo = ? ";

        // Al actualizar se excluye el producto que está siendo modificado
        if (idProductoExcluir != null) {
            sql += "AND id_producto <> ?";
        }

        try (
                Connection connection = ConexionBD.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {

            statement.setString(1, codigo);
            // Solamente se utiliza el segundo parámetro cuando se está actualizando
            if (idProductoExcluir != null) {
                statement.setInt(2, idProductoExcluir);
            }

            ResultSet resultSet = statement.executeQuery();
            // Si existe al menos una fila, el código ya está registrado
            boolean existe = resultSet.next();
            resultSet.close();
            return existe;
        } catch (SQLException ex) {
            mostrarErrorBaseDatos(ex);
            // Si ocurre un error se evita continuar con el guardado
            return true;
        }
    }
    private void mostrarErrorBaseDatos(SQLException ex) {
        // Se muestra el mensaje que devuelve la base de datos
        mostrarAlerta(Alert.AlertType.ERROR, "Error de base de datos",
                "Ocurrió un error",
                ex.getMessage()
        );
    }
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String encabezado, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(encabezado);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}