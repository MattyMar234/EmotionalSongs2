from Logger import Terminal
from Researchers_Base import *
from SpotifyUser import SpotifyUser
from pathFormatter import PathFormatter as PF


from colorama import init as colorama_init
from colorama import Fore
from colorama import Style

import threading
import time
import json
import requests
import os
import numpy as np
import csv
import ssl



class Track_and_Album_Research(DataResearch):

    URL = "https://api.spotify.com/v1/artists/[id]/top-tracks?market=US"
    FILE_SETTINGS = PF.formatPath(os.getcwd() + "\\Track_and_Album_settings.json")
    FILE_ID = PF.formatPath(os.getcwd() + "\\processedID.csv")
    MUTEX1 = threading.Lock()
    MUTEX2 = threading.Lock()
    MUTEX3 = threading.Lock()

    THREAD_NUMBER:int = 20

    CurrentIndex: int
    TotalElement: int
    file_list: list

    def __init__(self, Accounts:list, artistPath:str, trackPath:str, albumPath:str):
        super().__init__()

        self.Accounts = Accounts
        self.ArtistPath = artistPath        #PATH artists folder
        self.TrackPath = trackPath          #PATH track folder
        self.AlbumPath = albumPath          #PATH album folder

        self.file_list = []                 #lista dei file
        self.files_index = 0                #l'indice del file attuale
        self.currentLetterIndex = 0         #la lettera che si sta processando
        self.LetterElement = 0

        self.processed_ID:dict = {}              #gli ID già processati 
        self.Arists_ID:dict = {}            #tutti gli ID da fare
        self.TotalAlfabeticLetter:int = 0
        self.total_ID:int = 0
        self.processed_ID_count:int = 0

        #========================================================#
        ### Ripristino sessione precedente ###
        #informazione delle operazioni svolte precedentemente
        """lastSessionData = self.loadSettings(Track_and_Album_Research.FILE_SETTINGS)
        save = False

        #verifico se hol salvate delle informazioni precedentemente
        if lastSessionData == None:
            lastSessionData = self.inizializeConfiguration()
            save = True
            
        self.processed_ID: dict = lastSessionData["processed_ID"]   #Gli ID processati
                                        

        #se non ho infromazioni, salvo quelle di base
        if save:
            self.saveSettings(Track_and_Album_Research.FILE_SETTINGS, self.todict())"""
        self.loadProcessedID()

        #========================================================#
        #cerco i file che devo processare e gli ID

        for file in os.listdir(self.ArtistPath):
            if file.endswith('.json'):
                self.file_list.append(PF.formatPath(f'{self.ArtistPath}\\{file}'))
                
                with open(self.file_list[-1], "r") as f:
                    ordineAlfabetico = self.file_list[-1].split(PF.getSplitterChar())[-1].split('.')[0]
                    
                    print("reading file: ", self.file_list[-1], "\tkey: ", ordineAlfabetico)
                    fileData:dict = json.load(f)
                    temp:list = []

                    for k in fileData.keys():
                        temp.append(k)

                    #incremento in base al numero degli ID presenti nel file
                    self.total_ID += len(fileData.keys())

                    #print("ID found:", temp)
                    self.Arists_ID[ordineAlfabetico] = temp
                    #time.sleep(4)

                    #print("dict keys:",  self.Arists_ID.keys())
                    #print(f"key {ordineAlfabetico} element: ",  len(self.Arists_ID[ordineAlfabetico]))
                    #time.sleep(2)
            

        self.TotalAlfabeticLetter = len(self.Arists_ID)
        #print(self.Arists_ID)
        print("Total letter found:", self.TotalAlfabeticLetter)
        print("Total ID found:", self.total_ID)

        

        #print(self.Arists_ID.items())
        #========================================================#
        if not os.path.exists(self.TrackPath):
            os.mkdir(self.TrackPath)

        if not os.path.exists(self.AlbumPath):
            os.mkdir(self.AlbumPath)

        for alfabeticChar in self.Arists_ID.keys():
            if not os.path.exists(PF.formatPath(self.TrackPath + '\\' + alfabeticChar)):
                os.mkdir(PF.formatPath(self.TrackPath + '\\' + alfabeticChar))

            if not os.path.exists(PF.formatPath(self.AlbumPath + '\\' + alfabeticChar)):
                os.mkdir(PF.formatPath(self.AlbumPath + '\\' + alfabeticChar))

       #========================================================#

    def SaveThreadLoop(self):
        running =  True

        while running:
            time.sleep(4)
            with Track_and_Album_Research.MUTEX2:
                self.saveData()

            running = False
            for th in self.threads: 
                if th.is_alive():
                    running = True
                    break

    def appendData(self, id:str):
        temp:list = [id]

        with open(Track_and_Album_Research.FILE_ID, 'a', newline='') as file:
            writer = csv.writer(file)
            writer.writerow(temp)

    def saveData(self):
        self.saveSettings(Track_and_Album_Research.FILE_SETTINGS, self.todict())   
 
    def loadProcessedID(self):

        if not os.path.exists(Track_and_Album_Research.FILE_ID):
            with open(Track_and_Album_Research.FILE_ID, 'w'):
                pass
            return

        with open(Track_and_Album_Research.FILE_ID, 'r') as file:
            reader = csv.reader(file)
            for row in reader:
                self.processed_ID[row[0]] = None
               



    """def inizializeConfiguration(self) -> dict:
        data = {
            "processed_ID" : {}
        }

        return data"""
    
    
    
    def getElementCount(self) -> int:
        return self.TotalElement
    
    def getProgress(self) -> int:
        return self.ID_Index
    
    def todict(self) -> dict:
        out = super().toDict()
        #out["lastPageIndex"] = self.currentPageIndex
        #out["lastPageElement"] = self.pageElement
        out["processed_ID"] = self.processed_ID

        print(f"ID processed: {len(self.processed_ID)}")

        #print(out)
        return out

    def start(self):
        for Account in self.Accounts:
            for i in range(Track_and_Album_Research.THREAD_NUMBER):
                th = self.TrackReSerarcher(i, self, Account)
                self.threads.append(th)
                th.start()

        #self.SaveThread = threading.Thread(target=self.SaveThreadLoop)
        #self.SaveThread.start()



    class ElementFinder_Thread(threading.Thread):
        def __init__(self, classReference):
            super().__init__()
            self.classReference: Track_and_Album_Research = classReference
            self.ElementDict:dict = self.classReference.Arists_ID

        def run(self):
            fileNumber = len(self.classReference.file_list)
            
            while True:
                with TrackResearch.MUTEX1:
                    index = self.classReference.files_index
                    self.classReference.files_index += 1

                    if not index < fileNumber:
                        return
                    
                    key = self.classReference.file_list[index].split(PF.getSplitterChar())[-1].split('.')[0]
                    self.ElementDict[key] = []
                    
                with open(self.classReference.file_list[index], 'r') as file:
                    json_data:dict = json.load(file)
                    
                    #gli id presenti nel file
                    for artistID in json_data.keys():
                        #with TrackResearch.MUTEX1:
                        self.ElementDict[key].append(artistID)

                            #cerco eventuali duplicati
                        """duplicate = False
                        for alfabetico in self.ElementDict.keys():
                            if artistID in self.ElementDict[alfabetico]:
                                duplicate = True
                                break

                        if not duplicate:"""
                            
   
    class TrackReSerarcher(DataResearch.ResearchThread):

        def __init__(self, thNumber: int, classReference, Account: SpotifyUser):
            super().__init__(thNumber)
            
            self.classReference: Track_and_Album_Research = classReference
            self.Account:SpotifyUser = Account
            self.fileData_song:dict = {}
            self.fileData_album:dict = {}

            #self.scraper: SpotifyScraper = SpotifyScraper()

        def getelementAt(self, index: int) -> str:
            merged_list = sum(self.classReference.Arists_ID.values(), [])  # Unione di tutte le liste nel dizionario
            
            if index < 0 or index >= len(merged_list):
                return None, None  # L'indice è fuori dai limiti della lista unificata
            
            for key, lst in self.classReference.Arists_ID.items():
                if index < len(lst):
                    return merged_list[index], key  # Restituisce l'i-esimo elemento e la chiave del dizionario
                index -= len(lst)


        def getElement(self) -> tuple:
            totalKeys = self.classReference.TotalAlfabeticLetter
            #LetterIndex = self.classReference.currentLetterIndex  
            #LetterElement = self.classReference.LetterElement

            while self.classReference.currentLetterIndex < totalKeys: 

                #ottengo la la lettera attuale
                ArtistAlfabeticKey = [*self.classReference.Arists_ID][self.classReference.currentLetterIndex]

                #ottengo l'id & incremento l'indice
                artistID_list = self.classReference.Arists_ID[ArtistAlfabeticKey]
                artistID = artistID_list[self.classReference.LetterElement]

                #print(self.classReference.LetterElement, end = ' | ')

                #se ero l'ultimo elemento di quella lettera
                if self.classReference.LetterElement + 1 >= len(self.classReference.Arists_ID[ArtistAlfabeticKey]):
                    self.classReference.LetterElement = 0
                    self.classReference.currentLetterIndex += 1
                else:
                    self.classReference.LetterElement += 1
                   
                self.classReference.processed_ID_count += 1

                #Verifico se sono già stato processato
                if artistID in self.classReference.processed_ID:
                    #print(f'{artistID} already processed')
                    continue #ripeto tutta l'operazione
                else:
                    return (ArtistAlfabeticKey, artistID)
            
            return None
        
        
        def run(self):
            index:int = 0
            
            while self.running:

                #verifico qual è l'elemento che devo processare
                with ArtistsResearch.MUTEX2:
                    result = self.getElement()
                    if result == None:
                        return
                    #print(result)
                    
                #salvo i dati
                alfabeticChar = result[0]
                artistID = result[1]

               
                #eseguo la raccolta dei dati
                #albums, tracks = self.scraper.getArtist_albums_and_tracks(id)
                self.search_traks_and_album_by_artistID(artistID)
                

                #salvo i dati
                path1 = PF.formatPath(self.classReference.TrackPath + '\\' + alfabeticChar + "\\" + artistID + ".json")
                path2 = PF.formatPath(self.classReference.AlbumPath + '\\' + alfabeticChar + "\\" + artistID + ".json")

                with open(path1, 'w') as file:
                    json.dump(self.fileData_song, file, indent=4)

                with open(path2, 'w') as file:
                    json.dump(self.fileData_album, file, indent=4)

                self.fileData_song.clear()
                self.fileData_album.clear()

                #salvo il progresso
                with ArtistsResearch.MUTEX2:
                    self.classReference.processed_ID[artistID] = 1
                    self.classReference.appendData(artistID)

                


                """reties = 0
                while not self.search_traks_by_artistID(id):
                    reties += 1
                    if reties >= 24:
                        Terminal.error(" to much failed requests !!!")
                        os._exit(1)"""


            #Terminal.success(f" Search for \"{Fore.MAGENTA}{key}{Fore.RESET}\" completed")
            #Terminal.info(f" Thread ArtistSerarcher [{self.thNumber}] finished")

        


        def search_traks_and_album_by_artistID(self, artist_ID:str):
            
            data:dict = {}
            reties:int = 0
            proxyErrors = 0
            
            if artist_ID == "": 
                Terminal.error(" Invalid artist ID")
                os._exit(0)

            #ripeto finché ho errori
            while True:

                #creo la richiesta
                query = Track_and_Album_Research.URL
                query = query.replace("[id]", f'{artist_ID}')
                proxy = self.Account.getProxy()
                try:
                    response = requests.get(query, proxies=proxy,headers = self.Account.get_auth_headers())
                    reties += 1
                except Exception as e:
                    reties += 1
                    proxyErrors += 1
                    Terminal.error(e)
                    time.sleep(0.200)

                    if proxyErrors >= 2:
                        self.Account.changeProxy()

                    continue

                #verifico lo stato della richiesta
                if response.ok:
                    data = json.loads(response.content.decode("utf-8"))
                    break

                elif "limit exceeded" not in response.text:
                    with Track_and_Album_Research.MUTEX1:
                        temp = response.text.replace('\n','')
                        Terminal.error(f"req: {response.url} --> {response} --> {temp}" )

                if reties >= 24:
                    with Track_and_Album_Research.MUTEX1:
                        Terminal.error(" to much failed requests !!!")
                    os._exit(0)

                time.sleep(0.200)

            

            for idx, song in enumerate(data['tracks']):

                Artists_ID = []             #gli artisti che hanno realizzato quella canzone
                Album_Artists_ID = []       #gli album

                for arist in song['artists']:
                    Artists_ID.append(arist['id'])

                for arist in song['album']['artists']:
                    Album_Artists_ID.append(arist['id'])

                SongData = {
                    'album_ID'      :   song['album']['id'],
                    'artists_ID'    :   Artists_ID,
                    'duration_ms'   :   song['duration_ms'],
                    'spotify_url'   :   song['external_urls']['spotify'],
                    'id'            :   song['id'],
                    'name'          :   song['name'],
                    'popularity'    :   song['popularity']
                }

                songsAlbum = {
                    'id'            :   song['album']['id'],
                    'element'       :   song['album']['total_tracks'],
                    'spotify_url'   :   song['album']['external_urls']['spotify'],
                    'images'        :   song['album']['images'],
                    'name'          :   song['album']['name'],
                    'release_date'  :   song['album']['release_date'],
                    'type'          :   song['album']['album_type'],
                    #'album_genres'  :   song['album']['genres'],
                    #'popularity'    :   song['album']['popularity'],
                    'artists_ID'    :   Album_Artists_ID
                }

                self.fileData_song[SongData['id']] = SongData
                self.fileData_album[songsAlbum['id']] = songsAlbum  

            """self.writeJson(self.fileData_song, artist_ID, self.classReference.TrackPath)
            self.writeJson(self.fileData_album, artist_ID, self.classReference.AlbumPath)"""



