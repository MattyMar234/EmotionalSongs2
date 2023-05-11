from Logger import Terminal
from DatabaseInterface import DataBase

from dataclasses import dataclass
from datetime import datetime
from colorama import init as colorama_init
from colorama import Fore
from colorama import Style
import threading
import time
import json
import requests
import base64
import os


@dataclass
class Token:

    validToken_Time_s = 60 * 60 #60s * 60m = 1h
    token: str
    fetched_at: datetime
    
    def __init__(self, token: str, time, clientID, clientSecret):
        self.token = token
        self.clientID = clientID
        self.clientSecret = clientSecret

        if time == None:
            self.fetched_at = datetime.now()
        else:
            self.fetched_at = datetime.strptime(time, '%d/%m/%Y %H:%M:%S')

        self.getNewToken()
        
        if not self.isValid():
            ...
            

    def isValid(self):
        time_elapsed = datetime.now() - self.fetched_at
        return True if time_elapsed.seconds < self.validToken_Time_s else False

    def getNewToken(self) -> bool:
        auth_string = self.clientID + ":" + self.clientSecret
        auth_bytes = auth_string.encode("utf-8")
        auth_base64 = str(base64.b64encode(auth_bytes), "utf-8")

        Terminal.info(f" Request new Token")

        URL = 'https://accounts.spotify.com/api/token'

        headers = {
            "Authorization": "Basic " + auth_base64,
            "Content-Type": "application/x-www-form-urlencoded"
        }

        data = {"grant_type": "client_credentials"}

        response = requests.post(URL, headers = headers, data = data)
        json_file = json.loads(response.content)

        self.fetched_at = datetime.now()
        self.token = json_file["access_token"]

        Terminal.success(f" token: {self.token}")

        return True


    def toDict(self):
        dict = {
            "fetched" : self.fetched_at.strftime('%d/%m/%Y %H:%M:%S'),
            "token" : self.token
        }

        return dict


class DataResearch():
    
    MUTEX = threading.Lock()
    FILE_SETTINGS = "ResearcherSettings.json"
    PAGE_FILE_NAME = "Page[i].json"


    def __init__(self, threadNumber:int, database: DataBase, token: Token, Folderpath: str):
        self.token = token
        self.threadNumber = threadNumber
        self.database = database
        self.threads = []
        self.FolderPath = Folderpath
        self.informationsFile: dict

    def loadSettings(self) -> bool:
        if os.path.exists(self.FolderPath + "/" + DataResearch.FILE_SETTINGS):
            with open(self.FolderPath + DataResearch.FILE_SETTINGS, 'r') as file:
                data = file.read()

                if data != "":
                    self.informationsFile = json.loads(data)
                    return True
        else:
            return False

    def saveSettings(self):
        with open(self.FolderPath + "/" + DataResearch.FILE_SETTINGS, 'w') as file:
            json.dump(self.informationsFile, file,  sort_keys = True, indent=4)


    def savePage(self, pageIndex, pageData):
        fileName = self.FolderPath + "/" + DataResearch.PAGE_FILE_NAME.replace("[i]", f'{pageIndex}')
        with open(fileName, 'w') as file:
            json.dump(pageData, file,  sort_keys = True, indent=4)


    def loadPage(self, pageIndex) -> dict:
        fileName = self.FolderPath + "/" + DataResearch.PAGE_FILE_NAME.replace("[i]", f'{pageIndex}')
        
        if os.path.exists(fileName):
            with open(fileName, 'r') as file:
                data = file.read()
                if data != "":
                    return json.loads(data)
                else:
                    data = {}
                    return data
        else:
            data = {}
            return data

    def waith_threads(self) -> None:
        for t in self.threads:
            t.join()

    def finisched(self) -> bool:
        return False
    
    def stop(self):
        for th in self.threads:
            th.stop()

    class ResearchThread(threading.Thread):

        def __init__(self, thNumber, token: Token):
            threading.Thread.__init__(self)
            self.thNumber = thNumber
            self.lastRequest:str = ""
            self.lastResponse_json = None
            self.lastResponse = None
            self.running = True
            self.token = token


            self.JSON_Header:dict = None
            self.JSON_Items:dict = None

        def stop(self):
            self.running = False

        def get_auth_headers(self):
            return {"Authorization": "Bearer " + self.token.token}
        
        def make_Request_to(self, query: str):
            self.lastRequest = query

            response = requests.get(query, headers = self.get_auth_headers())

            if not response.ok:
                Terminal.error(f"req: {response.url} --> {response} --> {response.text}" )
                self.lasrResponse = None
                self.lastResponse_json = None
                return False
            else:
                self.lastResponse = response
                self.lastResponse_json = json.loads(self.lastResponse.content.decode("utf-8"))
                return True

    def todict(self) -> dict:
        out = {
         
        }

        return out
    

    
