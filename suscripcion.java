import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class suscripcion {
    static final String ARCHIVO_USUARIOS = "usuarios.txt";
    static java.util.Scanner sc = new java.util.Scanner(System.in);

    // Ahora devuelve String con el método de pago o "sinmetodo" si no se suscribe
    public static String gestionarNuevaCuenta(String usuario, String contrasena) {
        System.out.print("¿Quieres suscribirte ahora? (s/n): ");
        String respuesta = sc.nextLine().trim().toLowerCase();

        if (respuesta.equals("s")) {
            return mostrarOpciones(usuario, contrasena);
        } else {
            System.out.println("Puedes suscribirte más tarde desde el menú.");
            return "sinmetodo";
        }
    }

    // Devuelve resumen del método de pago si se suscribe, o "sinmetodo"
    private static String mostrarOpciones(String usuario, String contrasena) {
        System.out.println("\n--- Opciones de Membresía ---");
        System.out.println("1. Membresía de 1 mes  - $299");
        System.out.println("2. Membresía de 3 meses - $699");
        System.out.println("3. Membresía de 6 meses - $999");
        System.out.println("4. Membresía de 12 meses (1 año) - $1000");
        System.out.print("Elige una opción (1-4): ");

        String opcion = sc.nextLine().trim();
        int meses = 0;
        String tipo = "";
        double precio = 0;

        switch (opcion) {
            case "1":
                meses = 1;
                tipo = "Membresía 1 mes";
                precio = 299;
                break;
            case "2":
                meses = 3;
                tipo = "Membresía 3 meses";
                precio = 699;
                break;
            case "3":
                meses = 6;
                tipo = "Membresía 6 meses";
                precio = 999;
                break;
            case "4":
                meses = 12;
                tipo = "Membresía 12 meses";
                precio = 1000;
                break;
            default:
                System.out.println("Opción no válida. No se activó ninguna suscripción.");
                return "sinmetodo";
        }

        // Pedir datos de pago y devolver resumen del método pago
        String metodoPago = procesarPago(precio);
        if (metodoPago == null) {
            System.out.println("❌ El pago no se completó. No se activó la suscripción.");
            return "sinmetodo";
        }

        // Calcular fechas
        LocalDate hoy = LocalDate.now();
        LocalDate fin = hoy.plusMonths(meses);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String fechaInicio = hoy.format(fmt);
        String fechaFin = fin.format(fmt);

        actualizarSuscripcion(usuario, contrasena, fechaInicio, fechaFin, tipo, metodoPago);
        System.out.println("✅ " + tipo + " activada por $" + precio + ". Vigente hasta " + fechaFin);

        return metodoPago;
    }

    // Devuelve resumen método pago o null si falló
    private static String procesarPago(double precio) {
        System.out.println("\n💳 --- Pago de $" + precio + " ---");
        System.out.println("Seleccione método de pago:");
        System.out.println("1. Tarjeta de débito");
        System.out.println("2. Tarjeta de crédito");
        System.out.println("3. Tarjeta prepagada");
        System.out.print("Opción: ");
        String opcionPago = sc.nextLine().trim();

        String tipoPago = null;
        switch (opcionPago) {
            case "1":
                tipoPago = "Tarjeta de débito";
                break;
            case "2":
                tipoPago = "Tarjeta de crédito";
                break;
            case "3":
                tipoPago = "Tarjeta prepagada";
                break;
            default:
                System.out.println("Opción no válida. Cancelando pago...");
                return null;
        }

        System.out.println("\n--- Ingrese los datos de la tarjeta ---");
        System.out.print("Nombre del titular: ");
        String nombreTitular = sc.nextLine();

        System.out.print("Número de la tarjeta: ");
        String numeroTarjeta = sc.nextLine();

        System.out.print("Fecha de vencimiento (MM/AA): ");
        String fechaVencimiento = sc.nextLine();

        System.out.print("Número de seguridad (CVV): ");
        String cvv = sc.nextLine();

        System.out.print("Código postal: ");
        String codigoPostal = sc.nextLine();

        if (nombreTitular.isEmpty() || numeroTarjeta.isEmpty() || fechaVencimiento.isEmpty() || cvv.isEmpty() || codigoPostal.isEmpty()) {
            System.out.println("❌ Datos incompletos. No se procesó el pago.");
            return null;
        }

        System.out.println("✅ Pago procesado con éxito.");

        // Devolvemos tipo de pago + titular para guardar
        return tipoPago + " - Titular: " + nombreTitular;
    }

    private static void actualizarSuscripcion(String usuario, String contrasena, String fechaInicio, String fechaFin, String tipo, String metodoPago) {
        try {
            List<String> lineas = Files.readAllLines(Paths.get(ARCHIVO_USUARIOS));
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_USUARIOS))) {
                for (String linea : lineas) {
                    String[] datos = linea.split(";");
                    if (datos.length > 0 && datos[0].equals(usuario)) {
                        // Mantener puntos si existen, si no 0
                        String puntos = (datos.length > 6) ? datos[6] : "0";
                        bw.write(usuario + ";" + contrasena + ";" + fechaInicio + ";" + fechaFin + ";" + tipo + ";" + metodoPago + ";" + puntos);
                    } else {
                        bw.write(linea);
                    }
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error actualizando la suscripción.");
        }
    }
}
