#!/bin/bash

# Script de diagnóstico para Alterna Radio

echo "=== Diagnóstico Alterna Radio ==="
echo ""

# Verificar adb
echo "1. Verificando adb..."
if command -v adb &> /dev/null; then
    echo "   ✅ adb disponible en: $(which adb)"
else
    echo "   ❌ adb NO disponible"
    exit 1
fi

echo ""
echo "2. Verificando dispositivos conectados..."
adb_output=$(adb devices)
device_count=$(echo "$adb_output" | grep -c "device$")

if [ $device_count -eq 0 ]; then
    echo "   ❌ No hay dispositivos conectados"
    exit 1
else
    echo "   ✅ Dispositivos encontrados:"
    echo "$adb_output" | grep "device$"
fi

echo ""
echo "3. Verificando si la app está instalada..."
if adb shell pm list packages | grep -q "ar.alternaradio.app"; then
    echo "   ✅ App instalada"
else
    echo "   ❌ App NO instalada"
fi

echo ""
echo "4. Logs en tiempo real (presiona Ctrl+C para salir):"
echo "   Filtrando por 'AlternaRadio'..."
echo ""

# Limpiar logs anteriores
adb logcat -c

# Iniciar la app si está instalada
if adb shell pm list packages | grep -q "ar.alternaradio.app"; then
    echo "   Iniciando app..."
    adb shell am start -n ar.alternaradio.app/.MainActivity
    sleep 2
fi

# Mostrar logs
adb logcat --pidof ar.alternaradio.app | grep -E "AlternaRadio|AndroidRuntime|WebView" || adb logcat | grep -E "AlternaRadio|AndroidRuntime|WebView"


