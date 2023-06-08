plugins {
  id("java")
  id("jacoco")
  id("io.freefair.lombok") version "8.0.1"
  id("com.diffplug.spotless") version "6.18.0"
  id("org.springframework.boot") version "3.1.0"
}

group = "net.verotek.twitch"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_17
java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.1.0")
  implementation("org.springframework.boot:spring-boot-starter-web:3.1.0")
  implementation("org.springframework.boot:spring-boot-starter-thymeleaf:3.1.0")
  implementation("org.springframework.boot:spring-boot-devtools:3.1.0")
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0")
  implementation("org.modelmapper:modelmapper:3.1.1")
  implementation("org.postgresql:postgresql:42.6.0")
  implementation("com.github.twitch4j:twitch4j:1.15.0")

  testImplementation("com.google.truth:truth:1.1.4")
  testImplementation("org.mockito:mockito-core:5.3.1")
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

spotless {
  format("misc") {
    target("*.md", ".gitignore")
    trimTrailingWhitespace()
    indentWithTabs()
    endWithNewline()
  }
  java {
    googleJavaFormat()
    formatAnnotations()
  }
  kotlinGradle {
    trimTrailingWhitespace()
    indentWithSpaces(2)
    endWithNewline()
  }
}

jacoco {
  toolVersion = "0.8.8"
  reportsDirectory.set(layout.buildDirectory.dir("jacoco"))
}

tasks.jacocoTestReport {
  dependsOn(tasks.test)
  reports {
    html.required.set(true)
    html.outputLocation.set(layout.buildDirectory.dir("jacoco/html"))
    xml.required.set(false)
    csv.required.set(false)
  }
}

tasks.test {
  useJUnitPlatform()
  testLogging {
    events("passed", "skipped", "failed")
  }
  jacoco {
    isEnabled = true
  }
  finalizedBy(tasks.jacocoTestReport)
}
