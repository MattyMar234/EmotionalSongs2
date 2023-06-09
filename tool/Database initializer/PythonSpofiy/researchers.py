from Logger import Terminal
from DatabaseInterface import DataBase
from Researchers_Base import *
from PagesController import PageController
from pathFormatter import PathFormatter as PF
from SpotifyScraper import *

from colorama import init as colorama_init
from colorama import Fore
from colorama import Style

import threading
import time
import json
import requests
import os
import numpy as np



class TrackResearch(DataResearch):

    URL = "https://api.spotify.com/v1/artists/{id}/top-tracks?market=US"
    MUTEX1 = threading.Lock()
    MUTEX2 = threading.Lock()
    MUTEX3 = threading.Lock()

    CurrentIndex: int
    TotalElement: int
    file_list: list

    def __init__(self, threadNumber:int, database: DataBase, token: Token, artistPath:str, trackPath:str, albumPath:str, lastSessionData):
        super().__init__(threadNumber, database, token)

        self.ArtistPath = artistPath
        self.TrackPath = trackPath
        self.AlbumPath = albumPath

        self.pageController_Track = PageController(self.TrackPath)
        self.pageController_Album = PageController(self.AlbumPath)

        self.processed_ID: dict = lastSessionData["processed_ID"]
        self.Arists_ID:dict = {}

        #========================================================#
        #conto gli elementi e rimuovo i duplicati
        self.file_list = [ PF.formatPath(f"{self.ArtistPath}\\" + file) for file in os.listdir(self.ArtistPath) if file.endswith('.json')]
        self.files_index = 0

        for _ in range(32):
            th = self.ElementFinder_Thread(self)
            self.threads.append(th)
            

        for th in self.threads:
            th.start()
        
        for th in self.threads:
            th.join()

        with open("verifica.json", "w") as f:
            json.dump(self.Arists_ID, f, indent=4)
        #========================================================#
        
        self.TotalElement:int = 0
        for key in self.Arists_ID.keys():
            self.TotalElement += len(self.Arists_ID[key])
        
        self.ID_Index: int = 0
        self.totalkey = self.Arists_ID.keys()
        self.currentkey_index = 0
        self.threads.clear()
       #========================================================#

        
    @staticmethod
    def inizializeConfiguration() -> dict:
        data = {
            "processed_ID" : {}
        }

        return data
    
    def getElementCount(self) -> int:
        return self.TotalElement
    
    def getProgress(self) -> int:
        return self.ID_Index
    
    def toDict(self) -> dict:
        out = super().todict()
        #out["lastPageIndex"] = self.currentPageIndex
        #out["lastPageElement"] = self.pageElement
        out["processed_ID"] = self.processed_ID
        return out

    def start(self):
        for i in range(self.threadNumber):
            th = self.TrackReSerarcher(i, self)
            self.threads.append(th)
            th.start()

    @classmethod
    class ElementFinder_Thread(threading.Thread):
        def __init__(self, bho, classReference):
            super().__init__()
            self.classReference: TrackResearch = classReference
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

        def __init__(self, thNumber: int, classReference):
            super().__init__(thNumber, classReference.token)
            
            self.classReference: TrackResearch = classReference
            self.fileData_song:dict = {}
            self.fileData_album:dict = {}

            self.scraper: SpotifyScraper = SpotifyScraper()

        def getelementAt(self, index: int) -> str:
            merged_list = sum(self.classReference.Arists_ID.values(), [])  # Unione di tutte le liste nel dizionario
            
            if index < 0 or index >= len(merged_list):
                return None, None  # L'indice è fuori dai limiti della lista unificata
            
            for key, lst in self.classReference.Arists_ID.items():
                if index < len(lst):
                    return merged_list[index], key  # Restituisce l'i-esimo elemento e la chiave del dizionario
                index -= len(lst)


        def run(self):
            index:int = 0
            
            while self.running:

                #verifico qual è l'elemento che devo processare
                with ArtistsResearch.MUTEX1:
                    if self.classReference.ID_Index >= self.classReference.TotalElement:
                        return
                    else:
                        index = self.classReference.ID_Index
                        self.classReference.ID_Index += 1


                id, key = self.getelementAt(index)

                #verifico se esiste la cartella
                with ArtistsResearch.MUTEX2:
                    path1 = PF.formatPath(self.classReference.TrackPath + '\\' + key)
                    path2 = PF.formatPath(self.classReference.AlbumPath + '\\' + key)

                    if not os.path.exists(path1):
                        os.makedirs(path1)
                    if not os.path.exists(path2):
                        os.makedirs(path2)

                #eseguo lo screaping
                albums, tracks = self.scraper.getArtist_albums_and_tracks(id)

                with open(PF.formatPath(f'{path1}\\{id}.json'), 'w') as file:
                    json.dump(tracks, file, indent=4)

                with open(PF.formatPath(f'{path2}\\{id}.json'), 'w') as file:
                    json.dump(albums, file, indent=4)

                """reties = 0
                while not self.search_traks_by_artistID(id):
                    reties += 1
                    if reties >= 24:
                        Terminal.error(" to much failed requests !!!")
                        os._exit(1)"""


            #Terminal.success(f" Search for \"{Fore.MAGENTA}{key}{Fore.RESET}\" completed")
            #Terminal.info(f" Thread ArtistSerarcher [{self.thNumber}] finished")


        def search_traks_by_artistID(self, artist_ID:str) -> bool:
            if artist_ID == "": 
                Terminal.error(" Invalid artist ID")
                return False
            
            with ArtistsResearch.MUTEX3:
                time.sleep(0.050)

            if not super().make_Request_to(f'{TrackResearch.URL.replace("{id}", artist_ID)}'):
                return False
            
            songsList = []
            

            for idx, song in enumerate(self.lastResponse_json['tracks']):

                Artists_ID = []
                Album_Artists_ID = []
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

            self.writeJson(self.fileData_song, artist_ID, self.classReference.TrackPath)
            self.writeJson(self.fileData_album, artist_ID, self.classReference.AlbumPath)

            self.classReference.processed_ID[artist_ID] = True

            """self.classReference.pageController_Track.saveData(SongData, SongData['id'])
            self.classReference.pageController_Album.saveData(songsAlbum, songsAlbum['id'])"""

                

             
            return True

        

    

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


    def __init__(self, threadNumber:int, database: DataBase, tk: Token, startKey: str, endKey: str, path: str, lastSessionData):
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
        

    

