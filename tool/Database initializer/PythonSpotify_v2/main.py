from SpotifyUser import SpotifyUser
from researchers import *
from alive_progress import alive_bar
from dotenv import load_dotenv

class SpotifyInterface:

 
    fileData: str = "InterfaceData.json"
    OUTPUT_DIRECTORY_NAME: str = "Output"
    OUTPUT_DIRECTORY_ARTISTS: str = "Artists"
    OUTPUT_DIRECTORY_TRACK: str = "Tracks"
    OUTPUT_DIRECTORY_ALBUM: str = "Album"

    ACCOUNT_NUMBER:int = 4
  
    ARTISTS_FOLDER: str = PF.formatPath(f"{os.getcwd()}/{OUTPUT_DIRECTORY_NAME}/{OUTPUT_DIRECTORY_ARTISTS}")
    TRACK_FOLDER: str = PF.formatPath(f"{os.getcwd()}/{OUTPUT_DIRECTORY_NAME}/{OUTPUT_DIRECTORY_TRACK}")
    ALBUM_FOLDER: str = PF.formatPath(f"{os.getcwd()}/{OUTPUT_DIRECTORY_NAME}/{OUTPUT_DIRECTORY_ALBUM}")

    ARTISTS_INF_PATH = PF.formatPath(f"{os.getcwd()}\\{OUTPUT_DIRECTORY_NAME}\\{OUTPUT_DIRECTORY_ARTISTS}_Information.json")
    TRACK_INF_PATH = PF.formatPath(f"{os.getcwd()}\\{OUTPUT_DIRECTORY_NAME}\\{OUTPUT_DIRECTORY_TRACK}_Information.json")
    ALBUM_INF_PATH = PF.formatPath(f"{os.getcwd()}\\{OUTPUT_DIRECTORY_NAME}\\{OUTPUT_DIRECTORY_ALBUM}_Information.json")


    def __init__(self, database = None):
        
        self.database = database

        self.artistsResearch = None
        self.artistsResearch_Data = {}

        self.tracksResearch = None
        self.tracksResearch_Data = {}

        self.Accounts = []

        for i in range(13):
            self.Accounts.append(SpotifyUser(i + 38, True))

        for i in self.Accounts:
            i.getThread().join()

        Terminal.info(f" ------------ Start searching for tracks and album ------------")
        track_album_Research = Track_and_Album_Research(self.Accounts, SpotifyInterface.ARTISTS_FOLDER, SpotifyInterface.TRACK_FOLDER, SpotifyInterface.ALBUM_FOLDER)
        totalElement = track_album_Research.total_ID

        with(
            alive_bar(totalElement, force_tty = True, title = "Songs e Album download  : ", bar = 'blocks', length = 50, manual=True, enrich_print=False) as bar2
            #alive_bar(totalElement, force_tty = True, title = "Album download  : ", bar = 'blocks', length = 50, manual=True, enrich_print=False) as bar3
        ):  
            bar2(0.0)
            track_album_Research.start()

            while True:
                percentage = track_album_Research.processed_ID_count/totalElement
                bar2(percentage)
                key = list(track_album_Research.Arists_ID.keys())[track_album_Research.currentLetterIndex]
                
                #for k in key:
                bar2.text(f"|| Letter: {key}  index: {track_album_Research.LetterElement}/{len(track_album_Research.Arists_ID[key])}")
                #    break
                #bar3(percentage)
          

                if int(percentage) >= 1:
                    break
                else:
                    time.sleep(0.020)

        
        track_album_Research.waith_threads()


def main():

    spotify = SpotifyInterface()
    

    

    


if __name__ == '__main__':
    main()