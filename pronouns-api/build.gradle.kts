/*
 * This file was generated by the Gradle 'init' task.
 */

plugins {
    java
    `maven-publish`
    signing
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications.create<MavenPublication>("mavenJava") {
        from(components["java"])
        pom {
            artifactId = artifactId
            description.set("A Minecraft: Java Edition plugin that allows players to set pronouns.")
            url.set("https://lucyy.me")
            name.set("squirtgun")
            licenses {
                license {
                    name.set("GPL-3.0-or-later")
                    url.set("https://www.gnu.org/licenses/gpl-3.0.txt")
                }
            }
            developers {
                developer {
                    id.set("lucyy-mc")
                    name.set("Lucy Poulton")
                    email.set("lucy@poulton.xyz")
                }
            }
            scm {
                connection.set("scm:git:git://github.com/lucyy-mc/ProNouns.git")
                developerConnection.set("scm:git:git://github.com/lucyy-mc/ProNouns.git")
                url.set("https://github.com/lucyy-mc/ProNouns")
            }
        }
    }

    repositories {
        maven {
            val releasesUri = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsUri = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsUri else releasesUri

            val ossrhUsername: String? by project
            val ossrhPassword: String? by project

            if (ossrhUsername != null && ossrhPassword != null)
                credentials {
                    username = ossrhUsername
                    password = ossrhPassword
                }
        }
    }
}

signing {
    val signingKey: String? by project
    val signingPassword: String? by project
    if (signingKey != null && signingPassword != null) {
        useInMemoryPgpKeys(signingKey, signingPassword)
    }
    if (signatory == null) {
        logger.warn("No signatories available, skipping signing.")
    }
    sign(publishing.publications["mavenJava"])
}

description = "pronouns-api"