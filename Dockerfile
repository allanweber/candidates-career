FROM adoptopenjdk/openjdk11:alpine-jre

EXPOSE 8080

ENV JAVA_OPTS  "\
    -XX:+UnlockExperimentalVMOptions \
    -Xmx512M"

ADD target/candidates-career*.jar candidates-career.jar

#ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar candidates-career.jar"]
CMD ["/usr/bin/java", "$JAVA_OPTS -jar candidates-career.jar"]