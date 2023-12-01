import csv
import os
#import json
import ujson
import io

# Directory radice che contiene le cartelle con i file JSON
directory_radici = [
    "C:\\Users\\Utente\\Desktop\\Dataset Progetto\\Output\\Album",
    "C:\\Users\\Utente\\Desktop\\Dataset Progetto\\Output\\Artists",
    "C:\\Users\\Utente\\Desktop\\Dataset Progetto\\Output\\Tracks"]


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
                contenuto_file = ujson.load(file)   
            dati_cartella[elemento.split(".")[0]] = contenuto_file
            
    return dati_cartella

# Chiamata iniziale alla funzione per esplorare la directory radice

def main_JSON():
    for path in directory_radici:
        dati_finali = esplora_cartelle_e_file(path)

        fileName = path.split("\\")[-1] + ".json"

        # Scrivi i dati finali in un unico file JSON
        print(f"Dati totali scritti nel file {fileName}.")
        with open(fileName, "w") as file_json:
            ujson.dump(dati_finali, file_json, indent=4)


def append_data(path, csv_writer, csv_headers: list):
    
    for elemento in os.listdir(path):
        percorso_elemento = os.path.join(path, elemento)
        
        #se è una cartella, chiamata ricorsiva
        if os.path.isdir(percorso_elemento):
            csv_headers = append_data(percorso_elemento, csv_writer, csv_headers)

        #se è un file JSON, leggi i dati e scrivi nel file di output
        elif os.path.isfile(percorso_elemento) and elemento.endswith(".json"):
            #print(f'reading file {percorso_elemento}')
            
            with open(percorso_elemento, "r") as file:
                jsonData = ujson.load(file) 

            if(csv_headers == None):
                first_element = next(iter(jsonData.values()), {})
                csv_headers = list(first_element.keys())
                print(f"Headers: {csv_headers}")
                csv_writer.writerow(csv_headers)

            # Scorrere gli elementi nel file JSON e scrivere le righe nel file CSV
            for track_data in jsonData.values():
                # Converti gli ID degli artisti in una stringa separata da virgole
                #track_data['artists_ID'] = ', '.join(track_data['artists_ID'])

                # Scrivi i dati della traccia nel file CSV
                csv_writer.writerow([track_data[key] for key in csv_headers])
              
    return csv_headers
            

def main_TXT():
    for path in directory_radici:
        fileName = path.split("\\")[-1] + ".txt"

        with open(fileName, 'w', newline='', encoding='utf-8') as csv_file: 
            csv_writer = csv.writer(csv_file) 
            append_data(path, csv_writer, None)  



if __name__ == "__main__":
    #main_JSON()
    main_TXT()