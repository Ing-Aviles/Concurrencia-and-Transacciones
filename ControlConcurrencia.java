import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ControlConcurrencia {

    private static final String URL = "jdbc:postgresql://localhost:5432/tienda";
    private static final String USER = "postgres";
    private static final String PASSWORD = "3008";

    public static void main(String[] args) {
        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);

            int productoId = 1; 
            int cantidad = 1; 

            // Inicio de la transacción
            connection.setAutoCommit(false);

            int stockActual = obtenerStockProducto(connection, productoId);

            if (stockActual < cantidad) {
                throw new IllegalStateException("No hay suficiente stock disponible.");
            }

            actualizarStockProducto(connection, productoId, stockActual - cantidad);
            connection.commit();

            System.out.println("Compra realizada con éxito.");

            connection.close();
        } catch (SQLException e) {
            System.err.println("Error al realizar la compra: " + e.getMessage());
            e.printStackTrace();
            
            // En caso de error, hacer rollback para deshacer la transacción
            // try {
            //     if (connection != null) {
            //         connection.rollback();
            //         connection.close();
            //     }
            // } catch (SQLException ex) {
            //     System.err.println("Error al hacer rollback: " + ex.getMessage());
            //     ex.printStackTrace();
            // }
        }
    }

    private static int obtenerStockProducto(Connection connection, int productoId) throws SQLException {
        String sql = "SELECT stock FROM productos WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, productoId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("stock");
                } else {
                    throw new IllegalStateException("No se encontró el producto con ID: " + productoId);
                }
            }
        }
    }

    private static void actualizarStockProducto(Connection connection, int productoId, int nuevoStock) throws SQLException {
        String sql = "UPDATE productos SET stock = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, nuevoStock);
            statement.setInt(2, productoId);
            int filasActualizadas = statement.executeUpdate();
            if (filasActualizadas != 1) {
                throw new IllegalStateException("No se pudo actualizar el stock del producto.");
            }
        }
    }
}
