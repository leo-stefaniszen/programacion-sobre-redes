package ar.edu.et32.leo.modelo;

import java.io.Serializable;

public class Servidor implements Serializable {
    private static final long serialVersionUID = 1L;

    private String ipUrl;
    private String nombreServidor;
    private String endpointApi;
    private int codigoHttp;
    private int tiempoRespuesta;

    public Servidor() {
    }

    public Servidor(String ipUrl, String nombreServidor, String endpointApi, int codigoHttp, int tiempoRespuesta) {
        this.ipUrl = ipUrl;
        this.nombreServidor = nombreServidor;
        this.endpointApi = endpointApi;
        this.codigoHttp = codigoHttp;
        this.tiempoRespuesta = tiempoRespuesta;
    }

    public String getIpUrl() {
        return ipUrl;
    }

    public void setIpUrl(String ipUrl) {
        this.ipUrl = ipUrl;
    }

    public String getNombreServidor() {
        return nombreServidor;
    }

    public void setNombreServidor(String nombreServidor) {
        this.nombreServidor = nombreServidor;
    }

    public String getEndpointApi() {
        return endpointApi;
    }

    public void setEndpointApi(String endpointApi) {
        this.endpointApi = endpointApi;
    }

    public int getCodigoHttp() {
        return codigoHttp;
    }

    public void setCodigoHttp(int codigoHttp) {
        this.codigoHttp = codigoHttp;
    }

    public int getTiempoRespuesta() {
        return tiempoRespuesta;
    }

    public void setTiempoRespuesta(int tiempoRespuesta) {
        this.tiempoRespuesta = tiempoRespuesta;
    }

    public boolean estaCaido() {
        return codigoHttp >= 500 || tiempoRespuesta > 1500;
    }

    public boolean estaOnline() {
        return codigoHttp == 200 && tiempoRespuesta <= 1000;
    }

    public boolean tieneAlerta() {
        return !estaOnline() && !estaCaido();
    }

    public boolean esValido() {
        return ipUrl != null && !ipUrl.isBlank()
                && nombreServidor != null && !nombreServidor.isBlank()
                && endpointApi != null && !endpointApi.isBlank()
                && codigoHttp >= 100 && codigoHttp <= 599
                && tiempoRespuesta >= 0;
    }
}