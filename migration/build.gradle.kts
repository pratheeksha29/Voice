plugins {
  id("voice-android-library")
  alias(libs.plugins.anvil)
}

anvil {
  generateDaggerFactories.set(true)
}

android {
  buildFeatures {
    compose = true
  }
}

dependencies {
  implementation(projects.common)
  implementation(projects.strings)
  implementation(projects.playback)
  implementation(projects.data)
  implementation(projects.sleepTimer)
  implementation(projects.scanner)

  implementation(libs.dagger.core)
  implementation(libs.bundles.compose)
  implementation(libs.datastore)
  implementation(libs.prefs.core)

  testImplementation(libs.bundles.testing.jvm)
}
