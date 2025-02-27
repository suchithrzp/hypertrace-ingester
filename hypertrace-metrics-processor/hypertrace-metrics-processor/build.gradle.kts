plugins {
  java
  application
  jacoco
  id("org.hypertrace.docker-java-application-plugin")
  id("org.hypertrace.docker-publish-plugin")
  id("org.hypertrace.jacoco-report-plugin")
}

application {
  mainClass.set("org.hypertrace.core.serviceframework.PlatformServiceLauncher")
}

hypertraceDocker {
  defaultImage {
    imageName.set("hypertrace-ingester")
    javaApplication {
      serviceName.set("${project.name}")
      adminPort.set(8099)
    }
    namespace.set("razorpay")
  }
  tag("${project.name}" + "_" + System.getenv("IMAGE_TAG"))
}

tasks.test {
  useJUnitPlatform()
}

dependencies {
  // internal projects
  implementation(project(":hypertrace-view-generator:hypertrace-view-generator-api"))

  // frameworks
  implementation("org.hypertrace.core.serviceframework:platform-service-framework:0.1.33")
  implementation("org.hypertrace.core.serviceframework:platform-metrics:0.1.33")
  implementation("org.hypertrace.core.kafkastreams.framework:kafka-streams-framework:0.1.23")

  // open telemetry proto
  implementation("io.opentelemetry:opentelemetry-proto:1.6.0-alpha")

  constraints {
    implementation("org.glassfish.jersey.core:jersey-common:2.34") {
      because("introduced by org.hypertrace.core.kafkastreams.framework:" +
          "kafka-streams-framework@0.1.21 > io.confluent:kafka-streams-avro-serde@6.0.1 > " +
          "io.confluent:kafka-schema-registry-client@6.0.1 > " +
          "org.glassfish.jersey.core:jersey-common@2.30")
    }
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.1") {
      because("Denial of Service (DoS) " +
          "[Medium Severity][https://snyk.io/vuln/SNYK-JAVA-COMFASTERXMLJACKSONCORE-2326698] " +
          "in com.fasterxml.jackson.core:jackson-databind@2.12.2")
    }
  }

  // test
  testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
  testImplementation("org.mockito:mockito-core:3.8.0")
  testImplementation("org.junit-pioneer:junit-pioneer:1.3.8")
  testImplementation("org.apache.kafka:kafka-streams-test-utils:6.0.1-ccs")
}
