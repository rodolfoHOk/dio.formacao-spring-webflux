FROM gradle:8.7.0-jdk21

RUN apt update && apt upgrade -qq -y

ENV INSTALL_PATH /reactive-flashcards

RUN mkdir $INSTALL_PATH

WORKDIR $INSTALL_PATH

COPY . .
