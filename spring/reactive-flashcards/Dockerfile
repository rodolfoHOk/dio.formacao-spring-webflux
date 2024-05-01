FROM gradle:8.7.0-jdk21

RUN apt update && apt install -qq -y --no-recommends

ENV INSTALL_PATH /reactive-flashcards

RUN mkdir $INSTALL_PATH

WORKDIR $INSTALL_PATH

COPY . .
