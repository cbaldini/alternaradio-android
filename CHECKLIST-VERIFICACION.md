# ✅ CHECKLIST DE VERIFICACIÓN - ALTERNA RADIO

## Antes de Instalar la App

- [ ] **Teléfono conectado por USB**
  - Cable USB conectado
  - El teléfono reconoce la conexión

- [ ] **Depuración USB habilitada**
  - Abre Configuración → Opciones del Desarrollador
  - Activa "Depuración por USB"
  - Acepta el diálogo de depuración en el teléfono

- [ ] **ADB instalado en la computadora**
  - Ejecuta: `adb version`
  - Si no está instalado, descarga Android SDK Platform Tools

- [ ] **Conexión a Internet en el teléfono**
  - WiFi o datos móviles activos
  - Prueba en navegador: https://alternaradio.ar

---

## Durante la Compilación

- [ ] **Sin errores de compilación**
  ```bash
  cd /Users/cristianbaldini/AlternaRadio/web/alternaradio-android
  ./gradlew assembleDebug
  ```
  - Debe terminar con: "BUILD SUCCESSFUL"

- [ ] **APK generado correctamente**
  ```bash
  ls -lh app/build/outputs/apk/debug/app-debug.apk
  ```
  - Debe mostrar un archivo de ~10-15 MB

---

## Durante la Instalación

- [ ] **APK instalado sin errores**
  ```bash
  ./instalar-apk.sh
  # O manualmente:
  adb install -r app/build/outputs/apk/debug/app-debug.apk
  ```
  - Debe terminar con: "Success"

- [ ] **App aparece en el menú del teléfono**
  - Busca "Alterna Radio" en el menú de aplicaciones
  - El ícono debe ser visible

---

## Después de la Instalación

### ✅ Prueba 1: Iniciar la App

- [ ] **La app se abre**
  ```bash
  adb shell am start -n ar.alternaradio.app/.MainActivity
  ```

- [ ] **Aparece una barra de progreso**
  - La app comienza a cargar el contenido
  - Puedes ver pequeños segmentos que se cargan

- [ ] **Se carga el contenido de Alterna Radio**
  - El sitio web aparece en la pantalla
  - Puedes escuchar la transmisión de radio

### ✅ Prueba 2: Verificar Logs

- [ ] **Ver logs de éxito**
  ```bash
  adb logcat | grep AlternaRadio
  ```
  Debes ver mensajes como:
  - ✅ `onCreate iniciado`
  - ✅ `WebView encontrado correctamente`
  - ✅ `Cargando página: https://alternaradio.ar`
  - ✅ `Página cargada: https://alternaradio.ar`

### ✅ Prueba 3: Verificar Audio

- [ ] **Se escucha el audio de la radio**
  - Sube el volumen del teléfono
  - Debes escuchar la transmisión

---

## Si Algo Falla

### ❌ "Pantalla en blanco"

**Paso 1: Verificar logs**
```bash
adb logcat | grep AlternaRadio
```

**Causa posible 1: Sin conexión a Internet**
- Verifica que tienes WiFi o datos activos
- Abre un navegador en el teléfono
- Intenta acceder a https://alternaradio.ar

**Causa posible 2: Servidor fuera de línea**
- Desde tu computadora, abre: https://alternaradio.ar
- Si no carga, el servidor está fuera

**Causa posible 3: Error de WebView**
- Busca en los logs: `Error de WebView`
- Anota el mensaje de error exacto
- Intenta desinstalar y reinstalar la app

### ❌ "Error de instalación"

```bash
# Desinstalar versión anterior
adb uninstall ar.alternaradio.app

# Esperar 5 segundos
sleep 5

# Instalar nuevamente
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### ❌ "Error de compilación"

```bash
# Limpiar caché
./gradlew clean

# Compilar nuevamente
./gradlew assembleDebug
```

---

## Comandos Útiles

```bash
# Ver todos los logs de la app
adb logcat | grep AlternaRadio

# Ver solo errores
adb logcat | grep AlternaRadio | grep ERROR

# Ver logs de JavaScript
adb logcat | grep "JS Console"

# Instalar APK
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Desinstalar app
adb uninstall ar.alternaradio.app

# Iniciar app
adb shell am start -n ar.alternaradio.app/.MainActivity

# Ver dispositivos conectados
adb devices

# Limpiar logs
adb logcat -c

# Script de diagnóstico completo
./diagnostico-apk.sh
```

---

## Información de Contacto

- **App:** Alterna Radio
- **Versión:** 2.0.0
- **Paquete:** ar.alternaradio.app
- **Sitio Web:** https://alternaradio.ar

---

## ✨ Notas Importantes

1. **Remote Debugging Habilitado**
   - La app puede debuggarse desde Chrome DevTools
   - Es útil para ver errores de JavaScript

2. **Logging Detallado**
   - Cada paso de la inicialización está registrado
   - Facilita identificar problemas

3. **Validación de Componentes**
   - Se verifica que el WebView se inicializa correctamente
   - Se previenen crashes silenciosos

4. **APIs Modernas**
   - Usa AppCompatActivity (no Activity deprecado)
   - Usa WebResourceRequest/WebResourceError
   - Compatible con Android 14

---

**Última actualización:** Febrero 21, 2026
**¡Buena suerte! 🎵**


