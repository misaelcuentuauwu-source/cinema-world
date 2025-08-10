import java.util.Scanner;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.awt.Desktop;
import java.net.URI;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class reproductorpeliculas {
    static Scanner sc = new Scanner(System.in);
    static Set<String> codigosCanjeados = new HashSet<>();

    public static int iniciar(String usuario, int puntos, String fechaInicio, String fechaFin, String tipoSuscripcion, String metodoPago, String contrasena) {
        System.out.println("\n🎬 Bienvenido al cinema world, " + usuario + " 🎬");
        System.out.println("Tienes " + puntos + " puntos.");

        while (true) {
            System.out.println("\nOpciones:");
            System.out.println("1. ver fragmento de pelicula");
            System.out.println("2. Canjear código");
            System.out.println("3. Ver mi perfil");
            System.out.println("4. Cancelar suscripción");
            System.out.println("5. Salir del reproductor");
            System.out.print("Elige una opción: ");
            String opcion = sc.nextLine();

            switch (opcion) {
                case "1":
                    int puntosGanados = escogerTrailer();
                    if (puntosGanados > 0) {
                        puntos += puntosGanados;
                        System.out.println("Ganaste " + puntosGanados + " punto(s)! Total puntos: " + puntos);
                        // No actualizar archivo aquí para evitar bloqueos
                    } else {
                        System.out.println("No ganaste puntos.");
                    }
                    break;

                case "2":
                    int puntosCanje = canjearCodigo();
                    if (puntosCanje > 0) {
                        puntos += puntosCanje;
                        System.out.println("Total puntos: " + puntos);
                        // No actualizar archivo aquí para evitar bloqueos
                    }
                    break;

                case "3":
                    mostrarPerfil(usuario, puntos, fechaInicio, fechaFin, tipoSuscripcion, metodoPago);
                    break;

                case "4":
                    System.out.println("Cancelando suscripción...");
                    tipoSuscripcion = "Cancelada";
                    metodoPago = "sinmetodo";
                    fechaInicio = "";
                    fechaFin = "";

                    // Actualiza el archivo justo aquí, porque es un cambio importante
                    cw.actualizarUsuario(usuario, contrasena, fechaInicio, fechaFin, tipoSuscripcion, metodoPago, puntos);

                    System.out.println("Suscripción cancelada.");

                    String nuevoMetodoPago = suscripcion.gestionarNuevaCuenta(usuario, contrasena);

                    if (!nuevoMetodoPago.equals("sinmetodo")) {
                        try {
                            List<String> lineas = Files.readAllLines(Paths.get("usuarios.txt"));
                            for (String linea : lineas) {
                                String[] datos = linea.split(";");
                                if (datos[0].equals(usuario)) {
                                    fechaInicio = datos[2];
                                    fechaFin = datos[3];
                                    tipoSuscripcion = datos[4];
                                    metodoPago = datos[5];
                                    break;
                                }
                            }
                        } catch (IOException e) {
                            System.out.println("Error leyendo datos actualizados: " + e.getMessage());
                        }
                        System.out.println("Nueva suscripción activada: " + tipoSuscripcion + ", método: " + metodoPago);
                    } else {
                        System.out.println("No se activó nueva suscripción.");
                    }

                    return puntos;

                case "5":
                    System.out.println("Saliendo del reproductor...");
                    // Actualiza el archivo al salir guardando los puntos y demás
                    cw.actualizarUsuario(usuario, contrasena, fechaInicio, fechaFin, tipoSuscripcion, metodoPago, puntos);
                    return puntos;

                default:
                    System.out.println("Opción inválida. Intenta de nuevo.");
            }
        }
    }

    private static int escogerTrailer() {
        System.out.println("\n--- Escoger tráiler ---");
        System.out.println("1. Ver online");
        System.out.println("2. Ver nativo");
        System.out.print("Elige una opción: ");
        String opcion = sc.nextLine();

        switch (opcion) {
            case "1":
                return verTrailerOnline() ? 1 : 0;
            case "2":
                return verTrailerNativo() ? 1 : 0;
            default:
                System.out.println("Opción inválida. No se suman puntos.");
                return 0;
        }
    }

    private static boolean verTrailerOnline() {
        System.out.println("\nElige el tráiler para ver online:");
        String[] trailers = {"spiderman", "Minecraft", "Thunderbolts"};
        String[] urls = {
            "https://drive.google.com/file/d/10UUSQHiBZ48qA83VuoYPw-9AqfIaiKBn/view?usp=sharing",
            "https://www.youtube.com/watch?v=trailerB",
            "https://www.youtube.com/watch?v=trailerC"
        };
        for (int i = 0; i < trailers.length; i++) {
            System.out.println((i + 1) + ". " + trailers[i]);
        }
        System.out.print("Elige una opción: ");
        String opcion = sc.nextLine();

        try {
            int index = Integer.parseInt(opcion) - 1;
            if (index >= 0 && index < trailers.length) {
                System.out.println("Abriendo navegador para: " + trailers[index]);
                abrirNavegador(urls[index]);
                return true;
            }
        } catch (NumberFormatException e) {
            // Ignorar
        }
        System.out.println("Opción inválida. No se reprodujo ningún tráiler.");
        return false;
    }

    private static boolean verTrailerNativo() {
        System.out.println("\nElige el tráiler para ver nativo:");

        String[][] trailers = {
            {"Thunderbolts", "cositas/clips/th.mp4"},
            {"Trailer B", "cositas/clips/sp.mp4"},
            {"Trailer C", "cositas/clips/mn.mp4"}
        };

        for (int i = 0; i < trailers.length; i++) {
            System.out.println((i + 1) + ". " + trailers[i][0]);
        }

        System.out.print("Selecciona un número: ");
        String opcion = sc.nextLine();

        try {
            int index = Integer.parseInt(opcion) - 1;
            if (index >= 0 && index < trailers.length) {
                String nombre = trailers[index][0];
                String ruta = trailers[index][1];
                File video = new File(ruta);

                if (!video.exists()) {
                    System.out.println("El archivo no existe: " + ruta);
                    return false;
                }

                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().open(video);
                        System.out.println("Reproduciendo: " + nombre);
                        return true;
                    } catch (IOException e) {
                        System.out.println("No se pudo abrir el video: " + e.getMessage());
                    }
                } else {
                    System.out.println("Tu sistema no soporta la apertura de archivos desde Java.");
                }
            } else {
                System.out.println("Opción fuera de rango.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Opción inválida. Debes ingresar un número.");
        }

        return false;
    }

    private static void abrirNavegador(String url) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                System.out.println("No se pudo abrir el navegador automáticamente.");
                System.out.println("Visita manualmente este enlace: " + url);
            }
        } catch (Exception e) {
            System.out.println("Error al abrir navegador: " + e.getMessage());
        }
    }

    private static int canjearCodigo() {
        System.out.print("\nIntroduce el código a canjear: ");
        String codigo = sc.nextLine().trim();

        String[] codigosValidos = {"CINE50", "MOVIE50", "FILM50"};

        for (String valido : codigosValidos) {
            if (valido.equalsIgnoreCase(codigo)) {
                if (codigosCanjeados.contains(valido.toUpperCase())) {
                    System.out.println("Este código ya fue canjeado anteriormente.");
                    return 0;
                } else {
                    codigosCanjeados.add(valido.toUpperCase());
                    System.out.println("Código válido! Has recibido 50 puntos.");
                    return 50;
                }
            }
        }

        System.out.println("Código inválido.");
        return 0;
    }

    private static void mostrarPerfil(String usuario, int puntos, String fechaInicio, String fechaFin, String tipoSuscripcion, String metodoPago) {
        System.out.println("\n--- Perfil de " + usuario + " ---");
        System.out.println("Puntos acumulados: " + puntos);
        System.out.println("Suscripción: " + tipoSuscripcion);
        System.out.println("Válida desde: " + fechaInicio + " hasta " + fechaFin);
        System.out.println("Método de pago: " + metodoPago);
    }
}
