// Importa clases para manejar archivos, fechas y listas
import java.io.*;
// Importa clases para trabajar con archivos de forma moderna (leer, escribir)
import java.nio.file.Files;
import java.nio.file.Paths;
// Importa clases para trabajar con fechas (obtener fecha actual, formatear)
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
// Importa clase para manejar listas
import java.util.List;

// Declara la clase llamada suscripcion
public class suscripcion {
    // Declara una constante que guarda el nombre del archivo donde están los usuarios
    static final String ARCHIVO_USUARIOS = "usuarios.txt";
    // Crea un objeto Scanner para poder leer texto que escribe el usuario en consola
    static java.util.Scanner sc = new java.util.Scanner(System.in);

    // Método público que pregunta si el usuario quiere suscribirse ahora o no
    // Recibe el nombre de usuario y contraseña para luego usar
    // Devuelve el método de pago escogido o "sinmetodo" si no quiere suscribirse
    public static String gestionarNuevaCuenta(String usuario, String contrasena) {
        // Muestra el mensaje en pantalla y pide que el usuario responda
        System.out.print("¿Quieres suscribirte ahora? (s/si/n): ");
        // Lee lo que el usuario escribió y lo guarda en una variable, quitando espacios y con minúsculas
        String respuesta = sc.nextLine().trim().toLowerCase();

        // Compara si la respuesta es "s" o "si" para saber si quiere suscribirse
        if (respuesta.equals("s") || respuesta.equals("si")) {
            // Si sí, llama al método que muestra las opciones de membresía
            return mostrarOpciones(usuario, contrasena);
        } else {
            // Si no, dice que puede suscribirse después y devuelve "sinmetodo"
            System.out.println("Puedes suscribirte más tarde desde el menú.");
            return "sinmetodo";
        }
    }

    // Método privado que muestra las opciones de membresía y maneja la selección
    private static String mostrarOpciones(String usuario, String contrasena) {
        // Imprime el título de la sección
        System.out.println("\n--- Opciones de Membresía ---");
        // Muestra las opciones disponibles con su costo
        System.out.println("1. Membresía de 1 mes  - $299");
        System.out.println("2. Membresía de 3 meses - $699");
        System.out.println("3. Membresía de 6 meses - $999");
        System.out.println("4. Membresía de 12 meses (1 año) - $1000");
        // Pide que el usuario elija una opción
        System.out.print("Elige una opción (1-4): ");

        // Lee la opción que escribió el usuario
        String opcion = sc.nextLine().trim();
        // Variables que usarán para guardar datos según la opción elegida
        int meses = 0;       // Cantidad de meses que dura la membresía
        String tipo = "";    // Nombre de la membresía
        double precio = 0;   // Precio en pesos

        // Evalúa la opción que escribió el usuario
        switch (opcion) {
            case "1":
                meses = 1;                // Si eligió 1, la membresía dura 1 mes
                tipo = "Membresía 1 mes"; // Guardamos el nombre
                precio = 299;             // Guardamos el precio
                break;                    // Salimos del switch
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
                // Si no es ninguna opción válida, mostramos mensaje y regresamos "sinmetodo"
                System.out.println("Opción no válida. No se activó ninguna suscripción.");
                return "sinmetodo";
        }

        // Llama al método para procesar el pago, pasando el precio que calculamos
        String metodoPago = procesarPago(precio);
        // Si el pago no fue exitoso (metodoPago es null), indicamos que no se activó suscripción
        if (metodoPago == null) {
            System.out.println("❌ El pago no se completó. No se activó la suscripción.");
            return "sinmetodo";
        }

        // Obtenemos la fecha actual del sistema (hoy)
        LocalDate hoy = LocalDate.now();
        // Calculamos la fecha final sumando los meses de la membresía a la fecha de hoy
        LocalDate fin = hoy.plusMonths(meses);
        // Creamos un formato para mostrar fechas como "aaaa-mm-dd"
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Convertimos la fecha de inicio y la de fin a texto con el formato indicado
        String fechaInicio = hoy.format(fmt);
        String fechaFin = fin.format(fmt);

        // Actualizamos el archivo de usuarios con la nueva información de suscripción
        actualizarSuscripcion(usuario, contrasena, fechaInicio, fechaFin, tipo, metodoPago);
        // Confirmamos al usuario que su membresía está activa y por cuánto tiempo
        System.out.println("✅ " + tipo + " activada por $" + precio + ". Vigente hasta " + fechaFin);

        // Devolvemos el método de pago elegido para que se pueda guardar o usar después
        return metodoPago;
    }

    // Método que simula la parte de cobrar y pedir datos de la tarjeta al usuario
    private static String procesarPago(double precio) {
        // Mostramos un mensaje con el precio a pagar
        System.out.println("\n💳 --- Pago de $" + precio + " ---");
        // Mostramos las opciones de método de pago
        System.out.println("Seleccione método de pago:");
        System.out.println("1. Tarjeta de débito");
        System.out.println("2. Tarjeta de crédito");
        System.out.println("3. Tarjeta prepagada");
        // Pedimos que el usuario elija una opción
        System.out.print("Opción: ");
        // Leemos la opción elegida
        String opcionPago = sc.nextLine().trim();

        String tipoPago;
        // Evaluamos la opción y guardamos el tipo de tarjeta elegida
        switch (opcionPago) {
            case "1": tipoPago = "Tarjeta de débito"; break;
            case "2": tipoPago = "Tarjeta de crédito"; break;
            case "3": tipoPago = "Tarjeta prepagada"; break;
            default:
                // Si es opción inválida, cancelamos el pago y devolvemos null
                System.out.println("Opción no válida. Cancelando pago...");
                return null;
        }

        // Solicitamos los datos básicos de la tarjeta (simulación)
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

        // Verificamos que ningún dato esté vacío para que el pago pueda continuar
        if (nombreTitular.isEmpty() || numeroTarjeta.isEmpty() || fechaVencimiento.isEmpty() || cvv.isEmpty() || codigoPostal.isEmpty()) {
            // Si falta algún dato, mostramos mensaje y cancelamos
            System.out.println("❌ Datos incompletos. No se procesó el pago.");
            return null;
        }

        // Indicamos que el pago fue procesado con éxito (simulación)
        System.out.println("✅ Pago procesado con éxito.");

        // Retornamos el tipo de pago junto con el nombre del titular para guardarlo o mostrarlo
        return tipoPago + " - Titular: " + nombreTitular;
    }

    // Método que actualiza la suscripción del usuario en el archivo usuarios.txt
    // Recibe todos los datos necesarios para actualizar: usuario, contraseña, fechas, tipo y método de pago
    private static void actualizarSuscripcion(String usuario, String contrasena, String fechaInicio, String fechaFin, String tipo, String metodoPago) {
        // Variable para guardar los puntos actuales del usuario y no perderlos
        int puntos = 0;
        // Usamos un método externo (de la clase cw) para cargar la información del usuario actual
        cw.Usuario u = cw.cargarUsuario(usuario, contrasena);
        // Si el usuario existe, copiamos sus puntos actuales a la variable
        if (u != null) {
            puntos = u.puntos;
        }
        // Actualizamos el archivo usuarios.txt con la nueva información, manteniendo puntos y contraseña
        cw.actualizarUsuario(usuario, contrasena, fechaInicio, fechaFin, tipo, metodoPago, puntos);
    }
}
