FROM java:8
ADD target/sample1-0.0.1-SNAPSHOT-jar-with-dependencies.jar /opt/hzl/
CMD ["java", "-jar", "/opt/hzl/sample1-0.0.1-SNAPSHOT-jar-with-dependencies.jar"]
