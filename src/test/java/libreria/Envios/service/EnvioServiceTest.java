package libreria.Envios.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import libreria.Envios.dto.EnvioDTO;
import libreria.Envios.model.Envio;
import libreria.Envios.model.enums.EstadoEnvioProveedores;
import libreria.Envios.model.enums.TipoEnvio;
import libreria.Envios.repository.EnvioRepository;

@ExtendWith(MockitoExtension.class)
class EnvioServiceTest {

    @Mock
    private EnvioRepository envioRepository;

    @InjectMocks
    private EnvioService envioService;

    @Test
    void crear_deberiaCrearYRetornarDTO() {
        Envio envioGuardado = new Envio(1L, "ENV-ABC123", LocalDateTime.now(), null, null, null,
                EstadoEnvioProveedores.pendiente, "Calle 123", "notas", TipoEnvio.venta_online);
        given(envioRepository.save(any(Envio.class))).willReturn(envioGuardado);

        EnvioDTO resultado = envioService.crear("Calle 123", TipoEnvio.venta_online, "notas");

        assertThat(resultado).isNotNull();
        assertThat(resultado.getFolio()).startsWith("ENV-");
        assertThat(resultado.getEstado()).isEqualTo(EstadoEnvioProveedores.pendiente);
        assertThat(resultado.getDireccionDestino()).isEqualTo("Calle 123");
        assertThat(resultado.getTipoEnvio()).isEqualTo(TipoEnvio.venta_online);
    }

    @Test
    void programarEnvio_deberiaAsignarFechaYRetornarDTO() {
        Envio envio = new Envio(1L, "ENV-ABC", LocalDateTime.now(), null, null, null,
                EstadoEnvioProveedores.pendiente, "Calle", "notas", TipoEnvio.venta_online);
        given(envioRepository.findById(1L)).willReturn(Optional.of(envio));
        given(envioRepository.save(any(Envio.class))).willAnswer(invocation -> invocation.getArgument(0));

        LocalDateTime fecha = LocalDateTime.now().plusDays(1);
        EnvioDTO resultado = envioService.programarEnvio(1L, fecha);

        assertThat(resultado.getFechaEnvioProgramada()).isEqualTo(fecha);
    }

    @Test
    void iniciarEnvio_deberiaCambiarEstadoAEnTransito() {
        Envio envio = new Envio(1L, "ENV-ABC", LocalDateTime.now(), null, null, null,
                EstadoEnvioProveedores.pendiente, "Calle", "notas", TipoEnvio.venta_online);
        given(envioRepository.findById(1L)).willReturn(Optional.of(envio));
        given(envioRepository.save(any(Envio.class))).willAnswer(invocation -> invocation.getArgument(0));

        EnvioDTO resultado = envioService.iniciarEnvio(1L);

        assertThat(resultado.getEstado()).isEqualTo(EstadoEnvioProveedores.enTransito);
        assertThat(resultado.getFechaComienzoEnvio()).isNotNull();
    }

    @Test
    void recibirEnvio_deberiaCambiarEstadoARecibido() {
        Envio envio = new Envio(1L, "ENV-ABC", LocalDateTime.now(), null, null, null,
                EstadoEnvioProveedores.enTransito, "Calle", "notas", TipoEnvio.venta_online);
        given(envioRepository.findById(1L)).willReturn(Optional.of(envio));
        given(envioRepository.save(any(Envio.class))).willAnswer(invocation -> invocation.getArgument(0));

        EnvioDTO resultado = envioService.recibirEnvio(1L);

        assertThat(resultado.getEstado()).isEqualTo(EstadoEnvioProveedores.Recibido);
        assertThat(resultado.getFechaRecepcion()).isNotNull();
    }

    @Test
    void cancelarEnvio_deberiaCambiarEstadoACancelado() {
        Envio envio = new Envio(1L, "ENV-ABC", LocalDateTime.now(), null, null, null,
                EstadoEnvioProveedores.pendiente, "Calle", "notas", TipoEnvio.venta_online);
        given(envioRepository.findById(1L)).willReturn(Optional.of(envio));
        given(envioRepository.save(any(Envio.class))).willAnswer(invocation -> invocation.getArgument(0));

        EnvioDTO resultado = envioService.cancelarEnvio(1L);

        assertThat(resultado.getEstado()).isEqualTo(EstadoEnvioProveedores.Cancelado);
    }

