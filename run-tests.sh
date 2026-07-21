#!/bin/bash

#Настройка
IMAGE_NAME=nbank-tests
TEST_PROFILE=${1:-api} #аргумент запуска
TIMESTAMP=$(date +"%Y%m%d_%H%M")
TEST_OUTPUT_DIR="$(pwd)/test-output/$TIMESTAMP"

#Собираем докер образ
echo ">>> Сборка тестов запущена"
docker build -t $IMAGE_NAME .

mkdir -p "$TEST_OUTPUT_DIR/logs"
mkdir -p "$TEST_OUTPUT_DIR/results"
mkdir -p "$TEST_OUTPUT_DIR/report"

#Запуск докер контейнера
echo ">>> Тесты запущены"
MSYS_NO_PATHCONV=1 docker run --rm \
  -v "$TEST_OUTPUT_DIR/logs":/app/logs \
  -v "$TEST_OUTPUT_DIR/results":/app/target/surefire-reports \
  -v "$TEST_OUTPUT_DIR/report":/app/target/site \
  -e TEST_PROFILE="$TEST_PROFILE" \
  -e APIBASEURL=http://192.168.1.66:4111 \
  -e UIBASEURL=http://192.168.1.66:3000 \
$IMAGE_NAME

#Вывод итогов
echo ">>> Тесты завершены"
echo "Лог файл: $TEST_OUTPUT_DIR/logs/run.log"
echo "Результаты тестов: $TEST_OUTPUT_DIR/results"
echo "Финальный репорт: $TEST_OUTPUT_DIR/report"