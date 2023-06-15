import json
import requests
import time
from requests_html import HTMLSession
from bs4 import BeautifulSoup
from Logger import *
import threading

from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import TimeoutException
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium import webdriver

CHROME_DRIVER_PATH = 'C:\\Users\\Utente\\Desktop\\EmotionalSongs2\\tool\\Database initializer\\PythonSpofiy\\Selenium_web_driverchromedriver_v112.exe'
FIREFOX = 'C:\\Users\\Utente\\Desktop\\EmotionalSongs2\\tool\\Database initializer\\PythonSpofiy\\geckodriver.exe'


class Chrome_Driver(webdriver.Chrome):

    def __init__(self, driver_path: str):

        options = Options()
        #options.add_experimental_option('detach', True)
        #options.add_argument("--headless")
        options.add_argument("--enable-javascript")
        options.add_argument("--mute-audio")
        options.add_argument("--no-sandbox")
        options.add_argument("--disable-dev-shm-usage")
        options.add_argument('--disable-extensions')
        options.add_argument('--disable-gpu')
        options.add_argument("--log-level=OFF")
        options.add_argument("--log-level=3")

        prefs = {"profile.default_content_setting_values.notifications" : 2}
        options.add_experimental_option("prefs",prefs)

        super().__init__(executable_path = driver_path, options = options)

    def stop(self):
        self.quit()

class FireFox_Driver(webdriver.Firefox):

    def __init__(self, driver_path: str):

        options = Options()
        #options.add_experimental_option('detach', True)
        #options.add_argument("--headless")
        options.add_argument("--enable-javascript")

        prefs = {"profile.default_content_setting_values.notifications" : 2}
        options.add_experimental_option("prefs",prefs)

        super().__init__(executable_path = driver_path)

    def stop(self):
        self.quit()

