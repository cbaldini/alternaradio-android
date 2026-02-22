#!/bin/bash
# Script para copiar el APK a Desktop y mostrar instrucciones

APK_SOURCE="/Users/cristianbaldini/AlternaRadio/web/alternaradio-android/app/build/outputs/apk/debug/app-debug.apk"
APK_DEST="$HOME/Desktop/alterna-radio-corregido.apk"

echo "================================================"
echo "  Copiando APK a Desktop..."
echo "================================================"

if [ -f "$APK_SOURCE" ]; then
    cp "$APK_SOURCE" "$APK_DEST"
    echo ""
    echo "✅ APK copiado exitosamente a:"
    echo "   $APK_DEST"
    echo ""
    echo "================================================"
    echo "  PRÓXIMOS PASOS:"
    echo "================================================"
    echo ""
    echo "1. Abre el Finder"
    echo "2. Ve a Desktop"
    echo "3. Busca: alterna-radio-corregido.apk"
    echo "4. Click derecho → Compartir → WhatsApp"
    echo "5. O arrastra el archivo a WhatsApp"
    echo ""
    echo "En el Motorola G15:"
    echo "1. Abre el mensaje en WhatsApp"
    echo "2. Toca el archivo APK"
    echo "3. Toca instalar"
    echo ""
    echo "================================================"
else
    echo "❌ ERROR: APK no encontrado en:"
    echo "   $APK_SOURCE"
    echo ""
    echo "Asegúrate de compilar primero con:"
    echo "   ./gradlew assembleDebug"
fi

