package libreria.Envios.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import libreria.Envios.dto.EnvioDTO;
import libreria.Envios.model.enums.EstadoEnvioProveedores;
import libreria.Envios.model.enums.TipoEnvio;
import libreria.Envios.service.EnvioService;

@WebMvcTest(EnvioController.class)
class EnvioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EnvioService envioService;

    private EnvioDTO crearDTO(Long id) {
        return new EnvioDTO(id, "ENV-ABC" + id, LocalDateTime.now(), null, null, null,
                EstadoEnvioProveedores.pendiente, "Calle 123", "notas", TipoEnvio.venta_online);
    }

    @Test
    void crear_deberiaRetornar200() throws Exception {
        EnvioDTO dto = crearDTO(1L);
        given(envioService.crear(anyString(), any(TipoEnvio.class), anyString())).willReturn(dto);

        String body = """
                {
                    "direccionDestino": "Calle 123",
                    "tipoEnvio": "venta_online",
                    "notas": "notas"
                }
                """;

        mockMvc.perform(post("/api/v1/envios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void crear_cuandoFaltanParametros_deberiaRetornar400() throws Exception {
        String body = """
                {
                    "tipoEnvio": "venta_online"
                }
                """;

        mockMvc.perform(post("/api/v1/envios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crear_cuandoTipoEnvioInvalido_deberiaRetornar400() throws Exception {
        String body = """
                {
                    "direccionDestino": "Calle 123",
                    "tipoEnvio": "INVALIDO"
                }
                """;

        mockMvc.perform(post("/api/v1/envios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void obtenerTodos_deberiaRetornar200() throws Exception {
        given(envioService.obtenerTodos()).willReturn(List.of(crearDTO(1L), crearDTO(2L)));

        mockMvc.perform(get("/api/v1/envios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void obtenerPorId_deberiaRetornar200() throws Exception {
        given(envioService.obtenerPorId(1L)).willReturn(Optional.of(crearDTO(1L)));

        mockMvc.perform(get("/api/v1/envios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void obtenerPorId_cuandoNoExiste_deberiaRetornar404() throws Exception {
        given(envioService.obtenerPorId(99L)).willReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/envios/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void obtenerPorFolio_deberiaRetornar200() throws Exception {
        given(envioService.obtenerPorFolio("ENV-ABC")).willReturn(Optional.of(crearDTO(1L)));

        mockMvc.perform(get("/api/v1/envios/folio/ENV-ABC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void obtenerPorFolio_cuandoNoExiste_deberiaRetornar404() throws Exception {
        given(envioService.obtenerPorFolio("NO-EXISTE")).willReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/envios/folio/NO-EXISTE"))
                .andExpect(status().isNotFound());
    }

    @Test
    void obtenerPorEstado_deberiaRetornar200() throws Exception {
        given(envioService.obtenerPorEstado(EstadoEnvioProveedores.pendiente)).willReturn(List.of(crearDTO(1L)));

        mockMvc.perform(get("/api/v1/envios/estado").param("estado", "pendiente"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void programarEnvio_deberiaRetornar200() throws Exception {
        EnvioDTO dto = crearDTO(1L);
        dto.setFechaEnvioProgramada(LocalDateTime.of(2026, 7, 1, 10, 0));
        given(envioService.programarEnvio(anyLong(), any(LocalDateTime.class))).willReturn(dto);

        String body = """
                {
                    "fechaEnvioProgramada": "2026-07-01T10:00:00"
                }
                """;

        mockMvc.perform(patch("/api/v1/envios/1/programar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void programarEnvio_cuandoNoExiste_deberiaRetornar404() throws Exception {
        given(envioService.programarEnvio(anyLong(), any(LocalDateTime.class)))
                .willThrow(new RuntimeException("Envio no encontrado"));

        String body = """
                {
                    "fechaEnvioProgramada": "2026-07-01T10:00:00"
                }
                """;

        mockMvc.perform(patch("/api/v1/envios/99/programar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    void iniciarEnvio_deberiaRetornar200() throws Exception {
        given(envioService.iniciarEnvio(1L)).willReturn(crearDTO(1L));

        mockMvc.perform(post("/api/v1/envios/1/iniciar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void iniciarEnvio_cuandoNoExiste_deberiaRetornar404() throws Exception {
        given(envioService.iniciarEnvio(99L)).willThrow(new RuntimeException("Envio no encontrado"));

        mockMvc.perform(post("/api/v1/envios/99/iniciar"))
                .andExpect(status().isNotFound());
    }

    @Test
    void recibirEnvio_deberiaRetornar200() throws Exception {
        given(envioService.recibirEnvio(1L)).willReturn(crearDTO(1L));

        mockMvc.perform(post("/api/v1/envios/1/recibir"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void cancelarEnvio_deberiaRetornar200() throws Exception {
        given(envioService.cancelarEnvio(1L)).willReturn(crearDTO(1L));

        mockMvc.perform(post("/api/v1/envios/1/cancelar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void eliminar_deberiaRetornar204() throws Exception {
        willDoNothing().given(envioService).eliminar(1L);

        mockMvc.perform(delete("/api/v1/envios/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminar_cuandoNoExiste_deberiaRetornar404() throws Exception {
        willThrow(new RuntimeException("Envio no encontrado")).given(envioService).eliminar(99L);

        mockMvc.perform(delete("/api/v1/envios/99"))
                .andExpect(status().isNotFound());
    }
}
