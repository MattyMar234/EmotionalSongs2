import os
import json

# Directory radice che contiene le cartelle con i file JSON
directory_radici = ["C:\\Users\\Utente\\Desktop\\Dataset Progetto\\Output\\Album","C:\\Users\\Utente\\Desktop\\Dataset Progetto\\Output\\Artists","C:\\Users\\Utente\\Desktop\\Dataset Progetto\\Output\\Tracks"]


# Dizionario per memorizzare i dati
dati_finali = {}

# Funzione per esplorare le cartelle e leggere i file JSON
def esplora_cartelle_e_file(cartella):
    dati_cartella = {}

    for elemento in os.listdir(cartella):
        percorso_elemento = os.path.join(cartella, elemento)
        if os.path.isdir(percorso_elemento):
            dati_cartella[elemento] = esplora_cartelle_e_file(percorso_elemento)

        elif os.path.isfile(percorso_elemento) and elemento.endswith(".json"):
            print(f'reading file {percorso_elemento}')
            
            with open(percorso_elemento, "r") as file:
                contenuto_file = json.load(file)   
            dati_cartella[elemento.split(".")[0]] = contenuto_file
            
    return dati_cartella

# Chiamata iniziale alla funzione per esplorare la directory radice

for path in directory_radici:
    dati_finali = esplora_cartelle_e_file(path)

    fileName = path.split("\\")[-1] + ".json"

    # Scrivi i dati finali in un unico file JSON
    print(f"Dati totali scritti nel file JSON {fileName}.")
    with open(fileName, "w") as file_json:
        json.dump(dati_finali, file_json, indent=4)


