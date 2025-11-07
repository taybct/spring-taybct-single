IMAGE_NAME=spring-taybct-single
VERSION=3.5.0
docker build -f ./Dockerfile -t ghcr.io/taybct/$IMAGE_NAME:$VERSION --build-arg JAR_FILE=target/$IMAGE_NAME-$VERSION.jar .