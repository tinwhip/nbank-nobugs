# базовый докер образ
# каждый раз с нуля строить базовый образ (java, mvn, git)
# голый докер образ и устанавливать java, mvn
# если можно создать образ поверх другого образа, где все уже установлено
# маркетплейс всех докер образов - docker hub
FROM maven:3.9.9-eclipse-temurin-17

# дефолтные значения аргументов
ARG TEST_PROFILE=api
ARG APIBASEURL=http://localhost:4111
ARG UIBASEURL=http://localhost:3000
ARG UIREMOTE=http://localhost:4444/wd/hub

# Переменные окружения для контейнера
ENV TEST_PROFILE=${TEST_PROFILE}
ENV APIBASEURL=${APIBASEURL}
ENV UIBASEURL=${UIBASEURL}
ENV UIREMOTE=${UIREMOTE}

# работаем из папки app
WORKDIR /app

# Копируем только помник
COPY pom.xml .

# Загружаем зависимости и кэшируем
RUN mvn dependency:go-offline

# Копируем весь проект
COPY . .

# Теперь внутри есть зависимости, есть весь проект и мы готовы запускать тесты
USER root

# mvn test -P api
# mvn -DskipTests=true surefire-report:report
# лог выводился не в консоль, а в файл
# bash file
CMD /bin/bash -c " \
    mkdir -p /app/logs ; \
    { \
    echo '>>> Running tests with profile ${TEST_PROFILE}' ; \
    mvn test -q -P ${TEST_PROFILE} ; \
    \
    echo '>>> Running surefire-report:report' ; \
    mvn -DskipTests=true surefire-report:report ; \
    } > /app/logs/run.log 2>&1"