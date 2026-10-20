package notificaciones;

import interfaces.INotificador;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Notificador por correo. Por ahora solo registra el envio en el log.
 * Para enviar correos reales agrega la dependencia jakarta/javax mail y envia aqui el mensaje.
 */
public class NotificadorEmail implements INotificador {

    private static final Logger logger = LoggerFactory.getLogger(NotificadorEmail.class);

    @Override
    public void notificar(String destinatario, String asunto, String mensaje) {
        logger.info("[EMAIL simulado] Para: {} | Asunto: {} | Mensaje: {}", destinatario, asunto, mensaje);
    }
}
