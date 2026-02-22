# ⚡ REFERENCIA RÁPIDA - ALTERNA RADIO

## 🎯 Instalar Ahora

```bash
cd /Users/cristianbaldini/AlternaRadio/web/alternaradio-android
./instalar-apk.sh
```

---

## 🔥 Comandos Más Usados

### Compilar
```bash
./gradlew assembleDebug
```

### Instalar
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### Ver Logs
```bash
adb logcat | grep AlternaRadio
```

### Iniciar App
```bash
adb shell am start -n ar.alternaradio.app/.MainActivity
```

### Desinstalar
```bash
adb uninstall ar.alternaradio.app
```

### Diagnóstico Completo
```bash
./diagnostico-apk.sh
```

---

## 🧪 Verificar Instalación

```bash
adb devices              # Ver dispositivos
adb logcat             # Ver todos los logs
adb logcat | grep AlternaRadio  # Ver solo logs de la app
```

---

## 📂 Archivos Clave

```
app/build/outputs/apk/debug/app-debug.apk  ← EL APK
app/src/main/java/ar/alternaradio/app/MainActivity.java  ← Código principal
app/src/main/AndroidManifest.xml  ← Configuración
```

---

## 📖 Documentación

| Necesitas | Lee |
|-----------|-----|
| Instalar rápido | INICIO-RAPIDO-CORREGIDO.md |
| Guía completa | COMPILACION-FINAL.md |
| Checklist | CHECKLIST-VERIFICACION.md |
| Entender cambios | CAMBIOS-REALIZADOS.md |
| Ver índice | INDICE-COMPLETO.md |
| Troubleshooting | GUIA-INSTALACION.md |

---

## ❌ Problemas Comunes

### Pantalla en blanco
```bash
# Ver qué está pasando
adb logcat | grep AlternaRadio

# Si no ves logs, intenta:
adb shell am start -n ar.alternaradio.app/.MainActivity
```

### Error de instalación
```bash
# Desinstalar versión anterior
adb uninstall ar.alternaradio.app

# Esperar 5 segundos
sleep 5

# Reinstalar
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### Error de compilación
```bash
# Limpiar caché
./gradlew clean

# Compilar nuevamente
./gradlew assembleDebug
```

---

## 🔍 Logs Útiles

```bash
# Todo de la app
adb logcat | grep AlternaRadio

# Solo errores
adb logcat | grep "AlternaRadio\|ERROR"

# JavaScript console
adb logcat | grep "JS Console"

# Seguimiento de red
adb logcat | grep "Estado de red"

# Errores de WebView
adb logcat | grep "Error de WebView"
```

---

## 📊 Info Técnica

```
Versión:       2.0.0
API Mínimo:    21 (Android 5.0)
API Compilado: 34 (Android 14)
Tamaño:        ~10-15 MB
Paquete:       ar.alternaradio.app
```

---

## ✨ Estado Actual

✅ Compilada
✅ Testeada
✅ Documentada
✅ Lista para instalar

---

**¿Preguntas?** Lee [INDICE-COMPLETO.md](INDICE-COMPLETO.md)


