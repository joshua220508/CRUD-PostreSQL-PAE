
package com.example.proyectocrud.controller;

import com.example.proyectocrud.database.DBConnection;
import com.example.proyectocrud.model.Categoria;
import com.example.proyectocrud.model.Marca;
import com.example.proyectocrud.model.Producto;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

public class ProductosController {

    @FXML private Label lblTituloGestion;
    @FXML private Label lblCodigo;
    @FXML private Label lblNombre;
    @FXML private Label lblPrecio;
    @FXML private Label lblStock;
    @FXML private Label lblCategoria;
    @FXML private Label lblMarca;
    @FXML private Label lblFecha;
    @FXML private Label lblDescripcion;
    @FXML private Label lblTituloBusqueda;
    @FXML private Label lblFiltroCodigoNombre;
    @FXML private Label lblFiltroCategoria;
    @FXML private Label lblFiltroDesde;
    @FXML private Label lblFiltroHasta;
    @FXML private Label lblFiltroPrecioMin;
    @FXML private Label lblFiltroPrecioMax;
    @FXML private Label lblFiltroOrdenarPor;
    @FXML private Label lblTablaVacia;

    @FXML private javafx.scene.layout.VBox vboxPrincipal;
    @FXML private javafx.scene.layout.GridPane gridGestion;
    @FXML private javafx.scene.layout.HBox hboxBotonesGestion;
    @FXML private javafx.scene.layout.GridPane gridFiltros;
    @FXML private javafx.scene.layout.HBox hboxBotonesFiltros;

    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtStock;
    @FXML private TextField txtDescripcion;

    @FXML private ComboBox<Categoria> cbCategoria;
    @FXML private ComboBox<Marca> cbMarca;
    @FXML private DatePicker dpFecha;

    @FXML private Button btnNuevo;
    @FXML private Button btnGuardar;
    @FXML private Button btnActualizar;
    @FXML private Button btnEliminar;

    @FXML private TextField txtFiltroCodigoNombre;
    @FXML private TextField txtFiltroPrecioMin;
    @FXML private TextField txtFiltroPrecioMax;
    @FXML private ComboBox<Categoria> cbFiltroCategoria;
    @FXML private ComboBox<String> cbFiltroOrdenarPor;
    @FXML private DatePicker dpFiltroDesde;
    @FXML private DatePicker dpFiltroHasta;
    @FXML private CheckBox chkSoloExistencias;

    @FXML private Button btnBuscar;
    @FXML private Button btnLimpiarFiltros;

    @FXML private TableView<Producto> tvProductos;
    @FXML private TableColumn<Producto, String> colCodigo;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, String> colCategoria;
    @FXML private TableColumn<Producto, String> colMarca;
    @FXML private TableColumn<Producto, Number> colPrecio;
    @FXML private TableColumn<Producto, Number> colStock;
    @FXML private TableColumn<Producto, LocalDate> colFechaCreacion;

    private final ObservableList<Producto> productos =
            FXCollections.observableArrayList();

    private final ObservableList<Categoria> categorias =
            FXCollections.observableArrayList();

    private final ObservableList<Marca> marcas =
            FXCollections.observableArrayList();

    private Producto productoSeleccionado;

    @FXML
    public void initialize() {
        configurarTabla();
        configurarComboCategorias();
        configurarComboMarcas();

        cbFiltroOrdenarPor.setItems(
                FXCollections.observableArrayList(
                        "Código",
                        "Nombre",
                        "Precio ascendente",
                        "Precio descendente",
                        "Mayor stock",
                        "Fecha más reciente"
                )
        );

        cbFiltroOrdenarPor.setValue("Código");

        cargarCatalogos();
        cargarProductos();
    }

    private void configurarTabla() {
        colCodigo.setCellValueFactory(
                celda -> new ReadOnlyStringWrapper(
                        celda.getValue().getCodigo()
                )
        );

        colNombre.setCellValueFactory(
                celda -> new ReadOnlyStringWrapper(
                        celda.getValue().getNombre()
                )
        );

        colCategoria.setCellValueFactory(
                celda -> new ReadOnlyStringWrapper(
                        obtenerNombreCategoria(
                                celda.getValue().getIdCategoria()
                        )
                )
        );

        colMarca.setCellValueFactory(
                celda -> new ReadOnlyStringWrapper(
                        obtenerNombreMarca(
                                celda.getValue().getIdMarca()
                        )
                )
        );

        colPrecio.setCellValueFactory(
                celda -> new javafx.beans.property.ReadOnlyDoubleWrapper(
                        celda.getValue().getPrecio()
                )
        );

        colStock.setCellValueFactory(
                celda -> new javafx.beans.property.ReadOnlyIntegerWrapper(
                        celda.getValue().getStock()
                )
        );

        colFechaCreacion.setCellValueFactory(
                celda -> new javafx.beans.property.ReadOnlyObjectWrapper<>(
                        celda.getValue().getFechaCreacion()
                )
        );

        tvProductos.setItems(productos);
    }

