#!/usr/bin/env bash

set -euo pipefail

# Запустили локальный Kubernetes-кластер с помощью minikube, используя докер как драйвер
# (кластер будет запущен внутри докер контейнера)
minikube start --driver=docker

# Создали ConfigMap с именем selenoid-config, файл будет доступен под ключом browsers.json
kubectl create configmap selenoid-config --from-file=browsers.json=./nbank-chart/files/browsers.json \
  --dry-run=client \
  -o yaml |
kubectl apply -f -

# Устанавливаем Helm чарт с именем релиза nbank, беря шаблоны из ./nbank-chart
# Это создаст все ресурсы, описанные в шаблонах Helm (deployment, services)
helm upgrade --install nbank ./nbank-chart

echo "Ожидание запуска PostgreSQL....."
kubectl rollout status deployment/postgres --timeout=600s

echo "Ожидание запуска backend....."
kubectl rollout status deployment/backend --timeout=600s

echo "Ожидание запуска frontend....."
kubectl rollout status deployment/frontend --timeout=180s

echo "Ожидание запуска selenoid....."
kubectl rollout status deployment/selenoid --timeout=180s

echo "Ожидание запуска selenoid-ui....."
kubectl rollout status deployment/selenoid-ui --timeout=180s

#Все сервисы в неймспейсе default
echo
echo "SERVICES:"
kubectl get svc

#Все поды в неймспейсе default
echo
echo "PODS:"
kubectl get pods

#Логи конкретного сервиса
#kubectl logs deployment/backend

#Проброс портов на локальную машину
kubectl port-forward svc/frontend 3000:80 &
FRONTEND_PID=$!
kubectl port-forward svc/backend 4111:4111 &
BACKEND_PID=$!
kubectl port-forward svc/selenoid 4444:4444 &
SELENOID_PID=$!
kubectl port-forward svc/selenoid-ui 8080:8080 &
SELENOID_UI_PID=$!
kubectl port-forward service/postgres 5433:5432 &
POSTGRES_PID=$!

cleanup() {
  echo
  echo "Останавливаем port-forward..."

  kill \
    "$FRONTEND_PID" \
    "$BACKEND_PID" \
    "$SELENOID_PID" \
    "$POSTGRES_PID" \
    "$SELENOID_UI_PID" 2>/dev/null || true
}

trap cleanup EXIT INT TERM

echo
echo "Сервисы доступны:"
echo "Frontend:   http://localhost:3000"
echo "Backend:    http://localhost:4111"
echo "Selenoid:   http://localhost:4444"
echo "Selenoid UI: http://localhost:8080"
echo

wait