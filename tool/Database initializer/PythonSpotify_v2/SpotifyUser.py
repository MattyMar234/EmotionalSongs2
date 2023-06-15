import base64
import json
import threading
import requests
import time
import os
from Logger import Terminal
from datetime import datetime
from bs4 import BeautifulSoup
from requests_html import HTMLSession
from password_generator import PasswordGenerator
from mailtm import Email

from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import TimeoutException
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium import webdriver
from selenium.webdriver.support.ui import Select
from fp.fp import FreeProxy


CHROME_DRIVER_PATH = 'C:\\Users\\Utente\\Desktop\\EmotionalSongs2\\tool\\Database initializer\\PythonSpofiy_v2\\Selenium_web_driverchromedriver_v112.exe'

class ProxyFinder:

    URL = "https://www.proxydocker.com/"
    PROXYs:dict = {}
    PROXYs_KEY = []


    def __init__(self):
        pass

    @staticmethod
    def getProxy_at(index):
        if not ProxyFinder.PROXYs[ProxyFinder.PROXYs_KEY[index]]:
            ProxyFinder.PROXYs[ProxyFinder.PROXYs_KEY[index]] = True
            return ProxyFinder.PROXYs_KEY[index]
        else:
            return None
        
        

    @staticmethod
    def changeProxy(): 
        count = 0
        for f in ProxyFinder.PROXYs_KEY:
            if not ProxyFinder.PROXYs[f]:
                ProxyFinder.PROXYs[f] = True
                return count
            count += 1
    
    
    @staticmethod
    def freeProxy_at(index):
        ProxyFinder.PROXYs[ProxyFinder.PROXYs_KEY[index]] = False
            
    @staticmethod
    def getProxyCount():
        return len(ProxyFinder.PROXYs_KEY)

    @staticmethod
    def getAvailabeProxys():
        PROXYs = FreeProxy(https=True, timeout=2, elite=True).get_proxy_list(True)
        print(PROXYs)

        ProxyFinder.PROXYs_KEY = PROXYs
        
        for p in PROXYs:
            ProxyFinder.PROXYs[p] = False
        
ProxyFinder.getAvailabeProxys()


class Chrome_Driver(webdriver.Chrome):

    def __init__(self, driver_path: str):

        PROXY = "134.209.29.120:3128" # IP:PORT or HOST:PORT

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
        options.add_argument('--disable-dev-shm-usage')
        options.add_argument("disable-infobars")
        options.add_argument("--incognito")
        options.add_argument("--disable-blink-features=AutomationControlled")

        #options.add_argument('--proxy-server=http://%s' % PROXY)
        
        prefs = {"profile.default_content_setting_values.notifications" : 2}
        options.add_experimental_option("prefs",prefs)

        super().__init__(executable_path = driver_path, options = options)

    def stop(self):
        self.quit()


class SpotifyAPI_Token:
    pass

