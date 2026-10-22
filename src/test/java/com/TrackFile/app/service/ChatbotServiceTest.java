package com.TrackFile.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.TrackFile.app.domain.ChatHistorial;
import com.TrackFile.app.domain.Usuario;
import com.TrackFile.app.repository.ChatHistorialRepository;
import com.TrackFile.app.web.dto.response.ChatResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class ChatbotServiceTest {
    private ChatHistorialRepository historial;
    private CurrentUserService usuarios;
    private ChatbotService chatbot;
    private Usuario usuario;

    @BeforeEach
    void prepare() {
        historial = mock(ChatHistorialRepository.class);
        usuarios = mock(CurrentUserService.class);
        usuario = new Usuario();
        chatbot = new ChatbotService(historial, usuarios);
        when(usuarios.getUsuarioActual()).thenReturn(usuario);
    }

    @Test
    void respondeYGuardaLaPreguntaEnElHistorialDelUsuario() {
        when(historial.save(any(ChatHistorial.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        ChatResponse respuesta = chatbot.responder("¿DÓNDE VEO LOS DOCUMENTOS?");

        assertTrue(respuesta.getRespuestaBot().contains("Documentos"));
        assertEquals("¿DÓNDE VEO LOS DOCUMENTOS?", respuesta.getMensajeUsuario());
        ArgumentCaptor<ChatHistorial> captured = ArgumentCaptor.forClass(ChatHistorial.class);
        verify(historial).save(captured.capture());
        assertSame(usuario, captured.getValue().getUsuario());
        assertEquals(respuesta.getRespuestaBot(), captured.getValue().getRespuestaBot());
    }

    @Test
    void ofreceAyudaCuandoNoReconoceLaPregunta() {
        when(historial.save(any(ChatHistorial.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        ChatResponse respuesta = chatbot.responder("una pregunta fuera del catalogo");
        assertTrue(respuesta.getRespuestaBot().contains("No tengo una respuesta exacta"));
    }

    @Test
    void consultaUnicamenteElHistorialDelUsuarioActual() {
        ChatHistorial entry = new ChatHistorial();
        entry.setUsuario(usuario);
        entry.setMensajeUsuario("hola");
        entry.setRespuestaBot("Hola");
        when(historial.findByUsuarioOrderByFechaDesc(usuario)).thenReturn(List.of(entry));

        List<ChatResponse> resultado = chatbot.historial();

        assertEquals(1, resultado.size());
        assertEquals("hola", resultado.get(0).getMensajeUsuario());
        verify(historial).findByUsuarioOrderByFechaDesc(usuario);
    }
}
