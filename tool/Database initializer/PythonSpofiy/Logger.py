from datetime import date
from colorama import init as colorama_init
from colorama import Fore
from colorama import Style

import threading




class Terminal:

    Lock = threading.Lock()
    TerminalInfo  = f'[{Fore.YELLOW}INFO{Fore.RESET}]:'
    TerminalError = f'[{Fore.RED}ERROR{Fore.RESET}]:'
    TerminalSuccess = f'[{Fore.GREEN}SUCCESS{Fore.RESET}]:'
    Terminalretry = f'[{Fore.MAGENTA}RETRY{Fore.RESET}]:'

    def __init__(self):
        ...

    @staticmethod
    def info(text: str):
        with Terminal.Lock:
            print(f"{Terminal.TerminalInfo}{text}")

    @staticmethod
    def success(text: str):
        with Terminal.Lock:
            print(f"{Terminal.TerminalSuccess}{text}")

    @staticmethod
    def error(text):
        with Terminal.Lock:
            print(f"{Terminal.TerminalError}{text}")
    
    @staticmethod
    def retry(text):
        with Terminal.Lock:
            print(f"{Terminal.Terminalretry}{text}")



    @staticmethod
    def info_Notln(text: str):
        with Terminal.Lock:
            print(f"{Terminal.TerminalInfo}{text}", end = '')

    @staticmethod
    def success_Notln(text: str):
        with Terminal.Lock:
            print(f"{Terminal.TerminalSuccess}{text}", end = '')

    @staticmethod
    def error_Notln(text):
        with Terminal.Lock:
            print(f"{Terminal.TerminalError}{text}", end = '')
    
    @staticmethod
    def retry_Notln(text):
        with Terminal.Lock:
            print(f"{Terminal.Terminalretry}{text}", end = '')