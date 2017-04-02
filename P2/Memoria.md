<!--Portada-->

<div class="portada">


# Práctica 2
# Caso Práctico de Análisis y Evaluación de Redes en Twitter
*****

<img src="imgs/Twitter_logo.png" alt="" style="width: 250px; height: auto;"/>

<div class="portada-middle">

### Gestión de Información en la Web
### Máster en Ingeniería Informática
### Curso 2016/17
### Universidad de Granada

</div>
<div class="portada-down">

> Nombre: Aythami Estévez Olivas
> DNI: 70918176E
> Email: <aythae@correo.ugr.es>

</div>
</div>

<!-- Salto de página -->
<div style="page-break-before: always;"></div>

## Índice

<!--
Ejemplo de Indice final eliminando el enlace y añadiendo el número de página
- Apartado 1 <span style='float:right'>2</span>
-->

<!-- toc -->

- [1. Selección de un medio social, definición de una pregunta de investigación y obtención de un conjunto de datos asociado.](#1-seleccion-de-un-medio-social-definicion-de-una-pregunta-de-investigacion-y-obtencion-de-un-conjunto-de-datos-asociado)
- [2. Construcción de la red social on-line a analizar y visualizar.](#2-construccion-de-la-red-social-on-line-a-analizar-y-visualizar)
- [3. Cálculo de los valores de las medidas de análisis.](#3-calculo-de-los-valores-de-las-medidas-de-analisis)
- [4. Determinación de las propiedades de la red.](#4-determinacion-de-las-propiedades-de-la-red)
- [5. Calculo de los valores de las medidas de análisis de redes sociales.](#5-calculo-de-los-valores-de-las-medidas-de-analisis-de-redes-sociales)
- [6. Descubrimiento de comunidades en la red.](#6-descubrimiento-de-comunidades-en-la-red)
- [7. Visualización de la red social.](#7-visualizacion-de-la-red-social)
- [8. Discusión de los resultados obtenidos.](#8-discusion-de-los-resultados-obtenidos)
- [Bibliografía](#bibliografia)
- [Anexo: twitter_search](#anexo-twitter_search)

<!-- tocstop -->

<!-- Salto de página -->
<div style="page-break-before: always;"></div>

## 1. Selección de un medio social, definición de una pregunta de investigación y obtención de un conjunto de datos asociado.
Estos últimos días se ha creado mucha polémica en España por la sentencia de la Audiencia Nacional el caso de los chistes de Carrero Blanco [[1]](#1) hechos por la tuitera Casandra Vera, los cuales le han llevado a una condena de 1 año de cárcel y 7 de inhabilitación. Me resulta un tema interesante como se pueden interpretar los límites de la libertad de expresión, la delgada linea entre el humor negro y el delito. Por ello he decidido estudiar este tema usando Twitter como medio social.

La pregunta concreta de investigación es **¿Cúales los usuarios más relevantes que han tuiteado usando la palabras "Carrero Blanco"?**

La obtención de datos de Twitter se ha realizado de un modo distinto del comentado en clase debido a problemas de compatibilidad de NodeXL con mi versión de Office, por ello decidí descargar tuits usando la API de Twitter mediante el módulo de python **Tweepy**[[2]](#2). He creado un script en python llamado `twitter_search.py` que utiliza Tweepy para descargar tuits a partir de una cadena de caracteres usada para la búsqueda, además permite especificar el número de tuits a recuperar, las fechas entre las que se han escrito los tuits devueltos, o el idioma de los tuits. Ver el apartado [Anexo: twitter_search](#anexo-twitter_search) para más información.

## 2. Construcción de la red social on-line a analizar y visualizar.

## 3. Cálculo de los valores de las medidas de análisis.

## 4. Determinación de las propiedades de la red.

## 5. Calculo de los valores de las medidas de análisis de redes sociales.

## 6. Descubrimiento de comunidades en la red.

## 7. Visualización de la red social.

## 8. Discusión de los resultados obtenidos.

<!-- Salto de página -->
<div style="page-break-before: always;"></div>

## Bibliografía

<p id="1">

[1]: La Audiencia Nacional condena a un año de cárcel a Cassandra por los tuits sobre Carrero Blanco (n.d). Recuperado el 1 de Abril de 2017, desde <http://www.eldiario.es/politica/Audiencia-Nacional-condena-tuitera-Cassandra_0_627487833.html>

</p>

<p id="2">

[2]: Tweepy: Twitter for Python! (n.d). Recuperado el 1 de Abril de 2017, desde <https://github.com/tweepy/tweepy>

</p>
<!--
Ejemplo de formato de cita para un articulo
P. Cortez & A. Silva. (2008). Using Data Mining to Predict Secondary School Student Performance.
In A. Brito and J. Teixeira Eds., Proceedings of 5th FUture BUsiness TEChnology Conference
(FUBUTEC 2008) pp. 5-12, Porto, Portugal, EUROSIS, ISBN 978-9077381-39-7.
-->

<!-- Salto de página -->
<div style="page-break-before: always;"></div>

## Anexo: twitter_search