    private void configurarComboCategorias() {
        StringConverter<Categoria> conversor =
                new StringConverter<>() {
                    @Override
                    public String toString(Categoria categoria) {
                        return categoria == null
                                ? ""
                                : categoria.getNombre();
                    }

                    @Override
                    public Categoria fromString(String texto) {
                        return null;
                    }
                };

        cbCategoria.setConverter(conversor);
        cbFiltroCategoria.setConverter(conversor);
    }

    private void configurarComboMarcas() {
        cbMarca.setConverter(new StringConverter<>() {
            @Override
            public String toString(Marca marca) {
                return marca == null ? "" : marca.getNombre();
            }

            @Override
            public Marca fromString(String texto) {
                return null;
            }
        });
    }

    private void cargarCatalogos() {
        categorias.clear();
        marcas.clear();

        String sqlCategorias =
                "SELECT id_categoria, nombre, descripcion, estado " +
                        "FROM catalogos.categorias WHERE estado = 1 " +
                        "ORDER BY nombre";

        String sqlMarcas =
                "SELECT id_marca, nombre, pais_origen, estado " +
                        "FROM catalogos.marcas WHERE estado = 1 " +
                        "ORDER BY nombre";

        try (Connection conexion = DBConnection.getConnection()) {

            try (PreparedStatement ps =
                         conexion.prepareStatement(sqlCategorias);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    categorias.add(new Categoria(
                            rs.getInt("id_categoria"),
                            rs.getString("nombre"),
                            rs.getString("descripcion"),
                            rs.getInt("estado")
                    ));
                }
            }

            try (PreparedStatement ps =
                         conexion.prepareStatement(sqlMarcas);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    marcas.add(new Marca(
                            rs.getInt("id_marca"),
                            rs.getString("nombre"),
                            rs.getString("pais_origen"),
                            rs.getInt("estado")
                    ));
                }
            }

