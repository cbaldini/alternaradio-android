#!/bin/bash

echo "╔════════════════════════════════════════════════╗"
echo "║  Instalador Automático - Alterna Radio APK    ║"
echo "╚════════════════════════════════════════════════╝"
echo ""

# Verificar si estamos en el directorio correcto
if [ ! -f "settings.gradle" ]; then
    echo "❌ Error: Ejecuta este script desde alternaradio-android/"
    exit 1
fi

echo "Este script instalará lo necesario para compilar el APK."
echo ""
echo "Opciones:"
echo "1) Instalar Homebrew + Gradle (compilar desde terminal)"
echo "2) Solo mostrar instrucciones para Android Studio"
echo "3) Cancelar"
echo ""
read -p "Selecciona una opción (1-3): " opcion

case $opcion in
    1)
        echo ""
        echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        echo "INSTALANDO HOMEBREW Y GRADLE"
        echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        echo ""

        # Verificar si Homebrew está instalado
        if ! command -v brew >/dev/null 2>&1; then
            echo "📦 Instalando Homebrew..."
            echo "   (Esto puede tardar varios minutos)"
            echo ""
            /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

            if [ $? -ne 0 ]; then
                echo "❌ Error al instalar Homebrew"
                exit 1
            fi

            echo ""
            echo "✅ Homebrew instalado correctamente"
        else
            echo "✅ Homebrew ya está instalado"
        fi

        echo ""

        # Instalar Java si no está
        if ! command -v java >/dev/null 2>&1; then
            echo "☕ Instalando Java JDK 11..."
            brew install openjdk@11

            # Configurar JAVA_HOME
            echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 11)' >> ~/.zshrc
            export JAVA_HOME=$(/usr/libexec/java_home -v 11)

            echo "✅ Java instalado"
        else
            echo "✅ Java ya está instalado"
        fi

        echo ""

        # Instalar Gradle
        if ! command -v gradle >/dev/null 2>&1; then
            echo "🔧 Instalando Gradle..."
            brew install gradle

            if [ $? -ne 0 ]; then
                echo "❌ Error al instalar Gradle"
                exit 1
            fi

            echo "✅ Gradle instalado correctamente"
        else
            echo "✅ Gradle ya está instalado"
        fi

        echo ""
        echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        echo "✅ INSTALACIÓN COMPLETADA"
        echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        echo ""
        echo "Ahora puedes compilar el APK con:"
        echo ""
        echo "  gradle assembleDebug"
        echo ""
        echo "¿Deseas compilar ahora? (s/n)"
        read -p "> " compilar

        if [ "$compilar" = "s" ] || [ "$compilar" = "S" ]; then
            echo ""
            echo "🔨 Compilando APK..."
            gradle assembleDebug

            if [ $? -eq 0 ]; then
                echo ""
                echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                echo "✅ ¡APK COMPILADO EXITOSAMENTE!"
                echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
                echo ""
                echo "📍 Ubicación del APK:"
                echo "   app/build/outputs/apk/debug/app-debug.apk"
                echo ""
                echo "📱 Para instalar en tu dispositivo:"
                echo "   adb install -r app/build/outputs/apk/debug/app-debug.apk"
                echo ""
            else
                echo ""
                echo "❌ Error al compilar"
                echo "   Revisa los mensajes de error arriba"
            fi
        fi
        ;;

    2)
        echo ""
        echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        echo "ANDROID STUDIO - LA FORMA MÁS FÁCIL"
        echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        echo ""
        echo "📥 1. Descargar Android Studio:"
        echo "   https://developer.android.com/studio"
        echo ""
        echo "💿 2. Instalar siguiendo el asistente"
        echo ""
        echo "📂 3. Abrir Android Studio y:"
        echo "   - Click en 'Open'"
        echo "   - Navega a: $(pwd)"
        echo "   - Click en 'Open'"
        echo ""
        echo "⏳ 4. Esperar sincronización (primera vez: 5-15 min)"
        echo ""
        echo "🔨 5. Compilar:"
        echo "   - Menu: Build → Build Bundle(s) / APK(s) → Build APK(s)"
        echo ""
        echo "✅ 6. APK listo en:"
        echo "   app/build/outputs/apk/debug/app-debug.apk"
        echo ""
        echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        echo ""
        echo "💡 Ventajas de Android Studio:"
        echo "   ✓ Todo automatizado"
        echo "   ✓ No necesitas instalar nada manualmente"
        echo "   ✓ Interfaz gráfica fácil de usar"
        echo "   ✓ Incluye herramientas de desarrollo"
        echo ""
        ;;

    3)
        echo "Cancelado."
        exit 0
        ;;

    *)
        echo "❌ Opción inválida"
        exit 1
        ;;
esac

echo ""

