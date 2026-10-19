$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path $PSScriptRoot -Parent
$jdk = Get-ChildItem -LiteralPath (Join-Path $projectRoot '.tools\jdk') -Directory | Select-Object -First 1
$modules = (Get-ChildItem -Path (Join-Path $projectRoot '.tools\m2\org\openjfx') -Recurse -Filter '*-win.jar' | ForEach-Object FullName) -join ';'
$libraries = @(
    (Join-Path $projectRoot '.tools\m2\org\mariadb\jdbc\mariadb-java-client\3.5.3\mariadb-java-client-3.5.3.jar'),
    (Join-Path $projectRoot '.tools\m2\org\slf4j\slf4j-api\2.0.17\slf4j-api-2.0.17.jar'),
    (Join-Path $projectRoot '.tools\m2\org\slf4j\slf4j-nop\2.0.17\slf4j-nop-2.0.17.jar')
) -join ';'
Push-Location $projectRoot
try {
    & (Join-Path $jdk.FullName 'bin\java.exe') --module-path $modules --add-modules javafx.controls -cp "target\classes;target\test-classes;$libraries" edu.persian.UiSmoke
    exit $LASTEXITCODE
} finally { Pop-Location }
