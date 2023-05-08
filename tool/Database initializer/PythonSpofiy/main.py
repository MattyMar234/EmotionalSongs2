from dataclasses import dataclass
from dotenv import load_dotenv  #pip install python-dotenv
from datetime import datetime

import requests

import json
import time
import os

from colorama import init as colorama_init
from colorama import Fore
from colorama import Style

from DatabaseInterface import DataBase
from researchers import *
from Logger import Terminal
from researchers import *

load_dotenv()






class SpotifyInterface:

    fileData: str = "InterfaceData.json"
    OutputDirName: str = "Output"
    outputDir_ArtistName: str = "Artists"
    outputDir_tracksName: str = "Tracks"
    database: DataBase = None

    artistsFolder: str = f"{os.getcwd()}\\{OutputDirName}\\{outputDir_ArtistName}"
    trackFolder: str = f"{os.getcwd()}\\{OutputDirName}\\{outputDir_tracksName}"
    artistsFile_Path = artistsFolder + '//' + "Artists.json"

    token: Token
   
    clientID: str
    clientSecret: str
    operationType: str = "SearchArtists"



    def __init__(self, database: DataBase):
        
        self.clientID = os.getenv("CLIENT_ID")
        self.clientSecret = os.getenv("CLIENT_SECRET")
        self.loadData()

        self.database = database


        if self.operationType == "SearchArtists" and False:
            Terminal.info(f" ------------ Start searching for artists ------------")
            self.artistsResearch = ArtistsResearch(12, self.database, self.token, "a", "aaa")
            self.artistsResearch.start()
            self.artistsResearch.waith_threads()
        
        else:
            Terminal.info(f" ------------ Start searching for tracks and album ------------")
            self.trackResearch = TrackResearch(1, self.database, self.token)
            self.trackResearch.start()
            time.sleep(2)
            self.trackResearch.stop()
            
        Terminal.info(f" {'-'*64}")
           

    def __del__(self):
        self.saveData()
        
    
    def loadData(self) -> None:

        if self.fileData in os.listdir(os.getcwd()):
            fullPath = os.getcwd() + '\\' + self.fileData
            Terminal.info(f" File {fullPath} found")

            with open(fullPath, 'r') as file: 
                
                Terminal.info(f" Loading data....")
                data = file.read()
                
                if data == "":
                    Terminal.error(f" File Empty")
                    self.generateStartingData()
                
                else:
                    data:dict = json.loads(data)
                    self.token = Token(data['Token']['token'], data['Token']['fetched'], self.clientID, self.clientSecret) 
                    
                    Terminal.info(f" {self.token}")
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

        #self.getToken()
        self.saveData()

    def saveData(self) -> None:
        data = {}

        data["Operation"] = self.toDict()
        data["Token"] = self.token.toDict()

        with open(os.getcwd() + '\\' + self.fileData, 'w') as file:
            json.dump(data, file,  sort_keys=True, indent=4)

        Terminal.info(f" Data saved") 

    def toDict(self):
        output = {}
        output["operationType"] = self.operationType
        return output




def main():
    db: DataBase  = DataBase(PORT = 5432, IP = "localhost")
    db.inizializeDatabase()
    
    print("Element: ", db.getArtistsNumber())
    #db.getArtistAt_by_Followers(0)

    #driver = SpotifyInterface(db)

if __name__ == '__main__':
    main()


#https://kworb.net/spotify/artists.html