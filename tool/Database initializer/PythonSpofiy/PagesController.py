from pathFormatter import PathFormatter as PF
from Logger import Terminal

import os
import json
import threading

class PageController:

    PAGE_FILE_NAME = "Page[i].json"
    ELEMENT_PER_PAGE = 10000


    def __init__(self, path, lastPage = None):
        self.folderPath = path
        self.pageIndex = lastPage
        self.PageData = {}
        self.pageElement = 0

        self.file_MUTEX = threading.Lock()

        if lastPage != None:
            self.loadPage()
        else:
            self.pageIndex = 0

    def getFullPath(self) -> str:
        str = PF.formatPath(self.folderPath + "\\" + PageController.PAGE_FILE_NAME.replace("[i]", f'{self.pageIndex}'))
        return str

    def saveData(self, data: dict, key: str):
        with self.file_MUTEX:
            if(self.pageElement >= PageController.ELEMENT_PER_PAGE):
                self.savePage()
                self.PageData = {}
                self.pageElement = 0
                self.pageIndex += 1

            self.PageData[key] = data
            self.pageElement += 1
            #self.savePage()


    def savePage(self):
        fileName = self.getFullPath()

        with open(fileName, 'w') as file:
            json.dump(self.PageData, file,  sort_keys = False, indent=4)

    def loadPage(self):
        fileName = self.getFullPath()
        
        if os.path.exists(fileName):
            with open(fileName, 'r') as file:
                data = file.read()
                if data != "":
                    self.PageData = json.loads(data)
                    self.pageElement = len(self.PageData.keys())
        else:
            Terminal.error(f' File {fileName} does not exist')
                
    def toDict(self) -> dict:
        return {"lastPage" : self.pageIndex}