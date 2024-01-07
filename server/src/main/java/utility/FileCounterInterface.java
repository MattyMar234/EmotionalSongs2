package utility;

/**
 * L'interfaccia FileCounterInterface definisce un contratto per contare gli elementi di un file
 * all'interno di un percorso specificato.
 */
public interface FileCounterInterface {
    int getFileElementsCount(String path);
}