    @Test
    void programarEnvio_cuandoNoExiste_deberiaLanzarExcepcion() {
        given(envioRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> envioService.programarEnvio(99L, LocalDateTime.now()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("no encontrado");
    }

    @Test
    void iniciarEnvio_cuandoNoExiste_deberiaLanzarExcepcion() {
        given(envioRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> envioService.iniciarEnvio(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("no encontrado");
    }

    @Test
    void obtenerPorId_deberiaRetornarDTO() {
        Envio envio = new Envio(1L, "ENV-ABC", LocalDateTime.now(), null, null, null,
                EstadoEnvioProveedores.pendiente, "Calle", "notas", TipoEnvio.venta_online);
        given(envioRepository.findById(1L)).willReturn(Optional.of(envio));

        Optional<EnvioDTO> resultado = envioService.obtenerPorId(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(1L);
    }

    @Test
    void obtenerPorId_cuandoNoExiste_deberiaRetornarVacio() {
        given(envioRepository.findById(99L)).willReturn(Optional.empty());

        Optional<EnvioDTO> resultado = envioService.obtenerPorId(99L);

        assertThat(resultado).isEmpty();
    }

    @Test
    void obtenerPorFolio_deberiaRetornarDTO() {
        Envio envio = new Envio(1L, "ENV-ABC", LocalDateTime.now(), null, null, null,
                EstadoEnvioProveedores.pendiente, "Calle", "notas", TipoEnvio.venta_online);
        given(envioRepository.findByFolio("ENV-ABC")).willReturn(Optional.of(envio));

        Optional<EnvioDTO> resultado = envioService.obtenerPorFolio("ENV-ABC");

        assertThat(resultado).isPresent();
    }

    @Test
    void obtenerPorFolio_cuandoNoExiste_deberiaRetornarVacio() {
        given(envioRepository.findByFolio("NO-EXISTE")).willReturn(Optional.empty());

        Optional<EnvioDTO> resultado = envioService.obtenerPorFolio("NO-EXISTE");

        assertThat(resultado).isEmpty();
    }

    @Test
    void obtenerTodos_deberiaRetornarLista() {
        Envio envio1 = new Envio(1L, "ENV-001", LocalDateTime.now(), null, null, null,
                EstadoEnvioProveedores.pendiente, "Calle 1", null, TipoEnvio.venta_online);
        Envio envio2 = new Envio(2L, "ENV-002", LocalDateTime.now(), null, null, null,
                EstadoEnvioProveedores.enTransito, "Calle 2", null, TipoEnvio.envio_entre_sucursales);
        given(envioRepository.findAll()).willReturn(List.of(envio1, envio2));

        List<EnvioDTO> resultado = envioService.obtenerTodos();

        assertThat(resultado).hasSize(2);
    }

    @Test
    void obtenerPorEstado_deberiaRetornarListaFiltrada() {
        Envio envio = new Envio(1L, "ENV-001", LocalDateTime.now(), null, null, null,
                EstadoEnvioProveedores.pendiente, "Calle", null, TipoEnvio.venta_online);
        given(envioRepository.findByEstado(EstadoEnvioProveedores.pendiente)).willReturn(List.of(envio));

        List<EnvioDTO> resultado = envioService.obtenerPorEstado(EstadoEnvioProveedores.pendiente);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getEstado()).isEqualTo(EstadoEnvioProveedores.pendiente);
    }

    @Test
    void obtenerPorTipo_deberiaRetornarListaFiltrada() {
        Envio envio = new Envio(1L, "ENV-001", LocalDateTime.now(), null, null, null,
                EstadoEnvioProveedores.pendiente, "Calle", null, TipoEnvio.venta_online);
        given(envioRepository.findByTipoEnvio(TipoEnvio.venta_online)).willReturn(List.of(envio));

        List<EnvioDTO> resultado = envioService.obtenerPorTipo(TipoEnvio.venta_online);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getTipoEnvio()).isEqualTo(TipoEnvio.venta_online);
    }

    @Test
    void eliminar_cuandoNoExiste_deberiaLanzarExcepcion() {
        given(envioRepository.existsById(99L)).willReturn(false);

        assertThatThrownBy(() -> envioService.eliminar(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("no encontrado");
    }

    @Test
    void eliminar_cuandoExiste_deberiaEliminar() {
        given(envioRepository.existsById(1L)).willReturn(true);
        willDoNothing().given(envioRepository).deleteById(1L);

        envioService.eliminar(1L);

        then(envioRepository).should().deleteById(1L);
    }
}
