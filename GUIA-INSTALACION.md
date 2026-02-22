# 🎵 Alterna Radio - Guía de Instalación y Troubleshooting

## ✅ Estado Actual

La aplicación ha sido corregida y compilada correctamente. El APK está listo para instalar.

## 📱 Instalación Rápida

### Opción 1: Con Script (Recomendado)

```bash
cd /Users/cristianbaldini/AlternaRadio/web/alternaradio-android
./instalar-apk.sh
```

### Opción 2: Manual

1. Conecta tu teléfono por USB
2. Habilita "Depuración por USB" en Opciones del Desarrollador
3. Ejecuta:
   ```bash
   cd /Users/cristianbaldini/AlternaRadio/web/alternaradio-android
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```

## 🔍 Si la Pantalla Sigue en Blanco

### Paso 1: Verificar Conectividad
1. Abre cualquier navegador en tu teléfono
2. Intenta acceder a: https://alternaradio.ar
3. Si no carga, el problema es de conexión, no de la app

### Paso 2: Ver Logs de Debugging

```bash
./diagnostico-apk.sh
```

O manualmente:
```bash
adb logcat | grep AlternaRadio
```

Busca mensajes como:
- `onCreate iniciado` - La app se está inicializando
- `WebView encontrado correctamente` - El WebView se encontró
- `Cargando página:` - La URL está siendo cargada
- `Error de WebView` - Si hay un error de carga

### Paso 3: Reiniciar la App

```bash
adb shell am start -n ar.alternaradio.app/.MainActivity
```

## 🐛 Problemas Comunes y Soluciones

### Problema: "App se abre pero queda en blanco"

**Causas probables:**
1. **Sin conexión a Internet** 
   - Verifica que tu teléfono está conectado a WiFi o datos
   - Prueba en el navegador que puedes acceder a alternaradio.ar

2. **El servidor está fuera de línea**
   - Verifica que https://alternaradio.ar está disponible desde una computadora

3. **Problemas de certificado SSL**
   - Menos probable, pero si ves errores SSL en los logs, contacta al administrador del servidor

### Problema: "Error: WebView no encontrado"
- El layout activity_main.xml está corrupto
- Solución: Desinstala y reinstala la app

### Problema: "Error de conectividad"
- Verifica que tienes permisos de INTERNET habilitados (están en AndroidManifest.xml)
- En algunos teléfonos, puede requerir reiniciar

## 📊 Información de Debugging Útil

Los logs de la app incluyen:

| Log | Significado |
|-----|-------------|
| `onCreate iniciado` | La actividad está empezando |
| `WebView encontrado correctamente` | El componente está inicializado |
| `Cargando página: https://alternaradio.ar` | La URL se está cargando |
| `Página cargada:` | La página se cargó exitosamente |
| `Progreso: X%` | Muestra el progreso de carga |
| `JS Console [línea]: mensaje` | Errores de JavaScript desde la página web |
| `Error de WebView` | Hay un problema cargando la página |
| `Estado de red: conectado` | La conexión de Internet está disponible |

## 🔧 Cambios Realizados al Código

Se realizaron varias mejoras importantes:

1. ✅ **Cambio de Activity a AppCompatActivity** - Mejor compatibilidad
2. ✅ **Logging detallado** - Fácil identificar problemas
3. ✅ **Validación de componentes** - Verifica que WebView se inicializa
4. ✅ **Manejo robusto de errores** - No hay crashes silenciosos
5. ✅ **Métodos actualizados** - Usando APIs modernas de Android
6. ✅ **Ciclo de vida correcto** - onPause, onResume, onDestroy

## 📝 Archivos Importantes

- `app/src/main/java/ar/alternaradio/app/MainActivity.java` - Código principal (ACTUALIZADO)
- `app/src/main/AndroidManifest.xml` - Configuración de la app (ACTUALIZADO)
- `app/build/outputs/apk/debug/app-debug.apk` - El APK instalable
- `CAMBIOS-REALIZADOS.md` - Documentación de cambios
- `instalar-apk.sh` - Script para instalar
- `diagnostico-apk.sh` - Script para debuggear

## ✨ Próximos Pasos Opcionales

Para mejorar aún más la app:

1. **Compilar APK Release** (sin debugging):
   ```bash
   ./gradlew assembleRelease
   ```

2. **Agregar más features** como:
   - Caché de offline
   - Notificaciones push
   - Pantalla de splash mejorada

3. **Testing** en varios dispositivos

## 📞 Información de Contacto

Para problemas específicos de desarrollo, revisa:
- Los logs con `adb logcat`
- Los archivos en `CAMBIOS-REALIZADOS.md`
- La consola de JavaScript desde Chrome DevTools (con remote debugging)

---

**Última actualización:** Febrero 2026
**Versión de la app:** 2.0.0
**API mínimo:** 21 (Android 5.0)
**API compilado:** 34 (Android 14)


