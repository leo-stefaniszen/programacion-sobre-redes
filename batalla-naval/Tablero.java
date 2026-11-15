import java.io.Serializable;
import java.util.ArrayList;

/** Tablero de 10x10 con la flota colocada y el registro de disparos. */
public class Tablero implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int TAMANIO = 10;

    private ArrayList<ArrayList<Celda>> tablero;
    private ArrayList<Barco> barcos;

    public Tablero() {
        inicializarTablero();
    }

    public void inicializarTablero() {
        tablero = new ArrayList<ArrayList<Celda>>();
        barcos = new ArrayList<Barco>();
        for (int f = 0; f < TAMANIO; f++) {
            ArrayList<Celda> fila = new ArrayList<Celda>();
            for (int c = 0; c < TAMANIO; c++) {
                fila.add(new Celda());
            }
            tablero.add(fila);
        }
    }

    public Celda getCelda(int fila, int col) {
        return tablero.get(fila).get(col);
    }

    public ArrayList<Barco> getBarcos() {
        return barcos;
    }

    public boolean posicionValida(int fila, int col) {
        return fila >= 0 && fila < TAMANIO && col >= 0 && col < TAMANIO;
    }

    /** Coloca el barco si cabe y no choca con otro. No modifica nada si falla. */
    public boolean colocarBarco(String nombre, int fila, int col, int longitud, boolean horizontal) {
        if (!posicionValida(fila, col)) {
            return false;
        }
        int filaFinal = horizontal ? fila : fila + longitud - 1;
        int colFinal = horizontal ? col + longitud - 1 : col;
        if (!posicionValida(filaFinal, colFinal)) {
            return false;
        }
        for (int i = 0; i < longitud; i++) {
            int f = horizontal ? fila : fila + i;
            int c = horizontal ? col + i : col;
            if (getCelda(f, c).getTipo() == TipoCelda.BARCO) {
                return false;
            }
        }
        for (int i = 0; i < longitud; i++) {
            int f = horizontal ? fila : fila + i;
            int c = horizontal ? col + i : col;
            Celda celda = getCelda(f, c);
            celda.setTipo(TipoCelda.BARCO);
            celda.setNombreBarco(nombre);
        }
        barcos.add(new Barco(nombre, longitud));
        return true;
    }

    /** Resuelve un disparo sobre este tablero y describe el resultado. */
    public String disparar(int fila, int col) {
        if (!posicionValida(fila, col)) {
            return "Posicion fuera del tablero";
        }
        Celda celda = getCelda(fila, col);
        if (celda.isDisparada()) {
            return "Esa casilla ya fue disparada";
        }
        celda.setDisparada(true);
        if (celda.getTipo() == TipoCelda.BARCO) {
            celda.setTipo(TipoCelda.IMPACTO);
            Barco barco = buscarBarco(celda.getNombreBarco());
            if (barco != null) {
                barco.recibirImpacto();
                if (barco.estaHundido()) {
                    return "¡HUNDIDO! " + barco.getNombre();
                }
            }
            return "¡IMPACTO!";
        }
        celda.setTipo(TipoCelda.FALLO);
        return "Agua";
    }

    /** Marca en el tablero del rival lo que el servidor respondio de un disparo propio. */
    public void marcarDisparoEnemigo(int fila, int col, boolean impacto) {
        if (!posicionValida(fila, col)) {
            return;
        }
        Celda celda = getCelda(fila, col);
        celda.setDisparada(true);
        celda.setTipo(impacto ? TipoCelda.IMPACTO : TipoCelda.FALLO);
    }

    /** Anota en el tablero del rival un barco que el servidor reporto hundido. */
    public void anotarBarcoHundido(String nombre, int longitud) {
        Barco barco = new Barco(nombre, longitud);
        for (int i = 0; i < longitud; i++) {
            barco.recibirImpacto();
        }
        barcos.add(barco);
    }

    private Barco buscarBarco(String nombre) {
        for (int i = 0; i < barcos.size(); i++) {
            if (barcos.get(i).getNombre().equals(nombre)) {
                return barcos.get(i);
            }
        }
        return null;
    }

    public void mostrarTablero(boolean ocultarBarcos) {
        ArrayList<String> lineas = dibujar(ocultarBarcos);
        for (int i = 0; i < lineas.size(); i++) {
            System.out.println(lineas.get(i));
        }
    }

    /**
     * Devuelve el tablero como lineas de 23 caracteres visibles, todas del mismo
     * ancho para poder imprimir dos tableros lado a lado.
     */
    public ArrayList<String> dibujar(boolean ocultarBarcos) {
        ArrayList<String> lineas = new ArrayList<String>();
        StringBuilder cabecera = new StringBuilder("    ");
        for (int c = 0; c < TAMANIO; c++) {
            cabecera.append(c);
            if (c < TAMANIO - 1) {
                cabecera.append(' ');
            }
        }
        lineas.add(Consola.pinta(cabecera.toString(), Consola.GRIS));
        for (int f = 0; f < TAMANIO; f++) {
            StringBuilder fila = new StringBuilder();
            fila.append(Consola.pinta(String.format("%2d  ", f), Consola.GRIS));
            for (int c = 0; c < TAMANIO; c++) {
                fila.append(simbolo(getCelda(f, c), ocultarBarcos));
                if (c < TAMANIO - 1) {
                    fila.append(' ');
                }
            }
            lineas.add(fila.toString());
        }
        return lineas;
    }

    private String simbolo(Celda celda, boolean ocultarBarcos) {
        if (celda.getTipo() == TipoCelda.IMPACTO) {
            return Consola.pinta("X", Consola.ROJO + Consola.NEGRITA);
        }
        if (celda.getTipo() == TipoCelda.FALLO) {
            return Consola.pinta("O", Consola.GRIS);
        }
        if (celda.getTipo() == TipoCelda.BARCO && !ocultarBarcos) {
            return Consola.pinta("B", Consola.VERDE + Consola.NEGRITA);
        }
        return Consola.pinta("~", Consola.AZUL);
    }

    /** Lista cada barco con sus impactos y si sigue a flote. */
    public void mostrarBarcos() {
        for (int i = 0; i < barcos.size(); i++) {
            Barco barco = barcos.get(i);
            String estado = barco.estaHundido()
                    ? Consola.pinta("HUNDIDO", Consola.ROJO)
                    : Consola.pinta("ACTIVO", Consola.VERDE);
            System.out.println(String.format("  %-13s %d casillas  impactos: %d/%d  %s",
                    barco.getNombre(), barco.getLongitud(),
                    barco.getImpactos(), barco.getLongitud(), estado));
        }
    }

    public boolean todosLosBarcosCaidos() {
        if (barcos.isEmpty()) {
            return false;
        }
        for (int i = 0; i < barcos.size(); i++) {
            if (!barcos.get(i).estaHundido()) {
                return false;
            }
        }
        return true;
    }

    /** Se llama sobre el tablero del rival: si cayo toda su flota, gane. */
    public boolean verificarVictoria() {
        if (todosLosBarcosCaidos()) {
            Consola.banner("VICTORIA: hundiste toda la flota enemiga", Consola.VERDE);
            return true;
        }
        return false;
    }

    /** Se llama sobre el tablero propio: si cayo mi flota, perdi. */
    public boolean verificarDerrota() {
        if (todosLosBarcosCaidos()) {
            Consola.banner("DERROTA: tu flota fue hundida", Consola.ROJO);
            return true;
        }
        return false;
    }
}
