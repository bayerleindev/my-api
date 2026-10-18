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
  TARGET_IMAGE=gbcamargo/system-info-api:blue
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

Os manifests foram separados do código e estão no repositório [bayerleindev/my-api-manifests](https://github.com/bayerleindev/my-api-manifests). O Rollout configura um canary com NGINX nos pesos 5%, 10%, 50% e 100%, todos com promoção manual.

Crie a imagem e carregue-a no kind:

```bash
make image-build
make kind-load
```

Clone e aplique o repositório de manifests:

```bash
git clone https://github.com/bayerleindev/my-api-manifests.git
kubectl apply --kustomize my-api-manifests/k8s
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

## Pipeline GitOps no GitHub Actions

O workflow `Build and update manifests` é executado manualmente e recebe uma cor para identificar a versão. Ele:

1. executa os testes;
2. cria e publica `gbcamargo/system-info-api:<commit-sha>` no Docker Hub;
3. grava o SHA completo em `k8s/rollout.yaml` no repositório `my-api-manifests`;
4. cria um commit na branch `main` dos manifests.

Cadastre estes secrets em **Settings > Secrets and variables > Actions** deste repositório:

- `DOCKERHUB_TOKEN`: access token do Docker Hub com permissão de push;
- `MANIFESTS_TOKEN`: fine-grained personal access token do GitHub com acesso ao repositório `bayerleindev/my-api-manifests` e permissão **Contents: Read and write**.

Depois, abra **Actions > Build and update manifests > Run workflow**, informe uma cor, como `purple`, e execute. A resposta de `/api/system` e os logs da nova imagem mostrarão essa cor.

Como a aplicação do Argo CD deste laboratório está configurada com sincronização manual, o commit deixará a aplicação como `OutOfSync`. Inicie o rollout com:

```bash
argocd app sync my-api
kubectl argo rollouts get rollout my-api --watch
```

As promoções de 5%, 10%, 50% e 100% continuam manuais.
