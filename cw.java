import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class cw {
    static final String ARCHIVO_USUARIOS = "usuarios.txt";
    static Scanner sc = new Scanner(System.in);

    // Clase interna para manejar usuarios fácilmente
    static class Usuario {
        String usuario;
        String contrasena;
        String fechaInicio;
        String fechaFin;
        String tipoSuscripcion;
        String metodoPago;
        int puntos;
    }

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n--- MENÚ ---");
            System.out.println("1. Crear usuario");
            System.out.println("2. Iniciar sesión");
            System.out.println("3. Salir");
            System.out.print("Elige una opción: ");
            switch (sc.nextLine()) {
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

    static void crearUsuario() {
        System.out.print("Ingresa un nombre de usuario: ");
        String usuario = sc.nextLine().trim();
        if (usuario.isEmpty()) {
            System.out.println("❌ Debes poner algo en el nombre de usuario.");
            return;
        }

        System.out.print("Ingresa una contraseña: ");
        String contrasena = sc.nextLine().trim();
        if (contrasena.isEmpty()) {
            System.out.println("❌ Debes poner algo en la contraseña.");
            return;
        }

        if (usuarioExiste(usuario)) {
            System.out.println("❌ El usuario ya existe. Intenta con otro nombre.");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO_USUARIOS, true))) {
            writer.write(usuario + ";" + contrasena + ";;;" + "No suscrito" + ";" + "sinmetodo" + ";0");
            writer.newLine();
            System.out.println("✅ Usuario creado correctamente. Estado: No suscrito.");
        } catch (IOException e) {
            System.out.println("❌ Error al guardar el usuario.");
            return;
        }

        // Aquí llamas a la gestión de suscripción (asegúrate de tener la clase y método suscripcion.gestionarNuevaCuenta)
        String metodoPago = suscripcion.gestionarNuevaCuenta(usuario, contrasena);
        if (metodoPago != null && !metodoPago.equalsIgnoreCase("sinmetodo")) {
            actualizarMetodoPago(usuario, metodoPago);
        }
    }

    static boolean usuarioExiste(String usuarioBuscado) {
        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_USUARIOS))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(";");
                if (datos.length > 0 && datos[0].equals(usuarioBuscado)) {
                    return true;
                }
            }
        } catch (IOException e) {
            // Si no existe archivo, usuario no existe
        }
        return false;
    }

    static void iniciarSesion() {
        System.out.print("Usuario: ");
        String usuario = sc.nextLine();
        System.out.print("Contraseña: ");
        String contrasena = sc.nextLine();

        if (usuario.equals("admin") && contrasena.equals("2006")) {
            System.out.println("¡Bienvenido Admin!");
            Admin.menuAdmin(); // Asegúrate de tener esta clase
            return;
        }

        Usuario u = cargarUsuario(usuario, contrasena);
        if (u == null) {
            System.out.println("Usuario o contraseña incorrectos.");
            return;
        }

        // Lógica suscripción
        LocalDate hoy = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        boolean activo = false;
        if (!u.fechaFin.isEmpty()) {
            LocalDate fin = LocalDate.parse(u.fechaFin, fmt);
            activo = hoy.isBefore(fin) || hoy.isEqual(fin);
        }

        if (u.tipoSuscripcion.equalsIgnoreCase("Cancelada") || u.tipoSuscripcion.equalsIgnoreCase("No suscrito")) {
            System.out.println("Tu suscripción está " + u.tipoSuscripcion + ". Debes suscribirte para usar el reproductor.");
            String nuevoMetodoPago = suscripcion.gestionarNuevaCuenta(u.usuario, u.contrasena);
            if (nuevoMetodoPago != null && !nuevoMetodoPago.equalsIgnoreCase("sinmetodo")) {
                actualizarMetodoPago(u.usuario, nuevoMetodoPago);
                System.out.println("Suscripción activada, inicia sesión de nuevo.");
            }
            return;
        }

        if (activo) {
            System.out.println("Inicio de sesión exitoso. ¡Bienvenido, " + u.usuario + "!");
            System.out.println("Tu suscripción está activa hasta: " + u.fechaFin + " (Tipo: " + u.tipoSuscripcion + ")");
            // Llamar reproductor con tus parámetros y actualizar puntos
            int nuevosPuntos = reproductorpeliculas.iniciar(u.usuario, u.puntos, u.fechaInicio, u.fechaFin, u.tipoSuscripcion, u.metodoPago, u.contrasena);
            actualizarUsuario(u.usuario, u.contrasena, u.fechaInicio, u.fechaFin, u.tipoSuscripcion, u.metodoPago, nuevosPuntos);
        } else {
            System.out.println("Tu suscripción ha expirado o no es válida.");
            String nuevoMetodoPago = suscripcion.gestionarNuevaCuenta(u.usuario, u.contrasena);
            if (nuevoMetodoPago != null && !nuevoMetodoPago.equalsIgnoreCase("sinmetodo")) {
                actualizarMetodoPago(u.usuario, nuevoMetodoPago);
                System.out.println("Suscripción activada, inicia sesión de nuevo.");
            }
        }
    }

    static Usuario cargarUsuario(String usuarioBuscado, String contrasenaBuscado) {
        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO_USUARIOS))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] datos = linea.split(";");
                if (datos.length < 2) continue;

                if (datos[0].equals(usuarioBuscado) && datos[1].equals(contrasenaBuscado)) {
                    Usuario u = new Usuario();
                    u.usuario = datos[0];
                    u.contrasena = datos[1];
                    u.fechaInicio = datos.length > 2 ? datos[2] : "";
                    u.fechaFin = datos.length > 3 ? datos[3] : "";
                    u.tipoSuscripcion = datos.length > 4 ? datos[4] : "No suscrito";
                    u.metodoPago = datos.length > 5 ? datos[5] : "sinmetodo";
                    try {
                        u.puntos = Integer.parseInt(datos.length > 6 ? datos[6] : "0");
                    } catch (NumberFormatException e) {
                        u.puntos = 0;
                    }
                    return u;
                }
            }
        } catch (IOException e) {
            System.out.println("Error leyendo archivo usuarios: " + e.getMessage());
        }
        return null;
    }

    public static void actualizarUsuario(String usuario, String contrasena, String fechaInicio, String fechaFin,
                                         String tipoSuscripcion, String metodoPago, int puntos) {
        Path path = Paths.get(ARCHIVO_USUARIOS);
        Path tempPath = Paths.get("usuarios_temp.txt");
        boolean encontrado = false;

        try {
            List<String> lineas = Files.readAllLines(path, StandardCharsets.UTF_8);

            try (BufferedWriter bw = Files.newBufferedWriter(tempPath, StandardCharsets.UTF_8)) {
                for (String linea : lineas) {
                    String[] datos = linea.split(";");
                    if (datos.length > 0 && datos[0].equals(usuario)) {
                        String nuevaLinea = usuario + ";" + contrasena + ";" + fechaInicio + ";" + fechaFin + ";" +
                                tipoSuscripcion + ";" + metodoPago + ";" + puntos;
                        bw.write(nuevaLinea);
                        encontrado = true;
                    } else {
                        bw.write(linea);
                    }
                    bw.newLine();
                }
            }

            try {
                Files.move(tempPath, path, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                System.out.println("No se pudo reemplazar el archivo directamente: " + e.getMessage());
                System.out.println("Intentando eliminar archivo original y mover temporal...");

                try {
                    Files.delete(path);
                    Files.move(tempPath, path);
                    System.out.println("Archivo actualizado correctamente después de eliminar el original.");
                } catch (IOException ex) {
                    System.out.println("Error al eliminar o mover archivo: " + ex.getMessage());
                }
            }

            if (!encontrado) {
                System.out.println("Usuario no encontrado al actualizar.");
                Files.deleteIfExists(tempPath);
            }

        } catch (IOException e) {
            System.out.println("Error actualizando usuario: " + e.getMessage());
            try {
                Files.deleteIfExists(tempPath);
            } catch (IOException ex) {
                // Ignorar
            }
        }
    }

    static void actualizarMetodoPago(String usuario, String metodoPago) {
        String tempFile = "usuarios_temp.txt";
        try {
            List<String> lineas = Files.readAllLines(Paths.get(ARCHIVO_USUARIOS));

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
                for (String linea : lineas) {
                    String[] datos = linea.split(";");
                    if (datos.length > 0 && datos[0].equals(usuario)) {
                        String contrasena = datos.length > 1 ? datos[1] : "";
                        String fechaInicio = datos.length > 2 ? datos[2] : "";
                        String fechaFin = datos.length > 3 ? datos[3] : "";
                        String tipoSuscripcion = datos.length > 4 ? datos[4] : "No suscrito";
                        String puntos = datos.length > 6 ? datos[6] : "0";

                        writer.write(usuario + ";" + contrasena + ";" + fechaInicio + ";" + fechaFin + ";" +
                                tipoSuscripcion + ";" + metodoPago + ";" + puntos);
                    } else {
                        writer.write(linea);
                    }
                    writer.newLine();
                }
            }

            Files.delete(Paths.get(ARCHIVO_USUARIOS));
            Files.move(Paths.get(tempFile), Paths.get(ARCHIVO_USUARIOS));

        } catch (IOException e) {
            System.out.println("Error actualizando método de pago: " + e.getMessage());
        }
    }
}