class SpotifyScraper(object):

    #https://open.spotify.com/artist/66CXWjxzNUsdJxJ2JdwvnR/discography/album
    URL = "https://open.spotify.com/artist/[id]/discography/album"
    HEADERS ={"User-Agent": "Mozilla/5.0 (iPad; CPU OS 12_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148"}
    BASE_URL = "https://open.spotify.com"
    MUTEX1 = threading.Lock()
    DRIVER = None
    DRIVER2 = None


    def __init__(self):
        
        with SpotifyScraper.MUTEX1:
            if SpotifyScraper.DRIVER == None:
                SpotifyScraper.DRIVER = Chrome_Driver(CHROME_DRIVER_PATH)
                #SpotifyScraper.DRIVER = FireFox_Driver(FIREFOX)

                #SpotifyScraper.DRIVER2 = Chrome_Driver(CHROME_DRIVER_PATH)
                SpotifyScraper.DRIVER.get(SpotifyScraper.BASE_URL)
                WebDriverWait(SpotifyScraper.DRIVER, 20).until(EC.element_to_be_clickable((By.XPATH,'//*[@id="onetrust-accept-btn-handler"]'))).click()

                
            SpotifyScraper.DRIVER.execute_script("window.open('');")
            self.window = SpotifyScraper.DRIVER.window_handles[-1]
            #print(self.window)
        

        #print(SpotifyScraper.DRIVER.window_handles)
        #driver.window_handles[1]

    def __del__(self):
        if SpotifyScraper.DRIVER != None:
            with SpotifyScraper.MUTEX1:
                currentWindow = SpotifyScraper.DRIVER.current_window_handle
                self.SetMyWindow()
                SpotifyScraper.DRIVER.close()
                

    def SetMyWindow(self):
        currentWindow = SpotifyScraper.DRIVER.current_window_handle   
        if self.window != currentWindow:
            SpotifyScraper.DRIVER.switch_to.window(self.window)

    def durationToMilliseconds(self, duration: str) -> int:
        try:
            componenti = duration.split(':')
            durata_in_millisecondi = sum(int(c) * (60 ** i) * 1000 for i, c in enumerate(reversed(componenti)))
            return durata_in_millisecondi
        except Exception:
            return 0

    def getArtist_albums_and_tracks(self, artistID) -> dict:
        
        
        #response = self.s.get(SpotifyScraper.URL.replace("[id]", artistID), headers = SpotifyScraper.HEADERS, timeout=5)
        #response = self.s.get(SpotifyScraper.URL.replace("[id]", artistID))
        with SpotifyScraper.MUTEX1:
            self.SetMyWindow()
            SpotifyScraper.DRIVER.get(SpotifyScraper.URL.replace("[id]", artistID))

        loading = True
        start_time = time.time()

        while loading:
            with SpotifyScraper.MUTEX1:
                self.SetMyWindow()
                element= None
                try:
                    element = SpotifyScraper.DRIVER.find_element(By.CLASS_NAME, "hyHkMMynp3uUsmEtOkSN")
                except:
                    pass
                end_time = time.time()

            if element == None:
                elapsed_time = end_time - start_time
                if elapsed_time >= 12:
                    Terminal.error(" TimeOut")
                    SystemExit.code(1)
                    
            elif element != None:
                loading = False

        with SpotifyScraper.MUTEX1:
            self.SetMyWindow()
            body = SpotifyScraper.DRIVER.find_element(By.CSS_SELECTOR,'body')

        soup: BeautifulSoup = None
        html: None
        fullyloaded = False

        while not fullyloaded:
            fullyloaded = True
            with SpotifyScraper.MUTEX1:
                self.SetMyWindow()
                body.click()
                body.send_keys(Keys.END)
                html = SpotifyScraper.DRIVER.page_source

            soup = BeautifulSoup(str(html), 'html.parser')
            tables = soup.find_all('div', class_ = "JUa6JJNj7R_Y3i4P8YUX")
            
            for table in tables:
                total = 0
                rows = table.find_all('div', class_ = "h4HgbO_Uu1JYg5UGANeQ wTUruPetkKdWAR1dd6w4")
                
                if len(rows) != 0:
                    total +=1

            if total < len(rows)/2:
                fullyloaded = False 
                    

        
        
        artist_albums = {}
        artist_tracks = {}

        albumsHeader = soup.find_all('div', class_ = "fEvxx8vl3zTNWsuC8lpx")
        tables = soup.find_all('div', class_ = "JUa6JJNj7R_Y3i4P8YUX")


        for header,table in zip(albumsHeader,tables):
            
            TitleElement = header.find('span', class_='Type__TypeElement-sc-goli3j-0')
            albumElement = header.find_all('span', class_='Type__TypeElement-sc-goli3j-0 eMzEmF RANLXG3qKB61Bh33I0r2')
            albumImage   = header.find('div', class_='CmkY1Ag0tJDfnFXbGgju n1EzbHQahSKztskTUAm3')
            
            rows = table.find_all('div', class_ = "h4HgbO_Uu1JYg5UGANeQ wTUruPetkKdWAR1dd6w4")
           
            album_tracks_ID = []

            #estraggo le informazioni di ogni canzone nella tabella
            for row in rows:
                songName = row.find('div', class_='iCQtmPqY0QvkumAOuCjr').find('a').get_text(strip=True)
                songLink = row.find('div', class_='iCQtmPqY0QvkumAOuCjr').find('a')['href']
                songID = row.find('div', class_='iCQtmPqY0QvkumAOuCjr').find('a')['href'].split('/')[-1]
                duration = row.find('div', class_='Type__TypeElement-sc-goli3j-0 fjvaLo Btg2qHSuepFGBG6X0yEN').get_text(strip=True)
                Autorslink = row.find('span', class_='Type__TypeElement-sc-goli3j-0 fjvaLo rq2VQ5mb9SDAFWbBIUIn standalone-ellipsis-one-line').find_all('a')

                trackAutors_ID = []

                for link in Autorslink:
                    href = link.get('href')
                    trackAutors_ID.append(href.split('/')[-1])

                trackData = {
                    #'album_ID'    : TitleElement.find('a')['href'].split('/')[-1],
                    'artists_ID'  : trackAutors_ID,
                    'duration_ms' : self.durationToMilliseconds(duration),
                    'spotify_url' : f'{SpotifyScraper.BASE_URL + songLink}',
                    'id'          : songID,
                    'name'        : songName,
                    'images'        : [{
                        "height": 300,
                        "width" : 300,
                        "url"   : albumImage.find('img')['src']
                    }]    
                }
                
                album_tracks_ID.append(songID)

                if songID not in artist_tracks:
                    artist_tracks[songID] = trackData


            albumData = {
                'id'            : TitleElement.find('a')['href'].split('/')[-1],
                'spotify_url'   : f'{SpotifyScraper.BASE_URL + TitleElement.find("a")["href"]}',
                'type'          : albumElement[0].get_text(strip=True),
                'release_date'  : albumElement[1].get_text(strip=True),
                'element'       : len(rows),
                'name'          : TitleElement.get_text(strip=True),
                'artist'        : artistID,
                'album_tracks'  : album_tracks_ID,
                'images'        : [{
                    "height": 300,
                    "width" : 300,
                    "url"   : albumImage.find('img')['src']
                }]    
            }

            id = TitleElement.find('a')['href'].split('/')[-1]
            if id not in artist_albums:
                artist_albums[id] = albumData

            
        return artist_albums, artist_tracks
        

        """with open('results1.json', 'w') as file:
            json.dump(artist_albums, file, indent=4)

        with open('results2.json', 'w') as file:
            json.dump(artist_tracks, file, indent=4)"""
        
    
    def create_Spotify_account(self, email, password):
        SpotifyScraper.DRIVER.get("https://www.spotify.com/it/signup")
        time.sleep(8)

        SpotifyScraper.DRIVER.find_element(By.XPATH, "/html/body/div[1]/main/div/div/form/div[1]/input").send_keys(email)
        SpotifyScraper.DRIVER.find_element(By.XPATH, "/html/body/div[1]/main/div/div/form/div[2]/div[3]/input").click().send_keys(password)
        SpotifyScraper.DRIVER.find_element(By.XPATH, "/html/body/div[1]/main/div/div/form/div[3]/input").click().send_keys("matty")
        
        SpotifyScraper.DRIVER.find_element(By.XPATH, "/html/body/div[1]/main/div/div/form/div[4]/div[2]/div[1]/div/input").click().send_keys("2000")
        SpotifyScraper.DRIVER.find_element(By.XPATH, "/html/body/div[1]/main/div/div/form/div[4]/div[2]/div[2]/div/div[2]/select").click().send_keys("a\n")
        SpotifyScraper.DRIVER.find_element(By.XPATH, "/html/body/div[1]/main/div/div/form/div[4]/div[2]/div[3]/div/input").send_keys("20")

        SpotifyScraper.DRIVER.find_element(By.XPATH, "/html/body/div[1]/main/div/div/form/fieldset/div/div[5]/label/span[1]").click()
        SpotifyScraper.DRIVER.find_element(By.XPATH, "/html/body/div[1]/main/div/div/form/div[7]/div/label/span[1]").click()
        

    def make_API_registration(self):
        SpotifyScraper.DRIVER.get(SpotifyScraper.URL.replace("[id]", artistID))



if __name__ == "__main__":
    
    from requests_html import HTMLSession
    import urllib.request
    url = "https://open.spotify.com/artist/66CXWjxzNUsdJxJ2JdwvnR"

    """ session = HTMLSession()

        response = session.get(url)
        response.html.render()

        print(response.html.html)

        soup = BeautifulSoup(response.html.html, 'html.parser')
    """
    file = urllib.request.urlopen(url, headers={"User-Agent": "Mozilla/5.0 (iPad; CPU OS 12_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148"})
    content = file.read()
    print(content)
    #print(soup.find_all(By.CLASS_NAME, "mMx2LUixlnN_Fu45JpFB rkw8BWQi3miXqtlJhKg0 Yn2Ei5QZn19gria6LjZj"))