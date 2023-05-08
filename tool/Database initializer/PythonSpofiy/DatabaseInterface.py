import psycopg2
from colorama import Fore
from Logger import Terminal





class DataBase:


     # ============== tabelle associazioni ============== #
    NOME_TABELLA_ASSOCIAZIONE_ALBUM_CANZONE = "Associazione_ALBUM_CANZONE"
    NOME_TABELLA_ASSOCIAZIONE_ARISTA_CANZONE = "Associazione_ARTISTS_SONGS"
    NOME_TABELLA_ASSOCIAZIONE_GENERI_ARTISTA = "Associazione_Generi_Musicali_artista"



    # ============== tabella artisti ============== #
    NOME_TABELLA_ARTISTI = "Artist"
    
    #nomi colenne tabella artisti
    ARTISTI_COLONNA_NOME = "Name"
    ARTISTI_COLONNA_ID = "ID"
    ARTISTI_COLONNA_LINK = "Spotify_URL"
    ARTISTI_COLONNA_FOLLOW = "Followers"

    # ============== tabella album ============== #
    NOME_TABELLA_ALBUM = "Album"
    
    #nomi colenne tabella artisti
    ALBUM_COLONNA_NOME = "Name"
    ALBUM_COLONNA_ID = "ID"
    ALBUM_COLONNA_LINK = "Spotify_URL"
    ALBUM_COLONNA_ELEMENT = "Element"
    ALBUM_COLONNA_DATE = "ReleaseDate"
    ALBUM_COLONNA_TYPE = "Type"

   

    # ============== tabella canzoni ============== #
    NOME_TABELLA_CANZONI = "Song"

    #nomi colonne tabella Canzoni
    CANZONI_COLONNA_DURATION = "Duration_ms"
    CANZONI_COLONNA_LINK = "Spotify_URL"
    CANZONI_COLONNA_ID = "ID"
    CANZONI_COLONNA_NOME = "Title"
    CANZONI_COLONNA_POPULARITY = "popularity"


    # ============== tabelle immagini ============== #
    NOME_TABELLA_IMMAGINI_CANZONI = "SongImage"
    NOME_TABELLA_IMMAGINI_ALBUM = "AlbumImage"
    NOME_TABELLA_IMMAGINI_ARTISTA = "ArtistImage"

    IMMAGINI_LINK = "Link"
    IMMAGINI_COLONNA_SIZE = "Size"
    IMMAGINI_COLONNA_ITEM_ID = "ItemID"
    


    connection = None
    cursor = None

    def __init__(self, PORT, IP):
        self.connection = psycopg2.connect(host=IP, port=PORT, password="admin", dbname="EmotionalSongs", user = "postgres")
        self.cursor = self.connection.cursor()
        

    def executeCommand(func):
        def wrapper(self):
            func()
            self.connection.commit()
        return wrapper


    def inizializeDatabase(self, erase = False):
        try:
            if erase:
                Terminal.info(" Clearing database...")
                self.cursor.execute(f"""DROP TABLE IF EXISTS {self.NOME_TABELLA_ARTISTI}; """)
                self.cursor.execute(f"""DROP TABLE IF EXISTS {self.NOME_TABELLA_CANZONI}; """)

                #immagini
                self.cursor.execute(f"""DROP TABLE IF EXISTS {self.NOME_TABELLA_IMMAGINI_CANZONI}; """)
                self.cursor.execute(f"""DROP TABLE IF EXISTS {self.NOME_TABELLA_IMMAGINI_ALBUM}; """)
                self.cursor.execute(f"""DROP TABLE IF EXISTS {self.NOME_TABELLA_IMMAGINI_ARTISTA}; """)
                
                
                Terminal.success("database cleared successfully")

            #================================================================================#
            #=================================== [ASSOCIAZIONI] ==================================#
            #================================================================================#
            Terminal.info_Notln(f" creating table {self.NOME_TABELLA_ASSOCIAZIONE_ALBUM_CANZONE}: ")

            self.cursor.execute(f"""
                CREATE TABLE IF NOT EXISTS {self.NOME_TABELLA_ASSOCIAZIONE_ALBUM_CANZONE} (
                    {self.CANZONI_COLONNA_ID}   VARCHAR(32)     ,
                    {self.ALBUM_COLONNA_ID}     VARCHAR(32)     ,

                    PRIMARY KEY ({self.CANZONI_COLONNA}, {self.ALBUM_COLONNA_ID})   
                );
            """)
            print(f'{Fore.GREEN}Done{Fore.RESET}')

            #-------------------------------------------------------------------------------#

            Terminal.info_Notln(f" creating table {self.NOME_TABELLA_ASSOCIAZIONE_ARISTA_CANZONE}: ")
            self.cursor.execute(f"""
                CREATE TABLE IF NOT EXISTS {self.NOME_TABELLA_ASSOCIAZIONE_ALBUM_CANZONE} (
                    {self.CANZONI_COLONNA_ID}   VARCHAR(32)     ,
                    {self.ARTISTI_COLONNA_ID}   VARCHAR(32)     ,

                    PRIMARY KEY ({self.CANZONI_COLONNA}, {self.ARTISTI_COLONNA_ID})   
                );
            """)
            print(f'{Fore.GREEN}Done{Fore.RESET}')


            #-------------------------------------------------------------------------------#

            Terminal.info_Notln(f" creating table {self.NOME_TABELLA_ASSOCIAZIONE_GENERI_ARTISTA}: ")
            self.cursor.execute(f"""
                CREATE TABLE IF NOT EXISTS {self.NOME_TABELLA_ASSOCIAZIONE_ALBUM_CANZONE} (
                    {self.CANZONI_COLONNA_ID}   VARCHAR(32)     ,
                    {self.ARTISTI_COLONNA_ID}   VARCHAR(32)     ,

                    PRIMARY KEY ({self.CANZONI_COLONNA}, {self.ARTISTI_COLONNA_ID})   
                );
            """)
            print(f'{Fore.GREEN}Done{Fore.RESET}')



            #================================================================================#
            #=================================== [ARISTA] ==================================#
            #================================================================================#

            Terminal.info_Notln(f" creating table {self.NOME_TABELLA_ARTISTI}: ")
            self.cursor.execute(f"""
                CREATE TABLE IF NOT EXISTS {self.NOME_TABELLA_ARTISTI} (
                    {self.ARTISTI_COLONNA_ID}   VARCHAR(32)     PRIMARY KEY,
                    {self.ARTISTI_COLONNA_NOME} VARCHAR(60)     NOT NULL,
                    {self.ARTISTI_COLONNA_LINK} VARCHAR(64)     NOT NULL,
                    {self.ARTISTI_COLONNA_FOLLOW} Int
                );
            """)
            print(f'{Fore.GREEN}Done{Fore.RESET}')

            #================================================================================#
            #=================================== [CANZONE] ==================================#
            #================================================================================#

            Terminal.info_Notln(f" creating table {self.NOME_TABELLA_CANZONI}: ")
            self.cursor.execute(f"""
                CREATE TABLE IF NOT EXISTS {self.NOME_TABELLA_CANZONI} (
                    {self.CANZONI_COLONNA_ID}   VARCHAR(32)     PRIMARY KEY,
                    {self.CANZONI_COLONNA_NOME} VARCHAR(60)     NOT NULL,
                    {self.CANZONI_COLONNA_LINK} VARCHAR(64)     NOT NULL
                );
            """)
            print(f'{Fore.GREEN}Done{Fore.RESET}')


            #================================================================================#
            #==================================== [ALBUM] ===================================#
            #================================================================================#

            Terminal.info_Notln(f" creating table {self.NOME_TABELLA_ALBUM}: ")
            self.cursor.execute(f"""
                CREATE TABLE IF NOT EXISTS {self.NOME_TABELLA_CANZONI} (
                    {self.ALBUM_COLONNA_ID}         VARCHAR(32)     PRIMARY KEY,
                    {self.ALBUM_COLONNA_ELEMENT}    INT             NOT NULL,
                    {self.ALBUM_COLONNA_NOME}       VARCHAR(40)     NOT NULL,
                    {self.ALBUM_COLONNA_DATE}       VARCHAR(20)     NOT NULL,
                    {self.ALBUM_COLONNA_TYPE}       VARCHAR(20)     NOT NULL,
                    {self.ALBUM_COLONNA_LINK}       VARCHAR(60)     NOT NULL
                );
            """)
            print(f'{Fore.GREEN}Done{Fore.RESET}')

            #================================================================================#
            #================================== [IMMAGINI] ==================================#
            #================================================================================#

            Terminal.info_Notln(f" creating table {self.NOME_TABELLA_IMMAGINI_CANZONI}: ")
            self.cursor.execute(f"""
                CREATE TABLE IF NOT EXISTS {self.NOME_TABELLA_IMMAGINI_CANZONI} (
                    {self.IMMAGINI_LINK}                VARCHAR(60)     ,
                    {self.IMMAGINI_COLONNA_SIZE}        VARCHAR(20)     NOT NULL,
                    {self.IMMAGINI_COLONNA_ITEM_ID}     VARCHAR(32)     NOT NULL,

                    PRIMARY KEY ({self.IMMAGINI_COLONNA_SIZE}, {self.IMMAGINI_COLONNA_ITEM_ID})
                );""")
            
            print(f'{Fore.GREEN}Done{Fore.RESET}')

            #------------------------------------------------------------------------------#

            Terminal.info_Notln(f" creating table {self.NOME_TABELLA_IMMAGINI_ALBUM}: ")
            self.cursor.execute(f"""
                CREATE TABLE IF NOT EXISTS {self.NOME_TABELLA_IMMAGINI_ALBUM} (
                    {self.IMMAGINI_LINK}                VARCHAR(60)     ,
                    {self.IMMAGINI_COLONNA_SIZE}        VARCHAR(20)     NOT NULL,
                    {self.IMMAGINI_COLONNA_ITEM_ID}     VARCHAR(32)     NOT NULL,

                    PRIMARY KEY ({self.IMMAGINI_COLONNA_SIZE}, {self.IMMAGINI_COLONNA_ITEM_ID})
                );""")
            
            print(f'{Fore.GREEN}Done{Fore.RESET}')

            #------------------------------------------------------------------------------#

            Terminal.info_Notln(f" creating table {self.NOME_TABELLA_IMMAGINI_ARTISTA}: ")
            self.cursor.execute(f"""
                CREATE TABLE IF NOT EXISTS {self.NOME_TABELLA_IMMAGINI_ARTISTA} (
                    {self.IMMAGINI_LINK}                VARCHAR(60)     ,
                    {self.IMMAGINI_COLONNA_SIZE}        VARCHAR(20)     NOT NULL,
                    {self.IMMAGINI_COLONNA_ITEM_ID}     VARCHAR(32)     NOT NULL,

                    PRIMARY KEY ({self.IMMAGINI_COLONNA_SIZE}, {self.IMMAGINI_COLONNA_ITEM_ID})
                );""")
            
            print(f'{Fore.GREEN}Done{Fore.RESET}')

            self.executeCommand()

        except Exception as e:
            Terminal.error(f" {e}")

        
    @executeCommand
    def addArtist(self, data: dict):
        try:
            self.cursor.execute(f"""
                INSERT INTO {self.NOME_TABELLA_ARTISTI} ({self.ARTISTI_COLONNA_ID}, {self.ARTISTI_COLONNA_NOME}, {self.ARTISTI_COLONNA_LINK}, {self.ARTISTI_COLONNA_FOLLOW})
                VALUES ('{data['id']}', '{data['name']}', '{data['spotify_url']}', {data['followers']});
            """)
        except Exception as e:
            pass

    @executeCommand
    def addArtist(self, data: dict):
        try:
            self.cursor.execute(f"""
                INSERT INTO {self.NOME_TABELLA_ARTISTI} ({self.ARTISTI_COLONNA_ID}, {self.ARTISTI_COLONNA_NOME}, {self.ARTISTI_COLONNA_LINK}, {self.ARTISTI_COLONNA_FOLLOW})
                VALUES ('{data['id']}', '{data['name']}', '{data['spotify_url']}', {data['followers']});
            """)
        except Exception as e:
            pass

        
        self.executeCommand()

    def getArtistsNumber(self) -> int:
        self.cursor.execute(f"""SELECT COUNT(*) FROM {self.NOME_TABELLA_ARTISTI}""")
        return self.cursor.fetchall()[0][0]
    

    def getArtistAt_order_by_Followers(self, index: int):
        if index >= self.getArtistsNumber():
            return None

        self.cursor.execute(f"""
            SELECT * FROM {self.NOME_TABELLA_ARTISTI} 
            ORDER BY {self.ARTISTI_COLONNA_FOLLOW} DESC
            LIMIT 1 OFFSET {index};
        """)

        return RecordsConversion.ArtistToDict(self.cursor.fetchall()[0])
       

    def getArtistAt_order_by_ID(self, index: int):
        if index >= self.getArtistsNumber():
            return None

        self.cursor.execute(f"""
            SELECT * FROM {self.NOME_TABELLA_ARTISTI} 
            ORDER BY {self.ARTISTI_COLONNA_ID} 
            LIMIT 1 OFFSET {index};
        """)

        return RecordsConversion.ArtistToDict(self.cursor.fetchall()[0])

    
  



    def __del__(self):
        if self.cursor is not None:
            self.cursor.close()

        if self.connection is not None:
            self.connection.close()

class RecordsConversion:
    def __init__():
        ...

    @staticmethod
    def ArtistToDict(record) -> dict:
        dict = {
            'id': record[0],
            'name': record[1],
            'spotify_url': record[2],
            'followers': record[3]
        }
        return dict