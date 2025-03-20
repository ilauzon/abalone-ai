How to run the State Space Generator!

1. From the project root, navigate to ./composeApp/build/compose/binaries/main/app/com.bcit.abalone/
2. Put any testing .input files to be run inside that folder.
3. Run "com.bcit.abalone.exe"
4. Click the "Go to State Space Generator" button.
5. Click on the file you want to choose then click the "Enter" button.
6. The output files wil be generated in the same file as the executable.

How to compile the project from the source!

1. Make sure that java >= 21 is installed.
2. In the root directory of the project in the cmd, comp3981-abalone/, run "gradlew.bat createDistributable"
3. Navigate to ./composeApp/build/compose/binaries/main/app/com.bcit.abalone/
4. Run "com.bcit.abalone.exe"
   
This is a Kotlin Multiplatform project targeting Desktop.

* `/composeApp` is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - `commonMain` is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    `iosMain` would be the right folder for such calls.


Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…