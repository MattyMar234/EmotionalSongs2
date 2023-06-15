import platform
import os

class PathFormatter():

    @staticmethod
    def formatPath(path: str):
        systemType = platform.system()

        if systemType == "Windows":
            return path.replace("/", "\\")
        elif systemType == "Linux":
            return path.replace("\\", "/")
    
    @staticmethod
    def getSplitterChar() -> str:
        systemType = platform.system()

        if systemType == "Windows":
            return "\\"
        elif systemType == "Linux":
            return "/"
        
