import java.io.*;             // Importa clases para entrada y salida, como manejo de archivos
import java.util.*;           // Importa utilidades, como Scanner y List
import java.nio.file.*;       // Importa clases para manejo de archivos con rutas (Paths, Files)

public class Admin {
    // Constante con el nombre del archivo donde están guardados los usuarios
    static final String ARCHIVO_USUARIOS = "usuarios.txt";
    // Scanner para leer entradas del usuario desde consola
    static Scanner sc = new Scanner(System.in);

    // Método que muestra el menú principal del administrador
    public static void menuAdmin() {
        while (true) {  // Bucle infinito para mostrar el menú hasta que se decida salir
            System.out.println("\n--- MENÚ ADMIN ---");
            System.out.println("1. Ver todos los usuarios");
            System.out.println("2. Borrar un usuario");
            System.out.println("3. Salir");
            System.out.print("Elige una opción: ");
            String opcion = sc.nextLine();  // Leer la opción ingresada por el usuario

            switch (opcion) {
                case "1":    // Si elige "1", mostrar la lista de usuarios
                    mostrarUsuarios();
                    break;
                case "2":    // Si elige "2", borrar un usuario
                    borrarUsuario();
                    break;
                case "3":    // Si elige "3", salir del menú admin
                    System.out.println("Saliendo del menú admin...");
                    return; // Termina el método y vuelve al lugar desde donde fue llamado
                default:     // Si la opción no es válida
                    System.out.println("Opción inválida, intenta de nuevo.");
            }
        }
    }

    // Método que muestra todos los usuarios registrados en el archivo
    private static void mostrarUsuarios() {
        try {
            // Leer todas las líneas del archivo usuarios.txt en una lista
            List<String> lineas = Files.readAllLines(Paths.get(ARCHIVO_USUARIOS));

            // Mostrar encabezado con formato para las columnas
            System.out.println("\n--- LISTADO DE USUARIOS ---");
            System.out.printf("%-15s %-15s %-12s %-12s %-18s %-25s %-6s\n",
                    "Usuario", "Contraseña", "Fecha Inicio", "Fecha Fin", "Suscripción", "Método Pago", "Puntos");
            System.out.println("---------------------------------------------------------------------------------------------");

            // Recorrer cada línea del archivo (cada usuario)
            for (String linea : lineas) {
                // Separar los datos por el carácter ';'
                String[] datos = linea.split(";");
                // Obtener cada dato o dejar vacío si no existe
                String usuario = datos.length > 0 ? datos[0] : "";
                String contrasena = datos.length > 1 ? datos[1] : "";
                String fechaInicio = datos.length > 2 ? datos[2] : "";
                String fechaFin = datos.length > 3 ? datos[3] : "";
                String suscripcion = datos.length > 4 ? datos[4] : "";
                String metodoPago = datos.length > 5 ? datos[5] : "";
                String puntos = datos.length > 6 ? datos[6] : "0";

                // Imprimir los datos formateados en columnas
                System.out.printf("%-15s %-15s %-12s %-12s %-18s %-25s %-6s\n",
                        usuario, contrasena, fechaInicio, fechaFin, suscripcion, metodoPago, puntos);
            }
        } catch (IOException e) {
            // Si ocurre un error leyendo el archivo, mostrar mensaje
            System.out.println("Error leyendo archivo de usuarios.");
        }
    }

    // Método para borrar un usuario específico del archivo
    private static void borrarUsuario() {
        System.out.print("Ingresa el usuario que deseas borrar: ");
        String usuarioABorrar = sc.nextLine().trim(); // Leer y limpiar el nombre del usuario a borrar

        // Evitar que se borre el usuario admin por seguridad
        if (usuarioABorrar.equalsIgnoreCase("admin")) {
            System.out.println("No puedes borrar al usuario admin.");
            return; // Salir del método sin hacer nada
        }

        try {
            // Leer todas las líneas del archivo para procesarlas
            List<String> lineas = Files.readAllLines(Paths.get(ARCHIVO_USUARIOS));
            boolean encontrado = false;  // Bandera para saber si encontramos al usuario

            // Abrir el archivo para escribir (sobrescribiendo) con BufferedWriter
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO_USUARIOS))) {
                // Recorrer cada línea (usuario)
                for (String linea : lineas) {
                    String[] datos = linea.split(";");
                    // Si el usuario actual es el que queremos borrar, lo saltamos (no escribimos)
                    if (datos.length > 0 && datos[0].equals(usuarioABorrar)) {
                        encontrado = true;  // Marcamos que sí encontramos al usuario
                        continue;  // Saltar esta línea (eliminarla)
                    }
                    // Si no es el usuario a borrar, escribirla normalmente en el archivo
                    writer.write(linea);
                    writer.newLine();  // Agregar salto de línea
                }
            }

            // Mostrar resultado según si se encontró o no el usuario
            if (encontrado) {
                System.out.println("Usuario '" + usuarioABorrar + "' borrado correctamente.");
            } else {
                System.out.println("Usuario no encontrado.");
            }
        } catch (IOException e) {
            // Si ocurre un error al escribir el archivo, mostrar mensaje
            System.out.println("Error al borrar el usuario.");
        }
    }
}
