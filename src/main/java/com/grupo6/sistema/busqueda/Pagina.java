package com.grupo6.sistema.busqueda;

import java.util.List;

/** Una página de resultados: los registros de la página y los datos para la navegación. */
public class Pagina<T> {

    private final List<T> registros;
    private final int numero;
    private final int tamanio;
    private final long totalRegistros;

    public Pagina(List<T> registros, int numero, int tamanio, long totalRegistros) {
        this.registros = registros;
        this.numero = numero;
        this.tamanio = tamanio;
        this.totalRegistros = totalRegistros;
    }

    /** Número de la última página válida para un total de registros (mínimo 1). */
    public static int calcularTotalPaginas(long totalRegistros, int tamanio) {
        return (int) Math.max(1, (totalRegistros + tamanio - 1) / tamanio);
    }

    public List<T> getRegistros() {
        return registros;
    }

    public int getNumero() {
        return numero;
    }

    public int getTamanio() {
        return tamanio;
    }

    public long getTotalRegistros() {
        return totalRegistros;
    }

    public int getTotalPaginas() {
        return calcularTotalPaginas(totalRegistros, tamanio);
    }

    public boolean isTieneAnterior() {
        return numero > 1;
    }

    public boolean isTieneSiguiente() {
        return numero < getTotalPaginas();
    }

    /** Posición del primer registro mostrado (para "Mostrando 11 a 20 de 30"). */
    public long getDesde() {
        return totalRegistros == 0 ? 0 : (long) (numero - 1) * tamanio + 1;
    }

    public long getHasta() {
        return Math.min((long) numero * tamanio, totalRegistros);
    }
}
