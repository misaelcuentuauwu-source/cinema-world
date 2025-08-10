import java.util.Scanner;

public class metodoDepago {
    static Scanner sc = new Scanner(System.in);

    // Método que devuelve una cadena con resumen del método de pago
    public static String solicitarDatosPago() {
        System.out.println("--Elija su método de pago--");
        System.out.println("1. Tarjeta de débito");
        System.out.println("2. Tarjeta de crédito");
        System.out.println("3. Tarjeta prepagada");
        String tipoPago = "";
        int metodoPago = 0;
        while (true) {
            try {
                metodoPago = Integer.parseInt(sc.nextLine());
                if (metodoPago >= 1 && metodoPago <= 3) break;
                else System.out.println("Opción no válida, ingresa un número entre 1 y 3.");
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida, ingresa un número.");
            }
        }

        switch (metodoPago) {
            case 1: tipoPago = "Tarjeta de débito"; break;
            case 2: tipoPago = "Tarjeta de crédito"; break;
            case 3: tipoPago = "Tarjeta prepagada"; break;
        }

        System.out.println("--Ingrese los datos de la tarjeta--");

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

        System.out.println("--Datos ingresados correctamente--");

        // Solo guardamos tipo de pago y nombre titular para simplificar y evitar guardar datos sensibles
        return tipoPago + " - Titular: " + nombreTitular;
    }
}
