#!/bin/bash

echo "=========================================="
echo "  Alterna Radio - Compilador de APK"
echo "=========================================="
echo ""

# Verificar que estamos en el directorio correcto
if [ ! -f "settings.gradle" ]; then
    echo "❌ Error: Ejecuta este script desde el directorio alternaradio-android"
    exit 1
fi

# Verificar si gradle está instalado
GRADLE_CMD=""
if [ -x "gradlew" ]; then
    GRADLE_CMD="./gradlew"
    chmod +x gradlew 2>/dev/null
elif command -v gradle >/dev/null 2>&1; then
    GRADLE_CMD="gradle"
    echo "ℹ️  Usando Gradle del sistema"
else
    echo "❌ Error: Gradle no está instalado"
    echo ""
    echo "Por favor, usa una de estas opciones:"
    echo ""
    echo "OPCIÓN 1 (Recomendada): Instalar Android Studio"
    echo "  → Incluye todo lo necesario"
    echo "  → https://developer.android.com/studio"
    echo ""
    echo "OPCIÓN 2: Instalar Gradle con Homebrew"
    echo "  → brew install gradle"
    echo ""
    exit 1
fi

# Menú de opciones
echo "Selecciona el tipo de compilación:"
echo "1) APK Debug (para pruebas)"
echo "2) APK Release (sin firmar)"
echo "3) Limpiar proyecto"
echo "4) Salir"
echo ""
read -p "Opción: " opcion

case $opcion in
    1)
        echo ""
        echo "🔨 Compilando APK Debug..."
        $GRADLE_CMD assembleDebug
        if [ $? -eq 0 ]; then
            echo ""
            echo "✅ APK Debug compilado exitosamente!"
            echo "📍 Ubicación: app/build/outputs/apk/debug/app-debug.apk"
            echo ""
            read -p "¿Deseas instalar en un dispositivo conectado? (s/n): " instalar
            if [ "$instalar" = "s" ] || [ "$instalar" = "S" ]; then
                adb install -r app/build/outputs/apk/debug/app-debug.apk
                if [ $? -eq 0 ]; then
                    echo "✅ APK instalado en el dispositivo"
                else
                    echo "❌ Error al instalar. Verifica que el dispositivo esté conectado"
                fi
            fi
        else
            echo "❌ Error al compilar"
        fi
        ;;
    2)
        echo ""
        echo "🔨 Compilando APK Release..."
        $GRADLE_CMD assembleRelease
        if [ $? -eq 0 ]; then
            echo ""
            echo "✅ APK Release compilado exitosamente!"
            echo "📍 Ubicación: app/build/outputs/apk/release/app-release-unsigned.apk"
            echo ""
            echo "⚠️  NOTA: Este APK NO está firmado. Para producción debes firmarlo."
        else
            echo "❌ Error al compilar"
        fi
        ;;
    3)
        echo ""
        echo "🧹 Limpiando proyecto..."
        $GRADLE_CMD clean
        echo "✅ Proyecto limpiado"
        ;;
    4)
        echo "Saliendo..."
        exit 0
        ;;
    *)
        echo "❌ Opción inválida"
        exit 1
        ;;
esac

echo ""
echo "=========================================="
echo "  Proceso completado"
echo "=========================================="

