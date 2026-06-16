package libreria.Envios.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import libreria.Envios.model.Envio;

@Repository
public interface EnvioRepository extends JpaRepository<Envio, Long> {
}
