"""Provides classes for using commerce endpoints for the GW2 API

"""
import requests
from urllib.parse import urlunsplit, urlencode

class Price:
    """Represents aggregated buy and sell listing information.
    API Endpoint: /v2/commerce/prices/<item_id>

    Arguments:
        item_id - ID number of the item.

    """

    def __init__(self, item_id):
        request_url = 'https://api.guildwars2.com/v2/commerce/prices'
        req = requests.get(request_url + "/" + str(item_id))
        req.raise_for_status()

        req_json = req.json()
        self.id = req_json['id']
        self.whitelisted = req_json['whitelisted']
        self.buys_quantity = req_json['buys']['quantity']
        self.buys_unit_price = req_json['buys']['unit_price']
        self.sells_quantity = req_json['sells']['quantity']
        self.sells_unit_price = req_json['sells']['unit_price']


class Prices:
    """Represents aggregated buy and sell listing information in
    an array of Price objects
    API Endpoint: /v2/commerce/prices/<item_ids>

    Arguments:
        item_ids - Array of item IDs.

    """

    scheme = "https"
    host = "api.guildwars2.com"
    endpoint = "/v2/commerce/prices/prices"

    def __init__(self, item_ids):
        query = urlencode({'ids': ",".join(map(str, item_ids))})
        thing = urlunsplit((self.scheme, self.host, self.endpoint, query, ""))
        g = thing
