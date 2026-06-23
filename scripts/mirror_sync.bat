@echo off
REM ============================================================
REM  Capa MIRROR (simulada) - replica el buzon FTP al espejo.
REM  Ejecuta esto manualmente o agendalo en el Programador de
REM  tareas de Windows para simular una replica periodica.
REM ============================================================

set "FTP_DIR=%USERPROFILE%\SIERRAVP_FTP"
set "MIRROR_DIR=%USERPROFILE%\SIERRAVP_MIRROR"

if not exist "%FTP_DIR%" (
    echo [Mirror] No existe la carpeta FTP: %FTP_DIR%
    echo [Mirror] Publica primero desde Analitica -> Publicar en FTP.
    exit /b 1
)

if not exist "%MIRROR_DIR%" mkdir "%MIRROR_DIR%"

echo [Mirror] Replicando %FTP_DIR%  -->  %MIRROR_DIR%
robocopy "%FTP_DIR%" "%MIRROR_DIR%" /E /NJH /NJS /NP
echo [Mirror] Replica completada.
