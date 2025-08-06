import java.util.Scanner;
public class ProyectoRedes {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String usuario = "eduardo";
        String contrasena = "hola mundo";
        System.out.println("ingrese el usuario");
        String Ausuario = sc.nextLine();
        System.out.println("ingrese la contrasena");
        String Acontrasena = sc.nextLine();
        if (usuario.equals(Ausuario) && contrasena.equals(Acontrasena)) {
            System.out.println("Acceso concedido.");
        } else {
            System.out.println("Acceso denegado.");
        }
    }
}