class SpotifyUser():

    FOLDER = os.getcwd() + "\\SpotifyAccount"
    SAVE_MUTEX = threading.Lock()

    def __init__(self, number, static = False):
        
        self.MUTEX = threading.Lock()
        self.accountNumber = number
        self.filePath = f"{SpotifyUser.FOLDER}\\Account{self.accountNumber}.json"
        self.StaticAccount_filePath = f"{SpotifyUser.FOLDER}\\StaticAccount{self.accountNumber}.json"
        self.Email = None
        self.password = None
        self.emailFechted_at = None
        self.ClientID = None
        self.ClientSecret = None
        self.ready = False
        self.useStaticAccount = static

        self.lastAPI_call = time.time()
        self.API_call_delay = 0.100 #(60ms)
        self.API_call_count = 0

        self.API_token:str = None
        self.tokenFechted_at = None

        self.StartingThread = threading.Thread(target=self.loadData)
        self.StartingThread.start()

        self.proxyFinder = ProxyFinder()
        self.ProxyIndex = self.proxyFinder.changeProxy()

        print(f"User{self.accountNumber} proxy server: {self.proxyFinder.getProxy_at(self.ProxyIndex)}")

        #self.ProxyAddress = self.proxyFinder.(self.accountNumber).split(":")[0]
        #self.ProxyPort = self.proxyFinder.getProxy_at(self.accountNumber).split(":")[1]

    def changeProxy(self):
        self.ProxyIndex = self.proxyFinder.changeProxy()
        

    def getProxy(self) -> dict:
        temp = {
            "http": self.proxyFinder.getProxy_at(self.ProxyIndex),
            "https": self.proxyFinder.getProxy_at(self.ProxyIndex)
        }
        #print(temp)
        return temp

    def get_auth_headers(self):

        with self.MUTEX:
            if not self.tokenIsValid():
                self.getNewToken()

            dt = time.time() - self.lastAPI_call

            """if dt < self.API_call_delay:
                time.sleep(self.API_call_delay - dt)
                self.lastAPI_call = time.time()"""
                
        with self.MUTEX:
            time.sleep(0.180)
            self.API_call_count += 1
        #time.sleep(0.200)
        return {"Authorization": "Bearer " + self.API_token}
    

    def accountReady(self) -> bool:
        return self.ready
    
    def getThread(self):
        return self.StartingThread
    

    def getAPI_token(self) -> str:
        if not self.tokenIsValid():
            self.getNewToken()
        return self.API_token

    def tokenIsValid(self):
        if self.API_token == None:
            return False
        
        diff = datetime.now() - self.tokenFechted_at
        minutes_passed = (diff.total_seconds() + 20 ) / 60
        
        if minutes_passed >= 60:
            return False
        
        return True

    def getNewToken(self) -> bool:
        auth_string = self.ClientID + ":" + self.ClientSecret
        auth_bytes = auth_string.encode("utf-8")
        auth_base64 = str(base64.b64encode(auth_bytes), "utf-8")

        Terminal.info(f" Request new Token")

        URL = 'https://accounts.spotify.com/api/token'

        headers = {
            "Authorization": "Basic " + auth_base64,
            "Content-Type": "application/x-www-form-urlencoded"
        }

        data = {"grant_type": "client_credentials"}

        response = requests.post(URL, headers = headers, data = data,proxies = self.getProxy())
        json_file = json.loads(response.content)

        self.tokenFechted_at = datetime.now()
        self.API_token = json_file["access_token"]

        Terminal.success(f" token: {self.API_token}")

        return True 
        

    def createEmail(self):
        pwo = PasswordGenerator()
        pwo.minlen = 14 # (Optional)
        pwo.excludelchars = "#!&%*[](){}-_^<>&&|\\/£\"\'"


        if self.useStaticAccount:
            self.Email = f"mattymar_spotiScraper_{self.accountNumber}@gmail.com"
        else:
            emailDomain = Email()
            emailDomain.register()
            self.Email = emailDomain.address

        
        self.password = pwo.generate()
        self.emailFechted_at = datetime.now()
        self.loggedIn = False
        self.ClientID = None
        self.ClientSecret = None

        Terminal.info(f"User{self.accountNumber} new email created")


    def loadData(self):

        file_path = ""

        if self.useStaticAccount:
            file_path = self.StaticAccount_filePath
        else:
            file_path = self.filePath

        newAccount = False
        with self.MUTEX:
            if not os.path.exists(SpotifyUser.FOLDER): 
                os.mkdir(SpotifyUser.FOLDER)
                newAccount = True

            if not os.path.exists(file_path): 
                with open(file_path, 'w') as file:
                    pass
                newAccount = True
        
        with open(file_path, 'r') as file:
            s = file.read()

        if len(s) > 20 and not newAccount:
            data = json.loads(s)
            self.Email = data['email']
            self.password = data['password']
            self.emailFechted_at = datetime.strptime(data['fetched'], '%Y-%m-%d %H:%M:%S')
            self.loggedIn = data['logged']
            self.ClientID = data['ClientID']
            self.ClientSecret = data['ClientSecret']

            print(data)

            if not self.validEmail() and not self.useStaticAccount:
                self.createEmail()
                self.saveData()
            
            if self.ClientSecret == None:
                self.GenerateAPI_account(not self.loggedIn)
                self.saveData()
                
        else:
            self.createEmail()
            self.saveData()
            self.GenerateAPI_account()
            self.saveData()

        self.ready = True

    def saveData(self):

        file_path = ""

        if self.useStaticAccount:
            file_path = self.StaticAccount_filePath
        else:
            file_path = self.filePath

        with SpotifyUser.SAVE_MUTEX:
            if not os.path.exists(SpotifyUser.FOLDER):
                os.mkdir(SpotifyUser.FOLDER)

        data = {
            "email" : self.Email,
            "password" : self.password,
            "fetched" : self.emailFechted_at.strftime('%Y-%m-%d %H:%M:%S'),
            "logged" : self.loggedIn,
            "ClientID" : self.ClientID,
            "ClientSecret" : self.ClientSecret
        }

        with open(file_path, 'w') as file:
            json.dump(data, file, indent=4)


    def validEmail(self):
        if self.Email == None:
            return False
        
        diff = datetime.now() - self.emailFechted_at
        minutes_passed = (diff.total_seconds() + 20 ) / 60
        
        print(minutes_passed)

        if minutes_passed >= 30:
            return False
        
        return True

    def GenerateAPI_account(self, newAccount = True):

        driver = Chrome_Driver(CHROME_DRIVER_PATH)
    
        if newAccount:
            driver.get('https://www.spotify.com/it/signup')
            WebDriverWait(driver, 20).until(EC.element_to_be_clickable((By.ID,'email'))).click()
            driver.find_element(By.ID,'email').send_keys(self.Email)
            driver.find_element(By.ID,'password').send_keys(self.password)
            driver.find_element(By.ID,'displayname').send_keys(PasswordGenerator().generate())

            driver.find_element(By.ID,'year').send_keys("2000")
            Select(driver.find_element(By.ID,'month')).select_by_visible_text('Aprile')
            driver.find_element(By.ID,'day').send_keys("20")

            element = driver.find_element(By.XPATH,'/html/body/div[1]/main/div/div/form/fieldset/div/div[5]/label/span[1]')
            driver.execute_script("arguments[0].click();", element )
            element= driver.find_element(By.XPATH,'/html/body/div[1]/main/div/div/form/div[7]/div/label/span[1]')
            driver.execute_script("arguments[0].click();", element )

            time.sleep(0.400)
            element = driver.find_element(By.XPATH,'/html/body/div[1]/main/div/div/form/div[8]/div/button/span[1]')
            driver.execute_script("arguments[0].click();", element)

            while driver.current_url != "https://www.spotify.com/it/download/windows/":
                pass

            driver.get('https://developer.spotify.com/dashboard')

            element = WebDriverWait(driver, 10).until(EC.element_to_be_clickable((By.XPATH,'/html/body/div[1]/div/div/main/div/div/div/form/div[2]/div[1]/label/span[1]')))
            driver.execute_script("arguments[0].click();", element)

            element = driver.find_element(By.XPATH,'/html/body/div[1]/div/div/main/div/div/div/form/div[2]/div[2]/button[1]/span[1]')
            driver.execute_script("arguments[0].click();", element)
            time.sleep(0.200)
            self.saveData()

        else:
            driver.get('https://accounts.spotify.com/it/login')
            WebDriverWait(driver, 20).until(EC.element_to_be_clickable((By.ID,'login-username')))
            driver.find_element(By.ID,'login-username').send_keys(self.Email)
            driver.find_element(By.ID,'login-password').send_keys(self.password)

            element = driver.find_element(By.XPATH,'/html/body/div[1]/div/div[2]/div/div/div[1]/div[4]/button/span[1]')
            driver.execute_script("arguments[0].click();", element)
            time.sleep(4)

        
       
        driver.get('https://developer.spotify.com/dashboard/create')

        element = WebDriverWait(driver, 10).until(EC.element_to_be_clickable((By.ID,'name')))
        element.send_keys("scraper")

        element = driver.find_element(By.ID,'redirect_uris')
        element.send_keys("scraper")

        element = driver.find_element(By.ID,'description')
        element.send_keys("scraper")

        element = driver.find_element(By.XPATH,'/html/body/div[1]/div/div/main/div/div/form/div[1]/div[5]/div/input')
        driver.execute_script("arguments[0].click();", element)
        

        element = driver.find_element(By.XPATH,'/html/body/div[1]/div/div/main/div/div/form/div[2]/button/span[1]')
        driver.execute_script("arguments[0].click();", element)

        time.sleep(4)

        self.ClientID = driver.current_url.replace("/settings", '').split('/')[-1]

        for i in range(4):
            try:
                self.ClientID = driver.current_url.replace("/settings", '').split('/')[-1]
                driver.get(f'{driver.current_url.replace("/settings", "")}/settings')
                time.sleep(1)
                element = WebDriverWait(driver, 10).until(EC.element_to_be_clickable((By.XPATH,'/html/body/div[1]/div/div/main/div/div/div[4]/div/div/div[3]/button')))
                driver.execute_script("arguments[0].click();", element)
                break
            except:
                time.sleep(0.500)

        element = driver.find_element(By.XPATH,'/html/body/div[1]/div/div/main/div/div/div[4]/div/div/div[3]/div/span')
        self.ClientSecret = element.text

        Terminal.info(f"User{self.accountNumber} Spotify account created")

        driver.close()

if __name__ == "__main__":

    proxyFinder = ProxyFinder()
    proxies_ = proxyFinder.getProxy_at(0)

    proxy:dict = {
        'https' : proxies_,
        'http' : proxies_
    }

    
    response = requests.get("https://httpbin.org/ip", proxies=proxy)
    print(response.content)


    """response = requests.get("https://httpbin.org/ip")
    print("my public IP:")
    print(response.content)"""


        
     

