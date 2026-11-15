import java.io.Serializable;

/**
 * Objeto serializable que viaja por el socket en los dos sentidos.
 * El campo tipo define el significado del resto:
 *
 *  Cliente  -> Servidor : NOMBRE, BARCO, LISTO, DISPARO
 *  Servidor -> Cliente  : INFO, COLOCAR, BARCO_OK, BARCO_ERROR, TURNO,
 *                         ESPERA, RESULTADO, ATAQUE, FIN
 */
public class Mensaje implements Serializable {

    private static final long serialVersionUID = 1L;

    private String tipo;
    private String texto;
    private String nombre;
    private int fila = -1;
    private int col = -1;
    private int longitud = 0;
    private boolean horizontal = true;
    private boolean impacto = false;

    public Mensaje(String tipo, String texto) {
        this.tipo = tipo;
        this.texto = texto;
    }

    public static Mensaje barco(String nombre, int fila, int col, int longitud, boolean horizontal) {
        Mensaje m = new Mensaje("BARCO", null);
        m.nombre = nombre;
        m.fila = fila;
        m.col = col;
        m.longitud = longitud;
        m.horizontal = horizontal;
        return m;
    }

    public static Mensaje disparo(int fila, int col) {
        Mensaje m = new Mensaje("DISPARO", null);
        m.fila = fila;
        m.col = col;
        return m;
    }

    /** RESULTADO (para quien dispara) o ATAQUE (para quien recibe). */
    public static Mensaje jugada(String tipo, int fila, int col, String texto, boolean impacto) {
        Mensaje m = new Mensaje(tipo, texto);
        m.fila = fila;
        m.col = col;
        m.impacto = impacto;
        return m;
    }

    public String getTipo() {
        return tipo;
    }

    public String getTexto() {
        return texto;
    }

    public String getNombre() {
        return nombre;
    }

    public int getFila() {
        return fila;
    }

    public int getCol() {
        return col;
    }

    public int getLongitud() {
        return longitud;
    }

    public boolean isHorizontal() {
        return horizontal;
    }

    public boolean isImpacto() {
        return impacto;
    }

    @Override
    public String toString() {
        return "Mensaje[" + tipo + " fila=" + fila + " col=" + col + " texto=" + texto + "]";
    }
}
