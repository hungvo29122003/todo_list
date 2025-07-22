plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
    id ("kotlin-kapt")
}

android {
    namespace = "com.example.todo_list"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.todo_list"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures{
        //noinspection DataBindingWithoutKapt
        dataBinding = true
//        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Firebase
    // Import BoM cho nền tảng Firebase
//    implementation(libs.firebase.bom) // Thay thế latest_version
//
//    // Firebase Authentication
//    implementation(libs.firebase.auth.ktx)
    implementation(platform("com.google.firebase:firebase-bom:33.1.2")) // Dựa trên thông tin hiện có

    // Firebase Authentication (sẽ tự động sử dụng phiên bản tương thích từ BOM)
    implementation ("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-analytics")
    // Hoặc phiên bản mới nhất
    implementation("com.google.firebase:firebase-firestore-ktx")

    // Room (Thư viện lưu trữ dữ liệu)
//    implementation(libs.androidx.room.runtime) // Thay thế latest_version
//    annotationProcessor(libs.androidx.room.compiler) // Thay thế latest_version
//    // Để sử dụng Kotlin Symbol Processing (KSP) cho Room (tùy chọn, nhưng được khuyến nghị cho các dự án Kotlin)
//    // ksp("androidx.room:room-compiler:latest_version") // Thay thế latest_version
//    // Hỗ trợ Kotlin Extensions và Coroutines cho Room
//    implementation(libs.androidx.room.ktx) // Thay thế latest_version
//
//    // Lifecycle (ViewModel và LiveData)
//    implementation(libs.androidx.lifecycle.viewmodel.ktx) // Thay thế latest_version (ViewModel)
//    implementation(libs.androidx.lifecycle.livedata.ktx) // Thay thế latest_version (LiveData)
//
//
//    // RecyclerView
//    implementation(libs.androidx.recyclerview) // Thay thế latest_version
    implementation ("androidx.room:room-runtime:2.6.1") // PHIÊN BẢN MỚI NHẤT
    implementation ("androidx.room:room-ktx:2.6.1")
    // >>> SỬ DỤNG KAPT THAY VÌ KSP CHO Room compiler <<<
    kapt ("androidx.room:room-compiler:2.6.1")          // PHIÊN BẢN MỚI NHẤT


    // Lifecycle (ViewModel và LiveData)
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.0") // PHIÊN BẢN MỚI NHẤT
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.8.0") // PHIÊN BẢN MỚI NHẤT
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.8.0") // PHIÊN BẢN MỚI NHẤT


    // RecyclerView
    implementation ("androidx.recyclerview:recyclerview:1.3.2") // PHIÊN BẢN MỚI NHẤT

    // Material Design Components
    implementation ("com.google.android.material:material:1.12.0")
    implementation ("androidx.viewpager2:viewpager2:1.0.0")

}