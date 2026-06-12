# Guía de Despliegue y Verificación de Infraestructura - Intiva Platform

Este documento detalla los pasos para levantar, verificar y mantener localmente la infraestructura del backend de Intiva Platform, compuesta por:
1. **Nginx** (API Gateway / Proxy Inverso) - Puerto público: 80
2. **Spring Boot App** (Backend) - Puerto interno: 8080
3. **PostgreSQL** (Base de datos) - Puerto interno: 5432
4. **Redis** (Caché) - Puerto interno: 6379

---

# 1. BUILD Y LEVANTAR TODOS LOS SERVICIOS


### Crear el archivo .env con las variables de entorno
```bash
cp .env.example .env
```
*(Asegúrate de editar `.env` si necesitas cambiar contraseñas u otros parámetros de base de datos/caché).*

### Construir imagen de Spring Boot y levantar todos los servicios
```bash
docker-compose up -d --build
```

### Verificar que todos los contenedores estén corriendo
```bash
docker-compose ps
```

---

# 2. VERIFICAR CRITERIO DE ACEPTACIÓN
# Escenario #1: Nginx enruta peticiones al backend
# sin exponer el puerto interno 8080

### ✅ DEBE FUNCIONAR - Nginx en puerto 80 enruta al backend
```bash
curl http://localhost/actuator/health
```
**Respuesta esperada:**
```json
{"status":"UP"}
```

### ✅ DEBE FUNCIONAR - Endpoint de autenticación via Nginx
```bash
curl -X POST http://localhost/api/v1/authentication/sign-up \
  -H "Content-Type: application/json" \
  -d '{"email":"test@gmail.com","password":"Test1234!"}'
```

### ✅ DEBE FUNCIONAR - Swagger via Nginx
```bash
curl http://localhost/swagger-ui/index.html
```

### ❌ DEBE FALLAR - Puerto 8080 no expuesto directamente
```bash
curl http://localhost:8080/actuator/health
```
**Respuesta esperada:**
*Connection refused (o error de red equivalente, demostrando aislamiento).*

### ❌ DEBE FALLAR - Puerto 5432 no expuesto directamente
```bash
curl http://localhost:5432
```
**Respuesta esperada:**
*Connection refused.*

### ❌ DEBE FALLAR - Puerto 6379 no expuesto directamente
```bash
curl http://localhost:6379
```
**Respuesta esperada:**
*Connection refused.*

---

# 3. VERIFICAR LOGS DE ENRUTAMIENTO

### Ver logs de Nginx (confirma que recibe y reenvía peticiones)
```bash
docker logs intiva_nginx --tail 50
```

### Ver logs del backend (confirma que recibe peticiones de Nginx)
```bash
docker logs intiva_backend --tail 50
```

### Ver logs de PostgreSQL
```bash
docker logs intiva_postgres --tail 20
```

---

# 4. VERIFICAR RED INTERNA

### Inspeccionar la red interna - todos los servicios deben estar
```bash
docker network inspect intiva-api-platform_intiva_internal
```

### Verificar que backend puede comunicarse con postgres internamente
```bash
docker exec intiva_backend \
  curl -s http://localhost:8080/actuator/health
```

---

# 5. COMANDOS DE MANTENIMIENTO

### Detener todos los servicios
```bash
docker-compose down
```

### Detener y eliminar volúmenes (borra datos de PostgreSQL)
```bash
docker-compose down -v
```

### Reiniciar solo el backend
```bash
docker-compose restart backend
```

### Reiniciar solo nginx
```bash
docker-compose restart nginx
```

### Reconstruir solo el backend sin tocar otros servicios
```bash
docker-compose up -d --build backend
```

### Ver uso de recursos de cada contenedor
```bash
docker stats
```

6. ACCEDER A PGADMIN

```bash
 http://localhost:5050
```

### Acceder a la base de datos

```bash
Name: Intiva PostgreSQL

Host name/address: postgres

Port: 5432

Maintenance database: intiva_platform

Username: {POSTGRES_USER}

Password: {POSTGRES_PASSWORD}
```

