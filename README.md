# System Info API

API REST simples em Kotlin e Spring Boot para demonstrar deployments blue-green e canary.

## Requisitos

- Java 21 ou superior;
- Docker, caso queira criar a imagem de container.

O Maven Wrapper está incluído no projeto, portanto não é necessário instalar Maven.

## Executar

```bash
./mvnw spring-boot:run
```

A versão padrão da aplicação é `blue`. Ela pode ser configurada por variáveis de ambiente:

```bash
APP_VERSION=green APP_COLOR=green ./mvnw spring-boot:run
```

## Endpoints

### Informações do sistema

```bash
curl http://localhost:8080/api/system
```

O mesmo conteúdo também está disponível na raiz:

```bash
curl http://localhost:8080/
```

Exemplo de resposta:

```json
{
  "application": "system-info-api",
  "version": "blue",
  "color": "blue",
  "hostname": "my-host",
  "operatingSystem": {
    "name": "Linux",
    "version": "6.x",
    "architecture": "aarch64"
  },
  "java": {
    "version": "21.0.11",
    "vendor": "Eclipse Adoptium"
  },
  "runtime": {
    "availableProcessors": 2,
    "maxMemoryBytes": 536870912
  },
  "timestamp": "2026-10-18T17:50:56.792Z"
}
```

### Health checks

```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/health/liveness
curl http://localhost:8080/actuator/health/readiness
```

## Logs

A aplicação escreve logs no formato `chave=valor` ao iniciar e ao atender requisições:

```text
event=application_started application=system-info-api version=blue color=blue
event=system_info_requested version=blue color=blue hostname=my-host
event=http_request method=GET path=/api/system status=200 duration_ms=12
```

## Testes e build

```bash
./mvnw test
./mvnw package
```

O JAR executável será criado em `target/system-info-api-0.0.1-SNAPSHOT.jar`.

## Imagem Docker azul

Execute dentro desta pasta:

```bash
make image-build
```

Isso cria a imagem `system-info-api:blue`. As variáveis podem ser sobrescritas:

```bash
make image-build \
  IMAGE_NAME=system-info-api \
  IMAGE_TAG=green \
  APP_VERSION=green \
  APP_COLOR=green
```

O comando Docker equivalente é:

```bash
docker build \
  --build-arg APP_VERSION=blue \
  --build-arg APP_COLOR=blue \
  --tag system-info-api:blue \
  .
```

Para adicionar uma tag destinada a um registry:

```bash
make image-tag \
  TARGET_IMAGE=ghcr.io/usuario/system-info-api:blue
```

Execute a imagem:

```bash
docker run --rm --publish 8080:8080 system-info-api:blue
```

Para carregar a imagem no cluster kind do laboratório:

```bash
make kind-load
```

Para outro cluster ou tag:

```bash
make kind-load IMAGE_TAG=green KIND_CLUSTER=outro-cluster
```

## Implantar com Argo Rollouts

O manifesto [`k8s/rollout.yaml`](k8s/rollout.yaml) configura um canary com NGINX nos pesos 5%, 10%, 50% e 100%. Cada peso possui uma pausa sem duração e exige promoção manual.

Crie a imagem, carregue-a no kind e aplique o manifesto:

```bash
make image-build
make kind-load
kubectl apply --filename k8s/rollout.yaml
```

Acompanhe o rollout:

```bash
kubectl argo rollouts get rollout my-api --watch
```

Promova uma etapa por vez:

```bash
kubectl argo rollouts promote my-api
```

Existe uma pausa também depois de 100%. Portanto, a última promoção confirma a versão e conclui o rollout.
