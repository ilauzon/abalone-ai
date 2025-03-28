# How to run the Game Program:

1. From the project root, navigate to ./composeApp/build/compose/binaries/main/app/com.bcit.abalone/
2. Run "com.bcit.abalone.exe".
3. Choose the game settings you'd like to play with. Click "Apply Settings".
4. If choosing a mode where the bot moves first, click "Start" to start the game, otherwise 
start your turn and enjoy the game!

# How to run the State Space Generator:

1. From the project root, navigate to ./composeApp/build/compose/binaries/main/app/com.bcit.abalone/
2. Put any testing .input files to be run inside that folder.
3. Run "com.bcit.abalone.exe".
4. Click the "Go to State Space Generator" button.
5. Click on the file you want to choose then click the "Enter" button.
6. The output files wil be generated in the same folder as the executable.

# How to compile the project from source:

## Dependencies:
- JDK >= version 21

## Instructions:
1. In the project's root directory, comp3981-abalone/, run "gradlew.bat createDistributable"
2. Navigate to ./composeApp/build/compose/binaries/main/app/com.bcit.abalone/ to see the compiled executable "com.bcit.abalone.exe".
3. To use the executable, follow the steps to run the State Space Generator as described above.
   
This is a Kotlin Multiplatform project targeting Desktop.
Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…