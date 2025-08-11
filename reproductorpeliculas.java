// Importamos las clases necesarias para el programa
import java.util.Scanner;          // Para leer entradas del usuario
import java.util.HashSet;          // Para usar conjuntos que no permiten duplicados
import java.util.List;             // Para manejar listas
import java.util.Set;              // Interfaz para conjuntos
import java.awt.Desktop;           // Para abrir archivos o URLs con el sistema operativo
import java.net.URI;               // Para manejar direcciones web
import java.io.BufferedWriter;    // Para escribir archivos con buffer
import java.io.File;               // Para manejar archivos
import java.io.IOException;        // Para manejar excepciones de entrada/salida
import java.nio.file.Files;        // Para operaciones con archivos y directorios
import java.nio.file.Paths;        // Para especificar rutas en el sistema de archivos

// Declaramos la clase principal reproductorpeliculas
public class reproductorpeliculas {
    // Creamos un objeto Scanner estático para leer la entrada del usuario desde consola
    static Scanner sc = new Scanner(System.in);
    // Creamos un conjunto para guardar códigos canjeados y evitar duplicados
    static Set<String> codigosCanjeados = new HashSet<>();

    // Método principal que inicia el reproductor, recibe datos del usuario
    public static int iniciar(String usuario, int puntos, String fechaInicio, String fechaFin, String tipoSuscripcion, String metodoPago, String contrasena) {
        // Saludo personalizado al usuario con puntos actuales
        System.out.println("\n🎬 Bienvenido al cinema world, " + usuario + " 🎬");
        System.out.println("Tienes " + puntos + " puntos.");

        // Ciclo infinito para mostrar el menú hasta que el usuario decida salir
        while (true) {
            // Mostramos las opciones disponibles para el usuario
            System.out.println("\nOpciones:");
            System.out.println("1. ver fragmento de pelicula");
            System.out.println("2. Canjear código");
            System.out.println("3. Ver mi perfil");
            System.out.println("4. Cancelar suscripción");
            System.out.println("5. Salir del reproductor");
            System.out.println("6. Canjear puntos por productos"); // Nueva opción agregada
            System.out.print("Elige una opción: ");
            // Leemos la opción que el usuario ingresa
            String opcion = sc.nextLine();

            // Evaluamos la opción con switch-case
            switch (opcion) {
                case "1":
                    // Llamamos a escogerTrailer para ver un tráiler y ganar puntos
                    int puntosGanados = escogerTrailer();
                    if (puntosGanados > 0) {
                        puntos += puntosGanados; // Sumamos puntos ganados
                        System.out.println("Ganaste " + puntosGanados + " punto(s)! Total puntos: " + puntos);
                        // No actualizamos el archivo aquí para evitar bloqueos
                    } else {
                        System.out.println("No ganaste puntos.");
                    }
                    break;

                case "2":
                    // Llamamos a canjearCodigo para que el usuario ingrese un código
                    int puntosCanje = canjearCodigo();
                    if (puntosCanje > 0) {
                        puntos += puntosCanje; // Sumamos puntos si el código es válido
                        System.out.println("Total puntos: " + puntos);
                        // No actualizamos archivo aquí para evitar bloqueos
                    }
                    break;

                case "3":
                    // Mostramos el perfil del usuario con sus datos
                    mostrarPerfil(usuario, puntos, fechaInicio, fechaFin, tipoSuscripcion, metodoPago);
                    break;

                case "4":
                    // Preguntamos si quiere mantener la cuenta sin suscripción
                    System.out.print("¿Quieres mantener tu cuenta sin suscripción? (sí/no): ");
                    String respuesta = sc.nextLine().trim().toLowerCase();

                    if (respuesta.equals("sí") || respuesta.equals("si")) {
                        System.out.println("Cancelando suscripción y manteniendo la cuenta...");

                        // Actualizamos variables para indicar que la suscripción se canceló
                        tipoSuscripcion = "Cancelada";
                        metodoPago = "sinmetodo";
                        fechaInicio = "";
                        fechaFin = "";

                        // Actualizamos el archivo con los nuevos datos del usuario
                        cw.actualizarUsuario(usuario, contrasena, fechaInicio, fechaFin, tipoSuscripcion, metodoPago, puntos);

                        System.out.println("Suscripción cancelada. Tu cuenta sigue activa sin suscripción.");

                        // Intentamos renovar la suscripción con un nuevo método de pago
                        String nuevoMetodoPago = suscripcion.gestionarNuevaCuenta(usuario, contrasena);

                        if (!nuevoMetodoPago.equalsIgnoreCase("sinmetodo")) {
                            // Si se activó nuevo método, actualizamos los datos en memoria leyendo el archivo
                            try {
                                List<String> lineas = Files.readAllLines(Paths.get("usuarios.txt"));
                                for (String linea : lineas) {
                                    String[] datos = linea.split(";");
                                    if (datos[0].equals(usuario)) {
                                        // Leemos fechas y tipo de suscripción del archivo
                                        fechaInicio = datos.length > 2 ? datos[2] : "";
                                        fechaFin = datos.length > 3 ? datos[3] : "";
                                        tipoSuscripcion = datos.length > 4 ? datos[4] : "No suscrito";
                                        metodoPago = datos.length > 5 ? datos[5] : "sinmetodo";
                                        break;
                                    }
                                }
                            } catch (IOException e) {
                                System.out.println("Error leyendo datos actualizados: " + e.getMessage());
                            }
                            System.out.println("Nueva suscripción activada: " + tipoSuscripcion + ", método: " + metodoPago);
                        } else {
                            System.out.println("No se activó nueva suscripción. Estado: " + tipoSuscripcion);
                        }

                    } else if (respuesta.equals("no")) {
                        // Si decide eliminar la cuenta, procedemos a borrarla
                        System.out.println("Eliminando completamente la cuenta...");

                        try {
                            // Leemos todas las líneas del archivo usuarios.txt
                            List<String> lineas = Files.readAllLines(Paths.get("usuarios.txt"));
                            // Abrimos un archivo temporal para escribir las líneas que no correspondan al usuario
                            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("usuarios_temp.txt"))) {
                                for (String linea : lineas) {
                                    String[] datos = linea.split(";");
                                    if (!datos[0].equals(usuario)) {
                                        writer.write(linea);
                                        writer.newLine();
                                    }
                                }
                            }
                            // Borramos el archivo original y renombramos el temporal
                            Files.delete(Paths.get("usuarios.txt"));
                            Files.move(Paths.get("usuarios_temp.txt"), Paths.get("usuarios.txt"));

                            System.out.println("Cuenta eliminada correctamente.");
                            return puntos; // Salimos del método tras eliminar la cuenta

                        } catch (IOException e) {
                            System.out.println("Error al eliminar la cuenta: " + e.getMessage());
                        }
                    } else {
                        // Si la respuesta no es válida, no hacemos nada
                        System.out.println("Respuesta no reconocida. No se realizaron cambios.");
                    }

                    break;

                case "5":
                    // Opción para salir del reproductor
                    System.out.println("Saliendo del reproductor...");
                    // Guardamos los datos del usuario en archivo antes de salir
                    cw.actualizarUsuario(usuario, contrasena, fechaInicio, fechaFin, tipoSuscripcion, metodoPago, puntos);
                    return puntos; // Salimos del método

                case "6": // Nueva opción para canjear puntos por productos
                    int puntosRestantes = canjearPuntos(puntos);
                    if (puntosRestantes != puntos) {
                        puntos = puntosRestantes; // Actualizamos puntos tras canjear
                        System.out.println("Ahora te quedan " + puntos + " puntos.");
                    }
                    break;

                default:
                    // Si la opción no está contemplada, mostramos mensaje de error
                    System.out.println("Opción inválida. Intenta de nuevo.");
            }
        }
    }

    // Método para canjear puntos por productos como palomitas, nieve o soda
    private static int canjearPuntos(int puntosActuales) {
        System.out.println("\n--- Canjear puntos por productos ---");
        System.out.println("Tienes " + puntosActuales + " puntos disponibles.");
        System.out.println("Productos disponibles:");
        System.out.println("1. Palomitas (10 puntos)");
        System.out.println("2. Nieve (15 puntos)");
        System.out.println("3. Soda (12 puntos)");
        System.out.println("4. Cancelar");
        System.out.print("Elige un producto para canjear: ");
        // Leemos la opción del usuario
        String opcion = sc.nextLine();

        int costo = 0;        // Variable para guardar el costo en puntos
        String producto = ""; // Nombre del producto elegido

        // Evaluamos qué producto eligió el usuario
        switch (opcion) {
            case "1":
                costo = 10;
                producto = "Palomitas";
                break;
            case "2":
                costo = 15;
                producto = "Nieve";
                break;
            case "3":
                costo = 12;
                producto = "Soda";
                break;
            case "4":
                // Si elige cancelar, regresamos los puntos sin cambios
                System.out.println("Canje cancelado.");
                return puntosActuales;
            default:
                // Opción inválida, regresamos puntos sin cambios
                System.out.println("Opción no válida.");
                return puntosActuales;
        }

        // Verificamos si el usuario tiene suficientes puntos para canjear el producto
        if (puntosActuales >= costo) {
            System.out.println("Has canjeado " + costo + " puntos por " + producto + ". ¡Disfrútalo!");
            return puntosActuales - costo; // Restamos puntos y los regresamos
        } else {
            // Si no hay puntos suficientes, mostramos mensaje y no descontamos
            System.out.println("No tienes suficientes puntos para canjear " + producto + ".");
            return puntosActuales;
        }
    }

    // Método para elegir y ver un tráiler, devuelve puntos ganados
    private static int escogerTrailer() {
        System.out.println("\n--- Escoger tráiler ---");
        System.out.println("1. Ver online");
        System.out.println("2. Ver nativo");
        System.out.print("Elige una opción: ");
        String opcion = sc.nextLine();

        // Dependiendo de la opción, llamamos a verTrailerOnline o verTrailerNativo
        switch (opcion) {
            case "1":
                return verTrailerOnline() ? 1 : 0; // Devuelve 1 si se vio con éxito, 0 si no
            case "2":
                return verTrailerNativo() ? 1 : 0;
            default:
                System.out.println("Opción inválida. No se suman puntos.");
                return 0;
        }
    }

    // Método para abrir un tráiler online en el navegador
    private static boolean verTrailerOnline() {
        System.out.println("\nElige el tráiler para ver online:");
        // Array con nombres de trailers
        String[] trailers = {"spiderman", "Minecraft", "Thunderbolts"};
        // URLs correspondientes a cada tráiler
        String[] urls = {
            "https://drive.google.com/file/d/10UUSQHiBZ48qA83VuoYPw-9AqfIaiKBn/view?usp=sharing",
            "https://www.youtube.com/watch?v=trailerB",
            "https://www.youtube.com/watch?v=trailerC"
        };
        // Mostramos las opciones de trailers al usuario
        for (int i = 0; i < trailers.length; i++) {
            System.out.println((i + 1) + ". " + trailers[i]);
        }
        System.out.print("Elige una opción: ");
        String opcion = sc.nextLine();

        try {
            // Convertimos la opción a índice
            int index = Integer.parseInt(opcion) - 1;
            if (index >= 0 && index < trailers.length) {
                System.out.println("Abriendo navegador para: " + trailers[index]);
                abrirNavegador(urls[index]); // Abrimos la URL en el navegador
                return true; // Indica éxito
            }
        } catch (NumberFormatException e) {
            // Si no es número, ignoramos y sigue el flujo
        }
        System.out.println("Opción inválida. No se reprodujo ningún tráiler.");
        return false; // Indica que no se pudo reproducir
    }

    // Método para abrir un tráiler que está en el sistema de archivos local
    private static boolean verTrailerNativo() {
        System.out.println("\nElige el tráiler para ver nativo:");

        // Array 2D con nombre y ruta del archivo
        String[][] trailers = {
            {"Thunderbolts", "cositas/clips/th.mp4"},
            {"Spiderman No way home", "cositas/clips/sp.mp4"},
            {"Minecraft la pelicula", "cositas/clips/mn.mp4"}
        };

        // Mostramos las opciones al usuario
        for (int i = 0; i < trailers.length; i++) {
            System.out.println((i + 1) + ". " + trailers[i][0]);
        }

        System.out.print("Selecciona un número: ");
        String opcion = sc.nextLine();

        try {
            int index = Integer.parseInt(opcion) - 1;
            if (index >= 0 && index < trailers.length) {
                String nombre = trailers[index][0];  // Nombre del trailer
                String ruta = trailers[index][1];    // Ruta del archivo
                File video = new File(ruta);

                // Verificamos si el archivo existe
                if (!video.exists()) {
                    System.out.println("El archivo no existe: " + ruta);
                    return false;
                }

                // Verificamos que el sistema soporte abrir archivos
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().open(video); // Abrimos el video con el reproductor por defecto
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

        return false; // No se pudo reproducir
    }

    // Método para abrir una URL en el navegador predeterminado
    private static void abrirNavegador(String url) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url)); // Abrimos el navegador con la URL
            } else {
                System.out.println("No se pudo abrir el navegador automáticamente.");
                System.out.println("Visita manualmente este enlace: " + url);
            }
        } catch (Exception e) {
            System.out.println("Error al abrir navegador: " + e.getMessage());
        }
    }

    // Método para canjear códigos por puntos
    private static int canjearCodigo() {
        System.out.print("\nIntroduce el código a canjear: ");
        String codigo = sc.nextLine().trim(); // Leemos y limpiamos el código

        // Lista de códigos válidos
        String[] codigosValidos = {"CINE50", "MOVIE50", "FILM50"};

        // Recorremos los códigos para verificar si el ingresado es válido
        for (String valido : codigosValidos) {
            if (valido.equalsIgnoreCase(codigo)) {
                // Verificamos que el código no haya sido usado antes
                if (codigosCanjeados.contains(valido.toUpperCase())) {
                    System.out.println("Este código ya fue canjeado anteriormente.");
                    return 0;
                } else {
                    // Si es válido y no usado, agregamos al conjunto y damos puntos
                    codigosCanjeados.add(valido.toUpperCase());
                    System.out.println("Código válido! Has recibido 50 puntos.");
                    return 50;
                }
            }
        }

        // Si no es válido, mostramos mensaje
        System.out.println("Código inválido.");
        return 0;
    }

    // Método para mostrar el perfil del usuario con sus datos y puntos
    private static void mostrarPerfil(String usuario, int puntos, String fechaInicio, String fechaFin, String tipoSuscripcion, String metodoPago) {
        System.out.println("\n--- Perfil de " + usuario + " ---");
        System.out.println("Puntos acumulados: " + puntos);
        System.out.println("Suscripción: " + tipoSuscripcion);
        System.out.println("Válida desde: " + fechaInicio + " hasta " + fechaFin);
        System.out.println("Método de pago: " + metodoPago);
    }
}
