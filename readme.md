# Learn Couchbase Lite with Kotlin and Jetpack Compose

In this training you will be reviewing an Android Application written in Kotlin and JetPack Compose that uses the Couchbase Lite Android SDK for Kotlin. You will learn how to get and insert documents using the key-value engine, query the database using the QueryBuilder engine or SQL++, and learn how to sync information between your mobile app and Couchbase Capella with App Services. 


## Prerequisites
To run this prebuilt project, you will need:
- Familiarity with building Android Apps with <a target="_blank" rel="noopener noreferrer" href="https://developer.android.com/kotlin">Kotlin</a>, <a target="_blank" rel="noopener noreferrer"  href="https://developer.android.com/jetpack/compose/mental-model">JetPack Compose</a>, and Android Studio 
- [Android Studio Chimpmuck or above](https://developer.android.com/studio)
- Android SDK installed and setup (> v.32.0.0)
- Android Build Tools (> v.32.0.0)
- Android device or emulator running API level 23 or above
- JDK 11 (now embedded into Android Studio 4+)

### Installing Couchbase Lite Framework

- src/build.gradle already contains the appropriate additions for downloading and utilizing the Android Couchbase Lite dependency module. However, in the future, to include Couchbase Lite support within an Android app add the following within the Module gradle file (src/app/build.gradle)

```bash
allprojects {
    repositories {
        ...

        maven {
            url "https://mobile.maven.couchbase.com/maven2/dev/"
        }
    }
}
``` 
 
Then add the following to the <a target="_blank" rel="noopener noreferrer" href="https://github.com/couchbase-examples/android-kotlin-cblite-inventory-standalone/blob/main/src/app/build.gradle">app/build.gradle</a> file.

```bash
dependencies {
    ...

    implementation "com.couchbase.lite:couchbase-lite-android-ktx:3.0.2"
}
```

For more information on installation, please see the [Couchbase Lite Documentation](https://docs.couchbase.com/couchbase-lite/current/android/gs-install.html).

## Demo Application 

### Overview

TODO

### Architecture

The demo application uses <a target="_blank" rel="noopener noreferrer" href="https://developer.android.com/jetpack/guide">application architecture</a> concepts in developing modern Android applications recommended by the Android development team.  

<a target="_blank" rel="noopener noreferrer" href="https://insert-koin.io/">Koin</a>, the popular open-source Kotlin based injection library, is used to manage dependency inversion and injection.  Using Koin we can use JDK 11 versus Hilt or Dagger, which requires JDK 8.  

The application structure is a single Activity that uses <a target="_blank" rel="noopener noreferrer"  href="https://developer.android.com/jetpack/compose/mental-model">JetPack Compose</a> to render the multiple compose-based views.  In addition, the <a target="_blank" rel="noopener noreferrer" href="https://developer.android.com/jetpack/compose/navigation">Navigation Graph</a> is used to handle routing and navigation between various views.  

The Inventory Database is a custom class that manages the database state and lifecycle.  Querying and updating documents in the database is handled using the <a target="_blank" rel="noopener noreferrer" href="https://developer.android.com/jetpack/guide#data-layer">repository pattern</a>.  <a target="_blank" rel="noopener noreferrer" href="https://developer.android.com/jetpack/guide#domain-layer">ViewModels</a> will query or post updates to the repository and control the state of objects that the compose-based Views can use to display information. 

### Application Flow

TODO 

## Try it out

* Open src/build.gradle using Android Studio.
* Build and run the project.
* Verify that you see the login screen.

