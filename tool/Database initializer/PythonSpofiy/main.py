from dataclasses import dataclass
from dotenv import load_dotenv  #pip install python-dotenv
from datetime import datetime
import requests
import base64
import json
import time
import os

from colorama import init as colorama_init
from colorama import Fore
from colorama import Style

from Logger import Terminal
from DatabaseInterface import DataBase

load_dotenv()


@dataclass
class Token:

    validToken_Time_s = 60 * 60 #60s * 60m = 1h
    token: str
    fetched_at: datetime
    

    def __init__(self, token: str, time = None, LastRequestCount = 0):
        self.token = token
        self.requestCount = LastRequestCount

        if time == None:
            self.fetched_at = datetime.now()
        else:
            self.fetched_at = datetime.strptime(time, '%d/%m/%Y %H:%M:%S')

    def isValid(self):
        time_elapsed = datetime.now() - self.fetched_at

        return True if time_elapsed.seconds < self.validToken_Time_s else False

    def toDict(self):
        dict = {
            "fetched" : self.fetched_at.strftime('%d/%m/%Y %H:%M:%S'),
            "token" : self.token
        }

        return dict


class DataResearch:
    
    ElementAvailable: int
    ElementProcessed: int

    def __init__(self, ElementAvailable, ElementProcessed):
        self.ElementAvailable = ElementAvailable
        self.ElementProcessed = ElementProcessed

    def get_auth_headers(self, token: str):
        return {"Authorization": "Bearer " + token}

    def todict(self) -> dict:
        out = {
            "ElementAvailable" : self.ElementAvailable,
            "ElementProcessed" : self.ElementProcessed,
        }

        return out
    
class TrackResearch(DataResearch):

    artistsKey: str
    

    def __init__(self, LastArtistsKey = "a", ElementAvailable = 0, ElementProcessed = 0):
        super().__init__(ElementAvailable, ElementProcessed)
        self.artistsKey = LastArtistsKey


    def toDict(self) -> dict:
        out = super().todict()
        out["LastArtistsKey"] = self.artistsKey

        return out


class ArtistsResearch(DataResearch):

    URL = "https://api.spotify.com/v1/search"
    artistsKey: str
    outputPath = ""

    JSON_artistsHeader:dict
    JSON_artistItems: dict
    

    def __init__(self, outputPath, LastArtistsKey = "a", ElementAvailable = 0, ElementProcessed = 0, ):
        super().__init__(ElementAvailable, ElementProcessed)
        self.artistsKey = LastArtistsKey
        self.outputPath = outputPath

        self.JSON_artistsHeader:dict = None
        self.JSON_artistItems:dict = None

        self.totalArtists()

    def totalArtists(self) -> None:
        if os.path.getsize(self.outputPath) > 0 and False:

            with open(self.outputPath, 'r') as file:
                outputDict:dict = json.load(file)
                Terminal.info(f" Total artists saved: [{len(outputDict.keys())}]") 
        else:
            Terminal.info(f" Total artists saved: [0]")

    def setKey(self, key):
        self.artistsKey = key

    def nextKey(self):
        index = 0
        out = ""
    
        while True:
            ch = self.artistsKey[index]
            val = (ord(ch) - 97 + 1)%26
            out += chr(val + 97)
        
            if val == 0:
                if len(self.artistsKey) - 1 == index:
                    out += 'a'
                    break
                else:
                    index += 1
            else:
                break
        for i in range(index + 1, len(self.artistsKey)):
            out += self.artistsKey[i]

        Terminal.info(f" Next key: {out}")
        self.artistsKey = out


    def setPath(self, path: str) -> None:
        self.outputPath = path


    def search_for_artist(self, token:str, limitValue:int = 50, artistKeyName: str = "") -> bool:
        
        if artistKeyName == "":artistKeyName = self.artistsKey
        if limitValue > 50:limitValue = 50
        
        # ============== [creazione crichiesta] ============== #
        Terminal.info(f" Request Artists by Name \"{artistKeyName}\"")

        query = f"?q={artistKeyName}&type=artist&limit={limitValue}&offset=0"
        result = self.makeRequest(f'{self.URL}{query}', token)
        
        
        
        return result


    def makeRequest(self, query:str, token: str) -> bool:

        Terminal.info(" Collecting Data...")
        result = requests.get(query, headers = super().get_auth_headers(token))
        
        # ============== [verifica risultato] ============== #
        if not result.ok:
            Terminal.error(f"req: {result.url} --> {result.status}")
            return False
        
        Terminal.success(" Data collected")
        Terminal.info(" processing...")

        # ============== [Processamento header] ============== #
        reposnse_Json = json.loads(result.content.decode("utf-8"))

        self.JSON_artistsHeader = {
            'href' : reposnse_Json['artists']['href'],
            'limit' : reposnse_Json['artists']['limit'],
            'next' : reposnse_Json['artists']['next'],
            'offset' : reposnse_Json['artists']['offset'],
            'previous' : reposnse_Json['artists']['previous'],
            'total' : reposnse_Json['artists']['total'],
        }

        # ============== [Processamento items] ============== #
        self.JSON_artistItems = {
            'items' : reposnse_Json['artists']['items']
        }

        Terminal.success(" Data processed")
        Terminal.info(f" output: {self.JSON_artistsHeader}")
        return True
    

    def nextPageAvailable(self) -> bool:
        if self.JSON_artistsHeader == None or self.JSON_artistsHeader['next'] == None:
            return False
        else:
            return True
        

    def nextPage(self, token):
        if self.JSON_artistsHeader == None or self.JSON_artistsHeader['next'] == None:
            Terminal.error(f"next page not available")
            return False
        
        return self.makeRequest(self.JSON_artistsHeader['next'], token)

    
    def saveResearchOutput(self, db: DataBase, overWrite: bool = False, ) -> bool:
        Terminal.info(f" Save request Data...")
        
        if self.JSON_artistsHeader == None:
            Terminal.error(f"nothing to save")
            return False
        
        outputDict: dict = {}

        #carico le chiavi che ho già
        """if not overWrite and os.path.getsize(self.outputPath) > 0:
            with open(self.outputPath, 'r') as file:
                outputDict = json.load(file)"""

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
                'name' : artist['name'].replace('\'',"")
            }

            db.addAutor(artistsDataDict)

            #outputDict[artist['name']] = artistsDataDict
            
        #salvo le modifiche
        with open(self.outputPath, 'w') as file:
            json.dump(outputDict, file,  sort_keys = True, indent = 4)

        Terminal.success(f" data saved")
        self.totalArtists()
        return True
        


    def toDict(self) -> dict:
        out = super().todict()
        out["LastArtistsKey"] = self.artistsKey

        return out


