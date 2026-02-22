# Cómo Compilar el APK - Solución al Error de Gradle

## ❌ Problema
```
chmod: gradlew: No such file or directory
```

Este error ocurre porque el proyecto necesita el Gradle Wrapper o Gradle instalado en el sistema.

---

## ✅ SOLUCIONES

### OPCIÓN 1: Usar Android Studio (MÁS FÁCIL) ⭐ RECOMENDADO

Esta es la forma más sencilla y no requiere configuración manual:

1. **Descargar e instalar Android Studio**
   ```
   https://developer.android.com/studio
   ```

2. **Abrir el proyecto**
   - Abre Android Studio
   - File → Open
   - Selecciona la carpeta: `alternaradio-android`

3. **Android Studio descargará todo automáticamente**
   - El Gradle Wrapper
   - Todas las dependencias
   - El SDK de Android

4. **Compilar**
   - Build → Build Bundle(s) / APK(s) → Build APK(s)
   - El APK estará en: `app/build/outputs/apk/debug/app-debug.apk`

**Ventajas:**
- ✅ Todo automatizado
- ✅ No requiere configuración manual
- ✅ Incluye herramientas de desarrollo
- ✅ Fácil de actualizar

---

### OPCIÓN 2: Instalar Gradle con Homebrew (TERMINAL)

Si prefieres compilar desde la terminal:

1. **Instalar Gradle**
   ```bash
   brew install gradle
   ```

2. **Verificar instalación**
   ```bash
   gradle --version
   ```
   Deberías ver algo como: `Gradle 8.x`

3. **Compilar el APK**
   ```bash
   cd /Users/cristianbaldini/AlternaRadio/web/alternaradio-android
   
   # Opción A: Usar el script
   ./compilar.sh
   
   # Opción B: Comando directo
   gradle assembleDebug
   ```

4. **APK generado en:**
   ```
   app/build/outputs/apk/debug/app-debug.apk
   ```

**Requisitos:**
- Homebrew instalado
- Java JDK 11 o superior

---

### OPCIÓN 3: Instalar Java y Gradle manualmente

1. **Instalar Java JDK**
   ```bash
   brew install openjdk@11
   ```

2. **Configurar JAVA_HOME**
   ```bash
   echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 11)' >> ~/.zshrc
   source ~/.zshrc
   ```

3. **Instalar Gradle**
   ```bash
   brew install gradle
   ```

4. **Compilar**
   ```bash
   cd alternaradio-android
   gradle assembleDebug
   ```

---

## 🔍 Verificar Requisitos

Antes de compilar, verifica que tengas:

### 1. Java instalado
```bash
java -version
```
Debe mostrar Java 11 o superior.

Si no está instalado:
```bash
brew install openjdk@11
```

### 2. Gradle instalado (si no usas Android Studio)
```bash
gradle --version
```

Si no está instalado:
```bash
brew install gradle
```

---

## 🚀 Comando Rápido (Si ya tienes Gradle)

```bash
cd /Users/cristianbaldini/AlternaRadio/web/alternaradio-android
gradle assembleDebug
```

El APK estará en:
```
app/build/outputs/apk/debug/app-debug.apk
```

---

## 📱 Instalar el APK en tu dispositivo

Una vez compilado el APK:

### Método 1: Via USB
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### Método 2: Transferencia manual
1. Copia `app-debug.apk` a tu celular
2. Abre el archivo desde el administrador de archivos
3. Permite instalación de fuentes desconocidas
4. Instala

---

## ⚠️ Problemas Comunes

### "JAVA_HOME is not set"
```bash
export JAVA_HOME=$(/usr/libexec/java_home)
echo 'export JAVA_HOME=$(/usr/libexec/java_home)' >> ~/.zshrc
```

### "SDK location not found"
→ Usa Android Studio (Opción 1)

### "Gradle version too old"
```bash
brew upgrade gradle
```

---

## 💡 Recomendación Final

**Para la mejor experiencia, usa Android Studio (Opción 1).**

Es más fácil, más completo, y evita problemas de configuración manual.

---

## 📞 ¿Necesitas ayuda?

Si ninguna opción funciona, considera:

1. Descargar el APK pre-compilado (si está disponible)
2. Usar un servicio online de compilación
3. Pedir ayuda con los detalles del error específico

---

**Alterna Radio FM 88.1 MHZ**
Miramar, Buenos Aires, Argentina
https://alternaradio.ar

