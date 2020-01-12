FROM hseeberger/scala-sbt:8u222_1.3.6_2.12.10

ARG GID
ARG UID

RUN groupmod -g $GID sbtuser && usermod -u $UID sbtuser
