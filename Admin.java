import java.io.*;
import java.util.*;
import java.nio.file.*;

public class Admin {
    static final String ARCHIVO_USUARIOS = "usuarios.txt";
    static Scanner sc = new Scanner(System.in);

    public static void menuAdmin() {
        while (true) {
            System.out.println("\n--- MENÚ ADMIN ---");
            System.out.println("1. Ver todos los usuarios");
            System.out.println("2. Borrar un usuario");
            System.out.println("3. Salir");
            System.out.print("Elige una opción: ");
            String opcion = sc.nextLine();

            switch (opcion) {
                case "1":
                    mostrarUsuarios();
                    break;
                case "2":
                    borrarUsuario();
                    break;
                case "3":
                    System.out.println("Saliendo del menú admin...");
                    return;
                default:
                    System.out.println("Opción inválida, intenta de nuevo.");
            }
        }
    }

    private static void mostrarUsuarios() {
        try {
            List<String> lineas = Files.readAllLines(Paths.get(ARCHIVO_USUARIOS));
            System.out.println("\n--- LISTADO DE USUARIOS ---");
            System.out.printf("%-15s %-15s %-12s %-12s %-18s %-25s %-6s\n",
                    "Usuario", "Contraseña", "Fecha Inicio", "Fecha Fin", "Suscripción", "Método Pago", "Puntos");
            System.out.println("---------------------------------------------------------------------------------------------");
            for (String linea : lineas) {
                String[] datos = linea.split(";");
                String usuario = datos.length > 0 ? datos[0] : "";
                String contrasena = datos.length > 1 ? datos[1] : "";
                String fechaInicio = datos.length > 2 ? datos[2] : "";
                String fechaFin = datos.length > 3 ? datos[3] : "";
                String suscripcion = datos.length > 4 ? datos[4] : "";
                String metodoPago = datos.length > 5 ? datos[5] : "";
                String puntos = datos.length > 6 ? datos[6] : "0";

                System.out.printf("%-15s %-15s %-12s %-12s %-18s %-25s %-6s\n",
                        usuario, contrasena, fechaInicio, fechaFin, suscripcion, metodoPago, puntos);
            }
        } catch (IOException e) {
            System.out.println("Error leyendo archivo de usuarios.");
        }
    }

    private static void borrarUsuario() {
        System.out.print("Ingresa el usuario que deseas borrar: ");
        String usuarioABorrar = sc.nextLine().trim();

        if (usuarioABorrar.equalsIgnoreCase("admin")) {
            System.out.println("No puedes borrar al usuario admin.");
            return;
        }

        try {
            List<String> lineas = Files.readAllLines(Paths.get(ARCHIVO_USUARIOS));
            boolean encontrado = false;

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO_USUARIOS))) {
                for (String linea : lineas) {
                    String[] datos = linea.split(";");
                    if (datos.length > 0 && datos[0].equals(usuarioABorrar)) {
                        encontrado = true;
                        // No escribimos esta línea (la borramos)
                        continue;
                    }
                    writer.write(linea);
                    writer.newLine();
                }
            }

            if (encontrado) {
                System.out.println("Usuario '" + usuarioABorrar + "' borrado correctamente.");
            } else {
                System.out.println("Usuario no encontrado.");
            }
        } catch (IOException e) {
            System.out.println("Error al borrar el usuario.");
        }
    }
}
