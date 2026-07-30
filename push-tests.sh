#!/usr/bin/env bash

set -euo pipefail

DOCKERHUB_USERNAME="tinwhip"
IMAGE_NAME="nobugs-autotests"
TAG="${1:?Не передан тег Docker-образа}"

if [[ -z "${DOCKERHUB_TOKEN:-}" ]]; then
    echo "В переменные окружения не передан токен"
    exit 1
fi

DOCKERHUB_IMAGE="${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${TAG}"

echo "Авторизация в Dockerhub"
printf "%s" "${DOCKERHUB_TOKEN}" | docker login \
    --username "${DOCKERHUB_USERNAME}" \
    --password-stdin

echo "Билд образа докерфайла ${DOCKERHUB_IMAGE}"
docker build \
    --tag "${DOCKERHUB_IMAGE}" .

echo "Пуш образа"
docker push "${DOCKERHUB_IMAGE}"

echo "Для пула образа нужно произвести команду: docker pull ${DOCKERHUB_IMAGE}"