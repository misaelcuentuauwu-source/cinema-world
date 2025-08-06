import java.util.Scanner;

public class metodoDepago {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("--Elija su método de pago--");
        System.out.println("1. Tarjeta de débito");
        System.out.println("2. Tarjeta de crédito");
        System.out.println("3. Tarjeta prepagada");
        int metodoPago = sc.nextInt();
        sc.nextLine();

        if (metodoPago >= 1 && metodoPago <= 3) {
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
        } else {
            System.out.println("--Opción no válida--");
        }

        sc.close();
    }
}

