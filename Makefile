IMAGE_NAME ?= system-info-api
IMAGE_TAG ?= blue
APP_VERSION ?= $(IMAGE_TAG)
APP_COLOR ?= blue
KIND_CLUSTER ?= rollouts-lab

LOCAL_IMAGE := $(IMAGE_NAME):$(IMAGE_TAG)

.PHONY: help image-build image-tag kind-load

help:
	@echo "Comandos disponíveis:"
	@echo "  make image-build                         Cria $(LOCAL_IMAGE)"
	@echo "  make image-tag TARGET_IMAGE=<imagem>     Adiciona outra tag à imagem local"
	@echo "  make kind-load                           Carrega $(LOCAL_IMAGE) no kind"
	@echo ""
	@echo "Variáveis:"
	@echo "  IMAGE_NAME=$(IMAGE_NAME)"
	@echo "  IMAGE_TAG=$(IMAGE_TAG)"
	@echo "  APP_VERSION=$(APP_VERSION)"
	@echo "  APP_COLOR=$(APP_COLOR)"
	@echo "  KIND_CLUSTER=$(KIND_CLUSTER)"

image-build:
	docker build \
		--build-arg APP_VERSION=$(APP_VERSION) \
		--build-arg APP_COLOR=$(APP_COLOR) \
		--tag $(LOCAL_IMAGE) \
		.

image-tag:
	@test -n "$(TARGET_IMAGE)" || (echo "Informe TARGET_IMAGE. Exemplo: make image-tag TARGET_IMAGE=ghcr.io/usuario/system-info-api:blue" && exit 1)
	docker tag $(LOCAL_IMAGE) $(TARGET_IMAGE)

kind-load:
	kind load docker-image $(LOCAL_IMAGE) --name $(KIND_CLUSTER)
