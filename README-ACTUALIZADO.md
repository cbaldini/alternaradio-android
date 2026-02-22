# 🎵 Alterna Radio - Aplicación Android

Aplicación WebView para Alterna Radio (https://alternaradio.ar)

## 🆕 IMPORTANTE - Cambios Recientes

**Se han realizado mejoras significativas para solucionar el problema de la pantalla en blanco.**

Ver: [COMPILACION-FINAL.md](COMPILACION-FINAL.md)

---

## 🚀 Inicio Rápido

### 1. Compilar la App

```bash
./gradlew assembleDebug
```

### 2. Instalar en el Teléfono

```bash
./instalar-apk.sh
```

### 3. Verificar si Funciona

```bash
./diagnostico-apk.sh
```

---

## 📚 Documentación

| Documento | Propósito |
|-----------|-----------|
| **[COMPILACION-FINAL.md](COMPILACION-FINAL.md)** | 📖 Guía completa de compilación e instalación |
| **[CAMBIOS-REALIZADOS.md](CAMBIOS-REALIZADOS.md)** | 🔧 Detalles técnicos de las mejoras |
| **[GUIA-INSTALACION.md](GUIA-INSTALACION.md)** | 📱 Guía paso a paso para instalar |
| **[RESUMEN-CORRECCIONES.md](RESUMEN-CORRECCIONES.md)** | ✅ Resumen ejecutivo de cambios |

---

## 🛠 Cambios Realizados

✅ **MainActivity.java** - Actualizado a AppCompatActivity con logging detallado
✅ **AndroidManifest.xml** - Limpieza y optimización
✅ **build.gradle** - Configuración mejorada
✅ **Scripts** - Herramientas de instalación y diagnóstico

---

## 📋 Requisitos

- **Android Studio** 2023.1+
- **Android SDK 34** (compilado)
- **Android API 21+** (mínimo)
- **Java 11+**
- **Gradle 9.0+**

---

## 📱 Especificaciones

```
Versión:       2.0.0
API Mínimo:    21 (Android 5.0)
API Compilado: 34 (Android 14)
Tamaño:        ~10-15 MB
```

---

## 🐛 Si Hay Problemas

### La app abre pero queda en blanco

1. **Ver los logs:**
   ```bash
   adb logcat | grep AlternaRadio
   ```

2. **Verificar conexión a Internet:**
   - Abre un navegador en el teléfono
   - Intenta acceder a https://alternaradio.ar
   - Si no carga, tu teléfono no tiene conexión

3. **Usar el script de diagnóstico:**
   ```bash
   ./diagnostico-apk.sh
   ```

### Error durante la compilación

1. Limpiar caché:
   ```bash
   ./gradlew clean
   ```

2. Volver a compilar:
   ```bash
   ./gradlew assembleDebug
   ```

---

## 📞 Soporte Técnico

Para problemas específicos, revisa:
- Los logs con `adb logcat | grep AlternaRadio`
- [COMPILACION-FINAL.md](COMPILACION-FINAL.md) - Sección "Debugging"
- [CAMBIOS-REALIZADOS.md](CAMBIOS-REALIZADOS.md) - Mejoras técnicas

---

## 📄 Licencia

Alterna Radio © 2026

---

**Última actualización:** Febrero 21, 2026
**Versión:** 2.0.0


