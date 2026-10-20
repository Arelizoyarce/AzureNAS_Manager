package interfaces;

/** Notificaciones a usuarios (correo, etc.). */
public interface INotificador {

    void notificar(String destinatario, String asunto, String mensaje);
}
