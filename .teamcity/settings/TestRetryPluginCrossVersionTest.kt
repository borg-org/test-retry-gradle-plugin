import jetbrains.buildServer.configs.kotlin.v2018_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2018_2.DslContext
import jetbrains.buildServer.configs.kotlin.v2018_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.v2018_2.triggers.vcs

open class TestRetryPluginCrossVersionTest(os: Os) : BuildType({
    testRetryVcs()

    params {
        java8Home(os)
    }

    steps {
        gradle {
            tasks = "clean build"
            buildFile = ""
            gradleParams = "-s $useGradleInternalScansServer"
            param("org.jfrog.artifactory.selectedDeployableServer.defaultModuleVersionConfiguration", "GLOBAL")
        }
    }

    triggers {
        vcs {
            branchFilter = """
                +:*
                -:pull/*
            """.trimIndent()
        }
    }

    features {
        commitStatusPublisher {
            vcsRootExtId = "${DslContext.settingsRoot.id}"
            publisher = github {
                githubUrl = "https://api.github.com"
                authType = personalToken {
                    token = "credentialsJSON:7c8c64ad-776d-4469-8400-5618da5de337"
                }
            }
        }
    }

    agentRequirement(os)
}) {
    constructor(os: Os, init: TestRetryPluginCrossVersionTest.() -> Unit) : this(os) {
        init()
    }
}

object LinuxJava18 : TestRetryPluginCrossVersionTest(Os.linux, {
    name = "CrossVersionTest Linux - Java 1.8"
})

object MacOSJava18 : TestRetryPluginCrossVersionTest(Os.macos, {
    name = "CrossVersionTest MacOS - Java 1.8"
})

object WindowsJava18 : TestRetryPluginCrossVersionTest(Os.windows, {
    name = "CrossVersionTest Windows - Java 1.8"
})