# Envios — Logistica y Despacho

## Que es

El servicio de logistica de la libreria. Cada vez que los libros se mueven fisicamente — ya sea para entregarlos a un cliente, trasladarlos entre sucursales, o recibirlos de un proveedor — este microservicio lleva la cuenta del envio: cuando se creo, cuando salio, y cuando llego.

## Tres tipos de envio

| Tipo | Cuando se usa |
|------|--------------|
| `venta_online` | Un cliente compro por la tienda web y los libros deben llegar a su domicilio. |
| `envio_entre_sucursales` | La Sucursal Centro tiene libros que la Sucursal Norte necesita (o viceversa). |
| `envio_proveedor` | Un proveedor como "Distribuidora Cultural" esta despachando libros hacia una sucursal. |

## Ciclo de vida de un envio

```
pendiente --> [programar fecha] --> enTransito --> Recibido
    |
    +---------> Cancelado
```

1. **Crear** — Se registra el envio con su direccion destino y tipo. Se asigna un folio unico tipo `ENV-A3F8B2C1`.
2. **Programar** — Se asigna la fecha en que saldra. El estado sigue siendo `pendiente`.
3. **Iniciar** — El envio comienza. Pasa a `enTransito`.
4. **Recibir** — El destino confirma que llego. Pasa a `Recibido`.
5. En cualquier momento se puede **cancelar**.

## El folio

Cada envio tiene un identificador unico como `ENV-XXXXXXXX` (8 caracteres aleatorios). Se puede buscar por folio ademas de por ID.

## Como se integra

- **Sucursal** crea envios automaticamente cuando se aprueba una transferencia entre sucursales (reduce stock en origen, suma en destino, y crea el envio para rastrear el movimiento fisico).
- **TiendaWeb** consulta envios para que el cliente pueda rastrear su pedido.
- **Ventas** podria crear envios tipo `venta_online` al procesar una compra web.

## Ejecutar

```cmd
cd Envios
.\mvnw.cmd spring-boot:run
```

Puerto: **8084** | DB: `Envio`

## Endpoints

| Metodo | Ruta | Que hace |
|--------|------|----------|
| POST | `/api/v1/envios` | Crear envio |
| GET | `/api/v1/envios` | Listar todos |
| GET | `/api/v1/envios/{id}` | Ver por ID |
| GET | `/api/v1/envios/folio/{folio}` | Buscar por folio |
| GET | `/api/v1/envios/estado?estado=` | Filtrar por estado |
| GET | `/api/v1/envios/tipo?tipoEnvio=` | Filtrar por tipo |
| PATCH | `/api/v1/envios/{id}/programar` | Asignar fecha de envio |
| POST | `/api/v1/envios/{id}/iniciar` | Marcar en transito |
| POST | `/api/v1/envios/{id}/recibir` | Marcar recibido |
| POST | `/api/v1/envios/{id}/cancelar` | Cancelar envio |
