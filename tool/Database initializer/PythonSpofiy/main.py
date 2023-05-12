from dataclasses import dataclass
from dotenv import load_dotenv  #pip install python-dotenv
from datetime import datetime
from alive_progress import alive_bar

import requests

import json
import time
import os

from colorama import init as colorama_init
from colorama import Fore
from colorama import Style

#from DatabaseInterface import DataBase
from researchers import *
from Logger import Terminal
from researchers import *
from pathFormatter import PathFormatter as PF

load_dotenv()



class SpotifyInterface_backup(threading.Thread):
    def __init__(self, ref):
        threading.Thread.__init__(self)
        
        self.ref = ref
        self.running = True
        self.start()

    def stop(self):
        self.running = False

    def run(self):
        while self.running:
            self.ref.saveData()
            time.sleep(2)
        


class SpotifyInterface:

    #FILE PATHs
    fileData: str = "InterfaceData.json"
    OUTPUT_DIRECTORY_NAME: str = "Output"
    OUTPUT_DIRECTORY_ARTISTS: str = "Artists"
    OUTPUT_DIRECTORY_TRACK: str = "Tracks"
    OUTPUT_DIRECTORY_ALBUM: str = "Aracks"
  
    ARTISTS_FOLDER: str = PF.formatPath(f"{os.getcwd()}/{OUTPUT_DIRECTORY_NAME}/{OUTPUT_DIRECTORY_ARTISTS}")
    TRACK_FOLDER: str = PF.formatPath(f"{os.getcwd()}/{OUTPUT_DIRECTORY_NAME}/{OUTPUT_DIRECTORY_TRACK}")
    ALBUM_FOLDER: str = PF.formatPath(f"{os.getcwd()}/{OUTPUT_DIRECTORY_NAME}/{OUTPUT_DIRECTORY_ALBUM}")

    ARTISTS_INF_PATH = PF.formatPath(f"{os.getcwd()}\\{OUTPUT_DIRECTORY_NAME}\\{OUTPUT_DIRECTORY_ARTISTS}_Information.json")
    TRACK_INF_PATH = PF.formatPath(f"{os.getcwd()}\\{OUTPUT_DIRECTORY_NAME}\\{OUTPUT_DIRECTORY_TRACK}_Information.json")
    ALBUM_INF_PATH = PF.formatPath(f"{os.getcwd()}\\{OUTPUT_DIRECTORY_NAME}\\{OUTPUT_DIRECTORY_ALBUM}_Information.json")

    token: Token
   
    clientID: str
    clientSecret: str
    operationType: str = "SearchArtists"



    def __init__(self, database: DataBase):
        
        self.clientID = os.getenv("CLIENT_ID")
        self.clientSecret = os.getenv("CLIENT_SECRET")
        self.database = database

        self.artistsResearch = None
        self.artistsResearch_Data = {}

        self.tracksResearch = None
        self.tracksResearch_Data = {}
        
        self.loadData()
        self.saveData()
        self.backup = SpotifyInterface_backup(self)


 
        
        self.artistsResearch = ArtistsResearch(12, self.database, self.token, "a", "aaa", SpotifyInterface.ARTISTS_FOLDER, self.artistsResearch_Data)
        totalElement = self.artistsResearch.totalElement()

        with (alive_bar(totalElement, force_tty = True, title = "Artists download: ", bar = 'blocks', length = 50, manual=True, enrich_print=False) as bar1):

            bar1(0.0)

            Terminal.info(f" ------------[ Start searching for artists ]------------")
            self.artistsResearch.start()

            while True:
                percentage = self.artistsResearch.progress()/totalElement
                bar1(percentage)

                if int(percentage) >= 1:
                    break
                else:
                    time.sleep(0.050)
            
            self.artistsResearch.waith_threads()

            with(
                alive_bar(totalElement, force_tty = True, title = "Songs download  : ", bar = 'blocks', length = 50, manual=True, enrich_print=False) as bar2,
                alive_bar(totalElement, force_tty = True, title = "Album download  : ", bar = 'blocks', length = 50, manual=True, enrich_print=False) as bar3
            ):

                bar1(1.0) 
                bar2(0.0)
                bar3(0.0)

        
        

        """Terminal.info(f" ------------ Start searching for tracks and album ------------")
        self.trackResearch = TrackResearch(1, self.database, self.token)
        self.trackResearch.start()
        time.sleep(2)
        self.trackResearch.stop()"""
            
        Terminal.info(f" {'-'*64}")
        self.backup.stop()
           

    def __exit__(self):
        Terminal.info(f" Data saved") 
        self.saveData()

    def __del__(self):
        Terminal.info(f" Data saved") 
        self.saveData()
    
    def loadData(self) -> None:

        path1 = SpotifyInterface.ARTISTS_INF_PATH
        path2 = SpotifyInterface.TRACK_INF_PATH
        path3 = SpotifyInterface.ALBUM_INF_PATH

        if self.fileData in os.listdir(os.getcwd()):
            fullPath = PF.formatPath(os.getcwd() + '\\' + self.fileData)
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
        else:
            Terminal.error(f"File {self.fileData} does not exist")
            self.generateStartingData()


        if os.path.exists(path1):
            with open(path1, 'r') as file:
                data = file.read()
                if data != "": 
                    self.artistsResearch_Data = json.loads(data)["ArtistsResearch"]
                else: 
                    self.artistsResearch_Data = ArtistsResearch.inizializeConfiguration()
        else: 
            self.artistsResearch_Data = ArtistsResearch.inizializeConfiguration()
                    
        if os.path.exists(path2):
            with open(path2, 'r') as file: 
                pass

        if os.path.exists(path3):
            with open(path3, 'r') as file: 
                pass
        
        


        if SpotifyInterface.OUTPUT_DIRECTORY_NAME not in os.listdir(os.getcwd()):
            os.mkdir(SpotifyInterface.OUTPUT_DIRECTORY_NAME)
            Terminal.info(f" Missing Folder: {SpotifyInterface.OUTPUT_DIRECTORY_NAME}")
           
        if SpotifyInterface.OUTPUT_DIRECTORY_ARTISTS not in os.listdir(SpotifyInterface.OUTPUT_DIRECTORY_NAME):
            os.mkdir(SpotifyInterface.ARTISTS_FOLDER)
            Terminal.info(f" Missing Folder: {SpotifyInterface.ARTISTS_FOLDER}")

        if SpotifyInterface.OUTPUT_DIRECTORY_TRACK not in os.listdir(SpotifyInterface.OUTPUT_DIRECTORY_NAME):
            os.mkdir(SpotifyInterface.TRACK_FOLDER)
            Terminal.info(f" Missing Folder: {SpotifyInterface.TRACK_FOLDER}")
        
        if SpotifyInterface.OUTPUT_DIRECTORY_ALBUM not in os.listdir(SpotifyInterface.OUTPUT_DIRECTORY_NAME):
            os.mkdir(SpotifyInterface.ALBUM_FOLDER)
            Terminal.info(f" Missing Folder: {SpotifyInterface.ALBUM_FOLDER}")
        
        Terminal.info(f" {'-'*64}")   

    def generateStartingData(self) -> None:  
        self.saveData()

    def saveData(self) -> None:

        path1 = f"{os.getcwd()}/{SpotifyInterface.OUTPUT_DIRECTORY_NAME}/{SpotifyInterface.OUTPUT_DIRECTORY_ARTISTS}_Information.json"
        path2 = f"{os.getcwd()}/{SpotifyInterface.OUTPUT_DIRECTORY_NAME}/{SpotifyInterface.OUTPUT_DIRECTORY_TRACK}_Information.json"
        path3 = f"{os.getcwd()}/{SpotifyInterface.OUTPUT_DIRECTORY_NAME}/{SpotifyInterface.OUTPUT_DIRECTORY_ALBUM}_Information.json"
        
        data = {}

        data["Operation"] = self.toDict()
        data["Token"] = self.token.toDict()

        with open(os.getcwd() + '\\' + self.fileData, 'w') as file:
            json.dump(data, file,  sort_keys=True, indent = 4)

        data = {}

        if self.artistsResearch == None:
            data["ArtistsResearch"] = self.artistsResearch_Data
        else:
            data["ArtistsResearch"] = self.artistsResearch.toDict()

        
        with open(path1, 'w') as file:
            json.dump(data, file,  sort_keys=True, indent = 4)
        

    def toDict(self):
        output = {}
        output["operationType"] = self.operationType
        return output




def main():
    #db: DataBase  = DataBase(PORT = 5432, IP = "localhost")
    #db.inizializeDatabase()
    
    #print("Element: ", db.getArtistsNumber())
    #db.getArtistAt_by_Followers(0)
    db = None

    driver = SpotifyInterface(db)

if __name__ == '__main__':
    main()


#https://kworb.net/spotify/artists.html