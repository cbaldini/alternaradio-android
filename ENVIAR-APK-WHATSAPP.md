# 📱 CÓMO ENVIAR EL APK POR WHATSAPP

## Ubicación del APK Compilado

```
/Users/cristianbaldini/AlternaRadio/web/alternaradio-android/app/build/outputs/apk/debug/app-debug.apk
```

---

## Opción 1: Copiar a Desktop (Más Fácil)

```bash
cp /Users/cristianbaldini/AlternaRadio/web/alternaradio-android/app/build/outputs/apk/debug/app-debug.apk ~/Desktop/alterna-radio-v2.apk
```

Luego:
1. Abre el Finder
2. Ve a Desktop
3. Encuentra `alterna-radio-v2.apk`
4. Envía por WhatsApp

---

## Opción 2: Desde Finder

1. Abre Finder
2. Ve a: `/Users/tu-usuario/AlternaRadio/web/alternaradio-android/app/build/outputs/apk/debug/`
3. Busca: `app-debug.apk`
4. Click derecho → Compartir → WhatsApp

---

## Opción 3: Línea de Comandos

```bash
# Copiar el APK a una carpeta accesible
cp /Users/cristianbaldini/AlternaRadio/web/alternaradio-android/app/build/outputs/apk/debug/app-debug.apk ~/Downloads/alterna-radio.apk

# Mostrar dónde quedó
open ~/Downloads
```

Luego abre WhatsApp y selecciona el archivo desde Downloads.

---

## 📥 Para Instalar en el Móvil

### Desde Archivo Descargado en WhatsApp:

1. En WhatsApp, abre el mensaje con el APK
2. Toca el archivo
3. Si no hay opción para instalar, abre con Files
4. Busca "Instalar"

### Desde ADB (Si tienes tiempo):

```bash
adb install -r alterna-radio.apk
```

---

## ✅ Verificar que se instaló correctamente

- Abre el menú de apps del Motorola
- Busca "Alterna Radio"
- El ícono debe estar allí

---

## 🧪 Si Se Sigue Cerrando

Después de instalar, ejecuta:
```bash
adb logcat | grep "AlternaRadio"
```

Esto mostrará exactamente dónde y por qué se cierra.

---

**¡Enviá el APK al móvil y probá!** 🎵


