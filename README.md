# 📻 Alterna Radio - Versión 1.0.0

Una aplicación Android minimalista de reproductor de radio en vivo con diseño retro vintage, inspirada en radios analógicas clásicas.

## 🎯 Características Principales

### 🎨 Interfaz Visual
- **Diseño Retro Vintage**: Emula la estética de una radio analógica clásica
- **Display LED 7-Segmentos**: Frecuencia en vivo (88.1 MHz)
- **Dial Analógico**: Aguja posicionada en la frecuencia actual
- **Parlante 3D Redondeado**: Con textura de malla metálica que se desliza para revelar imágenes
- **Ecualizador Animado**: 12 barras que se mueven continuamente al ritmo visual
- **Logo Alterna Radio**: Profesional y visible

### 🎵 Funcionalidad de Audio
- Streaming en vivo desde Alterna Radio (88.1 MHz FM)
- Reproductor de audio integrado (WebView)
- Extracción automática del título de la canción que suena
- Actualización periódica del título cada 3 segundos
- Panel "AHORA SUENA" con información en tiempo real

### 📸 Galería de Imágenes
- **Carousel Vertical**: Deslizable verticalmente
- **Auto-scroll**: Cada 5 segundos hacia abajo automáticamente
- **Navegación Manual**: Botones izquierda/derecha para cambiar imágenes
- **Activación con INFO**: Al presionar el botón INFO, la textura del parlante se desliza hacia arriba revelando las imágenes
- **Cierre Suave**: Presionar INFO de nuevo devuelve la textura cubriendo las imágenes

### 🔘 Controles
- **Botón INFO**: Abre/cierra la galería de imágenes
- **Botón WEB**: Abre el navegador en https://radio.alterna.ar
- **Flechas Izq/Der**: Navegan el carousel de imágenes

## 🏗️ Arquitectura de la Aplicación

### Estructura del Proyecto
```
app/src/main/
├── java/ar/alternaradio/app/
│   ├── MainActivity.java                 (Actividad principal)
│   ├── NowPlayingExtractor.java         (Extractor de títulos)
│   ├── WebImageExtractor.java           (Extractor de imágenes)
│   ├── ImageCarouselAdapter.java        (Adaptador del carousel)
│   ├── EqualizerAnimator.java           (Animador del ecualizador)
│   ├── SpeakerAnimationController.java  (Animaciones del parlante)
│   ├── LEDTextView.java                 (Display LED personalizado)
│   └── SevenSegmentView.java            (Display 7-segmentos personalizado)
├── res/
│   ├── layout/activity_main.xml         (Layout principal)
│   ├── drawable/                         (Drawables XML e imágenes)
│   ├── values/colors.xml                (Paleta de colores)
│   ├── values/styles.xml                (Estilos de la app)
│   └── anim/                            (Animaciones XML)
└── assets/images/
    ├── logoradioplano.png               (Logo de Alterna Radio)
    ├── radio_vintage.png                (Imagen vintage)
    └── textura_parlante.jpg             (Textura del parlante)
```

## 📝 Componentes Principales

### 1. **MainActivity.java**
**Responsabilidad**: Orquesta toda la aplicación, maneja eventos y controla flujos.

**Variables Clave**:
- `webView`: WebView para cargar la página de radio
- `frequencyDisplay`: Display 7-segmentos para mostrar frecuencia
- `nowPlayingTitle`: TextView para mostrar canción actual
- `imageCarousel`: ViewPager2 para galería vertical
- `speakerContainer`: FrameLayout del parlante
- `equalizerAnimator`: Controlador de animaciones del ecualizador
- `speakerAnimator`: Controlador de animaciones del parlante
- `eqBars`: Lista de 12 Views para barras del ecualizador

**Métodos Principales**:
- `onCreate()`: Inicializa todas las vistas y configuraciones
- `configureWebView()`: Configura el WebView para cargar la radio
- `toggleCarousel()`: Muestra/oculta la galería animando la textura del parlante hacia arriba/abajo
- `extractWebContent()`: Extrae imágenes de la web
- `updateNowPlayingDisplay(String)`: Actualiza el texto de la canción
- `maybeLoadHome()`: Carga la página inicial de la radio

### 2. **NowPlayingExtractor.java**
**Responsabilidad**: Extrae automáticamente el título de la canción que suena desde la web.

**Funcionamiento**:
1. **Intenta 3 métodos en orden**:
   - API JSON de AzuraCast (`/api/nowplaying/1`)
   - DOM selectors específicos (`.now-playing`, `.song-title`, etc.)
   - `document.title` dividido por `|` o `-`

2. **Limpieza del Título**:
   - Remueve caracteres escapados
   - Divide por `|` o `-` para obtener solo el título
   - Limita a 200 caracteres

