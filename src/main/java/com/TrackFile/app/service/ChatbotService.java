package com.TrackFile.app.service;

import com.TrackFile.app.domain.ChatHistorial;
import com.TrackFile.app.domain.Usuario;
import com.TrackFile.app.repository.ChatHistorialRepository;
import com.TrackFile.app.web.dto.response.ChatResponse;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;

@Service
public class ChatbotService {

    private final ChatHistorialRepository chatHistorialRepository;
    private final CurrentUserService currentUserService;

    public ChatbotService(ChatHistorialRepository chatHistorialRepository,
            CurrentUserService currentUserService) {
        this.chatHistorialRepository = chatHistorialRepository;
        this.currentUserService = currentUserService;
    }

    public ChatResponse responder(String mensaje) {
        Usuario usuario = currentUserService.getUsuarioActual();

        String respuesta = obtenerRespuesta(mensaje);

        ChatHistorial historial = new ChatHistorial();
        historial.setUsuario(usuario);
        historial.setMensajeUsuario(mensaje);
        historial.setRespuestaBot(respuesta);
        historial.setMetadatos("{\"tipo\":\"predefinido\"}");

        ChatHistorial guardado = chatHistorialRepository.save(historial);

        return new ChatResponse(
                guardado.getId(),
                guardado.getMensajeUsuario(),
                guardado.getRespuestaBot(),
                guardado.getFecha());
    }

    public List<ChatResponse> historial() {
        Usuario usuario = currentUserService.getUsuarioActual();

        return chatHistorialRepository.findByUsuarioOrderByFechaDesc(usuario)
                .stream()
                .map(chat -> new ChatResponse(
                        chat.getId(),
                        chat.getMensajeUsuario(),
                        chat.getRespuestaBot(),
                        chat.getFecha()))
                .toList();
    }

    private String obtenerRespuesta(String mensaje) {
        String texto = limpiar(mensaje);

        if (texto.contains("hola")) {
            return "Hola, bienvenido a TrackFile. ¿En qué te puedo ayudar?";
        }

        if (texto.contains("buenos dias")) {
            return "Buenos días. Puedes preguntarme sobre documentos, certificados, vehículos, mantenimientos o tu perfil.";
        }

        if (texto.contains("buenas tardes")) {
            return "Buenas tardes. Estoy aquí para ayudarte a usar TrackFile.";
        }

        if (texto.contains("necesito ayuda") || texto.equals("ayuda")) {
            return "Claro. Dime qué necesitas hacer: ver documentos, subir un archivo, solicitar un certificado o revisar un vehículo.";
        }

        if (texto.contains("donde veo los documentos") || texto.equals("documentos")) {
            return "Para ver documentos, entra a la sección Documentos.";
        }

        if (texto.contains("como subo un documento") || texto.contains("subir documento")
                || texto.contains("subir archivo")) {
            return "Ve a Documentos, toca Subir documento, selecciona la persona, el tipo de documento, el archivo, el área y la fecha de vencimiento.";
        }

        if (texto.contains("documento vencido") || texto.contains("documento vencio")) {
            return "Debes subir un documento nuevo con la fecha actualizada.";
        }

        if (texto.contains("historial de documentos")) {
            return "Dentro del detalle de persona o vehículo, usa la opción Ver historial.";
        }

        if (texto.contains("certificado") || texto.contains("solicitud")) {
            return "Ve a Solicitudes, selecciona el tipo de certificado, escribe una descripción si aplica y envía la solicitud.";
        }

        if (texto.contains("donde veo mis solicitudes")) {
            return "Entra a la sección Solicitudes.";
        }

        if (texto.contains("vehiculo") || texto.contains("vehiculos") || texto.contains("placa")) {
            return "Para revisar vehículos, entra a Vehículos. Puedes ver placa, marca, modelo, propietario, conductor, documentos y mantenimientos.";
        }

        if (texto.contains("mantenimiento") || texto.contains("mantenimientos")) {
            return "Para mantenimientos, entra a Mantenimientos. Allí puedes ver sugeridos, programados y realizados.";
        }

        if (texto.contains("conductor")) {
            return "Si eres empresa, entra a Conductores para ver la información y documentos de los conductores.";
        }

        if (texto.contains("propietario")) {
            return "Si eres empresa, entra a Propietarios para ver la información, vehículos y documentos asociados.";
        }

        if (texto.contains("perfil")) {
            return "Para tus datos personales, entra a Perfil.";
        }

        if (texto.contains("empresa")) {
            return "Para información de la empresa, entra a Empresa.";
        }

        if (texto.contains("notificacion") || texto.contains("mensaje")) {
            return "Las notificaciones sirven para avisarte sobre documentos, vencimientos, solicitudes o cambios importantes.";
        }

        if (texto.contains("cerrar sesion")) {
            return "Usa el botón de cerrar sesión disponible en tu cuenta o perfil.";
        }

        if (texto.contains("contraseña") || texto.contains("contrasena") || texto.contains("clave")) {
            return "Usa la opción de recuperación si está disponible o contacta al administrador.";
        }

        if (texto.contains("error") || texto.contains("no funciona") || texto.contains("no carga")) {
            return "Intenta recargar la página. Si el problema sigue, comunícate con tu empresa o escribe a trackfile.noreply@gmail.com.";
        }

        if (texto.contains("soporte")) {
            return "Puedes contactar a TrackFile en trackfile.noreply@gmail.com.";
        }

        if (texto.contains("politicas") || texto.contains("privacidad")) {
            return "Puedes consultar las políticas en https://politicas-privacidad-trackfile.vercel.app/";
        }

        return "No tengo una respuesta exacta para eso todavía. Puedes preguntarme sobre documentos, certificados, vehículos, mantenimientos, perfil, empresa, notificaciones o soporte.";
    }

    private String limpiar(String texto) {
        if (texto == null)
            return "";

        String normalizado = Normalizer.normalize(texto.toLowerCase(), Normalizer.Form.NFD);
        return normalizado.replaceAll("\\p{M}", "");
    }
}