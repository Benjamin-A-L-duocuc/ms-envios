package libreria.Envios.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import libreria.Envios.dto.EnvioDTO;
import libreria.Envios.model.enums.EstadoEnvioProveedores;
import libreria.Envios.model.enums.TipoEnvio;
import libreria.Envios.service.EnvioService;

@RestController
@RequestMapping("/api/v1/envios")
public class EnvioController {

    @Autowired
    private EnvioService envioService;

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Map<String, Object> body) {
        try {
            String direccionDestino = (String) body.get("direccionDestino");
            String tipoEnvioStr = (String) body.get("tipoEnvio");
            String notas = (String) body.get("notas");
            if (direccionDestino == null || tipoEnvioStr == null) {
                return ResponseEntity.badRequest().body("direccionDestino y tipoEnvio son requeridos");
            }
            TipoEnvio tipoEnvio = TipoEnvio.valueOf(tipoEnvioStr);
            EnvioDTO dto = envioService.crear(direccionDestino, tipoEnvio, notas);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("tipoEnvio invalido");
        }
    }

    @GetMapping
    public ResponseEntity<List<EnvioDTO>> obtenerTodos() {
        return ResponseEntity.ok(envioService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnvioDTO> obtenerPorId(@PathVariable Long id) {
        return envioService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/folio/{folio}")
    public ResponseEntity<EnvioDTO> obtenerPorFolio(@PathVariable String folio) {
        return envioService.obtenerPorFolio(folio)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/estado")
    public ResponseEntity<List<EnvioDTO>> obtenerPorEstado(@RequestParam EstadoEnvioProveedores estado) {
        return ResponseEntity.ok(envioService.obtenerPorEstado(estado));
    }

    @GetMapping("/tipo")
    public ResponseEntity<List<EnvioDTO>> obtenerPorTipo(@RequestParam TipoEnvio tipoEnvio) {
        return ResponseEntity.ok(envioService.obtenerPorTipo(tipoEnvio));
    }

    @PatchMapping("/{id}/programar")
    public ResponseEntity<?> programarEnvio(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            LocalDateTime fecha = LocalDateTime.parse(body.get("fechaEnvioProgramada"));
            EnvioDTO dto = envioService.programarEnvio(id, fecha);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/iniciar")
    public ResponseEntity<?> iniciarEnvio(@PathVariable Long id) {
        try {
            EnvioDTO dto = envioService.iniciarEnvio(id);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/recibir")
    public ResponseEntity<?> recibirEnvio(@PathVariable Long id) {
        try {
            EnvioDTO dto = envioService.recibirEnvio(id);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarEnvio(@PathVariable Long id) {
        try {
            EnvioDTO dto = envioService.cancelarEnvio(id);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            envioService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
