#!/usr/bin/env bash

set -euo pipefail

# Корневая директория проекта — папка, где лежит этот скрипт.
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

COMPOSE_FILE="${PROJECT_DIR}/infra/docker_compose/docker-compose.yml"
JSON_FILE="${PROJECT_DIR}/infra/docker_compose/config/browsers.json"
ENV_FILE="$PROJECT_DIR/.env"
ENV_FILE_WINDOWS="$(cygpath -w "$ENV_FILE")"
# Позволяет не пересекаться с другими compose-проектами.
COMPOSE_PROJECT_NAME="nbank-autotests"

#Настройка для запуска автотестов
IMAGE_NAME=nbank-tests
TEST_PROFILE=${1:-all} #аргумент запуска
TIMESTAMP=$(date +"%Y%m%d_%H%M")
TEST_OUTPUT_DIR="${PROJECT_DIR}/test-output/$TIMESTAMP"

cleanup() {
  EXIT_CODE=$?

  echo
  echo ">>> Остановка Docker Compose окружения"

  docker compose \
    --project-name "$COMPOSE_PROJECT_NAME" \
    -f "$COMPOSE_FILE" \
    down --volumes --remove-orphans

  exit "$EXIT_CODE"
}

echo ">>> Проверка Docker"

if ! command -v docker >/dev/null 2>&1; then
  echo "Ошибка: команда docker не найдена"
  exit 1
fi

if ! docker info >/dev/null 2>&1; then
  echo "Ошибка: Docker не запущен"
  exit 1
fi

if ! docker compose version >/dev/null 2>&1; then
  echo "Ошибка: Docker Compose недоступен"
  exit 1
fi

echo ">>> Проверка файлов"

if [[ ! -f "$COMPOSE_FILE" ]]; then
  echo "Ошибка: docker-compose.yml не найден:"
  echo "$COMPOSE_FILE"
  exit 1
fi

if [[ ! -f "$ENV_FILE" ]]; then
  echo "Ошибка: файл .env не найден:"
  echo "$ENV_FILE"
  exit 1
fi

if [[ ! -f "$JSON_FILE" ]]; then
  echo "Ошибка: browsers.json не найден:"
  echo "$JSON_FILE"
  exit 1
fi

echo ">>> Docker pull все образы браузеров"

# Проверяем что jq установлен
if ! command -v jq &> /dev/null; then
  echo "Ошибка: jq не установлен"
  exit 1
fi

# Извлекаем все значения .image через jq
jq -r '.[].versions[].image' "$JSON_FILE" |
  tr -d '\r' |
  while IFS= read -r image; do
    echo "Pulling $image"
    docker pull "$image"
  done

echo ">>> Очистка предыдущего Docker Compose окружения"

docker compose \
  --project-name "$COMPOSE_PROJECT_NAME" \
  -f "$COMPOSE_FILE" \
  down --volumes --remove-orphans

echo ">>> Запуск Docker Compose"

trap cleanup EXIT

docker compose \
  --project-name "$COMPOSE_PROJECT_NAME" \
  -f "$COMPOSE_FILE" \
  up -d \
  --wait

#Собираем докер образ
echo ">>> Сборка тестов запущена"
docker build -t "${IMAGE_NAME}" "${PROJECT_DIR}"

mkdir -p "$TEST_OUTPUT_DIR/logs"
mkdir -p "$TEST_OUTPUT_DIR/results"
mkdir -p "$TEST_OUTPUT_DIR/report"

#Запуск докер контейнера
echo ">>> Тесты запущены"

if MSYS_NO_PATHCONV=1 docker run --rm \
  -v "$TEST_OUTPUT_DIR/logs":/app/logs \
  -v "$TEST_OUTPUT_DIR/results":/app/target/surefire-reports \
  -v "$TEST_OUTPUT_DIR/report":/app/target/site \
  -e TEST_PROFILE="$TEST_PROFILE" \
  --env-file "${ENV_FILE_WINDOWS}" \
  --network nbank-network \
  "$IMAGE_NAME"
then
  TEST_EXIT_CODE=0
else
  TEST_EXIT_CODE=$?
fi

echo
echo ">>> Тесты завершены"

if [[ "$TEST_EXIT_CODE" -eq 0 ]]; then
  echo "Статус: успешно"
else
  echo "Статус: есть упавшие тесты, код завершения: $TEST_EXIT_CODE"
fi

echo "Лог файл: $TEST_OUTPUT_DIR/logs/run.log"
echo "Результаты тестов: $TEST_OUTPUT_DIR/results"
echo "Финальный репорт: $TEST_OUTPUT_DIR/report"

exit "$TEST_EXIT_CODE"