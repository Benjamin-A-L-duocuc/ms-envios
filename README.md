# Envios

Gestion del ciclo de vida de envios: creacion, programacion, envio y recepcion.

## Puerto

**8084** | DB: `Envio`

## Endpoints

| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| POST | `/api/v1/envios` | Crear envio |
| GET | `/api/v1/envios` | Listar todos |
| GET | `/api/v1/envios/{id}` | Obtener por ID |
| GET | `/api/v1/envios/folio/{folio}` | Obtener por folio |
| GET | `/api/v1/envios/estado?estado=` | Filtrar por estado |
| GET | `/api/v1/envios/tipo?tipoEnvio=` | Filtrar por tipo |
| PATCH | `/api/v1/envios/{id}/programar` | Programar fecha de envio |
| POST | `/api/v1/envios/{id}/iniciar` | Marcar en transito |
| POST | `/api/v1/envios/{id}/recibir` | Marcar recibido |
| POST | `/api/v1/envios/{id}/cancelar` | Cancelar envio |
| DELETE | `/api/v1/envios/{id}` | Eliminar envio |

## Crear envio

```json
POST /api/v1/envios
{
  "direccionDestino": "Av. Siempre Viva 742, Santiago",
  "tipoEnvio": "venta_online",
  "notas": "Envio express"
}
```

### Tipos de envio

- `venta_online`
- `envio_entre_sucursales`
- `envio_proveedor`

### Estados posibles

- `pendiente`
- `enTransito`
- `Recibido`
- `Cancelado`

## Flujo tipico

1. `POST /` — crear envio (estado: pendiente)
2. `PATCH /{id}/programar` — asignar fecha
3. `POST /{id}/iniciar` — cambiar a enTransito
4. `POST /{id}/recibir` — cambiar a Recibido

## Ejecucion

```cmd
cd Envios
.\mvnw.cmd spring-boot:run
```
