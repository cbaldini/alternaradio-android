# Alterna Radio - App Android

Aplicación Android para Alterna Radio FM 88.1 MHz

## Requisitos

- **Android Studio** (versión Arctic Fox o superior)
- **JDK 11** o superior
- **SDK Android**: API 21 (Android 5.0) o superior

## Estructura del Proyecto

```
alternaradio-android/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── AndroidManifest.xml
│   │       ├── java/ar/alternaradio/app/
│   │       │   └── MainActivity.java
│   │       └── res/
│   │           ├── layout/
│   │           ├── values/
│   │           └── mipmap-*/
│   ├── build.gradle
│   └── proguard-rules.pro
├── gradle/
├── build.gradle
└── settings.gradle
```

## Compilar el APK

### Opción 1: Usando Android Studio (Recomendado)

1. **Instalar Android Studio**
   - Descarga desde: https://developer.android.com/studio
   - Instala los componentes SDK necesarios

2. **Abrir el proyecto**
   - Abre Android Studio
   - File → Open → Selecciona la carpeta `alternaradio-android`
   - Espera a que se sincronicen las dependencias de Gradle

3. **Compilar APK**
   - Build → Build Bundle(s) / APK(s) → Build APK(s)
   - El APK se generará en: `app/build/outputs/apk/debug/app-debug.apk`

4. **Compilar APK Release (firmado)**
   - Build → Generate Signed Bundle / APK
   - Selecciona APK
   - Crea o selecciona un keystore
   - El APK firmado estará en: `app/build/outputs/apk/release/app-release.apk`

### Opción 2: Usando línea de comandos

1. **Instalar herramientas**
   ```bash
   # En macOS con Homebrew
   brew install openjdk@11
   ```

2. **Descargar Command Line Tools**
   - Descarga desde: https://developer.android.com/studio#command-tools

3. **Compilar**
   ```bash
   cd alternaradio-android
   
   # Dar permisos al script gradle (primera vez)
   chmod +x gradlew
   
   # Compilar APK debug
   ./gradlew assembleDebug
   
   # Compilar APK release (sin firmar)
   ./gradlew assembleRelease
   ```

4. **El APK estará en:**
   - Debug: `app/build/outputs/apk/debug/app-debug.apk`
   - Release: `app/build/outputs/apk/release/app-release.apk`

## Instalar en dispositivo

### Via USB (Debug)
```bash
# Habilita "Depuración USB" en tu dispositivo Android
# Conecta el dispositivo via USB
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Via archivo APK
1. Copia el APK a tu dispositivo
2. Abre el archivo APK desde el administrador de archivos
3. Permite instalación desde fuentes desconocidas si es necesario
4. Instala la aplicación

## Características de la App

- ✅ WebView optimizado para streaming de audio
- ✅ Reproductor de audio en tiempo real
- ✅ Navegación completa del sitio web
- ✅ Soporte para pantallas de todos los tamaños
- ✅ Gestión de conectividad
- ✅ Barra de progreso de carga
- ✅ Botón atrás funcional
- ✅ Ícono personalizado con el logo de Alterna Radio

## Configuración

Para modificar la URL de la aplicación, edita:
```java
// app/src/main/java/ar/alternaradio/app/MainActivity.java
private static final String URL = "https://alternaradio.ar";
```

## Permisos

La aplicación requiere los siguientes permisos:
- `INTERNET`: Para acceder al sitio web
- `ACCESS_NETWORK_STATE`: Para verificar conectividad
- `WAKE_LOCK`: Para mantener la reproducción activa

## Versión

- **Version Code**: 1
- **Version Name**: 2.0.0
- **Package**: ar.alternaradio.app

## Soporte

- **Min SDK**: 21 (Android 5.0 Lollipop)
- **Target SDK**: 34 (Android 14)

## Publicación en Google Play Store

Para publicar en Play Store necesitas:

1. **Crear una cuenta de desarrollador** ($25 USD único pago)
   - https://play.google.com/console

2. **Firmar el APK**
   - Usa Android Studio: Build → Generate Signed Bundle / APK
   - Guarda el keystore en lugar seguro

3. **Crear un AAB (Android App Bundle)** - Recomendado
   ```bash
   ./gradlew bundleRelease
   ```
   El AAB estará en: `app/build/outputs/bundle/release/app-release.aab`

4. **Subir a Play Console**
   - Crea una nueva aplicación
   - Completa la información requerida
   - Sube el AAB
   - Completa el formulario de contenido
   - Envía para revisión

## Notas Importantes

- El APK debug está firmado con clave de depuración (NO publicar)
- Para producción, SIEMPRE usa un APK/AAB firmado con tu keystore
- Guarda el keystore en lugar seguro (necesario para actualizaciones)
- La primera compilación puede tardar varios minutos

## Solución de Problemas

### Error: SDK no encontrado
```bash
# Configura ANDROID_HOME
export ANDROID_HOME=$HOME/Library/Android/sdk
export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools
```

### Error de sincronización Gradle
- Verifica tu conexión a Internet
- File → Invalidate Caches / Restart en Android Studio

### APK no instala
- Verifica que el dispositivo permita fuentes desconocidas
- Desinstala versiones anteriores si existen

## Licencia

© 2026 Alterna Radio FM 88.1 MHZ, Miramar, Buenos Aires, Argentina

