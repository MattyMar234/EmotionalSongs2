#include <stdio.h>
#include <stdlib.h>
#include <string.h>


#define MAX_FILENAME_LENGTH 256

int main() {
  
    const char *filenameToSearch = "Linux_client-Application.jar";

    char command[MAX_FILENAME_LENGTH + 20];
    snprintf(command, sizeof(command), "java -jar ./%s", filenameToSearch);

    

    int result = system(command);
    if (result == -1) {
        perror("Errore nell'esecuzione");
        return EXIT_FAILURE;
    }
}
