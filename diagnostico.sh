#!/bin/bash

echo "╔════════════════════════════════════════════════╗"
echo "║  Diagnóstico del Sistema - Alterna Radio      ║"
echo "╚════════════════════════════════════════════════╝"
echo ""

# Variables para el resumen
PROBLEMS=0
READY=true

echo "🔍 Verificando requisitos para compilar APK..."
echo ""

# 1. Verificar Java
echo -n "1. Java JDK: "
if command -v java >/dev/null 2>&1; then
    JAVA_VERSION=$(java -version 2>&1 | head -1 | cut -d'"' -f2)
    echo "✅ Instalado (versión $JAVA_VERSION)"
else
    echo "❌ NO instalado"
    echo "   → Instalar: brew install openjdk@11"
    PROBLEMS=$((PROBLEMS + 1))
    READY=false
fi

echo ""

# 2. Verificar Gradle
echo -n "2. Gradle: "
if command -v gradle >/dev/null 2>&1; then
    GRADLE_VERSION=$(gradle --version 2>/dev/null | grep "Gradle" | head -1)
    echo "✅ Instalado ($GRADLE_VERSION)"
else
    echo "❌ NO instalado"
    echo "   → Instalar: brew install gradle"
    PROBLEMS=$((PROBLEMS + 1))
    READY=false
fi

echo ""

# 3. Verificar Homebrew
echo -n "3. Homebrew: "
if command -v brew >/dev/null 2>&1; then
    BREW_VERSION=$(brew --version | head -1)
    echo "✅ Instalado ($BREW_VERSION)"
else
    echo "❌ NO instalado"
    echo "   → Instalar: https://brew.sh"
    PROBLEMS=$((PROBLEMS + 1))
fi

echo ""

# 4. Verificar ADB (Android Debug Bridge)
echo -n "4. ADB (para instalar en dispositivo): "
if command -v adb >/dev/null 2>&1; then
    echo "✅ Instalado"
else
    echo "⚠️  NO instalado (opcional)"
    echo "   → Instalar: brew install android-platform-tools"
fi

echo ""

# 5. Verificar estructura del proyecto
echo -n "5. Estructura del proyecto: "
if [ -f "build.gradle" ] && [ -f "settings.gradle" ]; then
    echo "✅ Correcta"
else
    echo "❌ Incompleta"
    echo "   → Verifica que estés en el directorio alternaradio-android"
    READY=false
fi

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# Resumen
if [ "$READY" = true ]; then
    echo "✅ ¡SISTEMA LISTO PARA COMPILAR!"
    echo ""
    echo "Puedes compilar el APK con:"
    echo "  ./compilar.sh"
    echo "  o"
    echo "  gradle assembleDebug"
else
    echo "❌ SISTEMA NO ESTÁ LISTO"
    echo ""
    echo "Problemas encontrados: $PROBLEMS"
    echo ""
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo ""
    echo "📋 SOLUCIONES:"
    echo ""
    echo "OPCIÓN 1 (MÁS FÁCIL): Usar Android Studio"
    echo "  ✓ Incluye todo lo necesario automáticamente"
    echo "  ✓ No requiere instalaciones adicionales"
    echo "  ✓ Descarga: https://developer.android.com/studio"
    echo ""
    echo "OPCIÓN 2: Instalar herramientas manualmente"

    if ! command -v brew >/dev/null 2>&1; then
        echo ""
        echo "  1. Instalar Homebrew:"
        echo "     /bin/bash -c \"\$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)\""
    fi

    if ! command -v java >/dev/null 2>&1; then
        echo ""
        echo "  2. Instalar Java:"
        echo "     brew install openjdk@11"
    fi

    if ! command -v gradle >/dev/null 2>&1; then
        echo ""
        echo "  3. Instalar Gradle:"
        echo "     brew install gradle"
    fi
fi

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "📚 Más información: Lee COMO-COMPILAR.md"
echo ""

