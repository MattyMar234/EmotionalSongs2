#include <stdio.h>
#include <stdlib.h>
#include <string.h>



#define MAX_FILENAME_LENGTH 256

 // Nome del file .jar da cercare
const char *filenameToSearch = "Windows_client-Application.jar";

int main() {
   
    
    char command[MAX_FILENAME_LENGTH + 20]; // "java -jar " + lunghezza massima del nome file
    
    #if defined(_WIN32) || defined(_WIN64)
        snprintf(command, sizeof(command), "java -jar .\\%s", filenameToSearch);
    #else
        snprintf(command, sizeof(command), "java -jar ./%s", filenameToSearch);
    #endif
    
    
    

    int result = system(command);

    if (result == -1) {
        perror("Errore nell'esecuzione");
        system("pause");
        return EXIT_FAILURE;
    }
}
