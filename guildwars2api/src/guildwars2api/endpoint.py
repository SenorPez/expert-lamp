"""Provides a base class for endpoints for the Guild Wars 2 API

"""
from typing import Generator, TypeVar
from urllib.parse import urlunsplit

import requests
from tqdm import tqdm

T = TypeVar('T')


class Endpoint:
    scheme = "https"
    host = "api.guildwars2.com"

    def __init__(self, endpoint: str, item_ids: list[int]) -> None:
        """Initializes class

        :param item_ids: list of item ids. If left blank, all items are retrieved.
        """
        request_url = urlunsplit((self.scheme, self.host, endpoint, '', ''))

        if item_ids is None:
            req = requests.get(request_url)
            req.raise_for_status()
            item_ids = req.json()
            chunked_item_ids = self.chunk_array(item_ids, 100)

            self.values = []
            for chunk in tqdm(chunked_item_ids, total=len(item_ids) // 100 + 1):
                self.values.extend(self.get_api_data(request_url, chunk))

        else:
            self.values = self.get_api_data(request_url, item_ids)

    @staticmethod
    def chunk_array(array: list[T], chunk_length: int) -> Generator[list[T], None, None]:
        """Returns a series of arrays of chunk_length or less length.

        :param array: Input array
        :param chunk_length: Maximum length of chunk array
        """
        for i in range(0, len(array), chunk_length):
            yield array[i:i + chunk_length]

    @staticmethod
    def get_api_data(url: str, item_ids: list[int]) -> list:
        """Gets data from the Guild Wars 2 API.

        :param url: API resource URL
        :param item_ids: List of item IDs to retrieve
        :return: List containing JSON dict resources
        """
        params = {"ids": ",".join(map(str, item_ids))}
        req = requests.get(url, params=params)
        req.raise_for_status()
        return req.json()
