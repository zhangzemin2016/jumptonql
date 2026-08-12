import java.net.URI

plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.5.0"
}

group = "com.skyland"
version = "1.0.7"

repositories {
    mavenLocal()
    maven { url = URI("https://maven.aliyun.com/repository/public") }
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
dependencies {
    intellijPlatform {
//        intellijIdeaCommunity("2024.2.5")
        local("C:/Program Files/idea")

        // Add necessary plugin dependencies for compilation here, example:
        bundledPlugin("com.intellij.java")
    }
}

intellijPlatform {
    buildSearchableOptions = false
    
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "242"
            untilBuild = "262.*"
        }
        changeNotes = """
      <p>
      Test compatibility with version 262 <br/>
      </p>
      
      <p>
      使用261版本测试兼容
      </p>
    """.trimIndent()
    }
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }
}

