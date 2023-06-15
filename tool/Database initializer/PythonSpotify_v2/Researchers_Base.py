from Logger import Terminal
from dataclasses import dataclass
from datetime import datetime

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
            
        }

        return dict


class DataResearch():
    
    MUTEX = threading.Lock()
    FILE_SETTINGS = "ResearcherSettings.json"
    PAGE_FILE_NAME = "Page[i].json"
    

    def __init__(self):
        self.threads = []
        self.informationsFile: dict

    def toDict(self):
        dict = {
            
        }

        return dict

    def loadSettings(self, path:str) -> dict:
        if os.path.exists(path):
            with open(path, 'r') as file:
                data = file.read()

                if data != "":
                    informationsFile = json.loads(data)
                    return informationsFile
        else:
            with open(path, 'w') as file:
                pass
            return None

    def saveSettings(self, path:str, data:dict) -> None:
        with open(path, 'w') as file:
            json.dump(data, file, indent=4)

    def savePage(self, pageIndex, pageData, path):
        fileName = path + "/" + DataResearch.PAGE_FILE_NAME.replace("[i]", f'{pageIndex}')
        with open(fileName, 'w') as file:
            json.dump(pageData, file,  sort_keys = True, indent=4)


    def loadPage(self, pageIndex, path) -> dict:
        fileName = path + "/" + DataResearch.PAGE_FILE_NAME.replace("[i]", f'{pageIndex}')

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

        def __init__(self, thNumber):
            threading.Thread.__init__(self)
            self.thNumber = thNumber
            self.lastRequest:str = ""
            self.lastResponse_json = None
            self.lastResponse = None
            self.running = True
        
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
                self.lastResponse = None
                self.lastResponse_json = None
                return False
            else:
                self.lastResponse = response
                self.lastResponse_json = json.loads(self.lastResponse.content.decode("utf-8"))
                return True
            
        def writeJson(self, pageData, fileName, path):
            with open(path + f"/{fileName}.json", 'w') as file:
                json.dump(pageData, file,  sort_keys = False, indent=4)
