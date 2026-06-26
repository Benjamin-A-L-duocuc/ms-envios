package libreria.Envios.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import libreria.Envios.model.Envio;
import libreria.Envios.repository.EnvioRepository;

@Service
public class EnvioService {

	@Autowired
	private EnvioRepository envioRepository;

	@Transactional
	public Envio guardar(Envio envio) {
		return envioRepository.save(envio);
	}

	public Optional<Envio> obtenerPorId(Long id) {
		return envioRepository.findById(id);
	}

	public List<Envio> obtenerTodos() {
		return envioRepository.findAll();
	}

	@Transactional
	public Envio actualizar(Long id, Envio envio) {
		Envio existente = envioRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Envio no encontrado"));

		existente.setFolio(envio.getFolio());
		existente.setFechaSolicitud(envio.getFechaSolicitud());
		existente.setFechaEnvioProgramada(envio.getFechaEnvioProgramada());
		existente.setFechaComienzoEnvio(envio.getFechaComienzoEnvio());
		existente.setFechaRecepcion(envio.getFechaRecepcion());
		existente.setEstado(envio.getEstado());
		existente.setDireccionDestino(envio.getDireccionDestino());
		existente.setNotas(envio.getNotas());
		existente.setTipoEnvio(envio.getTipoEnvio());

		return envioRepository.save(existente);
	}

	@Transactional
	public void eliminar(Long id) {
		envioRepository.deleteById(id);
	}
}
