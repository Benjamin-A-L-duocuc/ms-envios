package libreria.Envios.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import libreria.Envios.model.Envio;
import libreria.Envios.model.enums.EstadoEnvioProveedores;

@Repository
public interface EnvioRepository extends JpaRepository<Envio, Long> {
    Optional<Envio> findByFolio(String folio);
    List<Envio> findByEstado(EstadoEnvioProveedores estado);
    List<Envio> findByTipoEnvio(libreria.Envios.model.enums.TipoEnvio tipoEnvio);
}