class SpotifyInterface:

    fileData: str = "InterfaceData.json"
    OutputDirName: str = "Output"
    outputDir_ArtistName: str = "Artists"
    outputDir_tracksName: str = "Tracks"
    database: DataBase = None

    artistsFolder: str = f"{os.getcwd()}\\{OutputDirName}\\{outputDir_ArtistName}"
    trackFolder: str = f"{os.getcwd()}\\{OutputDirName}\\{outputDir_tracksName}"
    artistsFile_Path = artistsFolder + '//' + "Artists.json"

    token: Token = ""
    artistsResearch: ArtistsResearch
   
    clientID: str
    clientSecret: str
    operationType: str = "SearchArtists"



    def __init__(self, database: DataBase):
        self.clientID = os.getenv("CLIENT_ID")
        self.clientSecret = os.getenv("CLIENT_SECRET")
        self.loadData()

        self.database = database


        if self.operationType == "SearchArtists":
            Terminal.info(f" start searching for artists")

            while True:

                if not self.token.isValid():
                    self.getToken()

                if self.artistsResearch.search_for_artist(self.token.token): 
                    self.artistsResearch.saveResearchOutput(self.database)

                    while self.artistsResearch.nextPageAvailable():
                        Terminal.info(f" {'-'*64}")
                        Terminal.info(f" request next page")
                        
                        if not self.token.isValid():
                            self.getToken()

                        self.artistsResearch.nextPage(self.token.token)
                        self.artistsResearch.saveResearchOutput(self.database)

                    Terminal.info(f" {'-'*64}")
                    self.artistsResearch.nextKey()
                    self.saveData()

                else:
                    break
                break

            Terminal.info(f" {'-'*64}")
           

    def __del__(self):
        self.saveData()
        
            
    

    def getToken(self) -> bool:
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

        token = json_file["access_token"]
        self.token = Token(token)
        Terminal.success(f" token: {token}")

        return True

        
    

    def loadData(self) -> None:

        if self.fileData in os.listdir(os.getcwd()):
            fullPath = os.getcwd() + '\\' + self.fileData
            Terminal.info(f" File {fullPath} found")

            with open(fullPath, 'r') as file: 
                
                Terminal.info(f" Loading data")
                data = file.read()
                
                if data == "":
                    Terminal.error(f" File Empty")
                    self.generateStartingData()
                
                else:
                    data:dict = json.loads(data)
                    self.token = Token(data['Token']['token'], data['Token']['fetched']) 
                    self.artistsResearch = ArtistsResearch(self.artistsFile_Path, data['Ricerca']['LastArtistsKey'], data['Ricerca']['ElementAvailable'], data['Ricerca']['ElementProcessed'])
        

                    Terminal.info(f" {self.token}")
                    Terminal.info(f" {self.artistsResearch}")
                    Terminal.info(f" {'-'*64}")
        
        else:
            Terminal.error(f"File {self.fileData} does not exist")
            self.generateStartingData()


        if self.OutputDirName not in os.listdir(os.getcwd()):
            os.mkdir(f"{os.getcwd()}\\{self.OutputDirName}")
            Terminal.info(f" Missing Folder")
           
        if self.outputDir_ArtistName not in os.listdir(f"{os.getcwd()}\\{self.OutputDirName}"):
            os.mkdir(self.artistsFolder)
            Terminal.info(f" Missing Folder")

        if self.outputDir_tracksName not in os.listdir(f"{os.getcwd()}\\{self.OutputDirName}"):
            os.mkdir(self.trackFolder)
            Terminal.info(f" Missing Folder")
            

    def generateStartingData(self) -> None:  
        Terminal.info(f"Creating system data...") 
        self.artistsResearch = ArtistsResearch(self.artistsFile_Path, 'a')

        self.getToken()
        self.saveData()

    def saveData(self) -> None:
        data = {}

        data["Operation"] = self.toDict()
        data["Token"] = self.token.toDict()
        data["Ricerca"] = self.artistsResearch.toDict()

        with open(os.getcwd() + '\\' + self.fileData, 'w') as file:
            json.dump(data, file,  sort_keys=True, indent=4)

        Terminal.info(f" Data saved") 

    def toDict(self):
        output = {}
        output["operationType"] = self.operationType
        return output




def main():
    db = DataBase(PORT = 5432, IP = "localhost")
    db.inizializeDatabase()

    driver = SpotifyInterface(db)

if __name__ == '__main__':
    main()


#https://kworb.net/spotify/artists.html