3. **Extracción Periódica**:
   - Se ejecuta cada 3 segundos automáticamente
   - Solo notifica si el título cambió
   - Mantiene el último valor conocido si hay error

**Métodos Públicos**:
- `startPeriodicExtraction()`: Inicia extracción continua
- `stopPeriodicExtraction()`: Detiene la extracción
- `extractCurrentSong()`: Extrae una sola vez

### 3. **EqualizerAnimator.java**
**Responsabilidad**: Anima las 12 barras del ecualizador con movimientos continuos.

**Características**:
- Anima cada barra entre 30%-200% de su altura original
- Duración: 250-450ms por ciclo (3x más rápido)
- Se repite continuamente en loop
- Alturas aleatorias para efecto visual dinámico

**Algoritmo**:
1. Para cada barra, obtiene altura inicial
2. Calcula altura aleatoria entre minHeight y maxHeight
3. Crea ValueAnimator de initialHeight → randomHeight → initialHeight
4. Al terminar, resetea flag y reinicia automáticamente

**Métodos**:
- `startAnimation()`: Inicia las animaciones
- `stopAnimation()`: Detiene las animaciones
- `isAnimating()`: Verifica si está animando

### 4. **ImageCarouselAdapter.java**
**Responsabilidad**: Adaptador para el ViewPager2 del carousel de imágenes.

**Características**:
- Carga imágenes desde URLs usando Glide
- Soporte para actualización dinámica de imágenes
- Layout vertical automático
- RecyclerView.Adapter para ViewPager2

**Métodos**:
- `updateImages(List<String>)`: Actualiza lista de imágenes
- `onCreateViewHolder()`: Crea holders de imagen
- `onBindViewHolder()`: Carga imagen en holder

### 5. **SpeakerAnimationController.java**
**Responsabilidad**: Controla animaciones especiales del parlante.

**Métodos**:
- `complexBeatAnimation()`: Animación al cambiar de imagen
- `simpleAnimation()`: Animación simple de pulsación

### 6. **LEDTextView.java**
**Responsabilidad**: Custom view que renderiza texto con efecto LED.

