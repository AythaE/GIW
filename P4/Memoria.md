<!--Portada-->

<div class="portada">


# Práctica 4
## Desarrollo de un Sistema de Recomendación basado en Filtrado Colaborativo



<div class="portada-imgs">

<img src="imgs/decsai.png" alt="Logo Deperamento de Ciencias de la Computacion e Inteligencia Artificial" style="display: block; float: left; width: 150px; height: auto; "/>

<img src="imgs/ugr.png" alt="Logo Universidad de Granada" style="display: block; float: left; width: 150px; height: auto;   margin-left: 50px; margin-top: 35px;"/>

</div>

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

- [1. Enunciado](#1-enunciado)
- [2. Descripción de la práctica](#2-descripcion-de-la-practica)
  * [2.1. Movie y MovieReview](#21-movie-y-moviereview)
  * [2.2. MovieLensUtils](#22-movielensutils)
  * [2.3. RecomendationSystem](#23-recomendationsystem)
- [3. Manual de usuario](#3-manual-de-usuario)
- [4. Conclusiones](#4-conclusiones)
- [Bibliografía](#bibliografia)

<!-- tocstop -->

<!-- Salto de página -->
<div style="page-break-before: always;"></div>

## 1. Enunciado
Los objetivos de esta práctica son:
1. Entender el proceso de recomendación basado en filtrado colaborativo.
2. Ser capaz de plasmarlo en un programa de ordenador.

## 2. Descripción de la práctica
He decidido desarrollar esta práctica usando el lenguaje de programación **Java** ya que es uno de los lenguajes en los que me siento más cómodo debido a mi experiencia con el mismo. Como entorno de desarrollo o IDE he decidido utilizar [**IntelliJ IDEA**](https://www.jetbrains.com/idea/) ya que me funcionó muy adecuadamente para la práctica previa.

A diferencia de la anterior ocasión, en este caso no he utilizado librería alguna para realizar mi implementación. En los siguientes apartados iré describiendo las clases utilizadas.

Está vez he pensado que sería interesante centrarme en el motor en sí del sistema de recomendación dejando de lado el aspecto gráfico, por lo que he desarrollado una aplicación de consola en lugar de gráfica.

### 2.1. Movie y MovieReview

La primera tarea a realizar es cargar el contenido de la colección MovieLens en memoria para poder trabajar con ella, para ello he creado la clase `Movie` y la clase `MovieReview` para contener los registros leídos del fichero `u.item` y `u.data` respectivamente.

Si analizamos el contenido del fichero `u.data` que según determina el `README` de la colección contiene 100.000 valoraciones de 943 usuarios sobre 1.682 items (películas). Cada linea de este fichero tiene la siguiente estructura: `user id | item id | rating | timestamp`, este ultimo campo lo descarto ya que no me aporta nada al sistema de recomendación, respecto al rating o calificación de cada película es un entero entre 1 (no le ha gustado nada) y 5 (le ha encantado).

Viendo esto he creado una clase **`MovieReview`** que contiene los campos `idUser`, `idMovie` y `stars` todos de tipo entero, así como un constructor con dichos argumentos, los métodos getters y un método `toString` para imprimir adecuadamente instancias de esta clase por consola.

El fichero `u.item` contiene información sobre las películas  donde cada registro contiene los datos: `movie id | movie title | release date | video release date | IMDb URL` así como otros 19 campos donde cada uno representa un género y contiene un 1 si la película es de dicho género o 0 si no. De este fichero he utilizado el campo `movie id` para poder enlazarlo con el fichero previo además de `movie title` para poder mostrarle al usuario las peĺiculas. En dicho título se incluye el año de estreno de la película. Por ello he creado la clase **`Movie`** que contiene los atributos privados `idMovie` de tipo entero y `title` de tipo String, además del constructor con dichos atributos, los métodos getter y el método `toString`.

<!-- Salto de página -->
<div style="page-break-before: always;"></div>

### 2.2. MovieLensUtils
Esta clase contiene un conjunto de métodos para acceder a la colección MovieLens, cargarla y manejarla. Para utilizarla es necesario indicarle el directorio donde se encuentra la colección descomprimida mediante la llamada al método **`setMovieLensFolder`**, esté comprueba el directorio que se le ha pasado buscando los archivos llamados `u.data` y `u.item`, en caso de encontrarlos crea 2 atributos de tipo `File` donde almacena las referencias a esos ficheros y para terminar devuelve un `boolean` True. En caso de que se produzca algún error devuelve False.

Una vez fijado el directorio se puede invocar al método **`loadCollection`** (que también devuelve un `boolean` indicando su éxito). Esté lee inicialmente el fichero `u.data` sabiendo que tiene un formato tabulado, creando instancias de la clase `MovieReview` por cada linea del fichero y añadiendolás a un diccionario (`Map<>` en Java) cuya clave es el ID la película valorada y su valor instancia creada. A su vez este `Map` se introduce como valor en otro `Map` cuya clave es el ID del usuario que haya realizado la valoración. Para aclarar esto último incluiré la signatura de está estructura
```
private static Map<Integer, Map<Integer, MovieReview>> movieReviewsByUser;
```
He creado esta estructura de datos así ya que resulta mucho mas fácil (y eficiente) buscar elementos en `Map` de Java ya que se puede realizar sin hacer recorridos sobre la estructura, también incluyen la operación `.containsKey(k)` que permite comprobar si existen alguna instancia del mapa con la clave k (muy útil para determinar si un usuario ha visto una película por ejemplo).

A la vez que se va rellenando esta estructura mientras se lee de fichero he creado otro diccionario cuya clave es el ID del usuario y cuyo valor es su calificación media, esto lo he realizado así por el mismo motivo, la media de las valoraciones se usa abundantemente en cálculos de similaridad o predicción, por ello he considerado que lo más indicado era precalcularlo y almacenarlo en una estructura donde las búsquedas se hagan de forma rápida. La siguiente es la signatura de dicha estructura.
```
private static Map<Integer, Float> meanRatingByUser;
```

Una vez finalizada la lectura del fichero `u.data` como he descrito paso a leer el fichero `u.items`, es necesario mencionar que hay que abrir dicho fichero en codificación ISO-8859-1 ya que debe de contener algún carácter no UTF-8 y que el carácter separador en este caso no es el tabulador si no "|". Para realizar esta lectura creo una instancia de `Movie` por cada linea del fichero. Dicha instancia se almacena en un `Map` con clave el ID de la película y valor su instancia `Movie` correspondiente, la siguiente linea muestra su signatura.
```
private static Map<Integer, Movie> movies;
```

Además de esto la clase contiene diversos métodos de acceso y modificación de las estructuras de datos mencionadas previamente, por lo que es esta clase la que almacena todos los datos de la colección y las demás deben usar sus métodos para manejar dichas estructuras.


<!-- Salto de página -->
<div style="page-break-before: always;"></div>

### 2.3. RecomendationSystem
Esta es la clase principal del programa donde se lleva a cabo toda la interacción con el usuario así como los cálculos propios de un sistema de recomendación colaborativo basado en usuarios.

En esta clase se invoca a los métodos para cargar la colección MovieLens que he mencionado previamente, a continuación permite elegir la medida de similaridad a utilizar: Coseno o Pearson. Acto seguido permite elegir también si las predicciones se han de calcular de la manera simple o teniendo en cuenta las diferencias de interpretación y escala. Tras esto se piden un total de 20 valoraciones al usuario (número de valoraciones controlado por `NUM_INITIAL_REVIEWS`) que son almacenadas en las estructuras pertinentes de la clase `MovieLensUtils`, con estas valoraciones se inicia el cálculo de similaridad entre el usuario activo y el resto de la BD en el método **`getSimilarityWithOtherUsers(userID)`**. Dicho método aplica el método de calculo elegido por el usuario a cada uno de los miembros de la BD y devuelve un diccionario con clave ID de usuario y valor su similaridad con el usuario activo, además dicho diccionario se encuentra ordenado por similaridad para facilitar la extracción de los vecinos más cercanos. Cabe mencionar que el cálculo de similaridad se ha paralelizado en 3 hilos:
- Uno que cálcula el numerador.
- Otro que calcula la parte del denominador correspondiente al usuario U
- Otro que calcula la parte del denominador correspondiente al usuario V

En el método **`getNeighbourhood(sortedSimilarityMap)`** se calcula el vecindario del usuario actual, que simplemente corresponde con los 10 items con mayor similaridad (vecinos más cercanos), el número de vecinos más cercanos se controla con la constante `K`.

Acto seguido se invoca al método **`getBestPredictedReviews(closestNeighbours)`** que va recorriendo las películas que han visto los vecinos más cercanos y no ha visto el usuario actual para realizar el cálculo de predicción deseado sobre cada una de esas películas. Una vez ha finalizado elimina todas aquellas predicciones menores que 3. Por último el `Map<ID película, MovieReviewPredicho>` de  devuelto por  este método se ordena por calificación de las películas y se le muestra al usuario.


## 3. Manual de usuario

Ahora describiré la utilización del programa creado. Teniendo en cuenta que ha sido creado en Java es necesario tener instalada la máquina virtual de Java o JRE en su versión 8.

Para ejecutar el `.jar` creado es necesario situarlo en un directorio que contenga la carpeta `ml-data/` con la colección MovieLens descomprimida, ya que en caso contrario mostrará un error de que no encuentra dicha colección. Una vez se cumpla esa condición hay que ejecutar:
```
java -jar SistemaRecomendacion.jar
```

Se mostrará el inicio del programa y se nos preguntará por el cálculo de similaridad a utilizar así como el de predicción, tras esto se comenzará a valorar las películas presentadas aleatoriamente dándoles una valoración basada en estrellas desde 1 (\*) a 5 (*****) como se puede ver en el siguiente extracto del programa.
```
Sistema de recomendación de películas
================================================================================
Autor: Aythami Estévez Olivas
Fecha: Mayo, 2017
Licencia: GPL v3
================================================================================

Seleccione la medida de similaridad a utilizar coseno o pearson: [c/p] p

Seleccione si desea realizar las predicciones compensando las diferencias de interpretación y escala o no: [s/n] s

Por favor, valore las siguientes películas para que el sistema le recomiende otras similares.

Película: Cape Fear (1962)
Valoración (desde * hasta *****): **

[...]
```

Una vez se hayan valorado 20 películas se nos mostrarán los resultados de las predicciones ordenados por calificación predicha así como el tiempo ocupado en realizar el proceso de recomendación.

```
Las siguientes películas le podrían ser de su agrado según las valoraciones que ha realizado:

	- Película: Usual Suspects, The (1995)
	- Valoración predicha por el sistema: 5

 [...]

===============================================================================
Tiempo para generar las recomendaciones: 0.196s.
```

Adicionalmente el programa dispone de un modo **depuración** utilizable al pasarle el argumento `-d` en la invocación. Este imprime por pantalla los cálculos intermedios de similaridad y permite al usuario utilizar un usuario existente de la BD como usuario activo por lo que no es necesario que realice valoraciones él mismo.

## 4. Conclusiones

El desarrollo de esta práctica ha cubierto los aspectos fundamentales de la creación de un sistema de recomendación por filtrado colaborativo centrado en el usuarios. Hubiera resultado interesante haber creado un sistema híbrido que realice una recomendación por contenido inicialmente a partir de unas pocas valoraciones iniciales y ofrezca recomendaciones por filtrado colaborativo posteriormente ya que no creo que hubiera sido demasiado trabajo adicional y permitiría conocer los fundamentos de ese otro tipo de sistema colaborativo. Relacionado con esto último se podría utilizar la información demográfica de los usuarios para mejorar las predicciones creadas, esto se me ocurrió inicialmente al ver la estructura del dataset, pero no sé como se podría incluir exactamente en el sistema, supongo que ponderando la similaridad entre usuarios por los datos demográficos de estos. Por último resultaría interesante alguna forma de evaluar el sistema de recomendación creado, aunque fuera algo tan simple como algunos ejemplos de los valores esperados con diversas técnicas de similaridad o predicción utilizando algún usuario de la BD como usuario activo, ya que teniendo en cuenta el volumen de cálculos resulta complicado asegurarse de que los resultados son los adecuados y podría ser relativamente sencillo cometer un error en los cálculos sin siquiera darse cuenta al ver los resultados.

<!-- Salto de página -->
<div style="page-break-before: always;"></div>

## Bibliografía


<p id="1">

[1]: Juan Manuel Fernández Luna (2015). Tema 5: Sistemas de Recomendación. Gestión de Información en la Web. Universidad de Granada.

</p>
