# GUÍA RÁPIDA - Compilar APK de Alterna Radio

## 🚀 Inicio Rápido

### Opción 1: Con Android Studio (MÁS FÁCIL)

1. **Descargar Android Studio**
   - Ve a: https://developer.android.com/studio
   - Descarga e instala

2. **Abrir el proyecto**
   - Abre Android Studio
   - Click en "Open"
   - Selecciona la carpeta `alternaradio-android`
   - Espera a que descargue dependencias (puede tardar)

3. **Compilar**
   - Menú: Build → Build Bundle(s) / APK(s) → Build APK(s)
   - Espera a que termine (ver barra inferior)
   - Click en "locate" cuando termine

4. **Tu APK está listo!**
   - Archivo: `app-debug.apk`
   - Cópialo a tu celular e instálalo

---

### Opción 2: Línea de Comandos (AVANZADO)

```bash
cd alternaradio-android

# Opción A: Usar el script automático
./compilar.sh

# Opción B: Compilar manualmente
./gradlew assembleDebug
```

El APK estará en: `app/build/outputs/apk/debug/app-debug.apk`

---

## 📱 Instalar en tu celular

### Método 1: Via Cable USB
1. Activa "Depuración USB" en tu Android:
   - Ajustes → Acerca del teléfono
   - Toca 7 veces en "Número de compilación"
   - Vuelve → Opciones de desarrollador
   - Activa "Depuración USB"

2. Conecta el cable USB

3. En la terminal:
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

### Método 2: Copiar APK
1. Copia `app-debug.apk` a tu celular (por WhatsApp, email, etc.)
2. Abre el archivo desde el celular
3. Permite "Instalar desde fuentes desconocidas" si te lo pide
4. Instala

---

## ❓ Preguntas Frecuentes

**P: ¿Necesito pagar algo?**
R: No. Compilar y usar el APK es gratis. Solo pagas si quieres publicar en Play Store ($25).

**P: ¿Funciona en iPhone?**
R: No, esto es solo para Android.

**P: ¿Es seguro?**
R: Sí, el código está aquí y puedes revisarlo. Es tu propia app.

**P: ¿Cuánto tarda compilar?**
R: Primera vez: 5-15 minutos (descarga dependencias)
   Siguientes veces: 1-3 minutos

**P: ¿Qué Android necesito?**
R: Android 5.0 (Lollipop) o superior

---

## 🆘 Problemas Comunes

### "SDK not found"
Instala Android Studio completo, incluye el SDK.

### "gradlew: Permission denied"
```bash
chmod +x gradlew
```

### No puedo instalar el APK
Ve a: Ajustes → Seguridad → Permitir instalación de fuentes desconocidas

---

## 📞 Contacto

Alterna Radio FM 88.1 MHZ
Miramar, Buenos Aires, Argentina
https://alternaradio.ar