            cbCategoria.setItems(categorias);
            cbFiltroCategoria.setItems(categorias);
            cbMarca.setItems(marcas);

        } catch (SQLException e) {
            mostrarError("No se pudieron cargar las categorías y marcas.", e);
        }
    }

    private String obtenerNombreCategoria(int id) {
        return categorias.stream()
                .filter(c -> c.getIdCategoria() == id)
                .map(Categoria::getNombre)
                .findFirst()
                .orElse("Categoría " + id);
    }

    private String obtenerNombreMarca(int id) {
        return marcas.stream()
                .filter(m -> m.getIdMarca() == id)
                .map(Marca::getNombre)
                .findFirst()
                .orElse("Marca " + id);
    }

    private void cargarProductos() {
        productos.clear();

        StringBuilder sql = new StringBuilder(
                "SELECT p.id_producto, p.codigo, p.nombre, " +
                        "p.descripcion, p.precio, p.stock, p.fecha_creacion, " +
                        "p.id_categoria, p.id_marca, p.estado " +
                        "FROM catalogos.productos p " +
                        "WHERE p.estado = 1"
        );

        String texto = txtFiltroCodigoNombre.getText().trim();

        if (!texto.isEmpty()) {
            sql.append(" AND (p.codigo ILIKE ? OR p.nombre ILIKE ?)");
        }

        Categoria categoria = cbFiltroCategoria.getValue();

        if (categoria != null) {
            sql.append(" AND p.id_categoria = ?");
        }

        if (dpFiltroDesde.getValue() != null) {
            sql.append(" AND p.fecha_creacion >= ?");
        }

        if (dpFiltroHasta.getValue() != null) {
            sql.append(" AND p.fecha_creacion <= ?");
        }

        if (!txtFiltroPrecioMin.getText().trim().isEmpty()) {
            sql.append(" AND p.precio >= ?");
        }

        if (!txtFiltroPrecioMax.getText().trim().isEmpty()) {
            sql.append(" AND p.precio <= ?");
        }

        if (chkSoloExistencias.isSelected()) {
            sql.append(" AND p.stock > 0");
        }

        String orden = cbFiltroOrdenarPor.getValue();

        if (orden == null) {
            orden = "Código";
        }

        switch (orden) {
            case "Nombre":
                sql.append(" ORDER BY p.nombre ASC");
                break;
            case "Precio ascendente":
                sql.append(" ORDER BY p.precio ASC");
                break;
            case "Precio descendente":
                sql.append(" ORDER BY p.precio DESC");
                break;
            case "Mayor stock":
                sql.append(" ORDER BY p.stock DESC");
                break;
            case "Fecha más reciente":
                sql.append(" ORDER BY p.fecha_creacion DESC");
                break;
            default:
                sql.append(" ORDER BY p.codigo ASC");
        }

        try (Connection conexion = DBConnection.getConnection();
             PreparedStatement ps =
                     conexion.prepareStatement(sql.toString())) {

            int indice = 1;

            if (!texto.isEmpty()) {
                ps.setString(indice++, "%" + texto + "%");
                ps.setString(indice++, "%" + texto + "%");
            }

            if (categoria != null) {
                ps.setInt(indice++, categoria.getIdCategoria());
            }

            if (dpFiltroDesde.getValue() != null) {
                ps.setDate(indice++, java.sql.Date.valueOf(
                        dpFiltroDesde.getValue()
                ));
            }

            if (dpFiltroHasta.getValue() != null) {
                ps.setDate(indice++, java.sql.Date.valueOf(
                        dpFiltroHasta.getValue()
                ));
            }

            if (!txtFiltroPrecioMin.getText().trim().isEmpty()) {
                ps.setDouble(indice++, Double.parseDouble(
                        txtFiltroPrecioMin.getText().trim()
                ));
            }

            if (!txtFiltroPrecioMax.getText().trim().isEmpty()) {
                ps.setDouble(indice++, Double.parseDouble(
                        txtFiltroPrecioMax.getText().trim()
                ));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    productos.add(new Producto(
                            rs.getInt("id_producto"),
                            rs.getString("codigo"),
                            rs.getString("nombre"),
                            rs.getString("descripcion"),
                            rs.getDouble("precio"),
                            rs.getInt("stock"),
                            rs.getDate("fecha_creacion").toLocalDate(),
                            rs.getInt("id_categoria"),
                            rs.getInt("id_marca"),
                            rs.getInt("estado")
                    ));
                }
            }

            lblTablaVacia.setVisible(productos.isEmpty());
            lblTablaVacia.setManaged(productos.isEmpty());

        } catch (NumberFormatException e) {
            mostrarMensaje("Los precios de los filtros deben ser números válidos.");
        } catch (SQLException e) {
            mostrarError("No se pudieron consultar los productos.", e);
        }
    }

    @FXML
    private void nuevoProducto(ActionEvent event) {
        limpiarFormulario();
    }

    private void limpiarFormulario() {
        productoSeleccionado = null;

        txtCodigo.clear();
        txtNombre.clear();
        txtDescripcion.clear();
        txtPrecio.clear();
        txtStock.clear();

        cbCategoria.getSelectionModel().clearSelection();
        cbMarca.getSelectionModel().clearSelection();

        dpFecha.setValue(LocalDate.now());

        tvProductos.getSelectionModel().clearSelection();
    }

    private boolean validarFormulario() {
        if (txtCodigo.getText().trim().isEmpty()
                || txtNombre.getText().trim().isEmpty()
                || txtDescripcion.getText().trim().isEmpty()
                || txtPrecio.getText().trim().isEmpty()
                || txtStock.getText().trim().isEmpty()
                || cbCategoria.getValue() == null
                || cbMarca.getValue() == null
                || dpFecha.getValue() == null) {

            mostrarMensaje("Completa todos los campos del producto.");
            return false;
        }

        try {
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            int stock = Integer.parseInt(txtStock.getText().trim());

            if (!Double.isFinite(precio) || precio < 0) {
                mostrarMensaje("El precio debe ser un número válido mayor o igual a cero.");
                return false;
            }

            if (stock < 0) {
                mostrarMensaje("El stock no puede ser negativo.");
                return false;
            }

        } catch (NumberFormatException e) {
            mostrarMensaje("Precio o stock inválido. Revisa los valores ingresados.");
            return false;
        }

        return true;
    }

    @FXML
    private void guardarProducto(ActionEvent event) {
        if (!validarFormulario()) {
            return;
        }

        String sql =
                "INSERT INTO catalogos.productos " +
                        "(codigo, nombre, descripcion, precio, stock, " +
                        "fecha_creacion, id_categoria, id_marca, estado) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 1)";

        try (Connection conexion = DBConnection.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            llenarParametrosProducto(ps);

            ps.executeUpdate();

            mostrarMensaje("Producto guardado correctamente.");
            cargarProductos();
            limpiarFormulario();

        } catch (SQLException e) {
            mostrarError("No se pudo guardar el producto. Verifica que el código no exista.", e);
        }
    }

    private void llenarParametrosProducto(PreparedStatement ps)
            throws SQLException {

        ps.setString(1, txtCodigo.getText().trim());
        ps.setString(2, txtNombre.getText().trim());
        ps.setString(3, txtDescripcion.getText().trim());
        ps.setDouble(4, Double.parseDouble(txtPrecio.getText().trim()));
        ps.setInt(5, Integer.parseInt(txtStock.getText().trim()));
        ps.setDate(6, java.sql.Date.valueOf(dpFecha.getValue()));
        ps.setInt(7, cbCategoria.getValue().getIdCategoria());
        ps.setInt(8, cbMarca.getValue().getIdMarca());
    }

    @FXML
    private void actualizarProducto(ActionEvent event) {
        if (productoSeleccionado == null) {
            mostrarMensaje("Selecciona un producto de la tabla para actualizarlo.");
            return;
        }

        if (!validarFormulario()) {
            return;
        }

        String sql =
                "UPDATE catalogos.productos SET " +
                        "codigo = ?, nombre = ?, descripcion = ?, precio = ?, " +
                        "stock = ?, fecha_creacion = ?, id_categoria = ?, " +
                        "id_marca = ? " +
                        "WHERE id_producto = ? AND estado = 1";

        try (Connection conexion = DBConnection.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            llenarParametrosProducto(ps);
            ps.setInt(9, productoSeleccionado.getIdProducto());

            int filas = ps.executeUpdate();

            if (filas > 0) {
                mostrarMensaje("Producto actualizado correctamente.");
                cargarProductos();
                limpiarFormulario();
            } else {
                mostrarMensaje("El producto ya no está activo o no existe.");
            }

        } catch (SQLException e) {
            mostrarError("No se pudo actualizar el producto. Verifica el código y los datos.", e);
        }
    }

    @FXML
    private void eliminarProducto(ActionEvent event) {
        if (productoSeleccionado == null) {
            mostrarMensaje("Selecciona un producto de la tabla para eliminarlo.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("Eliminar producto");
        confirmacion.setContentText(
                "¿Deseas marcar como inactivo el producto "
                        + productoSeleccionado.getNombre() + "?"
        );

        Optional<ButtonType> respuesta = confirmacion.showAndWait();

        if (respuesta.isEmpty()
                || respuesta.get() != ButtonType.OK) {
            return;
        }

        String sql =
                "UPDATE catalogos.productos SET estado = 0 " +
                        "WHERE id_producto = ? AND estado = 1";

        try (Connection conexion = DBConnection.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, productoSeleccionado.getIdProducto());

            int filas = ps.executeUpdate();

            if (filas > 0) {
                mostrarMensaje("Producto eliminado lógicamente.");
                cargarProductos();
                limpiarFormulario();
            } else {
                mostrarMensaje("El producto ya estaba inactivo o no existe.");
            }

        } catch (SQLException e) {
            mostrarError("No se pudo eliminar el producto.", e);
        }
    }

    @FXML
    private void seleccionarProducto(MouseEvent event) {
        Producto producto = tvProductos.getSelectionModel().getSelectedItem();

        if (producto == null) {
            return;
        }

        productoSeleccionado = producto;

        txtCodigo.setText(producto.getCodigo());
        txtNombre.setText(producto.getNombre());
        txtDescripcion.setText(producto.getDescripcion());
        txtPrecio.setText(String.valueOf(producto.getPrecio()));
        txtStock.setText(String.valueOf(producto.getStock()));
        dpFecha.setValue(producto.getFechaCreacion());

        cbCategoria.setValue(
                categorias.stream()
                        .filter(c -> c.getIdCategoria()
                                == producto.getIdCategoria())
                        .findFirst()
                        .orElse(null)
        );

        cbMarca.setValue(
                marcas.stream()
                        .filter(m -> m.getIdMarca()
                                == producto.getIdMarca())
                        .findFirst()
                        .orElse(null)
        );
    }

    @FXML
    private void buscarProductos(ActionEvent event) {
        cargarProductos();
    }

    @FXML
    private void buscarPorCodigo(ActionEvent event) {
        txtFiltroCodigoNombre.setText(txtCodigo.getText().trim());
        cargarProductos();
    }

    @FXML
    private void buscarPorNombre(ActionEvent event) {
        txtFiltroCodigoNombre.setText(txtNombre.getText().trim());
        cargarProductos();
    }

    @FXML
    private void limpiarFiltros(ActionEvent event) {
        txtFiltroCodigoNombre.clear();
        txtFiltroPrecioMin.clear();
        txtFiltroPrecioMax.clear();

        cbFiltroCategoria.getSelectionModel().clearSelection();

        dpFiltroDesde.setValue(null);
        dpFiltroHasta.setValue(null);

        chkSoloExistencias.setSelected(false);
        cbFiltroOrdenarPor.setValue("Código");

        cargarProductos();
    }

    private void mostrarMensaje(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Gestión de productos");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void mostrarError(String mensaje, Exception e) {
        e.printStackTrace();

        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText(mensaje);
        alerta.setContentText(e.getMessage());
        alerta.showAndWait();
    }
}
