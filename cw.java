// Importamos las clases necesarias para manejar archivos, fechas, entrada de usuario, etc.
import java.io.*; // Para manejar archivos, lectura y escritura
import java.nio.charset.StandardCharsets; // Para especificar codificación UTF-8 al leer/escribir archivos
import java.nio.file.*; // Para manejar rutas y operaciones avanzadas con archivos
import java.time.LocalDate; // Para manejar fechas sin tiempo (solo año, mes, día)
import java.time.format.DateTimeFormatter; // Para dar formato y parsear fechas
import java.util.List; // Para manejar listas (colecciones de líneas de archivos)
import java.util.Scanner; // Para leer entrada de usuario desde consola

// Declaramos la clase principal del programa
public class cw {
    // Constante con el nombre del archivo donde se guardan los usuarios
    static final String ARCHIVO_USUARIOS = "usuarios.txt";
    // Scanner para leer entrada de usuario desde consola
    static Scanner sc = new Scanner(System.in);

    // Clase interna para almacenar los datos de un usuario
    static class Usuario {
        String usuario;          // Nombre de usuario
        String contrasena;       // Contraseña del usuario
        String fechaInicio;      // Fecha de inicio de la suscripción
        String fechaFin;         // Fecha fin de la suscripción
        String tipoSuscripcion;  // Tipo de suscripción (ejemplo: "No suscrito", "Cancelada", etc)
        String metodoPago;       // Método de pago usado
        int puntos;              // Puntos acumulados del usuario (pueden ser usados para recompensas)
    }

    // Método principal que se ejecuta al iniciar el programa
    public static void main(String[] args) {
        // Bucle infinito para mostrar el menú hasta que el usuario decida salir
        while (true) {
            System.out.println("\n--- MENÚ ---");
            System.out.println("1. Crear usuario");    // Opción para registrar nuevo usuario
            System.out.println("2. Iniciar sesión");    // Opción para ingresar con usuario existente
            System.out.println("3. Salir");              // Opción para terminar programa
            System.out.print("Elige una opción: ");
            // Leemos la opción elegida y la evaluamos
            switch (sc.nextLine()) {
                case "1":
                    crearUsuario(); // Llama a método que crea un nuevo usuario
                    break;
                case "2":
                    iniciarSesion(); // Llama a método que permite iniciar sesión
                    break;
                case "3":
                    System.out.println("Saliendo del sistema...");
                    return; // Termina el programa
                default:
                    System.out.println("Opción no válida. Intenta de nuevo."); // Opción inválida
            }
        }
    }

