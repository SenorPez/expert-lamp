"""Provides classes for using commerce endpoints for the Guild Wars 2 API

"""
from urllib.parse import urlunsplit

import requests


class Prices:
    """Represents aggregated buy and sell listing information in an array of Price objects
    API Endpoint: /v2/commerce/prices/<item_ids>
    """
    scheme = "https"
    host = "api.guildwars2.com"
    endpoint = "/v2/commerce/prices"

    def __init__(self, item_ids=None):
        request_url = urlunsplit((self.scheme, self.host, self.endpoint, '', ''))

        if item_ids is None:
            req = requests.get(request_url)
            req.raise_for_status()
            item_ids = req.json()
            chunked_item_ids = self.chunk_array(item_ids, 100)

            self.values = []
            for chunk in chunked_item_ids:
                self.values.extend(self.get_api_data(request_url, chunk))
        else:
            self.values = self.get_api_data(request_url, item_ids)

    @staticmethod
    def chunk_array(array, chunk_length):
        for i in range(0, len(array), chunk_length):
            yield array[i:i + chunk_length]

    @staticmethod
    def get_api_data(url, item_ids):
        params = {"ids": ",".join(map(str, item_ids))}
        req = requests.get(url, params=params)
        req.raise_for_status()
        return req.json()
