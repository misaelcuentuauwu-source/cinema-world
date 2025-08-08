import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

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
            writer.write(usuario + "," + contrasena + ",,,No suscrito");
            writer.newLine();
            System.out.println("✅ Usuario creado correctamente. Estado: No suscrito.");
        } catch (IOException e) {
            System.out.println("❌ Error al guardar el usuario.");
            return;
        }

        suscripcion.gestionarNuevaCuenta(usuario, contrasena);
    }

    static boolean usuarioExiste(String usuarioBuscado) {
        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_USUARIOS))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length > 0 && datos[0].equals(usuarioBuscado)) {
                    return true;
                }
            }
        } catch (IOException e) {
            // Si el archivo no existe aún, se asume que el usuario no existe
        }
        return false;
    }

    static void iniciarSesion() {
        System.out.print("Usuario: ");
        String usuario = sc.nextLine();
        System.out.print("Contraseña: ");
        String contrasena = sc.nextLine();

        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO_USUARIOS))) {
            boolean encontrado = false;
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] datos = linea.split(",");
                if (datos.length < 2) continue; // Saltar datos corruptos

                String user = datos[0];
                String pass = datos[1];

                if (usuario.equals(user) && contrasena.equals(pass)) {
                    encontrado = true;
                    System.out.println("Inicio de sesión exitoso. ¡Bienvenido, " + usuario + "!");

                    String fechaFin = datos.length > 3 ? datos[3] : "";
                    String tipo = datos.length > 4 ? datos[4] : "No suscrito";

                    LocalDate hoy = LocalDate.now();
                    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    boolean activo = false;

                    if (!tipo.equalsIgnoreCase("No suscrito") && !fechaFin.isEmpty()) {
                        LocalDate fin = LocalDate.parse(fechaFin, fmt);
                        activo = hoy.isBefore(fin) || hoy.isEqual(fin);
                    }

                    if (activo) {
                        System.out.println("Tu suscripción está activa hasta: " + fechaFin + " (Tipo: " + tipo + ")");
                        reproductorpeliculas.iniciar(usuario); // Llamar al reproductor
                    } else {
                        System.out.println("No tienes una suscripción activa.");
                        suscripcion.gestionarNuevaCuenta(usuario, contrasena);
                    }
                    break;
                }
            }

            if (!encontrado) {
                System.out.println("Usuario o contraseña incorrectos.");
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo de usuarios.");
        }
    }
}
