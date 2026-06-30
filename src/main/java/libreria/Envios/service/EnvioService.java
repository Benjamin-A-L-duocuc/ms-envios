package libreria.Envios.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import libreria.Envios.dto.EnvioDTO;
import libreria.Envios.model.Envio;
import libreria.Envios.model.enums.EstadoEnvioProveedores;
import libreria.Envios.model.enums.TipoEnvio;
import libreria.Envios.repository.EnvioRepository;

@Service
public class EnvioService {

    @Autowired
    private EnvioRepository envioRepository;

    private EnvioDTO toDTO(Envio envio) {
        return new EnvioDTO(
            envio.getId(), envio.getFolio(), envio.getFechaSolicitud(),
            envio.getFechaEnvioProgramada(), envio.getFechaComienzoEnvio(),
            envio.getFechaRecepcion(), envio.getEstado(),
            envio.getDireccionDestino(), envio.getNotas(), envio.getTipoEnvio()
        );
    }

    @Transactional
    public EnvioDTO crear(String direccionDestino, TipoEnvio tipoEnvio, String notas) {
        Envio envio = new Envio();
        envio.setFolio("ENV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        envio.setFechaSolicitud(LocalDateTime.now());
        envio.setEstado(EstadoEnvioProveedores.pendiente);
        envio.setDireccionDestino(direccionDestino);
        envio.setTipoEnvio(tipoEnvio);
        envio.setNotas(notas);
        return toDTO(envioRepository.save(envio));
    }

    @Transactional
    public EnvioDTO programarEnvio(Long id, LocalDateTime fechaEnvioProgramada) {
        Envio existente = envioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Envio no encontrado"));
        existente.setFechaEnvioProgramada(fechaEnvioProgramada);
        return toDTO(envioRepository.save(existente));
    }

    @Transactional
    public EnvioDTO iniciarEnvio(Long id) {
        Envio existente = envioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Envio no encontrado"));
        existente.setFechaComienzoEnvio(LocalDateTime.now());
        existente.setEstado(EstadoEnvioProveedores.enTransito);
        return toDTO(envioRepository.save(existente));
    }

    @Transactional
    public EnvioDTO recibirEnvio(Long id) {
        Envio existente = envioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Envio no encontrado"));
        existente.setFechaRecepcion(LocalDateTime.now());
        existente.setEstado(EstadoEnvioProveedores.Recibido);
        return toDTO(envioRepository.save(existente));
    }

    @Transactional
    public EnvioDTO cancelarEnvio(Long id) {
        Envio existente = envioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Envio no encontrado"));
        existente.setEstado(EstadoEnvioProveedores.Cancelado);
        return toDTO(envioRepository.save(existente));
    }

    public Optional<EnvioDTO> obtenerPorId(Long id) {
        return envioRepository.findById(id).map(this::toDTO);
    }

    public Optional<EnvioDTO> obtenerPorFolio(String folio) {
        return envioRepository.findByFolio(folio).map(this::toDTO);
    }

    public List<EnvioDTO> obtenerTodos() {
        return envioRepository.findAll().stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    public List<EnvioDTO> obtenerPorEstado(EstadoEnvioProveedores estado) {
        return envioRepository.findByEstado(estado).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    public List<EnvioDTO> obtenerPorTipo(TipoEnvio tipoEnvio) {
        return envioRepository.findByTipoEnvio(tipoEnvio).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public void eliminar(Long id) {
        if (!envioRepository.existsById(id)) {
            throw new RuntimeException("Envio no encontrado");
        }
        envioRepository.deleteById(id);
    }
}
