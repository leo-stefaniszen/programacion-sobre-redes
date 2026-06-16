package ar.edu.et32.leo;

public class Producto {

    private String nombre;
    private float compra;
    private float venta;
    private int stock;

    public Producto(
            String nombre,
            float compra,
            float venta,
            int stock
    ) {
        this.nombre = nombre;
        this.compra = compra;
        this.venta = venta;
        this.stock = stock;
    }

    public String getNombre() {
        return nombre;
    }

    public float getCompra() {
        return compra;
    }

    public float getVenta() {
        return venta;
    }

    public int getStock() {
        return stock;
    }

    @Override
    public String toString() {

        return nombre + ";" +
                compra + ";" +
                venta + ";" +
                stock;
    }
}