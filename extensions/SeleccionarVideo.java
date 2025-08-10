import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.awt.Desktop;

public class SeleccionarVideo {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("¿Qué video quieres ver?");
        System.out.println("1. Gato bailando");
        System.out.println("2. Dos gatos bailando");
        System.out.print("Selecciona 1 o 2: ");
        int opcion = sc.nextInt();

        String rutaGato = "/home/misael/Vídeos/gato.mp4";
        String rutaGato2 = "/home/misael/Vídeos/gato2.mp4";

        String rutaSeleccionada = null;

        if (opcion == 1) {
            rutaSeleccionada = rutaGato;
        } else if (opcion == 2) {
            rutaSeleccionada = rutaGato2;
        } else {
            System.out.println("Opción no válida.");
            sc.close();
            return;
        }

        File video = new File(rutaSeleccionada);
        try {
            if (video.exists()) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(video);
                    System.out.println("Reproduciendo video...");
                } else {
                    System.out.println("Tu sistema no soporta la apertura de archivos desde Java.");
                }
            } else {
                System.out.println("El archivo no existe: " + rutaSeleccionada);
            }
        } catch (IOException e) {
            System.out.println("No se pudo abrir el video.");
            e.printStackTrace();
        }

        sc.close();
    }
}
