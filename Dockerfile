####################################
## This is a Dockerfile for building an image for running the GC Server
##
####################################
FROM ubuntu

# TODO: command to get the project in container so build can be ran

RUN ./gradlew build
