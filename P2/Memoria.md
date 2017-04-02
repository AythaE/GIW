<!--Portada-->

<div class="portada">


# Práctica 2
# Caso Práctico de Análisis y Evaluación de Redes en Twitter
*****

<img src="imgs/Twitter_logo.png" alt="Logo Twitter" style="width: 250px; height: auto;"/>

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

- [1. Selección de un medio social, definición de una pregunta de investigación y obtención de un conjunto de datos asociado](#1-seleccion-de-un-medio-social-definicion-de-una-pregunta-de-investigacion-y-obtencion-de-un-conjunto-de-datos-asociado)
  * [1.1 Obtención de datos](#11-obtencion-de-datos)
  * [1.2 Pregunta de investigación](#12-pregunta-de-investigacion)
- [2. Construcción de la red social on-line a analizar y visualizar](#2-construccion-de-la-red-social-on-line-a-analizar-y-visualizar)
- [3. Cálculo de los valores de las medidas de análisis](#3-calculo-de-los-valores-de-las-medidas-de-analisis)
- [4. Determinación de las propiedades de la red](#4-determinacion-de-las-propiedades-de-la-red)
- [5. Calculo de los valores de las medidas de análisis de redes sociales](#5-calculo-de-los-valores-de-las-medidas-de-analisis-de-redes-sociales)
- [6. Descubrimiento de comunidades en la red](#6-descubrimiento-de-comunidades-en-la-red)
- [7. Visualización de la red social](#7-visualizacion-de-la-red-social)
- [8. Discusión de los resultados obtenidos](#8-discusion-de-los-resultados-obtenidos)
- [Anexo: twitter_search](#anexo-twitter_search)
  * [Tutorial](#tutorial)
  * [Uso del programa](#uso-del-programa)
- [Bibliografía](#bibliografia)

<!-- tocstop -->

<!-- Salto de página -->
<div style="page-break-before: always;"></div>

## 1. Selección de un medio social, definición de una pregunta de investigación y obtención de un conjunto de datos asociado
Estos últimos días se ha creado mucha polémica en España por la sentencia de la Audiencia Nacional el caso de los chistes de Carrero Blanco [[1]](#1) hechos por la tuitera Casandra Vera, los cuales le han llevado a una condena de 1 año de cárcel y 7 de inhabilitación. Me resulta un tema interesante como se pueden interpretar los límites de la libertad de expresión, la delgada linea entre el humor negro y el delito. Por ello he decidido estudiar este tema usando Twitter como medio social.

### 1.1 Obtención de datos
La obtención de datos de Twitter se ha realizado de un modo distinto del comentado en clase debido a problemas de compatibilidad de NodeXL con mi versión de Office, por ello decidí descargar tuits usando la API de Twitter mediante el módulo de python **Tweepy**[[2]](#2). He creado un script en python llamado `twitter_search.py` que utiliza Tweepy para descargar tuits a partir de una cadena de caracteres usada para la búsqueda, además permite especificar el número de tuits a recuperar, las fechas entre las que se han escrito los tuits devueltos, o el idioma de los tuits. Ver el apartado [Anexo: twitter_search](#anexo-twitter_search) para más información.

### 1.2 Pregunta de investigación
La pregunta concreta de investigación es **¿Cúales los usuarios más relevantes que han tuiteado usando la palabras "Carrero Blanco"?**

Es necesario acotar un poco más la pregunta de investigación, ya que no he determinado una ventana temporal para estudiar el tema, por ello empecé descargando todos los tuits que se habían escrito en distintos días para ver como evoluciona su repercusión a lo largo del tiempo y elegir el día más interesante para estudiarlo. Con estos datos he creado la siguiente gráfica que representa el número de tuits respecto a los días:

<img src="imgs/Evolucion_num_Tweets.png" alt="Gráfico de evolución del número de Tweets respecto a la fecha" style="text-align: center;"/>

Como se puede apreciar se produce un importante incremento del número de Tweets que contienen "Carrero Blanco" el día **29 de Marzo** (llegando a 115.254 tuits), coincidiendo con la sentencia que condenaba a Casandra Vera por sus chistes. Por ello este será el día seleccionado para realizar el estudio.

## 2. Construcción de la red social on-line a analizar y visualizar

## 3. Cálculo de los valores de las medidas de análisis

## 4. Determinación de las propiedades de la red

## 5. Calculo de los valores de las medidas de análisis de redes sociales

## 6. Descubrimiento de comunidades en la red

## 7. Visualización de la red social

## 8. Discusión de los resultados obtenidos


## Anexo: twitter_search
**Twitter_search** es un programa en Python para recuperar tuits que contengan ciertas palabras o hashtags y exportarlos en ficheros `.csv`, permite seleccionar las fechas entre las que buscar los tuits, el número de tuits a recuperar y el idioma de los tuits. Usa el módulo Tweepy [[2]](#2) para manejar las llamadas a la API de Twitter.

Tras descargar los tuits genera tres ficheros `.csv`:
- `queryX_(dateSince_dateUntil)_Tweets.csv`: Contiene el texto e id de los tuits descargados, su fecha de creación, su autor, el número de seguidores de este, el número de "amigos" (gente a la que sigue) de este, el número de tuits escritos, el número de me gustas, la zona horaria y la localización.
- `queryX_(dateSince_dateUntil)_Users.csv`: Contiene los mismos datos del usuario que el fichero previo pero con una sola entrada por cada usuario, en el caso previo si un mismo usuario ha escrito varios tuits aparecerá varias veces en el fichero. Está preparádo para ser importado en [Gephi](https://gephi.org/) como una hoja de cálculo, en concreto como tabla de nodos.
- `queryX_(dateSince_dateUntil)_Edges.csv`: Contiene las relaciones de los tuits descargados, es decir menciones a usuarios y retuits.  Está preparádo para ser importado en [Gephi](https://gephi.org/) como una hoja de cálculo, en concreto como tabla de aristas.

Es posible que existan menciones a usuarios no existentes en el fichero de usuarios, estos usuarios se pueden añadir marcando el checkbox de **crear nodos inexistentes** al importar la lista de aristas en Gephi. Para crearlos Gephi crea nuevas filas en el fichero de nodos con todas las columnas vacías a excepción del ID que saca del fichero de relaciones, por ello para que al menos aparezca el label es necesario acceder al _Laboratorio de datos_ de Gephi y seleccionar la opción de copiar columna ID sobre la columna Label. Con esto podemos analizar la red social mejor al tener no solo los usuarios que han escrito tuits, si no aquellos que han sido mencionados o retuiteados en estos tuits, como inconveniente es que de esos usuarios generados por Gephi solo poseemos su ID y Label, no sus datos de twitter como el número de seguidores, por ello habrá que tener precaución al usar estas medidas en el análisis ya que existen valores perdidos.

Para usar este programa es necesario registrar una aplicación en Twitter para obtener los credenciales necesarios para consultar la API, una vez obtenidos hay que copiarlos como cadenas de caracteres en el fichero [`credentials.py`](credentials.py).

### Tutorial
Para obtener una descripción detallada de los pasos a realizar para utilizar este programa se puede seguir el siguiente tutorial que también utiliza Tweepy para descargar datos de Twitter y utilizarlos en Gephi [[3]](#3).

### Uso del programa
Es necesario instalar Python 3 y `virtualenv` antes de usar este programa, una vez hecho esto se pueden instalar el resto de dependencias ejecutando los siguientes comandos (los comandos han sido probados en una máquina Debian, pueden ser diferentes en su S.O.):

```
virtualenv -p$(which python3) venv
source venv/bin/activate
pip install -r requirements.txt
```

Y para ejecutar el script hay que introducir el siguiente comando:

```
python twitter_search.py
```

<img src="imgs/Ejemplo_twitter_search.png" alt="Ejemplo de ejecución de twitter_search" style="width: 500px; height: auto;"/>



<!-- Salto de página -->
<div style="page-break-before: always;"></div>

## Bibliografía

<p id="1">

[1]: La Audiencia Nacional condena a un año de cárcel a Cassandra por los tuits sobre Carrero Blanco (n.d). Recuperado el 1 de Abril de 2017, desde <http://www.eldiario.es/politica/Audiencia-Nacional-condena-tuitera-Cassandra_0_627487833.html>

</p>

<p id="2">

[2]: Tweepy: Twitter for Python! (n.d). Recuperado el 1 de Abril de 2017, desde <https://github.com/tweepy/tweepy>

</p>

<p id="3">

[3]: Maths with Python 6: Twitter API – Tweepy for social media and networks (with Gephi) (n.d). Recuperado el 2 de Abril de 2017, desde <https://thebrickinthesky.wordpress.com/2014/06/26/maths-with-python-6-twitter-api-tweepy-for-social-media-and-networks-with-gephi/>

</p>
<!--
Ejemplo de formato de cita para un articulo
P. Cortez & A. Silva. (2008). Using Data Mining to Predict Secondary School Student Performance.
In A. Brito and J. Teixeira Eds., Proceedings of 5th FUture BUsiness TEChnology Conference
(FUBUTEC 2008) pp. 5-12, Porto, Portugal, EUROSIS, ISBN 978-9077381-39-7.
-->
