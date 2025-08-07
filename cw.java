// Esta línea importa todas las clases del paquete java.io.
// Nos permite usar cosas como File, FileWriter, BufferedReader, etc. 
// que sirven para leer, escribir y manipular archivos de texto en Java.
import java.io.*;

// Importamos todas las clases del paquete java.util, incluyendo Scanner, ArrayList, etc.
// Sirve para trabajar con estructuras de datos, entrada de usuario, y utilidades generales.
import java.util.*;

public class cw {
    static final String ARCHIVO_USUARIOS = "usuarios.txt";
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n--- MENÚ ---");
            System.out.println("1. Crear usuario");
            System.out.println("2. Iniciar sesión");
            System.out.println("3. Salir");
            System.out.print("Elige una opción: ");
            String opcion = sc.nextLine();

            switch (opcion) {
                case "1":
                    crearUsuario();
                    break;
                case "2":
                    iniciarSesion();
                    break;
                case "3":
                    System.out.println("Saliendo del sistema...");
                    return;
                default:
                    System.out.println("Opción no válida. Intenta de nuevo.");
            }
        }
    }

    // Método para crear un usuario nuevo y guardarlo en el archivo
   static void crearUsuario() {
    System.out.print("Ingresa un nombre de usuario: ");
    String usuario = sc.nextLine().trim();  // trim() elimina espacios al inicio y final

    // Validar que el usuario no esté vacío
    if (usuario.isEmpty()) {
        System.out.println("❌ Debes poner algo en el nombre de usuario.");
        return;  // Salimos del método sin crear usuario
    }

    System.out.print("Ingresa una contraseña: ");
    String contrasena = sc.nextLine().trim();

    // Validar que la contraseña no esté vacía
    if (contrasena.isEmpty()) {
        System.out.println("❌ Debes poner algo en la contraseña.");
        return;
    }

    // Verificamos si el usuario ya existe
    if (usuarioExiste(usuario)) {
        System.out.println("❌ El usuario ya existe. Intenta con otro nombre.");
        return;
    }

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO_USUARIOS, true))) {
        /*
         * Guardamos usuario y contraseña, seguido de:
         *  - fechas de inicio y fin vacías porque no está suscrito
         *  - el texto "No suscrito" para indicar su estado
         * Los datos se guardan separados por comas para luego poder leerlos.
         */
        writer.write(usuario + "," + contrasena + ",,," + "No suscrito");
        writer.newLine();
        System.out.println("✅ Usuario creado correctamente. Estado: No suscrito.");
    } catch (IOException e) {
        System.out.println("❌ Error al guardar el usuario.");
    }
}

// Método para verificar si un usuario ya está registrado
static boolean usuarioExiste(String usuarioBuscado) {
    try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_USUARIOS))) {
        String linea;
        while ((linea = reader.readLine()) != null) {
            String[] datos = linea.split(",");
            String usuario = datos[0];
            if (usuario.equals(usuarioBuscado)) {
                return true;
            }
        }
    } catch (IOException e) {
        // Si el archivo no existe aún, se asume que el usuario no existe
    }
    return false;
}

    // Método para iniciar sesión verificando usuario y contraseña
 static void iniciarSesion() {
    System.out.print("Usuario: ");
    String usuario = sc.nextLine();

    System.out.print("Contraseña: ");
    String contrasena = sc.nextLine();

    try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_USUARIOS))) {
        String linea;
        boolean usuarioEncontrado = false;

        while ((linea = reader.readLine()) != null) {
            String[] datos = linea.split(",");

            String usuarioGuardado = datos[0];
            String contrasenaGuardada = datos[1];

            if (usuario.equals(usuarioGuardado) && contrasena.equals(contrasenaGuardada)) {
                usuarioEncontrado = true;
                System.out.println("Inicio de sesión exitoso. ¡Bienvenido, " + usuario + "!");

                String fechaFin = datos.length > 3 ? datos[3] : "";
                String tipo = datos.length > 4 ? datos[4] : "No suscrito";

                java.time.LocalDate hoy = java.time.LocalDate.now();
                java.time.format.DateTimeFormatter formato = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");

                boolean suscripcionActiva = false;

                if (!tipo.equalsIgnoreCase("No suscrito") && !fechaFin.isEmpty()) {
                    java.time.LocalDate fechaFinSuscripcion = java.time.LocalDate.parse(fechaFin, formato);
                    suscripcionActiva = (hoy.isBefore(fechaFinSuscripcion) || hoy.isEqual(fechaFinSuscripcion));
                }

                if (suscripcionActiva) {
                    System.out.println("Tu suscripción está activa hasta: " + fechaFin + " (Tipo: " + tipo + ")");
                } else {
                    System.out.println("No tienes una suscripción activa.");
                    System.out.print("¿Quieres suscribirte ahora? (s/n): ");
                    String respuesta = sc.nextLine().trim().toLowerCase();

                    if (respuesta.equals("s")) {
                        // Calculamos la fecha de fin que será hoy + 1 mes
                        java.time.LocalDate nuevaFechaFin = hoy.plusMonths(1);
                        String nuevaFechaFinStr = nuevaFechaFin.format(formato);
                        String nuevoTipo = "Básico";

                        // Actualizamos el archivo para reflejar la nueva suscripción
                        actualizarSuscripcion(usuario, datos[1], hoy.format(formato), nuevaFechaFinStr, nuevoTipo);

                        System.out.println("¡Suscripción activada! Vigente hasta " + nuevaFechaFinStr);
                    } else {
                        System.out.println("Puedes suscribirte más tarde desde el menú.");
                    }
                }
                return; // terminamos la función después de iniciar sesión
            }
        }
        if (!usuarioEncontrado) {
            System.out.println("Usuario o contraseña incorrectos.");
        }
    } catch (IOException e) {
        System.out.println("Error al leer el archivo de usuarios.");
    }
}

// Método para actualizar la suscripción en el archivo usuarios.txt
static void actualizarSuscripcion(String usuario, String contrasena, String fechaInicio, String fechaFin, String tipo) {
    try {
        List<String> lineas = java.nio.file.Files.readAllLines(java.nio.file.Paths.get(ARCHIVO_USUARIOS));
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO_USUARIOS))) {
            for (String linea : lineas) {
                String[] datos = linea.split(",");
                if (datos[0].equals(usuario)) {
                    // Escribimos la línea actualizada con la suscripción nueva
                    writer.write(usuario + "," + contrasena + "," + fechaInicio + "," + fechaFin + "," + tipo);
                } else {
                    // Escribimos las líneas que no cambian tal cual
                    writer.write(linea);
                }
                writer.newLine();
            }
        }
    } catch (IOException e) {
        System.out.println("Error actualizando la suscripción.");
    }
}

}