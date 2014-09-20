# LinuxDay Cagliari Android

App Android per visualizzare il programma e restare informati sul [Linux Day](http://www.linuxday.it/) di Cagliari
organizzato dal [GULCh](http://www.gulch.it/) (**G**ruppo **U**tenti **L**inux **C**agliari **h**...?).

Questa app è basata sull'applicazione [FOSDEM Companion for Android](https://github.com/cbeyls/fosdem-companion-android) di [Christophe Beyls](https://github.com/cbeyls).

L'applicazione sarà disponibile a breve nel Google Play Store.

## Come compilare

Tutte le dipendenze sono gestite tramite [Gradle](http://www.gradle.org/).

### Android Studio

In [Android Studio](http://developer.android.com/sdk/installing/studio.html), selezionare "Import project" e aprire il file **build.gradle** presente nella cartella principale del progetto.

Nella schermata successiva mantenere "Use gradle wrapper" selezionato e cliccare sul pulsante "OK".

Se invece si preferisce usare il gradle installato nel sistema, deselezionare la voce ed eventualmente inserire il percorso della Gradle home.

### Gradle

Il progetto contiene il Gradle Wrapper, per cui non è necessario installare Gradle preventivamente.

#### Linux, Mac

```
chmod +x gradlew
./gradlew assemble
```

#### Windows

```
gradlew assemble
```

## Licenza

Il progetto è rilasciato sotto licenza [Apache License, Versione 2.0](http://www.apache.org/licenses/LICENSE-2.0)

## Used libraries

* [Android Support Library](http://developer.android.com/tools/support-library/) di **The Android Open Source Project**
* [ViewPagerIndicator](http://viewpagerindicator.com/) di **Jake Wharton**
* [PhotoView](https://github.com/chrisbanes/PhotoView) by **Chris Banes**
* [PreferenceFragment Support](https://gist.github.com/cbeyls/7475726) di **Christophe Beyls**
* [Apache Commons IO](http://commons.apache.org/proper/commons-io/) di **The Apache Software Foundation**
* [Apache Commons Lang](http://commons.apache.org/proper/commons-lang/) di **The Apache Software Foundation**
* [Apache Commons Collections](http://commons.apache.org/proper/commons-collections/) di **The Apache Software Foundation**
* [FasterXML Jackson](http://wiki.fasterxml.com/JacksonHome) di **FasterXML, LLC**
* [ORMLite](http://ormlite.com/) di **Gray Watson**

## Contributors

* Paolo Cortis
* Christophe Beyls (Progetto originale)
