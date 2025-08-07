import java.util.Scanner;

public class sm{
    static Scanner sc = new Scanner(System.in);

    // Método estático que recibe el usuario que inició sesión y muestra el menú
    public static void menuMembresias(String usuario) {
        System.out.println("\n¡Bienvenido al sistema de membresías, " + usuario + "!");
        boolean continuar = true;

        while (continuar) {
            System.out.println("\n--- Menú de Membresías ---");
            System.out.println("1. Registrar nueva membresía");
            System.out.println("2. Ver membresías activas");
            System.out.println("3. Salir");
            System.out.print("Elige una opción: ");
            String opcion = sc.nextLine();

            switch (opcion) {
                case "1":
                    System.out.println("Función para registrar nueva membresía (por implementar).");
                    break;
                case "2":
                    System.out.println("Función para ver membresías activas (por implementar).");
                    break;
                case "3":
                    System.out.println("Saliendo del sistema de membresías...");
                    continuar = false;
                    break;
                default:
                    System.out.println("Opción no válida, intenta de nuevo.");
            }
        }
    }
}