class ArtistsResearch(DataResearch):

    URL = "https://api.spotify.com/v1/search"
    MUTEX1 = threading.Lock()
    MUTEX2 = threading.Lock()
    MUTEX3 = threading.Lock()
    FILE_MUTEX = threading.Lock()

    ELEMENT_PER_PAGE = 12000

    StartArtistKey: str = "a"
    CurrentArtistKey: str = "a"
    EndArtistKey: str = "b"


    def __init__(self, threadNumber:int, database, tk: Token, startKey: str, endKey: str, path: str, lastSessionData):
        super().__init__(threadNumber, database, tk)
        
        ArtistsResearch.CurrentArtistKey = startKey
        ArtistsResearch.StartArtistKey = startKey
        ArtistsResearch.EndArtistKey = endKey
        ArtistsResearch.Database = database

        self.path = path
        #self.lastSessionData = lastSessionData

        #self.currentPageIndex: int = self.lastSessionData["lastPageIndex"]
        #self.pageElement = len(self.pageData.keys())
        self.processedKey: dict = lastSessionData["processedKey"]
        self.pageData: dict = {}#self.loadPage(self.currentPageIndex)

        """for k in self.processedKey.keys():
            if self.processedKey[k] and os.path.exists(path + f"{k}.json"):
                with open(path + f"{k}.json", 'r') as file:
                    if file.read() == "":
                        self.processedKey[k] = False
            else:
                self.processedKey[k] = False"""

        
    @staticmethod
    def inizializeConfiguration() -> dict:
        data = {
            "processedKey" : {}
        }

        return data
    
    def toDict(self) -> dict:
        out = super().todict()
        #out["lastPageIndex"] = self.currentPageIndex
        #out["lastPageElement"] = self.pageElement
        out["processedKey"] = self.processedKey
        return out
        
    def start(self):
        #Terminal.info(f"creating Threads for Search Artists")
        for i in range(self.threadNumber):
            th = self.ArtistSerarcher(i, self.token, self)
            self.threads.append(th)
            th.start()

    def totalElement(self) -> int:
        return (self.keyToNumber(ArtistsResearch.EndArtistKey) - self.keyToNumber(ArtistsResearch.StartArtistKey))
    
    def progress(self):
        return (self.keyToNumber(ArtistsResearch.CurrentArtistKey))

    def keyToNumber(self, k: str) -> int:
        k = k.lower()
        sum = 0
 
        for i in range(len(k)):
            sum += ((ord(k[i]) - 96) * (26**i))
        return sum - 1

    @staticmethod
    def setkeys(start: str, end: str):
        ArtistsResearch.CurrentArtistKey = start
        ArtistsResearch.LastArtistKey = end

    @classmethod
    @staticmethod
    def nextKey(artistsKey: str) -> str:
        index = 0
        out = ""
    
        while True:
            ch = artistsKey[index]
            val = (ord(ch) - 97 + 1)%26
            out += chr(val + 97)
        
            if val == 0:
                if len(artistsKey) - 1 == index:
                    out += 'a'
                    break
                else:
                    index += 1
            else:
                break
        for i in range(index + 1, len(artistsKey)):
            out += artistsKey[i]

        return out
    
    """def makeString(self, k: int) -> str:
    str = ""
    while True:
        res = (k % 26)
        str += chr(res + 97)
        if k < 26:
            return str
        k -= res
        k = int(k/26)"""

    class ArtistSerarcher(DataResearch.ResearchThread):

        def __init__(self, thNumber, token, classRef):
            super().__init__(thNumber, token)

            self.controllerClass: ArtistsResearch = classRef
            self.fileData:dict = {}

        def run(self):
            #Terminal.info(f" Thread ArtistSerarcher [{self.thNumber}] started")

            while self.running:
                key: str
                
                with ArtistsResearch.MUTEX1:
                    FindKey = True

                    while FindKey:
                        key = ArtistsResearch.CurrentArtistKey

                        #è l'ultima chiave ?
                        if key == ArtistsResearch.EndArtistKey:
                            self.running = False
                            FindKey = False
                            break
                    
                        #se no, non è stata ancora processata ?
                        if key not in self.controllerClass.processedKey or not self.controllerClass.processedKey[key]:
                            self.controllerClass.processedKey[key] = False
                            FindKey = False
                        else:
                            Terminal.success(f" \"{Fore.MAGENTA}{key}{Fore.RESET}\" already processed")
                        
                        ArtistsResearch.CurrentArtistKey = ArtistsResearch.nextKey(key)
                
                if key == ArtistsResearch.EndArtistKey:   
                   break 
                    
                #Terminal.info(f" Search Artist by: \"{Fore.MAGENTA}{key}{Fore.RESET}\"")

                while not self.search_for_artist(50, key):
                    Terminal.retry(self.lastRequest)
                    
                self.appendData()
                
                while self.nextPageAvailable():
                    result = self.nextPage()
                    
                    if not result:
                        Terminal.retry(self.lastRequest +"\n")
                        while not self.Re_requestPage():
                            Terminal.retry(self.lastRequest +"\n")
                    
                    self.appendData()
               
                Terminal.success(f" Search for \"{Fore.MAGENTA}{key}{Fore.RESET}\" completed")
                """with self.controllerClass.FILE_MUTEX:
                    self.controllerClass.savePage(self.controllerClass.currentPageIndex, self.controllerClass.pageData, self.controllerClass.path)"""
                
                self.saveResearchOutput(key)
                self.controllerClass.processedKey[key] = True
                
            #Terminal.info(f" Thread ArtistSerarcher [{self.thNumber}] finished")

    
        def search_for_artist(self, limitValue:int, artistKeyName: str) -> bool:
            
            #Terminal.info(f" Request Artists by Name \"{artistKeyName}\"")
            if artistKeyName == "": 
                return False
            if limitValue > 50:limitValue = 50
            
            return self.makeRequest(f'{ArtistsResearch.URL}{f"?q={artistKeyName}&type=artist&limit={limitValue}&offset=0"}')


        def makeRequest(self, query:str) -> bool:
            with ArtistsResearch.MUTEX3:
                time.sleep(0.050)

            super().make_Request_to(query)

            if self.lastResponse == None:
                return False

            self.JSON_Header = {
                'href'      :   self.lastResponse_json['artists']['href'],
                'limit'     :   self.lastResponse_json['artists']['limit'],
                'next'      :   self.lastResponse_json['artists']['next'],
                'offset'    :   self.lastResponse_json['artists']['offset'],
                'previous'  :   self.lastResponse_json['artists']['previous'],
                'total'     :   self.lastResponse_json['artists']['total'],
            }

            self.JSON_Items = {
                'items'     :   self.lastResponse_json['artists']['items']
            }
            #Terminal.success(f" Output: {self.JSON_artistsHeader}")
            return True
        

        def nextPageAvailable(self) -> bool:
            if self.JSON_Header == None or self.JSON_Header['next'] == None:
                return False
            else:
                return True
        
        def Re_requestPage(self) -> bool:
            if self.JSON_Header == None:
                Terminal.error(f"page not available")
                return False
            return self.makeRequest(self.JSON_Header['href'])


        def nextPage(self) -> bool:
            if self.JSON_Header == None or self.JSON_Header['next'] == None:
                Terminal.error(f"next page not available")
                return False
            return self.makeRequest(self.JSON_Header['next'])
        
    
        def appendData(self) -> bool:
            if self.JSON_Header == None:
                return False
            
            outputDict: dict = {}

            #processo i nuovi elementi
            for artist in self.JSON_Items['items']:
                if outputDict.get(artist['name']) is not None:
                    continue

                artistsDataDict: dict = {
                    'spotify_url' : artist['external_urls']['spotify'],
                    'popularity' : artist['popularity'],
                    'followers' : artist['followers']['total'],
                    'genres' : artist['genres'],
                    'images' : artist['images'],
                    'id' : artist['id'],
                    'type' : artist['type'],
                    'name' : artist['name'].replace('\''," ")
                }

                if artistsDataDict['id'] not in self.fileData:
                    self.fileData[artistsDataDict['id']] = artistsDataDict
        

        def saveResearchOutput(self, key) -> bool:
            self.writeJson(self.fileData, key, self.controllerClass.path)
            self.fileData = {}
            
            return True
        

    

