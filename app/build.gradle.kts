plugins {
    antlr
    groovy
    application
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://artifactory.openpreservation.org/artifactory/vera-dev/")
    }
    maven {
        url = uri("https://repository.jboss.org/nexus/content/repositories/thirdparty-releases/")
        url = uri("https://repository.jboss.org/maven2")
    }
    maven {
        url = uri("https://repository.jboss.org/nexus/content/repositories/thirdparty-releases")
    }
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

dependencies {
    testImplementation("org.apache.groovy:groovy:4.0.12")
    testImplementation("org.spockframework:spock-core:2.3-groovy-4.0")
    testImplementation("junit:junit:4.13.2")
    implementation("com.google.guava:guava:31.1-jre")
    antlr("org.antlr:antlr4:4.12.0")
    implementation("javax.media:jai-core:unknown")
    implementation("org.apache.velocity:velocity-engine-core:2.3")
    implementation("gov.nasa.pds:validate:3.2.0")
    implementation("commons-io:commons-io:2.12.0")
    implementation("info.picocli:picocli:4.7.3")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

application {
    applicationName = "pds3to4"
    mainClass.set("org.thirtyninealpharesearch.chemin.App")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.generateGrammarSource {
    arguments = arguments + listOf("-no-visitor", "-package", "org.thirtyninealpharesearch.chemin.pds3")
}

tasks.generateTestGrammarSource {
    arguments = arguments + listOf("-no-visitor", "-package", "org.thirtyninealpharesearch.chemin.pds3")
}

tasks.distTar {
    compression = Compression.GZIP
    archiveExtension.set("tar.gz")
}
