import psycopg2


class DataBase:

    connection = None
    cursor = None

    def __init__(self, PORT, IP):
        self.connection = psycopg2.connect(host=IP, port=PORT, password="admin", dbname="EmotionalSongs", user = "postgres")
        self.cursor = self.connection.cursor()

    def executeCommand(self):
        self.connection.commit()

    def inizializeDatabase(self):

        self.cursor.execute("""
            DROP TABLE Autor
        """)
        self.cursor.execute("""
            CREATE TABLE IF NOT EXISTS Autor (
                ID VARCHAR(32)              PRIMARY KEY,
                name VARCHAR(60)            NOT NULL,
                spotify_url VARCHAR(64)     NOT NULL,
                followers int
            );
        """)
        self.executeCommand()


    def addAutor(self, data: dict):
        try:
            self.cursor.execute(f"""
                INSERT INTO Autor (ID, name, spotify_url, followers)
                VALUES ('{data['id']}', '{data['name']}', '{data['spotify_url']}', {data['followers']})
            ;""")

                #WHERE NOT EXISTS (SELECT * FROM Autor WHERE ID = '{data['id']}');
            self.executeCommand()
        except:
            pass


    def __del__(self):
        if self.cursor is not None:
            self.cursor.close()

        if self.connection is not None:
            self.connection.close()