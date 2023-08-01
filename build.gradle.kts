plugins {
  id("java")
  id("jacoco")
  id("io.freefair.lombok") version "8.0.1"
  id("com.diffplug.spotless") version "6.19.0"
  id("org.springframework.boot") version "3.1.2"
}

group = "net.verotek.twitch"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_17
java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.1.2")
  implementation("org.springframework.boot:spring-boot-starter-web:3.1.2")
  implementation("org.springframework.boot:spring-boot-starter-thymeleaf:3.1.2")
  implementation("org.springframework.boot:spring-boot-devtools:3.1.2")
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0")
  implementation("org.modelmapper:modelmapper:3.1.1")
  implementation("org.postgresql:postgresql:42.6.0")
  implementation("com.github.twitch4j:twitch4j:1.15.0")

  testImplementation("com.google.truth:truth:1.1.4")
  testImplementation("org.mockito:mockito-core:5.3.1")
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.3")
}

spotless {
  format("misc") {
    target("**/*.properties", ".gitignore")
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

tasks.register("bootRunDev") {
  group = "application"
  description = "Run Spring application with dev profile"
  dependencies {
    implementation("com.h2database:h2:2.1.214")
  }
  doFirst {
    tasks.bootRun.configure {
      systemProperty("spring.profiles.active", "dev")
    }
  }
  finalizedBy("bootRun")
}

tasks.bootBuildImage {
  val registry = System.getenv("CONTAINER_REGISTRY")
  val name = System.getenv("CONTAINER_NAME")
  val version = System.getenv("CONTAINER_VERSION")
  val login = System.getenv("CONTAINER_LOGIN")
  val pass = System.getenv("CONTAINER_PASS")

  imageName.set("${registry}/${name}:${version}")

  docker {
    publishRegistry {
      username.set(login)
      password.set(pass)
      url.set("https://${registry}")
    }
  }
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
