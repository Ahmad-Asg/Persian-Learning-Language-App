param([string]$XamppPath)
$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path $PSScriptRoot -Parent
$locations = @()
if ($XamppPath) { $locations += $XamppPath }
$locations += (Join-Path $projectRoot '.tools\xampp')
$locations += 'C:\xampp'
$xampp = $locations | Where-Object { Test-Path -LiteralPath (Join-Path $_ 'mysql\bin\mysql.exe') } | Select-Object -First 1
$mysql = if ($xampp) { Join-Path $xampp 'mysql\bin\mysql.exe' } else { $null }
$configuration = Join-Path $projectRoot 'config\local.properties'
if (-not $mysql) { throw 'XAMPP was not found. Install it in C:\xampp or run this script with -XamppPath "your XAMPP folder".' }
if (Test-Path -LiteralPath $configuration) { Write-Output 'Database configuration already exists; no account or data was changed.'; exit 0 }
$secret = [Convert]::ToHexString([System.Security.Cryptography.RandomNumberGenerator]::GetBytes(24))
$sql = @"
CREATE DATABASE IF NOT EXISTS persian_learning CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
CREATE USER 'persian_app'@'localhost' IDENTIFIED BY '$secret';
CREATE USER 'persian_app'@'127.0.0.1' IDENTIFIED BY '$secret';
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, INDEX, REFERENCES ON persian_learning.* TO 'persian_app'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, INDEX, REFERENCES ON persian_learning.* TO 'persian_app'@'127.0.0.1';
"@
& $mysql --host=127.0.0.1 --port=3306 --user=root --execute=$sql
if ($LASTEXITCODE -ne 0) { throw 'MariaDB setup failed. Check that MySQL is running in XAMPP and root access is available.' }
$settings = "db.url=jdbc:mariadb://127.0.0.1:3306/persian_learning?connectTimeout=5000`ndb.user=persian_app`ndb.password=$secret`n"
[System.IO.File]::WriteAllText($configuration,$settings,[System.Text.UTF8Encoding]::new($false))
Write-Output 'Created persian_learning and the application account. Credentials were saved only in the ignored config/local.properties file.'
