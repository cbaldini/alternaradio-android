# 🔧 SOLUCIÓN PARA "IllegalStateException"

## ¿Qué es IllegalStateException?

Este error ocurre cuando se intenta usar un objeto en un estado inválido. En Android con WebView, esto sucede cuando:

1. **WebView aún no está listo** cuando intentas usarlo
2. **Activity fue destruida** pero el código sigue intentando acceder
3. **Thread incorrecto** - WebView debe usarse en el UI thread

---

## ✅ Soluciones Aplicadas

### 1. **Delay después de setContentView**
```java
Thread.sleep(100);  // Pequeña pausa para asegurar que el layout está listo
```

### 2. **Usar runOnUiThread para loadUrl**
```java
runOnUiThread(new Runnable() {
    @Override
    public void run() {
        webView.loadUrl(URL);
    }
});
```

### 3. **Validación adicional en configurarWebView**
```java
if (webView == null) {
    throw new IllegalStateException("WebView es null");
}
```

### 4. **Try-catch separado para cada operación**
- Configuración de WebSettings
- Establecimiento de WebViewClient
- Establecimiento de WebChromeClient
- Carga de URL

---

## 📱 Nuevo APK

**Ubicación:**
```
app/build/outputs/apk/debug/app-debug.apk
```

### Cambios:
- ✅ Delay de 100ms para sincronización
- ✅ loadUrl en UI thread (runOnUiThread)
- ✅ Validaciones adicionales
- ✅ Try-catch separados para cada paso
- ✅ Mejor logging para identificar el problema

---

## 🚀 Cómo Instalar

```bash
cd /Users/cristianbaldini/AlternaRadio/web/alternaradio-android
adb uninstall ar.alternaradio.app
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## 🧪 Si Sigue con IllegalStateException

Ejecuta los logs:
```bash
adb logcat | grep "AlternaRadio"
```

Busca líneas como:
- `[ERROR]` - Indica dónde exactamente falla
- `[ERROR_LOAD]` - Error al cargar URL
- `[ERROR_CONFIG]` - Error en configuración
- `[ERROR_CLIENT]` - Error en WebViewClient

Comparte exactamente qué dice el `[ERROR]`.

---

## 📊 Cambios de Código

| Cambio | Propósito |
|--------|-----------|
| `Thread.sleep(100)` | Sincronizar layout |
| `runOnUiThread` | loadUrl en UI thread correcto |
| `if (webView == null)` | Validación adicional |
| Try-catch separados | Aislar exactamente dónde falla |
| Logging detallado | Ver qué paso falla |

---

## 🎯 Próximos Pasos

1. Instala el nuevo APK
2. Abre la app en el Motorola G15
3. Si funciona: ¡Listo! 🎉
4. Si sigue fallando: Ejecuta `adb logcat | grep AlternaRadio` y comparte los errores

---

**¡Prueba el nuevo APK!** 🎵

Este debería resolver el `IllegalStateException`.


