# Batalla Naval en red (consola, Java)

Juego para dos jugadores con sockets TCP, un hilo por cliente en el servidor y
mensajes serializables. Todo por consola, con colores y formato de texto.

## Archivos

| Archivo | Para que sirve |
|---|---|
| `TipoCelda.java` | Enum: `AGUA`, `BARCO`, `IMPACTO`, `FALLO` |
| `Celda.java` | Una casilla: tipo, si fue disparada, a que barco pertenece |
| `Barco.java` | Nombre, longitud, impactos, `recibirImpacto()`, `estaHundido()` |
| `Tablero.java` | `ArrayList<ArrayList<Celda>>` de 10x10, colocar, disparar, dibujar |
| `Consola.java` | Colores ANSI y formato de texto |
| `Mensaje.java` | Objeto serializable que viaja por el socket (el protocolo) |
| `Servidor.java` | `ServerSocket`, emparejamiento en salas, clases `Jugador` y `Partida` |
| `Cliente.java` | Conexion, colocacion de la flota, los dos tableros y los disparos |

## Compilar

```bash
javac *.java
```

## Jugar en la misma maquina

Tres terminales:

```bash
java Servidor 5000
java Cliente 127.0.0.1 5000
java Cliente 127.0.0.1 5000
```

El cliente sin parametros pregunta la IP y el puerto:

```bash
java Cliente
```

Si la terminal no entiende los colores ANSI, agrega `--sincolor`:

```bash
java Cliente 127.0.0.1 5000 --sincolor
```

En Windows, para que se vean bien los signos de apertura (`¡`), ejecuta antes
`chcp 65001` en la consola.

## Como se juega

1. Cada jugador escribe su nombre y entra a una sala. El servidor empareja de
   dos en dos, y atiende varias salas al mismo tiempo.
2. Colocacion: para cada barco se escribe `fila columna orientacion`, por
   ejemplo `3 4 H`. Filas y columnas van de 0 a 9, `H` es horizontal y `V` es
   vertical. Escribiendo `R` ese barco se coloca solo en una posicion libre.
3. Flota: Portaaviones (5), Acorazado (4), Crucero (3), Submarino (3),
   Destructor (2).
4. En tu turno escribes `fila columna` para disparar. El servidor responde
   `Agua`, `¡IMPACTO!` o `¡HUNDIDO! <barco>` y el turno pasa al rival.
5. Gana quien hunde primero los cinco barcos del otro.

Simbolos del tablero: `~` agua, `B` barco propio, `X` impacto, `O` fallo.
El tablero de la izquierda es el propio (muestra los barcos) y el de la derecha
es el del rival (los barcos intactos quedan ocultos).

## Protocolo

Un solo objeto `Mensaje` serializable viaja en los dos sentidos por
`ObjectOutputStream` / `ObjectInputStream`. El campo `tipo` manda:

| Sentido | Tipo | Contenido |
|---|---|---|
| Cliente a servidor | `NOMBRE` | nombre del jugador al conectarse |
| Cliente a servidor | `BARCO` | nombre, fila, col, longitud, horizontal |
| Cliente a servidor | `LISTO` | la flota quedo colocada |
| Cliente a servidor | `DISPARO` | fila, col |
| Servidor a cliente | `INFO` / `ESPERA` | texto informativo o turno del rival |
| Servidor a cliente | `COLOCAR` | la sala esta armada, coloca tu flota |
| Servidor a cliente | `BARCO_OK` / `BARCO_ERROR` | validacion de la posicion |
| Servidor a cliente | `TURNO` | te toca disparar |
| Servidor a cliente | `RESULTADO` | fila, col, texto, impacto (tu disparo) |
| Servidor a cliente | `ATAQUE` | fila, col, texto, impacto (disparo del rival) |
| Servidor a cliente | `FIN` | `GANASTE`, `PERDISTE` o `DESCONEXION: ...` |

## Hilos y sincronizacion

- `Servidor.main` acepta conexiones en un bucle y guarda al jugador que espera.
- Con dos jugadores crea una `Partida` y lanza **un hilo por cliente**.
- El estado compartido de la sala (tableros, turno, listos, fin) se toca solo
  dentro de metodos `synchronized` de `Partida`, asi que los dos hilos nunca lo
  dejan inconsistente.
- El envio por socket tambien esta sincronizado en `Jugador.enviar`.
- Si un jugador se cae, el servidor avisa al otro con `FIN` y sigue escuchando.

## Historial

Al terminar una partida el servidor agrega una linea a `partidas.txt`:

```
2026-11-15 19:14:04 | sala 2 | gano Ana | perdio Caro
```

## Docker (servidor)

```bash
docker compose up --build -d
docker compose logs -f servidor
docker compose down
```

El servidor queda publicado en el puerto 5000 del host. Los clientes se conectan
con la IP de esa maquina:

```bash
java Cliente <ip-del-host> 5000
```

Cuando el servidor corre en Docker, `partidas.txt` queda dentro del contenedor:

```bash
docker compose exec servidor cat /app/partidas.txt
```

## Probar con una VM (parte de redes)

En una VM con Debian ligero o Alpine, con el servidor corriendo en el host:

```bash
sudo apt update
sudo apt install default-jdk
java -version

# copiar los .java a la VM (scp, carpeta compartida o git) y compilar ahi
javac *.java
java Cliente <ip-del-host-con-docker> 5000
```

En Alpine: `apk add openjdk17`.

Para que la VM alcance al contenedor, la red de la VM debe ser **bridge** (o
NAT con reenvio del puerto 5000) y el firewall del host debe dejar pasar ese
puerto.

## Decisiones de diseno

| Decision | Razon |
|---|---|
| Un solo `Mensaje` para todo el protocolo | un tipo serializable es mas facil de leer que ocho clases casi iguales |
| El servidor guarda los dos tableros | es el arbitro: valida turno, casilla repetida y victoria |
| El cliente tambien guarda sus dos tableros | dibuja sin pedirle nada al servidor y el rival queda oculto |
| Cliente de un solo hilo | el juego es por turnos, el cliente solo reacciona al mensaje que llega |
| Un hilo por cliente en el servidor | cada conexion avanza a su ritmo, varias salas a la vez |
| Azul agua, verde flota propia, rojo impacto, amarillo acento | cada color dice una sola cosa, gris para lo secundario |
| Victoria en verde, derrota en rojo | el cartel final reusa el color que ya significa algo en el tablero |
| Tableros lado a lado | el jugador compara su flota y la del rival de un vistazo |
| `--sincolor` | consolas viejas muestran los codigos ANSI como basura |
