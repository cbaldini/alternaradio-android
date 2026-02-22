#!/bin/bash

# ============================================
# COMANDOS ÚTILES - Alterna Radio Android
# ============================================

echo ""
echo "╔════════════════════════════════════════╗"
echo "║  Alterna Radio - Comandos Útiles      ║"
echo "╚════════════════════════════════════════╝"
echo ""

# Función para mostrar menú
mostrar_menu() {
    echo "Selecciona una acción:"
    echo ""
    echo "COMPILACIÓN:"
    echo "  1) Compilar APK Debug"
    echo "  2) Compilar APK Release"
    echo "  3) Limpiar proyecto"
    echo ""
    echo "INSTALACIÓN:"
    echo "  4) Instalar APK en dispositivo"
    echo "  5) Desinstalar app del dispositivo"
    echo ""
    echo "INFORMACIÓN:"
    echo "  6) Ver dispositivos conectados"
    echo "  7) Ver logs de la app"
    echo "  8) Ver tamaño del APK"
    echo ""
    echo "UTILIDADES:"
    echo "  9) Abrir ubicación del APK"
    echo " 10) Verificar estructura del proyecto"
    echo ""
    echo "  0) Salir"
    echo ""
}

# Verificar que estamos en el directorio correcto
if [ ! -f "settings.gradle" ]; then
    echo "❌ Error: Ejecuta este script desde alternaradio-android/"
    exit 1
fi

# Dar permisos a gradlew
chmod +x gradlew 2>/dev/null

# Mostrar menú
mostrar_menu
read -p "Opción: " opcion

case $opcion in
    1)
        echo ""
        echo "🔨 Compilando APK Debug..."
        ./gradlew assembleDebug
        if [ $? -eq 0 ]; then
            echo "✅ APK creado: app/build/outputs/apk/debug/app-debug.apk"
        fi
        ;;
    2)
        echo ""
        echo "🔨 Compilando APK Release..."
        ./gradlew assembleRelease
        if [ $? -eq 0 ]; then
            echo "✅ APK creado: app/build/outputs/apk/release/app-release-unsigned.apk"
        fi
        ;;
    3)
        echo ""
        echo "🧹 Limpiando proyecto..."
        ./gradlew clean
        echo "✅ Proyecto limpiado"
        ;;
    4)
        echo ""
        echo "📱 Instalando APK..."
        if [ -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
            adb install -r app/build/outputs/apk/debug/app-debug.apk
            if [ $? -eq 0 ]; then
                echo "✅ App instalada correctamente"
            else
                echo "❌ Error al instalar. Verifica que el dispositivo esté conectado"
            fi
        else
            echo "❌ APK no encontrado. Compila primero (opción 1)"
        fi
        ;;
    5)
        echo ""
        echo "🗑️  Desinstalando app..."
        adb uninstall ar.alternaradio.app
        if [ $? -eq 0 ]; then
            echo "✅ App desinstalada"
        else
            echo "❌ No se pudo desinstalar"
        fi
        ;;
    6)
        echo ""
        echo "📱 Dispositivos conectados:"
        adb devices -l
        ;;
    7)
        echo ""
        echo "📋 Logs de la app (Ctrl+C para salir):"
        adb logcat -s AlternaRadio:* AndroidRuntime:E
        ;;
    8)
        echo ""
        echo "📊 Tamaño de APKs:"
        if [ -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
            echo -n "  Debug: "
            du -h app/build/outputs/apk/debug/app-debug.apk | cut -f1
        else
            echo "  Debug: No compilado"
        fi
        if [ -f "app/build/outputs/apk/release/app-release-unsigned.apk" ]; then
            echo -n "  Release: "
            du -h app/build/outputs/apk/release/app-release-unsigned.apk | cut -f1
        else
            echo "  Release: No compilado"
        fi
        ;;
    9)
        echo ""
        echo "📂 Abriendo carpeta de APKs..."
        if [ -d "app/build/outputs/apk" ]; then
            open app/build/outputs/apk
            echo "✅ Carpeta abierta"
        else
            echo "❌ Carpeta no existe. Compila primero"
        fi
        ;;
    10)
        echo ""
        echo "📁 Estructura del proyecto:"
        echo ""
        tree -L 3 -I 'build|gradle' . 2>/dev/null || find . -type d -maxdepth 3 | grep -v build | grep -v gradle | head -20
        ;;
    0)
        echo "Saliendo..."
        exit 0
        ;;
    *)
        echo "❌ Opción inválida"
        exit 1
        ;;
esac

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "Proceso completado"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

