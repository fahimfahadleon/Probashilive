plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    signingConfigs {
        getByName("debug") {
            storeFile = file("/home/fahim/StudioProjects/Probashilive/probashilive.jks")
            storePassword = "64742812"
            keyAlias = "key0"
            keyPassword = "64742812"
        }
        create("release") {
            storeFile = file("/home/fahim/StudioProjects/Probashilive/probashilive.jks")
            storePassword = "64742812"
            keyAlias = "key0"
            keyPassword = "64742812"
        }
    }
    namespace = "com.probashiincltd.probashilive"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.probashiincltd.probashilive"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures{
        viewBinding = true
        dataBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }

        debug {
            signingConfig = signingConfigs.getByName("debug")
        }
    
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    packaging {
        resources {
            resources.excludes.add("META-INF/INDEX.LIST")
            resources.excludes.add("META-INF/INDEX.LIST")
            resources.excludes.add("META-INF/DEPENDENCIES")
            resources.excludes.add("META-INF/LICENSE")
            resources.excludes.add("META-INF/LICENSE.txt")
            resources.excludes.add("META-INF/license.txt")
            resources.excludes.add("META-INF/NOTICE")
            resources.excludes.add("META-INF/NOTICE.txt")
            resources.excludes.add("META-INF/notice.txt")
            resources.excludes.add("META-INF/ASL2.0")
            resources.excludes.add("META-INF/*.kotlin_module")
        }
    }
    buildToolsVersion = "34.0.0"

    configurations {
        all {
            exclude(group = "xpp3", module = "xpp3")
        }
    }

}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-crashlytics:18.6.2")
    implementation("com.google.firebase:firebase-messaging:23.4.1")
    implementation("androidx.activity:activity:1.8.0")
    testImplementation("junit:junit:4.14-SNAPSHOT")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation(platform("com.google.firebase:firebase-bom:32.7.2"))
    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.google.android.gms:play-services-auth:20.7.0")
    implementation(files("libs/java-json.jar"))


    //library implementation
    implementation ("com.intuit.sdp:sdp-android:1.1.0")

    implementation ("io.github.scwang90:refresh-layout-kernel:2.1.0")
    implementation ("io.github.scwang90:refresh-header-classics:2.1.0")
    implementation ("io.github.scwang90:refresh-footer-classics:2.1.0")
    implementation ("de.hdodenhof:circleimageview:3.1.0")

    implementation ("org.igniterealtime.smack:smack-android:4.4.7")
    implementation ("org.igniterealtime.smack:smack-android-extensions:4.4.7")
    implementation ("org.igniterealtime.smack:smack-tcp:4.4.7")
    implementation ("org.igniterealtime.smack:smack-core:4.4.7")
    implementation ("org.igniterealtime.smack:smack-im:4.4.7")
    implementation ("org.igniterealtime.smack:smack-legacy:4.4.7")
    implementation ("org.igniterealtime.smack:smack-bosh:4.4.7")
    implementation ("org.igniterealtime.smack:smack-resolver-minidns:4.4.7")
    implementation ("org.igniterealtime.smack:smack-resolver-dnsjava:4.4.7")


    implementation ("com.github.bumptech.glide:glide:4.16.0")

    implementation ("com.guolindev.permissionx:permissionx:1.7.1")

    implementation ("com.google.code.gson:gson:2.10.1")


    val camerax_version = "1.4.0-alpha04"
    implementation("androidx.camera:camera-core:${camerax_version}")
    implementation("androidx.camera:camera-camera2:${camerax_version}")
    implementation("androidx.camera:camera-lifecycle:${camerax_version}")
    implementation("androidx.camera:camera-video:${camerax_version}")
    implementation("androidx.camera:camera-view:${camerax_version}")
    implementation("androidx.camera:camera-mlkit-vision:${camerax_version}")
    implementation("androidx.camera:camera-extensions:${camerax_version}")

    implementation ("com.github.f0ris.sweetalert:library:1.6.2")
    implementation ("com.github.NodeMedia:NodeMediaClient-Android:3.2.7")


//    implementation ("com.arthenica:mobile-ffmpeg-full:4.4")



}