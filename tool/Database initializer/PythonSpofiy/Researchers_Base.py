from Logger import Terminal
from dataclasses import dataclass
from datetime import datetime
from DatabaseInterface import DataBase

import threading
import time
import requests
import base64
import json
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
    


    def __init__(self, threadNumber:int, database: DataBase, token: Token):
        self.token = token
        self.threadNumber = threadNumber
        self.database = database
        self.threads = []

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
        out = {}
        return out