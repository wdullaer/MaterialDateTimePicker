# Material DateTime Picker - Seleccione una hora/fecha con estilo

[![Únete al chat en https://gitter.im/wdullaer/MaterialDateTimePicker](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/wdullaer/MaterialDateTimePicker?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
![Maven Central](https://img.shields.io/maven-central/v/com.wdullaer/materialdatetimepicker.svg)
![estado de compilación](https://travis-ci.org/wdullaer/MaterialDateTimePicker.svg?branch=master)


Material DateTime Picker intenta ofrecerle los selectores de fecha y hora como se muestra en [Especificación de diseño de materiales](http://www.google.com/design/spec/components/pickers.html), con una API
fácil de usar.
La biblioteca utiliza [el código de los marcos de Android](https://android.googlesource.com/platform/frameworks/opt/datetimepicker/) La biblioteca utiliza [el código de los marcos de Android] como base y lo ajusta para que esté lo más idéntico posible al ejemplo de los diseños de materiales.

Soporte para Android 4.0 y superior.

Siéntase libre de crear un _fork_ o emitir solicitudes de _pull request_ en github. Los problemas se pueden informar en el rastreador de problemas github.

**Diseño de la versión #2**

Selector de fecha | Selector de tiempo
--- | ---
![Selector de fechas](https://raw.github.com/wdullaer/MaterialDateTimePicker/gh-pages/images/date_picker_v2.png) | ![Selector de tiempo](https://raw.github.com/wdullaer/MaterialDateTimePicker/gh-pages/images/time_picker_v2.png)

**Diseño de la versión #1**

Selector de fecha | Selector de tiempo
---- | ----
![Selector de fechas](https://raw.github.com/wdullaer/MaterialDateTimePicker/gh-pages/images/date_picker.png) | ![Selector de tiempo](https://raw.github.com/wdullaer/MaterialDateTimePicker/gh-pages/images/time_picker.png)


## Tabla de contenido
    1. [Ajustar](#setup)
    2. [Usar selectores de fecha/hora de material](#using-material-datetime-pickers)
    1. [Implementar oyentes](#implement-an-ontimesetlistenerondatesetlistener)
    2. [Crear selectores](#create-a-timepickerdialogdatepickerdialog-using-the-supplied-factory)
    3. [Elección de tema para los Pickers](#theme-the-pickers)
    3. [Opciones adicionales](#additional-options)
    4. [Preguntas más frecuentes](#faq)
    5. [Mejoras potenciales](#potential-improvements)
6. [Licencia](#license)


## Ajustes
    La forma más fácil de agregar a la Biblioteca la _Material DateTime Picker_ a su proyecto es agregarla como una dependencia `build.gradle`
    ```groovy
    a sus dependencias{
        compile 'com.wdullaer:materialdatetimepicker:3.6.0'
}
```

También puede agregar la biblioteca como una biblioteca de Android a su proyecto. Todos los archivos de la biblioteca están en `library`


## Usar Material DateTime Picker <tt>selectores de fecha/hora</tt>
La biblioteca sigue la misma API que otros selectores en el marco de Android.
Para una implementación básica, necesitarás

1. Implementar un `OnTimeSetListener`/`OnDateSetListener` <tt>Oyente en tiempo establecido/Oyente en la fecha establecida</tt>
2. Crear un `TimePickerDialog`/`DatePickerDialog` <tt>Diagrama de selector de tiempo/Diagrama selector de fecha</tt> usando la fabricaciones previas suministradas
3. Dale un tema a los selectores

### Implementa la opción `OnTimeSetListener`/`OnDateSetListener` <tt>En oyente tiempo establecido/Oyente en la fecha establecida</tt>
Para recibir la fecha u hora configurada en el selector, deberá implementar las interfaces `OnTimeSetListener` <tt>En oyente tiempo establecido</tt> o 
 `OnDateSetListener` <tt>Oyente en la fecha establecida</tt>. Normalmente, esta será la `Activity` <tt>Actividad</tt> o `Fragment` <tt>Fragmentos</tt> que crearan los Selectores. Las devoluciones de llamada utilizan la misma API que los buscadores estándar de Android.
```java
@Override
public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
  String time = "You picked the following time: "+hourOfDay+"h"+minute+"m"+second;
  timeTextView.setText(time);
}

@Override
public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
  String date = "You picked the following date: "+dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
  dateTextView.setText(date);
}
```

### Crea un `TimePickerDialog`/`DatePickerDialog` <tt>diálogo de selector de tiempo/diálogo selector de fecha</tt> usando las fabricaciones suministradas
Deberá crear una nueva instancia de `TimePickerDialog` o `DatePickerDialog` <tt>diálogo de selector de tiempo/diálogo selector de fecha</tt> utilizando el método estático `newInstance()` <tt>Nueva instancia()</tt>, suministrando los valores predeterminados correctos y una devolución de llamada. Una vez que los diálogos están configurados, puede escribir `show()` <tt>mostrar()</tt>.
```java
Calendar now = Calendar.getInstance();
DatePickerDialog dpd = DatePickerDialog.newInstance(
  MainActivity.this,
  now.get(Calendar.YEAR),
  now.get(Calendar.MONTH),
  now.get(Calendar.DAY_OF_MONTH)
);
dpd.show(getFragmentManager(), "Datepickerdialog");
```

### Darle un tema a los selectores
La biblioteca contiene 2 versiones de diseño para cada selector.

* Versión 1: este es el diseño original. Se basa en el diseño que Google utilizó en el kitkat y en la era inicial del diseño de materiales.
* Versión 2: este diseño se basa en las directrices que Google publicó al ejecutar Android marshmallow. Este es el diseño predeterminado y aún el más actual.

Puede configurar la versión de diseño usando la fábrica
```java
dpd.setVersion(DatePickerDialog.Version.VERSION_2);
```

Los selectores serán temáticos de forma automática en función del tema actual en el que se crean, en función del `ColorAccent` <tt>Acentuar color</tt> actual. También puede tema los cuadros de diálogo a través del método `setAccentColor(int color)`. Alternativamente, puedes darle un tema a los selectores sobrescribiendo los recursos de color `mdtp_accent_color` y `mdtp_accent_color_dark` en el proyecto.
```xml
<color name="mdtp_accent_color">#009688</color>
<color name="mdtp_accent_color_dark">#00796b</color>
```

El orden exacto en que se seleccionan los colores es el siguiente:

1. `setAccentColor(int color)` en código java
2. `android.R.attr.colorAccent` (si es Android 5.0+)
3. `R.attr.colorAccent` (p.ej. cuando se usa AppCompat)
4. `R.color.mdtp_accent_color` y `R.color.mdtp_accent_color_dark` si ninguno de los otros está configurado en su proyecto

Los selectores también tienen un tema oscuro. Esto se puede especificar de forma global utilizando el atributo `mdtp_theme_dark` <tt>tema oscuro</tt> en su tema o las funciones `setThemeDark(boolean themeDark)` <tt>establecer el tema oscuro</tt>. La función llama a sobrescribir la configuración XML.
```xml
<item name="mdtp_theme_dark">true</item>
```


## Opciones adicionales
* `TimePickerDialog` <tt>diálogo de selector de tiempo</tt> tema oscuro  
El `TimePickerDialog` <tt>diálogo de selector de tiempo</tt> tiene un tema oscuro que se puede establecer tipeando
```java
tpd.setThemeDark(true);
```

* `DatePickerDialog` <tt>diálogo selector de fecha</tt> tema oscuro
El `DatePickerDialog` <tt>diálogo selector de fecha</tt> tiene un tema oscuro que se puede establecer tipeando
```java
dpd.setThemeDark(true);
```

* `setAccentColor(String color)` <tt>establecer el color (color de la cadena)</tt> y `setAccentColor(int color)`
Ajuste el color de acento que utilizará el cuadro de diálogo. La versión String analiza el color usando `Color.parseColor()`. La versión int requiere una cadena de bytes ColorInt. Establecerá explícitamente el color a totalmente opaco.

* `setOkColor()` <tt>ajustar color()</tt> y `setCancelColor()`<tt>cancelar ajuste de color()</tt>
Ajuste el color del texto para el botón Aceptar o Cancelar. Se comporta de manera similar a `setAccentColor()` <tt>establecer color de acento</tt>

* `TimePickerDialog` `setTitle(String title)` <tt>selector de tiempo diálogo, establecer título (título de la cadena)</tt> 
Muestra un título en la parte superior del `TimePickerDialog` <tt>diálogo de selector de tiempo</tt>

* `DatePickerDialog` `setTitle(String title)`
Muestra un título en la parte superior del `DatePickerDialog` en lugar del día de la semana

* `setOkText()` y `setCancelText()`  
Ajuste el texto personalizado para el diálogo en Aceptar y cancelar etiquetas. Puede tomar recursos de una Cadena. Funciona tanto en DatePickerDialog como en TimePickerDialog

* `setMinTime(Timepoint time)`  
Ajuste el tiempo mínimo válido para ser seleccionados. Los valores de tiempo más temprano en el día serán desactivados

* `setMaxTime(Timepoint time)`  
Ajuste el tiempo válido máximo para ser seleccionado. Los valores de tiempo más tarde en el día serán desactivados

* `setSelectableTimes(Timepoint[] times)`  
Puede pasar una serie de `Timepoints`. Estos valores son las únicas selecciones válidas en el selector. `setMinTime(Timepoint time)`, `setMaxTime(Timepoint time)` y `setDisabledTimes(Timepoint [] times)` recortarán aún más esta lista. Intente especificar puntos de tiempo solo hasta la resolución de su selector (es decir, no agregue segundos si la resolución del selector es de minutos).

* `setDisabledTimes(Timepoint[] times)`  
Puede pasar una serie de `Timepoints`. Estos valores no estarán disponibles para la selección. Estos tienen prioridad sobre `setSelectableTimes` y `setTimeInterval`. Tenga cuidado al usar esto sin tiempos seleccionables: el redondeo a un punto de tiempo válido es una operación muy costosa si se inhabilitan muchos puntos de tiempo consecutivos. Intente especificar Puntos de tiempo solo hasta la resolución de su selector (es decir, no agregue segundos si la resolución del selector es de minutos).

* `setTimeInterval(int hourInterval, int minuteInterval, int secondInterval)`  
Establezca el intervalo de tiempos seleccionables en TimePickerDialog. Este es un contenedor de conveniencia alrededor de `setSelectableTimes`. El intervalo para los tres componentes de tiempo se puede establecer de forma independiente. Si no está utilizando el selector de segundos/minutos, configure el elemento respectivo en 60 para un mejor rendimiento.

* `setTimepointLimiter(TimepointLimiter limiter)`  
Pase en una implementación personalizada de`TimeLimiter`
Desactive `setSelectableTimes`, `setDisabledTimes`, `setTimeInterval`, `setMinTime` y `setMaxTime`

* `setSelectableDays(Calendar[] days)`  
You can pass a `Calendar[]` to the `DatePickerDialog`. The values in this list are the only acceptable dates for the picker. It takes precedence over `setMinDate(Calendar day)` and `setMaxDate(Calendar day)`

* `setDisabledDays(Calendar[] days)`  
Los valores en este `Calendario []` están explícitamente deshabilitados (no seleccionables). Esta opción se puede usar junto con `setSelectableDays(Calendar [] days)`: en caso de que haya un conflicto `setDisabledDays(Calendar [] days)` tendrá prioridad sobre `setSelectableDays(Calendar [] days)`

* `setHighlightedDays(Calendar[] days)`  
Puede pasar un `Calendario []` de días para resaltar. Se presentarán en negrita. Puede modificar el color de los días resaltados sobrescribiendo `mdtp_date_picker_text_highlighted`
* `showYearPickerFirst(boolean yearPicker)`  
Muestre primero el selector de año, en lugar del selector de mes y día.

* `OnDismissListener` and `OnCancelListener`  
Ambos selectores pueden pasar un `DialogInterface.OnDismissLisener` o` DialogInterface.OnCancelListener` que le permite ejecutar el código cuando se produce cualquiera de estos eventos.
```java
tpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
    @Override
    public void onCancel(DialogInterface dialogInterface) {
      Log.d("TimePicker", "Dialog was cancelled");
    }
});
```

* `vibrate(boolean vibrate)` <tt>vibrar</tt> 
Establezca si los cuadros de diálogo deben hacer vibrar el dispositivo cuando se realiza una selección. Esta predeterminación es `true`.

* `dismissOnPause(boolean dismissOnPause)` <tt>desactivado en pausa</tt> 
Ajuste si el seleccionador se descarta cuando la Actividad principal está en pausa o si se recrea cuando se reanuda la Actividad.

* `setLocale(Locale locale)` <tt>establecer local</tt> 
Permite al cliente establecer una configuración regional personalizada que se utilizará al generar varias cadenas en los selectores. Por defecto, se usará la configuración regional actual del dispositivo. Debido a que los selectores se adaptarán a la configuración regional del dispositivo de forma predeterminada, solo debería tener que usar esto en circunstancias muy excepcionales.

* `DatePickerDialog` <tt>Diagrama selector de fecha</tt> `autoDismiss(boolean autoDismiss)` <tt>descarte automático</tt>
Si se establece en `true` <tt>cierto</tt>, se cerrará el selector cuando el usuario seleccione una fecha. Esta predeterminación es `false` <tt>falso</tt>.

* `TimepickerDialog` <tt>Diagrama de selector de tiempo</tt> `enableSeconds(boolean enableSconds)` <tt>habilitar segundos</tt> and `enableMinutes(boolean enableMinutes)` <tt>habilitar minutos</tt>
Le permite habilitar o deshabilitar un selector de segundos y minutos en el `TimepickerDialog`. Habilitar el selector de segundos implica habilitar el selector de minutos. Deshabilitar el selector de minutos desactivará el selector de segundos. Se usará la última configuración aplicada. Por defecto `enableSeconds = false` y `enableMinutes = true`.

* `DatePickerDialog` <tt>Diagrama selector de fecha</tt> `setTimeZone(Timezone timezone)` <tt>establecer zona horaria</tt> *obsoleto*  
Establece la `Timezone` utilizada para representar el tiempo internamente en el selector. El valor predeterminado es la zona horaria predeterminada actual del dispositivo.
Este método ha quedadoobsoleto: debe usar el método `newInstance()` que toma un calendario configurado en la TimeZone adecuada.

* `DatePickerDialog` <tt>Diagrama selector de fecha</tt> `setDateRangeLimiter(DateRangeLimiter limiter)` <tt>ajuste el limitador de rango de fecha (limitador de límite de rango de fecha)</tt>
Proporcione una implementación personalizada de DateRangeLimiter, que le brinda control total sobre los días disponibles para la selección. Esto desactiva todas las demás opciones que limitan la selección de fecha.

* `getOnTimeSetListener()` <tt>ponte a tiempo al oyente</tt> y `getOnDateSetListener()` <tt>ponerse al día con el oyente configurado</tt> 
Recibidores que permiten la recuperación de una referencia a las devoluciones de llamada actualmente asociadas con los recolectores

## Preguntas más frecuentes

### ¿Por qué no usar `SupportDialogFragment` <tt>fragmento de diálogo de soporte</tt>?
No utilizar las versiones de la biblioteca de soporte ha sido una elección bien considerada, basada en las siguientes consideraciones:

* Lmenos del 5% de los dispositivos que usan el mercado Android no admiten `Fragments` <tt>Fragmentos</tt> nativos, un número que disminuirá aún más en el futuro.
* ESi usas `SupportFragments` en tu aplicación, puedes usar el `FragmentManager` normal. Ambos pueden existir uno al lado del otro.

Esto significa que en la configuración actual, todos pueden usar la biblioteca: personas que usan la biblioteca de soporte y personas que no usan la biblioteca de soporte.

Finalmente, cambiar el `SupportDialogFragment` <tt>fragmento de diálogo de soporte</tt> ahora romperá la API para todas las personas que usan esta biblioteca.

Si realmente necesita el `SupportDialogFragment` <tt>fragmento de diálogo de soporte</tt, puede hacer un fork en la biblioteca (Implica cambiar las 2 líneas de código, por lo que debería ser lo suficientemente fácil para mantenerlo actualizado con el upstream) o use este fork: https://github.com/infinum/MaterialDateTimePicker

```groovy
dependencies {
  compile 'co.infinum:materialdatetimepicker-support:3.6.0'
}
```

### ¿Por qué el `DatePickerDialog` <tt>diálogo de selección de fechas</tt> devuelve el mes -1 seleccionado?
En la clase `Calendar` <tt>Calendario</tt> de Java, los meses usan indexación basada en 0: enero es el mes 0, diciembre es el mes 11. Esta convención es ampliamente utilizada en el mundo java, por ejemplo, el _native Android DatePicker_.

### ¿Cómo uso una versión diferente de la biblioteca de soporte en mi aplicación?
Esta biblioteca depende de la biblioteca del soporte de Android. Debido a que jvm solo permite cargar una versión de una clase con espacio de nombres completo, se encontrará con problemas si su aplicación depende de una versión diferente de la biblioteca de soporte que la utilizada en esta aplicación. Gradle es generalmente bueno resolviendo conflictos de versiones (por defecto conservará la última versión de una biblioteca), pero si tiene problemas (por ejemplo, porque ha desactivado la resolución de conflictos), 
puede desactivar la carga de la biblioteca de soporte para MaterialDateTimePicker <tt>material de selección de tiempo y fecha</tt>.

Usando el siguiente fragmento en el archivo `build.gradle` de su aplicación, puede excluir la posibilidad de que se instale la biblioteca de soporte transitivo de esta biblioteca.

```groovy
compile ('com.wdullaer:materialdatetimepicker:3.6.0') {
        exclude group: 'com.android.support'
}
```

Su aplicación deberá depender al menos de las siguientes piezas de la biblioteca de soporte

```groovy
compile 'com.android.support:support-v4:26.0.1'
compile 'com.android.support:support-v13:26.0.1'
compile 'com.android.support:design:26.0.1'
```

Esto funcionará bien, siempre y cuando la versión de la biblioteca de soporte de la que depende su aplicación sea lo suficientemente reciente (admite `RecyclerView` <tt>Vista al reciclador</tt>) y google no lance una versión en el futuro que contenga cambios de última hora. (Si/Cuando esto ocurra, intentaré documentarlo). Vea el documento [#338](https://github.com/wdullaer/MaterialDateTimePicker/issues/338) para más información.

### ¿Cómo puedo convertir esto en un selector de año y mes?
Este DatePickerDialog <tt>Selector de fecha</tt> se enfoca en seleccionar fechas, lo que significa que su elemento de diseño central es el selector de días. Como esta vista de calendario es el centro del diseño, no tiene sentido intentar deshabilitarlo. Como tal, la selección de solo años y meses, sin un día, no está dentro del alcance de esta biblioteca y no se agregará.
### ¿Cómo uso mi lógica personalizada para habilitar/deshabilitar las fechas?
`DatePickerDialog` <tt>Limitador de rango de fechas</tt> expone algunos métodos de utilidad para habilitar/deshabilitar fechas para escenarios comunes. Si sus necesidades no están cubiertas por estas, puede suministrar una implementación personalizada de la interfaz `DateRangeLimiter` <tt>Limitador de rango de fechas</tt>.
Debido a que `DateRangeLimiter`<tt>Limitador de rango de fechas</tt> se conserva cuando el `Dialog` <tt>Dialogo</tt> hace una pausa, su implementación también debe implementar `Parcelable`.

```java
class MyDateRangeLimiter implements DateRangeLimiter {
  public MyDateRangeLimiter(Parcel in) {

  }

  @Override
  public int getMinYear() {
    return 1900;
  }

  @Override
  public int getMaxYear() {
    return 2100;
  }

  @Override
  public Calendar getStartDate() {
    Calendar output = Calendar.newInstance();
    output.set(Calendar.YEAR, 1900);
    output.set(Calendar.DAY_OF_MONTH, 1);
    output.set(Calendar.MONTH, Calendar.JANUARY);
    return output;
  }

  @Override
  public Calendar getEndDate() {
    Calendar output = Calendar.newInstance();
    output.set(Calendar.YEAR, 2100);
    output.set(Calendar.DAY_OF_MONTH, 1);
    output.set(Calendar.MONTH, Calendar.JANUARY);
    return output;
  }

  @Override
  public boolean isOutOfRange(int year, int month, int day) {
    return false;
  }

  @Override
  public Calendar setToNearestDate(Calendar day) {
      return day;
  }

  @Override
  public void writeToParcel(Parcel out) {

  }

  @Override
  public int describeContents() {
    return 0;
  }

  public static final Parcelable.Creator<MyDateRangeLimiter> CREATOR
        = new Parcelable.Creator<MyDateRangeLimiter>() {
    public MyDateRangeLimiter createFromParcel(Parcel in) {
        return new MyDateRangeLimiter(in);
    }

    public MyDateRangeLimiter[] newArray(int size) {
        return new MyDateRangeLimiter[size];
    }
  };
}
```

Cuando proporcione un `DateRangeLimiter` <tt>Limitador de rango de fechas</tt> personalizado, los métodos integrados para configurar las fechas activadas/desactivadas ya no funcionarán. Tendrá que ser completamente manejado por tu implementación.

### ¿Por qué se pierden mis devoluciones de llamada cuando el dispositivo cambia de orientación?
La solución simple es desactivar a los selectores cuando su actividad está en pausa.

```java
tpd.dismissOnPause(true);
```

Si desea retener a los selectores cuando ocurre un cambio de orientación, las cosas se vuelven un poco más complicadas.

Por defecto, cuando se produce una orientación, Android destruye y recrea toda su `Activity` <tt>Actividad</tt>. Siempre que sea posible, esta biblioteca conservará su estado en un cambio de orientación. Las únicas excepciones notables son las diferentes devoluciones de llamada y oyentes. Estas interfaces a menudo se implementan en `Activities` <tt>Actividades</tt> o `Fragments` <tt>fragmentos</tt>. Tratar de retenerlos ingenuamente causaría pérdidas de memoria. Además de requerir explícitamente que las interfaces de devolución de llamada se implementen en una `Activity` <tt>Actividad</tt>, no hay una manera segura de retener las devoluciones de llamada de manera adecuada, que yo sepa.

Esto significa que es su responsabilidad configurar a los oyentes en la `Activity`'s <tt>Actividad</tt> `onResume()` <tt>en espera</tt> de la devolución de llamadas.

```java
@Override
public void onResume() {
  super.onResume();

  DatePickerDialog dpd = (DatePickerDialog) getFragmentManager().findFragmentByTag("Datepickerdialog");
  TimePickerDialog tpd = (TimePickerDialog) getFragmentManager().findFragmentByTag("TimepickerDialog");

  if(tpd != null) tpd.setOnTimeSetListener(this);
  if(dpd != null) dpd.setOnDateSetListener(this);
}
```


## Mejoras potenciales
* Landscape timepicker puede usar alguna mejora
* Implementar el nuevo estilo de seleccionadores
* Limpieza de código: hay un poco de saliva y cinta adhesiva en los ajustes que he hecho.
* Documente todas las opciones en ambos selectores


## Licencia
    Copyright (c) 2015 Wouter Dullaert

    Licencia bajo la Licencia Apache, Versión 2.0 (la "Licencia");
    no puede usar este archivo excepto en conformidad con la licencia.
    Ypuede obtener una copia de la licencia en

    http://www.apache.org/licenses/LICENSE-2.0

    A menos que lo exija la ley aplicable o se acuerde por escrito, el software 
    distribuido bajo la Licencia se distribuye "TAL CUAL", SIN GARANTÍAS
    O CONDICIONES DE NINGÚN TIPO, ya sea expresa o implícita.
   Consulte la Licencia para conocer el idioma específico que rige los permisos y
   limitaciones bajo la Licencia.
