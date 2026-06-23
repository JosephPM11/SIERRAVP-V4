#!/usr/bin/env bash
# ============================================================
#  Capa MIRROR (simulada) - replica el buzon FTP al espejo.
#  Agendable con cron para simular una replica periodica.
# ============================================================
set -euo pipefail

FTP_DIR="${HOME}/SIERRAVP_FTP"
MIRROR_DIR="${HOME}/SIERRAVP_MIRROR"

if [ ! -d "$FTP_DIR" ]; then
  echo "[Mirror] No existe la carpeta FTP: $FTP_DIR"
  echo "[Mirror] Publica primero desde Analitica -> Publicar en FTP."
  exit 1
fi

mkdir -p "$MIRROR_DIR"
echo "[Mirror] Replicando $FTP_DIR  -->  $MIRROR_DIR"
cp -f "$FTP_DIR"/* "$MIRROR_DIR"/ 2>/dev/null || true
echo "[Mirror] Replica completada."