class TrackResearch(DataResearch):

    URL = "https://api.spotify.com/v1/artists/{id}/top-tracks?market=US"
    MUTEX1 = threading.Lock()
    MUTEX2 = threading.Lock()
    MUTEX3 = threading.Lock()

    CurrentIndex: int
    TotalElement: int

    def __init__(self, threadNumber:int, database: DataBase, token: Token):
        super().__init__(threadNumber, database, token)

        TrackResearch.TotalElement = database.getArtistsNumber()
        TrackResearch.CurrentIndex = 0

    def start(self):
        Terminal.info(f"creating Threads for Search Tracks")

        for i in range(self.threadNumber):
            th = self.TrackReSerarcher(i, self.database, self.token)
            th.start()

            self.threads.append(th)


    class TrackReSerarcher(DataResearch.ResearchThread):

        def __init__(self, thNumber: int, database: DataBase, token: Token):
            super().__init__(thNumber, token)
            self.database = database

        def run(self):
            Terminal.info(f" Thread {self.__class__.name} [{self.thNumber}] started")
            index: int = 0

            while self.running:
                with ArtistsResearch.MUTEX1:
                    index = TrackResearch.CurrentIndex
                    TrackResearch.CurrentIndex += 1
                    artistsDict = self.database.getArtistAt_order_by_ID(index) 
                    
                
                self.search_traks_by_artistID(artistsDict['id'])
                    
                    

               
            #Terminal.success(f" Search for \"{Fore.MAGENTA}{key}{Fore.RESET}\" completed")
            #Terminal.info(f" Thread ArtistSerarcher [{self.thNumber}] finished")

    
        def search_traks_by_artistID(self, artist_ID:str) -> bool:
            
            if artist_ID == "": 
                Terminal.error(" Invalid artist ID")
                return False
            
            return self.makeRequest(f'{TrackResearch.URL.replace("{id}", artist_ID)}')


        def makeRequest(self, query:str) -> bool:
            with ArtistsResearch.MUTEX3:
                time.sleep(0.040)

            if not super().make_Request_to(query):
                return False
            
            songsList = []
            

            for idx, song in enumerate(self.lastResponse_json['tracks']):
                
                SongData = {
                    'album_ID'      :   song['album']['id'],
                    'duration_ms'   :   song['duration_ms'],
                    'spotify_url'   :   song['external_urls']['spotify'],
                    'id'            :   song['id'],
                    'name'          :   song['name'],
                    'popularity'    :   song['popularity']
                }

                songsArtists = song['artists']

                songsAlbum = {
                    'id'            :   song['album']['id'],
                    'element'       :   song['album']['total_tracks'],
                    'spotify_url'   :   song['album']['external_urls']['spotify'],
                    'images'        :   song['album']['images'],
                    'name'          :   song['album']['name'],
                    'release_date'  :   song['album']['release_date'],
                    'type'          :   song['album']['album_type'],
                }   

                print(SongData)

                
                """'spotify_url' : artist['external_urls']['spotify'],
                    'popularity' : artist['popularity'],
                    'followers' : artist['followers']['total'],
                    'genres' : artist['genres'],
                    'images' : artist['images'],
                    'id' : artist['id'],
                    'type' : artist['type'],
                    'name' : artist['name'].replace('\''," ")"""

            """self.JSON_Items = {
                'items'     :   self.lastResponse_json['artists']['items']
            }"""
            #Terminal.success(f" Output: {self.JSON_artistsHeader}")
            return True

        

    def toDict(self) -> dict:
        out = super().todict()
        return out


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
        

    

