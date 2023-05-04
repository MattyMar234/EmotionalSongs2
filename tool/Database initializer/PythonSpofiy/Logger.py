from datetime import date
from colorama import init as colorama_init
from colorama import Fore
from colorama import Style

TerminalInfo  = f'[{Fore.YELLOW}INFO{Fore.RESET}]:'
TerminalError = f'[{Fore.RED}ERROR{Fore.RESET}]:'
TerminalSuccess = f'[{Fore.GREEN}SUCCESS{Fore.RESET}]:'


class Terminal:
    def __init__(self):
        ...

    @staticmethod
    def info(text: str):
        print(f"{TerminalInfo}{text}")

    @staticmethod
    def success(text: str):
        print(f"{TerminalSuccess}{text}")

    @staticmethod
    def error(text):
        print(f"{TerminalError}{text}")