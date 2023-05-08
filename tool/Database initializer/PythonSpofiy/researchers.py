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


    def __init__(self, threadNumber:int, database: DataBase, token: Token):
        self.token = token
        self.threadNumber = threadNumber
        self.database = database
        self.threads = []

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

        def get_auth_headers(self, token: str):
            return {"Authorization": "Bearer " + token}
        
        def make_Request_to(self, query: str):
            self.lastRequest = query

            """with DataResearch.MUTEX:
                if not self.token.isValid():
                    self.token.getNewToken()"""

            response = requests.get(query, headers = self.get_auth_headers(self.token.token))

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
                time.sleep(0.100)

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

    CurrentArtistKey: str = "a"
    EndArtistKey: str = "b"

    Database: DataBase
    token: Token

    def __init__(self, threadNumber:int, database: DataBase, token: Token, startKey: str, endKey: str):
        super().__init__(token, threadNumber)
        
        ArtistsResearch.CurrentArtistKey = startKey
        ArtistsResearch.EndArtistKey = endKey
        ArtistsResearch.Database = database
        
        
    def start(self):
        Terminal.info(f"creating Threads for Search Artists")

        for i in range(super().threadNumber):
            th = self.ArtistSerarcher(i)
            super().threads.append(th)
            th.start()


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

    class ArtistSerarcher(DataResearch.ResearchThread):

        def __init__(self, thNumber):
            super().__init__(self, thNumber)


        def run(self):
            Terminal.info(f" Thread ArtistSerarcher [{self.thNumber}] started")

            while self.running:
                key: str
                
                with ArtistsResearch.MUTEX1:
                    key = ArtistsResearch.CurrentArtistKey

                    if key == ArtistsResearch.EndArtistKey:
                        self.running = False
                        break
                    
                    ArtistsResearch.CurrentArtistKey = ArtistsResearch.nextKey(key)
                    
                    
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
            Terminal.info(f" Thread ArtistSerarcher [{self.thNumber}] finished")

    
        def search_for_artist(self, limitValue:int, artistKeyName: str) -> bool:
            
            #Terminal.info(f" Request Artists by Name \"{artistKeyName}\"")
            if artistKeyName == "": 
                return False
            if limitValue > 50:limitValue = 50
            
            return self.makeRequest(f'{ArtistsResearch.URL}{f"?q={artistKeyName}&type=artist&limit={limitValue}&offset=0"}')


        def makeRequest(self, query:str) -> bool:
            with ArtistsResearch.MUTEX3:
                time.sleep(0.100)

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


        def nextPage(self, token) -> bool:
            if self.JSON_artistsHeader == None or self.JSON_artistsHeader['next'] == None:
                Terminal.error(f"next page not available")
                return False
            return self.makeRequest(self.JSON_artistsHeader['next'])
        
        

        
        def saveResearchOutput(self) -> bool:
            
            if self.JSON_artistsHeader == None:
                return False
            
            outputDict: dict = {}

            #processo i nuovi elementi
            for artist in self.JSON_artistItems['items']:
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

                ArtistsResearch.Database.addArtist(artistsDataDict)
                #outputDict[artist['name']] = artistsDataDict
            return True
        

    def toDict(self) -> dict:
        out = super().todict()
        return out

