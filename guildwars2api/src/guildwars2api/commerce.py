"""Provides classes for using commerce endpoints for the Guild Wars 2 API

"""
from urllib.parse import urlunsplit

import requests


class Prices:
    """Represents aggregated buy and sell listing information in an array of Price objects
    API Endpoint: /v2/commerce/prices/<item_ids>

    Arguments:
        item_ids - Array of item IDs.
    """
    scheme = "https"
    host = "api.guildwars2.com"
    endpoint = "/v2/commerce/prices"

    def __init__(self, item_ids):
        request_url = urlunsplit((self.scheme, self.host, self.endpoint, '', ''))
        params = {"ids": ",".join(map(str, item_ids))}
        req = requests.get(request_url, params=params)
        req.raise_for_status()

        self.values = req.json()
