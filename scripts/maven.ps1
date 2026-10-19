param([Parameter(ValueFromRemainingArguments=$true)][string[]]$MavenArguments)
$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path $PSScriptRoot -Parent
$portableJdk = Get-ChildItem -LiteralPath (Join-Path $projectRoot '.tools\jdk') -Directory -ErrorAction SilentlyContinue | Select-Object -First 1
if ($portableJdk) { $env:JAVA_HOME = $portableJdk.FullName; $env:PATH = "$env:JAVA_HOME\bin;$env:PATH" }
$portableMaven = Join-Path $projectRoot '.tools\apache-maven-3.9.9\bin\mvn.cmd'
Push-Location $projectRoot
try {
    if (Test-Path -LiteralPath $portableMaven) { & $portableMaven "-Dmaven.repo.local=$projectRoot\.tools\m2" @MavenArguments }
    else { & mvn @MavenArguments }
    exit $LASTEXITCODE
} finally { Pop-Location }
