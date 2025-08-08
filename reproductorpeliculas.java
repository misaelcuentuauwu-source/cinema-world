import java.util.Scanner;

public class reproductorpeliculas {
    static Scanner sc = new Scanner(System.in);

    public static void iniciar(String usuario) {
        System.out.println("\n🎬 Bienvenido al cinema world, " + usuario + " 🎬");
        System.out.println("1. Ver película");
        System.out.println("2. Buscar película");
        System.out.println("3. Salir del reproductor");

        while (true) {
            System.out.print("Elige una opción: ");
            String opcion = sc.nextLine();
            switch (opcion) {
                case "1":
                    System.out.println("Reproduciendo una película al azar... 🍿");
                    break;
                case "2":
                    System.out.print("Introduce el nombre de la película: ");
                    String nombre = sc.nextLine();
                    System.out.println("Buscando y reproduciendo: " + nombre);
                    break;
                case "3":
                    System.out.println("Saliendo del reproductor...");
                    return;
                default:
                    System.out.println("Opción inválida. Intenta de nuevo.");
            }
        }
    }
}
