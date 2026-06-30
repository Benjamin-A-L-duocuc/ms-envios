package libreria.Envios.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import libreria.Envios.model.enums.EstadoEnvioProveedores;
import libreria.Envios.model.enums.TipoEnvio;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnvioDTO {
    private Long id;
    private String folio;
    private LocalDateTime fechaSolicitud;
    private LocalDateTime fechaEnvioProgramada;
    private LocalDateTime fechaComienzoEnvio;
    private LocalDateTime fechaRecepcion;
    private EstadoEnvioProveedores estado;
    private String direccionDestino;
    private String notas;
    private TipoEnvio tipoEnvio;
}
