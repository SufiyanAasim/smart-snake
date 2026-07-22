# Build and Run script for Smart Snake
# Target JDK: Android OpenJDK 21 or system default

$JDK_PATH = "C:\Program Files\Android\openjdk\jdk-21.0.8\bin"
$JAVAC = Join-Path $JDK_PATH "javac.exe"
$JAVA = Join-Path $JDK_PATH "java.exe"
$JAR = Join-Path $JDK_PATH "jar.exe"

# Fallback to system path if the Android JDK is not found
if (-not (Test-Path $JAVAC)) {
    Write-Host "Android OpenJDK 21 not found at default location. Checking system PATH..." -ForegroundColor Yellow
    $JAVAC = "javac"
    $JAVA = "java"
    $JAR = "jar"
}

Write-Host "=== Compiling Project ===" -ForegroundColor Cyan
if (-not (Test-Path "out")) {
    New-Item -ItemType Directory -Force -Path "out" | Out-Null
}

& $JAVAC -cp "lib/sqlite-jdbc.jar;lib/slf4j-api.jar;lib/slf4j-simple.jar" -d out src/project/*.java
if ($LASTEXITCODE -ne 0) {
    Write-Error "Compilation failed!"
    exit $LASTEXITCODE
}
Write-Host "Compilation successful!" -ForegroundColor Green

Write-Host "=== Packaging JAR Executable ===" -ForegroundColor Cyan
if (-not (Test-Path "dist")) {
    New-Item -ItemType Directory -Force -Path "dist" | Out-Null
}

& $JAR --create --file dist/SmartSnake.jar --main-class project.Project -C out project
if ($LASTEXITCODE -ne 0) {
    Write-Error "Packaging failed!"
    exit $LASTEXITCODE
}
Write-Host "JAR packaged successfully at dist/SmartSnake.jar" -ForegroundColor Green

Write-Host "=== Running Smart Snake ===" -ForegroundColor Cyan
& $JAVA -cp "dist/SmartSnake.jar;lib/sqlite-jdbc.jar;lib/slf4j-api.jar;lib/slf4j-simple.jar" project.Project
