from Logger import Terminal
from DatabaseInterface import DataBase
from Researchers_Base import *
from PagesController import PageController


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

    def __init__(self, threadNumber:int, database: DataBase, token: Token, artistPath:str, trackPath:str, albumPath:str):
        super().__init__(threadNumber, database, token)

        #TrackResearch.TotalElement = database.getArtistsNumber()
        #TrackResearch.CurrentIndex = 0
        self.ArtistPath = artistPath
        self.TrackPath = trackPath
        self.AlbumPath = albumPath

        self.pageController_Track = PageController(self.TrackPath)
        self.pageController_Album = PageController(self.AlbumPath)

        self.Arists_ID:dict = {}

        #========================================================#
        #conto gli elementi e rimuovo i duplicati
        self.file_list = [ f"{self.ArtistPath}/" + file for file in os.listdir(self.ArtistPath) if file.endswith('.json')]
        self.files_index = 0

        for _ in range(20):
            th = self.ElementFinder_Thread(self)
            self.threads.append(th)
            th.start()
        
        for th in self.threads:
            th.join()
        
        self.pageController_Track.savePage()
        self.pageController_Album.savePage()
        #========================================================#
        
        self.array_ID = np.array(list(self.Arists_ID.keys()))
        self.TotalElement = self.array_ID.shape[0]
        self.ID_Index = 0
        self.threads.clear()
        self.Arists_ID.clear()
    
    def getElementCount(self) -> int:
        return self.TotalElement
    
    def getProgress(self) -> int:
        return self.ID_Index
    
    def toDict(self) -> dict:
        out = super().todict()
        return out

    def start(self):
        #Terminal.info(f"creating Threads for Search Tracks")
        for i in range(self.threadNumber):
            th = self.TrackReSerarcher(i, self)
            self.threads.append(th)
            th.start()

    @classmethod
    class ElementFinder_Thread(threading.Thread):
        def __init__(self, bho, classReference):
            super().__init__()
            self.classReference: TrackResearch = classReference

        def run(self):
            fileNumber = len(self.classReference.file_list)
            
            while True:
                with TrackResearch.MUTEX1:
                    index = self.classReference.files_index
                    self.classReference.files_index += 1

                    if not index < fileNumber:
                        return
                    
                with open(self.classReference.file_list[index], 'r') as file:
                    json_data:dict = json.load(file)
                    for k in json_data.keys():
                        with TrackResearch.MUTEX2:
                            self.classReference.Arists_ID[k] = None

    @classmethod
    class TrackReSerarcher(DataResearch.ResearchThread):

        def __init__(self, thNumber: int, bho, classReference):
            self.classReference: TrackResearch = classReference
            super().__init__(thNumber, self.classReference.token)

        def run(self):
            index:int = 0
            #Terminal.info(f" Thread {self.__class__.name} [{self.thNumber}] started")

            while self.running:
                with ArtistsResearch.MUTEX1:
                    if self.classReference.ID_Index >= self.classReference.TotalElement:
                        return
                    else:
                        index = self.classReference.ID_Index
                        self.classReference.ID_Index += 1
        
                    
                #artistsDict = self.database.getArtistAt_order_by_ID(index) 
                id = self.classReference.array_ID[index]
                reties = 0

                while not self.search_traks_by_artistID(id):
                    reties += 1
                    if reties >= 24:
                        Terminal(" to much failed requests !!!")
                        os._exit(1)
                    
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

                for arist in song['artists']:
                    Artists_ID = arist['id']

                for arist in song['album']['artists']:
                    Album_Artists_ID = arist['id']

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

                self.classReference.pageController_Track.saveData(SongData, SongData['id'])
                self.classReference.pageController_Album.saveData(songsAlbum, songsAlbum['id'])

                

             
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
        super().__init__(threadNumber, database, tk, path)
        
        ArtistsResearch.CurrentArtistKey = startKey
        ArtistsResearch.StartArtistKey = startKey
        ArtistsResearch.EndArtistKey = endKey
        ArtistsResearch.Database = database

        self.lastSessionData = lastSessionData

        self.processedKey: dict = self.lastSessionData["processedKey"]
        self.currentPageIndex: int = self.lastSessionData["lastPageIndex"]
        self.pageData: dict = self.loadPage(self.currentPageIndex)
        self.pageElement = len(self.pageData.keys())

        
    @staticmethod
    def inizializeConfiguration() -> dict:
        data = {
            "lastPageElement" : 0,
            "lastPageIndex" : 0,
            "processedKey" : {}
        }

        return data
    
    def toDict(self) -> dict:
        out = super().todict()
        out["lastPageIndex"] = self.currentPageIndex
        out["lastPageElement"] = self.pageElement
        out["processedKey"] = self.processedKey
        return out
        
    def start(self):
        Terminal.info(f"creating Threads for Search Artists")

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

            self.controllerClass = classRef

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
                    pass

                with ArtistsResearch.MUTEX2:
                    self.saveResearchOutput()
                

                while self.nextPageAvailable():
                    result = self.nextPage()
                    
                    if not result:
                        Terminal.retry(self.lastRequest +"\n")
                        while not self.Re_requestPage():
                            Terminal.retry(self.lastRequest +"\n")
                    
                    with ArtistsResearch.MUTEX2:
                        self.saveResearchOutput()
               
                Terminal.success(f" Search for \"{Fore.MAGENTA}{key}{Fore.RESET}\" completed")
                with self.controllerClass.FILE_MUTEX:
                    self.controllerClass.savePage(self.controllerClass.currentPageIndex, self.controllerClass.pageData)
                
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
                time.sleep(0.040)

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
        
        def Re_requestPage(self, token) -> bool:
            if self.JSON_Header == None:
                Terminal.error(f"page not available")
                return False
            return self.makeRequest(self.JSON_Header['href'])


        def nextPage(self) -> bool:
            if self.JSON_Header == None or self.JSON_Header['next'] == None:
                Terminal.error(f"next page not available")
                return False
            return self.makeRequest(self.JSON_Header['next'])
        
        

        def saveResearchOutput(self) -> bool:
            
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

                with self.controllerClass.FILE_MUTEX:
                    if self.controllerClass.pageElement == ArtistsResearch.ELEMENT_PER_PAGE:
                        self.controllerClass.savePage(self.controllerClass.currentPageIndex, self.controllerClass.pageData)
                        
                        self.controllerClass.currentPageIndex += 1
                        self.controllerClass.pageData = {}
                        self.controllerClass.pageElement = 0
                    
                    if artistsDataDict['id'] not in self.controllerClass.pageData:
                        self.controllerClass.pageData[artistsDataDict['id']] = artistsDataDict
                        self.controllerClass.pageElement += 1


                #ArtistsResearch.Database.addArtist(artistsDataDict)
                #outputDict[artist['name']] = artistsDataDict
            return True
        

    

