# 📱 INSTRUCCIONES PARA INSTALAR EL APK CORREGIDO

## El Nuevo APK

Se ha compilado un nuevo APK que corrige el `IllegalStateException`:

```
Ubicación: app/build/outputs/apk/debug/app-debug.apk
Cambios: Thread.sleep(100ms) + runOnUiThread + validaciones adicionales
```

---

## Opción A: Con ADB (Si usas terminal)

```bash
# 1. Desinstalar versión anterior
adb uninstall ar.alternaradio.app

# 2. Instalar nuevo APK
adb install app/build/outputs/apk/debug/app-debug.apk

# 3. Iniciar la app para verificar
adb shell am start -n ar.alternaradio.app/.MainActivity

# 4. Ver los logs en tiempo real
adb logcat | grep "AlternaRadio"
```

---

## Opción B: Copiar a Desktop y enviar por WhatsApp

```bash
# Copiar el APK al Desktop
cp app/build/outputs/apk/debug/app-debug.apk ~/Desktop/alterna-radio-v2.apk
```

Luego:
1. Abre Finder → Desktop
2. Busca `alterna-radio-v2.apk`
3. Envía por WhatsApp
4. En el Motorola, abre el mensaje y toca para instalar

---

## Opción C: Manual desde Finder

1. Abre Finder
2. Presiona `Cmd + Shift + G` (Go to Folder)
3. Pega: `/Users/tu-usuario/AlternaRadio/web/alternaradio-android/app/build/outputs/apk/debug/`
4. Busca: `app-debug.apk`
5. Click derecho → Compartir → WhatsApp

---

## ✅ Después de Instalar

### Verificar que se instaló:
1. Abre el menú de apps del Motorola
2. Busca "Alterna Radio"
3. El ícono debe estar visible

### Abrir la app:
1. Toca sobre "Alterna Radio"
2. Debería abrirse sin cerrarse
3. Debería cargar https://alternaradio.ar

---

## 🧪 Si Se Sigue Cerrando

Ejecuta esto:
```bash
adb logcat -c
adb shell am start -n ar.alternaradio.app/.MainActivity
sleep 3
adb logcat | grep "AlternaRadio"
```

**Busca líneas con:**
- `[OK]` - Operación exitosa
- `[ERROR]` - Error (esto es lo importante)
- `[EXCEPCION]` - Excepción (también importante)

**Copia TODAS las líneas que comienzan con `[ERROR]` o `[EXCEPCION]` y comparte.**

---

## 📝 Ejemplo de Salida Esperada (Logs)

Si funciona correctamente, deberías ver algo como:

```
[OK] Layout establecido
[OK] Vistas obtenidas
[OK] WebView validado
[OK] WebView configurado
[CARGANDO] https://alternaradio.ar
[OK] URL cargada
[INICIO_PAGINA] https://alternaradio.ar
[FIN_PAGINA] https://alternaradio.ar
```

---

## ❌ Si Ves Errores

Si ves algo como:
```
[ERROR] ...
[EXCEPCION] ...
```

**Copia exactamente qué dice después de `[ERROR]` o `[EXCEPCION]` y comparte.**

---

## 🎯 Resumen Rápido

1. **Instala el nuevo APK** (Opción A, B o C arriba)
2. **Abre la app en el Motorola**
3. **Si funciona:** ¡Listo! 🎉
4. **Si no funciona:** Ejecuta los logs y comparte los errores

---

**¡Necesitamos ese nuevo APK en el Motorola!** 🎵


