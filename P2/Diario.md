# Diario de la práctica 2: Caso Práctico de Análisis y Evaluación de Redes en Twitter
## 28/03/17
Voy a usar la librería de Python [Tweepy](http://www.tweepy.org/). He creado una [aplicación en Twitter](https://dev.twitter.com/) para obtener mis credenciales de acceso OAuth.

He probado el [Getting Started de Tweepy](http://docs.tweepy.org/en/v3.5.0/getting_started.html) y soy capaz de recuperar Tweets usando el método `api.search()`.

He tenido problemas con la paginación y tendre que ver como resolverlo.

Para escribir los datos a CSV esto parece  [prometedor](http://stackoverflow.com/a/21869560/6441806)

Probando con la librería he logrado obtener los campos que coinciden con los devueltos por nodeXL en el método `tweetFields(tweet)`, además he logrado obtener multiples tweets (+ de 2000) sobre un tema entre fechas delimitadas concatenando búsquedas uniendo sus resultados en una sola lista.

## 02/04/2017
Estoy probando como se podría realizar una poda pathfinder, para ello siguiendo la recomendación dada en los apuntes he dado con la página del árticulo del MST-PF, el algoritmo creado por Oscar Cordon et al. pero solo es aplicable a grafos no dirigidos, por lo que necesito otra variante. Para grafos dirigidos lo más rápido es un Fast pathfinder.

Como en la implementación del articulo el fastpathfinder es para no dirigidos solo he buscado otra implemeación entre las recomendadas, por ello he descargado la herramienta **Network Workbench**. He encontrado su manual por ahi ya que la documentación online esta caida y redirigue todo a la pagina de inicio. Lo primero es exportar el grafo desde Gephi, segun la documentacion de NWB admite formato .net de pajek asique exporto en gephi todo a ese formato pero obtengo errores de que la red es demasiado grande, asique aplico un ligero filtado con componente gigante y k-core a 2. Tambien se han observado problemas en NWB con los autoenlaces asique además filtro bucles en Gephi (que no son demasiados). Todo esto es exportado a `nwb/CarreroBlanco29-03_Giant_K2.net`. Una vez hecho esto cargo la red en NWB y aplico fast pathfinder en _Analysis_ > _Directed and Pondered_ > _Fast pathfinder network_. Dejando todos los parametros tal cual a excepción de lo que implica el peso que lo he cambiado a similaridad, ya que si una persona menciona mucho a otra en twitter esto denota similaridad no diferencia. (Ver documentación)

Pero nada, multiples errores y tiempos demasiado elevados. Proyecto parece descontinuado.
