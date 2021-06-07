FROM rappdw/docker-java-python:openjdk1.8.0_171-python3.6.6

MAINTAINER Valentin BURGAUD <valentin.burgaud@ens-rennes.fr>

RUN python -m pip install --upgrade pip

COPY requirements.txt requirements.txt

RUN python -m pip install -r requirements.txt

WORKDIR /optimize

RUN mkdir -p sqlite/

COPY src/main/resources src/main/resources

COPY build/libs/optimization.jar optimization.jar

ENTRYPOINT ["java", "-jar", "optimization.jar"]