#include <stdio.h>
#include <stdlib.h>
#include <string.h>


#define MAX_FILENAME_LENGTH 256

int main() {
    // Nome del file .jar da cercare
    const char *filenameToSearch = "client-Application.jar";

    char command[MAX_FILENAME_LENGTH + 20]; // "java -jar " + lunghezza massima del nome file
    snprintf(command, sizeof(command), "java -jar .\\%s", filenameToSearch);

    // printf("Esecuzione del server: %s\n", command);
    // system("pause");
    //printf("\e[8;80;200t");
    //system("mode con: cols=120 lines=80");
    

    int result = system(command);
    if (result == -1) {
        perror("Errore nell'esecuzione");
        return EXIT_FAILURE;
    }
}
