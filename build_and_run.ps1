# Build and Run script for Smart Snake Game
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
if (-not (Test-Path "out/production/Project")) {
    New-Item -ItemType Directory -Force -Path "out/production/Project" | Out-Null
}

& $JAVAC -d out/production/Project src/project/*.java
if ($LASTEXITCODE -ne 0) {
    Write-Error "Compilation failed!"
    exit $LASTEXITCODE
}
Write-Host "Compilation successful!" -ForegroundColor Green

Write-Host "=== Packaging JAR Executable ===" -ForegroundColor Cyan
if (-not (Test-Path "dist")) {
    New-Item -ItemType Directory -Force -Path "dist" | Out-Null
}

& $JAR --create --file dist/SmartSnakeGame.jar --main-class project.Project -C out/production/Project project
if ($LASTEXITCODE -ne 0) {
    Write-Error "Packaging failed!"
    exit $LASTEXITCODE
}
Write-Host "JAR packaged successfully at dist/SmartSnakeGame.jar" -ForegroundColor Green

Write-Host "=== Running Smart Snake Game ===" -ForegroundColor Cyan
& $JAVA -jar dist/SmartSnakeGame.jar
