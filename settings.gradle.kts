plugins {
    id("com.gradle.enterprise") version "3.11.4"
    id("org.danilopianini.gradle-pre-commit-git-hooks") version "1.0.20"
}

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
        publishOnFailure()
    }
}

gitHooks {
    commitMsg { conventionalCommits() }
    createHooks()
}

rootProject.name = "thread-inheritable-resource-loader".toLowerCase()