**Características**:
- Fuente monoespaciada
- Color blanco (#FFFFFF)
- Sombra LED para efecto luminoso
- Tamaño 16sp

### 7. **SevenSegmentView.java**
**Responsabilidad**: Custom view que dibuja números en estilo 7-segmentos.

**Características**:
- Dibuja dígitos individuales como en displays analógicos
- Matriz de 7 segmentos para cada dígito
- Color azul (#3B4DA8) con sombra
- Soporte para decimales

### 8. **WebImageExtractor.java**
**Responsabilidad**: Extrae URLs de imágenes desde la página web usando JavaScript.

**Métodos**:
- `extractImages()`: Busca todas las imágenes img y srcset en la página

## 🎨 Diseño Visual

### Paleta de Colores
```xml
<color name="colorPrimary">#1a1a1a</color>           (Negro principal)
<color name="colorPrimaryDark">#0a0a0a</color>      (Negro oscuro)
<color name="radio_red">#3B4DA8</color>              (Azul Alterna)
<color name="radio_white">#FFFFFF</color>           (Blanco)
<color name="radio_gray">#333333</color>            (Gris oscuro)
<color name="alterna_logo_blue">#3B4DA8</color>     (Azul logo)
<color name="led_green_bright">#00FF00</color>      (Verde LED)
```

### Componentes UI
- **Parlante**: 
  - Altura: 378dp
  - Esquinas: 24dp redondeadas
  - Textura: Malla metálica (hexágonos)
  - Overlay: ImageView animable

- **Display LED**: 
  - SevenSegmentView 72dp alto
  - Muestra frecuencia actual

- **Carousel**: 
  - ViewPager2 378dp altura
  - Orientación vertical
  - Auto-scroll cada 5 segundos

- **Números Dial**: 
  - Tamaño: 16sp (100% más grande que original)
  - Color: Gris (#666666)
  - Fuente: Monospace

- **Ecualizador**: 
  - 12 barras de 4dp ancho
  - Colores: Rojo (#3B4DA8)
  - Altura: 6-18dp estáticas (se animan)
  - Duración: 250-450ms por ciclo

## 🔄 Flujos de Funcionamiento

### Flujo de Arranque
```
App.onCreate()
  ↓
Inicializar vistas (WebView, Displays, Carousel, etc.)
  ↓
Configura WebView → load https://radio.alterna.ar
  ↓
Inicia ecualizador animado (startAnimation)
  ↓
Inicia extracción periódica de títulos (cada 3 seg)
  ↓
Carga página → extrae imágenes
  ↓
App lista para usar
```

### Flujo de Presionar INFO
```
Usuario presiona INFO
  ↓
toggleCarousel() activado
  ↓
Si no visible:
  - speakerTextureOverlay.animate()
  - translationY: 0 → -height
  - alpha: 1 → 0
  - duration: 400ms
  - Luego: visibility = GONE
  ↓
Si visible:
  - speakerTextureOverlay.setVisibility(VISIBLE)
  - speakerTextureOverlay.animate()
  - translationY: -height → 0
  - alpha: 0 → 1
  - duration: 400ms
  ↓
Carousel mostrado/oculto debajo
```

### Flujo de Extracción de Canción
```
Timer (cada 3 segundos)
  ↓
extractCurrentSong()
  ↓
JavaScript ejecutado:
  1. Intenta API AzuraCast
  2. Intenta selectores DOM
  3. Intenta document.title
  ↓
Resultado limpiado:
  - divide por " | "
  - divide por " - "
  - limita a 200 chars
  ↓
Si cambió respecto a anterior:
  - updateNowPlayingDisplay(titulo)
  - callback.onSongExtracted()
  ↓
Nueva extracción programada
```

### Flujo del Ecualizador
```
equalizerAnimator.startAnimation()
  ↓
Para cada barra (12 total):
  - Obtiene altura inicial
  - Calcula altura aleatoria (30%-200%)
  - Crea ValueAnimator
  - Anima en 250-450ms
  ↓
onAnimationEnd():
  - isAnimating = false
  - Llama startAnimation() nuevamente
  ↓
Loop continuo mientras app está activa
```

## 🛠️ Compilación e Instalación

### Requisitos
- Android SDK API 21+
- Gradle 9.0.0
- Java JDK 11+

### Compilar APK Debug
```bash
cd /Users/cristianbaldini/Proyectos/AlternaRadio/alternaradio-android
./gradlew assembleDebug -x lint
```
**Ubicación generada**: `app/build/outputs/apk/debug/app-debug.apk`

### Compilar APK Release
```bash
./gradlew assembleRelease -x lint
```
**Ubicación generada**: `app/build/outputs/apk/release/app-release-unsigned.apk`

### Instalar en Dispositivo
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### Script Interactivo
```bash
./compilar.sh
```
Menú interactivo que permite elegir:
1. APK Debug
2. APK Release
3. Limpiar proyecto

## 📦 Dependencias

```gradle
dependencies {
    // AndroidX
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'androidx.viewpager2:viewpager2:1.0.0'
    
    // Material Design
    implementation 'com.google.android.material:material:1.9.0'
    
    // Glide (carga de imágenes)
    implementation 'com.github.bumptech.glide:glide:4.15.1'
    
    // Notificaciones
    implementation 'androidx.core:core:1.10.1'
}
```

## 🔒 Permisos

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## 📱 Compatibilidad

- **Android Mínimo**: API 21 (Android 5.0)
- **Android Objetivo**: API 34
- **Orientación**: Portrait (vertical)
- **Pantallas**: Optimizado para 5-6 pulgadas
- **Densidades**: mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi

## 🎯 Configuración de Animaciones

### Ecualizador
- **Duración**: 250-450ms por ciclo
- **Rango**: 30%-200% de altura original
- **Repetición**: Continua

### Parlante (Texture Overlay)
- **Duración**: 400ms
- **Movimiento**: Traslación vertical
- **Fade**: 1.0 → 0.0 o 0.0 → 1.0

### Carousel
- **Auto-scroll**: Cada 5 segundos
- **Orientación**: Vertical
- **Transición**: Suave

## 📋 Archivos de Configuración

- `build.gradle`: Configuración de build del proyecto
- `app/build.gradle`: Dependencias y versiones
- `gradle.properties`: Propiedades globales
- `local.properties.example`: Plantilla de local.properties
- `AndroidManifest.xml`: Permisos y configuración

## 🐛 Problemas Conocidos

- Auto-scroll del carousel se pausa si el usuario scrollea manualmente
- Extracción de imágenes depende de la estructura HTML de la página actual
- Título limitado a 200 caracteres

## 📈 Mejoras Futuras

- [ ] Soporte para múltiples radios/frecuencias
- [ ] Selector de temas de color
- [ ] Grabación de audio
- [ ] Historial de canciones
- [ ] Favoritos/Playlist

## 👨‍💻 Información Técnica

- **Versión**: 1.0.0
- **Lenguaje**: Java
- **Framework**: Android Native
- **Compilador**: Gradle 9.0.0
- **Última Actualización**: Julio 2024
- **Autor**: Alterna Radio Development Team

## 📄 Licencia

© 2024 Alterna Radio. Todos los derechos reservados.

---

**Para más información o reporte de bugs, contacta al equipo de desarrollo.**

