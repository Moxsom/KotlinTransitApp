import java.lang.System.getProperty

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://api.mapbox.com/downloads/v2/releases/maven") {
            authentication {
                create<BasicAuthentication>("mapbox")
            }
            credentials {
                // Do not change the username below.
                // This should always be mapbox (not your username).
                username = "mapbox"
                // Use the secret token you stored in gradle.properties as the password
                password = "sk.eyJ1IjoibW94c29tIiwiYSI6ImNscDFmbWpqOTBncGUyam80MXIyNDM5cWcifQ.UsqwAqFA7CUrYMRAfRmRnQ"
            }
        }
    }
}


rootProject.name = "TransitApp"
include(":app")