    // Método para crear un nuevo usuario y guardarlo en archivo
    static void crearUsuario() {
        System.out.print("Ingresa un nombre de usuario: ");
        String usuario = sc.nextLine().trim(); // Leemos y limpiamos espacios
        if (usuario.isEmpty()) { // Validamos que no esté vacío
            System.out.println("❌ Debes poner algo en el nombre de usuario.");
            return; // Salimos si es inválido
        }

        System.out.print("Ingresa una contraseña: ");
        String contrasena = sc.nextLine().trim();
        if (contrasena.isEmpty()) { // Validamos que la contraseña no esté vacía
            System.out.println("❌ Debes poner algo en la contraseña.");
            return;
        }

        // Verificamos si el usuario ya existe para evitar duplicados
        if (usuarioExiste(usuario)) {
            System.out.println("❌ El usuario ya existe. Intenta con otro nombre.");
            return;
        }

        // Abrimos el archivo para agregar el nuevo usuario al final (modo append)
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO_USUARIOS, true))) {
            // Guardamos datos separados por ";" en formato:
            // usuario;contraseña;fechaInicio;fechaFin;tipoSuscripcion;metodoPago;puntos
            // Al crear, las fechas quedan vacías y la suscripción como "No suscrito"
            writer.write(usuario + ";" + contrasena + ";;;" + "No suscrito" + ";" + "sinmetodo" + ";0");
            writer.newLine(); // Salto de línea para siguiente registro
            System.out.println("✅ Usuario creado correctamente. Estado: No suscrito.");
        } catch (IOException e) {
            System.out.println("❌ Error al guardar el usuario."); // Si hay problema con archivo
            return;
        }

        // Aquí se llama a la gestión de suscripción (debes tener esa clase y método definidos)
        String metodoPago = suscripcion.gestionarNuevaCuenta(usuario, contrasena);
        if (metodoPago != null && !metodoPago.equalsIgnoreCase("sinmetodo")) {
            actualizarMetodoPago(usuario, metodoPago); // Actualiza método de pago en archivo
        }
    }

    // Método para verificar si un usuario ya existe en el archivo
    static boolean usuarioExiste(String usuarioBuscado) {
        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_USUARIOS))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(";"); // Separa la línea en campos por ";"
                if (datos.length > 0 && datos[0].equals(usuarioBuscado)) {
                    return true; // Usuario encontrado
                }
            }
        } catch (IOException e) {
            // Si no existe el archivo, consideramos que no existe el usuario
        }
        return false; // Usuario no encontrado
    }

    // Método para iniciar sesión, validando usuario y contraseña
    static void iniciarSesion() {
        System.out.print("Usuario: ");
        String usuario = sc.nextLine();
        System.out.print("Contraseña: ");
        String contrasena = sc.nextLine();

        // Validación especial para administrador hardcodeado
        if (usuario.equals("admin") && contrasena.equals("2006")) {
            System.out.println("¡Bienvenido Admin!");
            Admin.menuAdmin(); // Llama a menú especial de administrador (debes definir esta clase)
            return;
        }

        // Carga los datos del usuario si coincide usuario y contraseña
        Usuario u = cargarUsuario(usuario, contrasena);
        if (u == null) {
            System.out.println("Usuario o contraseña incorrectos.");
            return;
        }

        LocalDate hoy = LocalDate.now(); // Fecha actual
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Formato para fechas

        // Validamos que el usuario tenga fechas de suscripción válidas
        if (!u.fechaInicio.isEmpty() && !u.fechaFin.isEmpty()) {
            // Convertimos strings de fechas a objetos LocalDate para comparar
            LocalDate fechaInicio = LocalDate.parse(u.fechaInicio, fmt);
            LocalDate fechaFin = LocalDate.parse(u.fechaFin, fmt);

            // Detectamos si la fecha del sistema está adelantada (hoy < fechaInicio)
            if (hoy.isBefore(fechaInicio)) {
                System.out.println("Error: La fecha del sistema está adelantada respecto a tu suscripción.");
                System.out.println("Se eliminará tu cuenta para evitar fraudes o errores.");
                eliminarCuenta(u.usuario); // Borra cuenta por seguridad
                System.out.println("Por favor, crea una nueva cuenta y suscríbete nuevamente.");
                return;
            }

            // Verificamos si la suscripción está activa (fechaInicio <= hoy <= fechaFin)
            boolean activo = (hoy.isAfter(fechaInicio) || hoy.isEqual(fechaInicio)) &&
                             (hoy.isBefore(fechaFin) || hoy.isEqual(fechaFin));

            if (!activo) { // Si no está activo, pedir renovar suscripción
                System.out.println("Tu suscripción ha expirado o no es válida.");
                String nuevoMetodoPago = suscripcion.gestionarNuevaCuenta(u.usuario, u.contrasena);
                if (nuevoMetodoPago != null && !nuevoMetodoPago.equalsIgnoreCase("sinmetodo")) {
                    actualizarMetodoPago(u.usuario, nuevoMetodoPago);
                    System.out.println("Suscripción activada, inicia sesión de nuevo.");
                }
                return;
            }

            // Suscripción válida: mostramos mensaje de bienvenida
            System.out.println("Inicio de sesión exitoso. ¡Bienvenido, " + u.usuario + "!");
            System.out.println("Tu suscripción está activa hasta: " + u.fechaFin + " (Tipo: " + u.tipoSuscripcion + ")");

            // Llamamos al reproductor de películas para iniciar sesión y actualizar puntos
            int nuevosPuntos = reproductorpeliculas.iniciar(u.usuario, u.puntos, u.fechaInicio, u.fechaFin, u.tipoSuscripcion, u.metodoPago, u.contrasena);

            // Actualizamos los datos del usuario en el archivo, incluyendo nuevos puntos
            actualizarUsuario(u.usuario, u.contrasena, u.fechaInicio, u.fechaFin, u.tipoSuscripcion, u.metodoPago, nuevosPuntos);

        } else {
            // Caso donde no hay fechas válidas
            if (u.tipoSuscripcion.equalsIgnoreCase("Cancelada") || u.tipoSuscripcion.equalsIgnoreCase("No suscrito")) {
                System.out.println("Tu suscripción está " + u.tipoSuscripcion + ". Debes suscribirte para usar el reproductor.");
                String nuevoMetodoPago = suscripcion.gestionarNuevaCuenta(u.usuario, u.contrasena);
                if (nuevoMetodoPago != null && !nuevoMetodoPago.equalsIgnoreCase("sinmetodo")) {
                    actualizarMetodoPago(u.usuario, nuevoMetodoPago);
                    System.out.println("Suscripción activada, inicia sesión de nuevo.");
                }
            } else {
                System.out.println("Error en las fechas de suscripción. Por favor, contacta soporte.");
            }
        }
    }

    // Método para eliminar completamente la cuenta del usuario (lo borra del archivo)
    static void eliminarCuenta(String usuario) {
        Path path = Paths.get(ARCHIVO_USUARIOS);           // Ruta del archivo original
        Path tempPath = Paths.get("usuarios_temp.txt");     // Archivo temporal para reescribir

        try {
            // Leemos todas las líneas del archivo original
            List<String> lineas = Files.readAllLines(path, StandardCharsets.UTF_8);

            // Abrimos el archivo temporal para escribir
            try (BufferedWriter bw = Files.newBufferedWriter(tempPath, StandardCharsets.UTF_8)) {
                for (String linea : lineas) {
                    String[] datos = linea.split(";"); // Separamos línea por campos
                    // Solo copiamos al temporal las líneas que no coinciden con el usuario a borrar
                    if (datos.length > 0 && !datos[0].equals(usuario)) {
                        bw.write(linea);
                        bw.newLine();
                    }
                }
            }

            // Reemplazamos archivo original por el temporal (sin el usuario eliminado)
            Files.move(tempPath, path, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Cuenta eliminada correctamente.");
        } catch (IOException e) {
            System.out.println("Error eliminando cuenta: " + e.getMessage());
        }
    }

    // Método para cargar un usuario desde archivo si usuario y contraseña coinciden
    static Usuario cargarUsuario(String usuarioBuscado, String contrasenaBuscado) {
        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO_USUARIOS))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                System.out.println("Leyendo línea: '" + linea + "'"); // DEBUG: mostrar línea leída

                if (linea.trim().isEmpty()) continue; // Ignorar líneas vacías

                String[] datos = linea.split(";"); // Dividir línea en campos separados por ";"
                if (datos.length < 2) {
                    System.out.println("Línea ignorada por formato incorrecto (menos de 2 campos): " + linea);
                    continue; // Saltar línea si formato no es correcto
                }

                // Si usuario y contraseña coinciden con lo buscado, creamos objeto Usuario
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
                        u.puntos = 0; // Si no es número, asignar 0 puntos
                    }
                    return u; // Retornamos usuario cargado
                }
            }
        } catch (IOException e) {
            System.out.println("Error leyendo archivo usuarios: " + e.getMessage());
        }
        return null; // No se encontró usuario o hubo error
    }

    // Método para actualizar los datos de un usuario en el archivo
    public static void actualizarUsuario(String usuario, String contrasena, String fechaInicio, String fechaFin,
                                         String tipoSuscripcion, String metodoPago, int puntos) {
        Path path = Paths.get(ARCHIVO_USUARIOS);        // Archivo original
        Path tempPath = Paths.get("usuarios_temp.txt");  // Archivo temporal para reescribir
        boolean encontrado = false;                       // Para saber si usuario fue encontrado y actualizado

        try {
            List<String> lineas = Files.readAllLines(path, StandardCharsets.UTF_8);

            // Abrimos temporal para escritura
            try (BufferedWriter bw = Files.newBufferedWriter(tempPath, StandardCharsets.UTF_8)) {
                for (String linea : lineas) {
                    String[] datos = linea.split(";");
                    // Si línea corresponde al usuario que queremos actualizar
                    if (datos.length > 0 && datos[0].equals(usuario)) {
                        // Formamos línea nueva con datos actualizados
                        String nuevaLinea = usuario + ";" + contrasena + ";" + fechaInicio + ";" + fechaFin + ";" +
                                tipoSuscripcion + ";" + metodoPago + ";" + puntos;
                        bw.write(nuevaLinea);
                        encontrado = true;
                    } else {
                        // Copiamos línea sin cambio
                        bw.write(linea);
                    }
                    bw.newLine();
                }
            }

            // Intentamos reemplazar el archivo original por el temporal
            try {
                Files.move(tempPath, path, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                System.out.println("No se pudo reemplazar el archivo directamente: " + e.getMessage());
                System.out.println("Intentando eliminar archivo original y mover temporal...");

                // Si falla, borramos archivo original y movemos temporal manualmente
                try {
                    Files.delete(path);
                    Files.move(tempPath, path);
                    System.out.println("Archivo actualizado correctamente después de eliminar el original.");
                } catch (IOException ex) {
                    System.out.println("Error al eliminar o mover archivo: " + ex.getMessage());
                }
            }

            if (!encontrado) { // Si no encontró usuario para actualizar
                System.out.println("Usuario no encontrado al actualizar.");
                Files.deleteIfExists(tempPath); // Borra archivo temporal si existe
            }

        } catch (IOException e) {
            System.out.println("Error actualizando usuario: " + e.getMessage());
            try {
                Files.deleteIfExists(tempPath);
            } catch (IOException ex) {
                // Ignorar error al borrar temp
            }
        }
    }

    // Método para actualizar solo el método de pago de un usuario en el archivo
    static void actualizarMetodoPago(String usuario, String metodoPago) {
        String tempFile = "usuarios_temp.txt"; // Archivo temporal
        try {
            List<String> lineas = Files.readAllLines(Paths.get(ARCHIVO_USUARIOS));

            // Abrimos archivo temporal para escribir las líneas actualizadas
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
                for (String linea : lineas) {
                    String[] datos = linea.split(";");
                    // Si línea corresponde al usuario, actualizamos método de pago
                    if (datos.length > 0 && datos[0].equals(usuario)) {
                        String contrasena = datos.length > 1 ? datos[1] : "";
                        String fechaInicio = datos.length > 2 ? datos[2] : "";
                        String fechaFin = datos.length > 3 ? datos[3] : "";
                        String tipoSuscripcion = datos.length > 4 ? datos[4] : "No suscrito";
                        String puntos = datos.length > 6 ? datos[6] : "0";

                        // Reescribimos línea con método de pago actualizado
                        writer.write(usuario + ";" + contrasena + ";" + fechaInicio + ";" + fechaFin + ";" +
                                tipoSuscripcion + ";" + metodoPago + ";" + puntos);
                    } else {
                        // Copiamos línea sin cambio
                        writer.write(linea);
                    }
                    writer.newLine();
                }
            }

            // Reemplazamos archivo original por el temporal con cambio
            Files.delete(Paths.get(ARCHIVO_USUARIOS));
            Files.move(Paths.get(tempFile), Paths.get(ARCHIVO_USUARIOS));

        } catch (IOException e) {
            System.out.println("Error actualizando método de pago: " + e.getMessage());
        }
    }
}
