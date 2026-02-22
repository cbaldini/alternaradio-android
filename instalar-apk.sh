#!/bin/bash

# Script para instalar la app en el teléfono Android

echo "=== Alterna Radio - Instalador ==="
echo ""

# Verificar si adb está disponible
if ! command -v adb &> /dev/null; then
    echo "❌ Error: adb no está instalado o no está en el PATH"
    echo "Instala Android SDK Platform Tools desde: https://developer.android.com/tools/releases/platform-tools"
    exit 1
fi

# Verificar si hay dispositivos conectados
echo "Buscando dispositivos conectados..."
device_count=$(adb devices | grep -c "device$")

if [ $device_count -eq 0 ]; then
    echo "❌ Error: No hay dispositivos Android conectados"
    echo "Por favor:"
    echo "  1. Conecta tu teléfono por USB"
    echo "  2. Habilita 'Depuración por USB' en Opciones del Desarrollador"
    echo "  3. Acepta el diálogo de depuración en el teléfono"
    exit 1
fi

echo "✅ Dispositivo encontrado"
echo ""

# Ruta del APK
APK_PATH="app/build/outputs/apk/debug/app-debug.apk"

# Verificar que el APK existe
if [ ! -f "$APK_PATH" ]; then
    echo "❌ Error: El APK no se encuentra en $APK_PATH"
    echo "Por favor, compila primero con: ./gradlew clean assembleDebug"
    exit 1
fi

echo "📦 Instalando APK..."
adb install -r "$APK_PATH"

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Instalación completada exitosamente"
    echo ""
    echo "Para abrir la app, puedes:"
    echo "  1. Buscar 'Alterna Radio' en el menú de aplicaciones"
    echo "  2. O ejecutar: adb shell am start -n ar.alternaradio.app/.MainActivity"
    echo ""
    echo "Para ver los logs de depuración:"
    echo "  adb logcat | grep AlternaRadio"
else
    echo ""
    echo "❌ Error durante la instalación"
    echo "Posibles causas:"
    echo "  - El APK es incompatible con tu dispositivo"
    echo "  - No hay espacio en el teléfono"
    echo "  - La app ya está instalada (intenta: adb uninstall ar.alternaradio.app)"
    exit 1
fi


