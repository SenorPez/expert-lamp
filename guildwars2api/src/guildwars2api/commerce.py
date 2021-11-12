"""Provides classes for using commerce endpoints from the Guild Wars 2 API

"""

from guildwars2api.endpoint import Endpoint


class Prices(Endpoint):
    """Represents aggregated buy and sell listing information from the trading post.
    API Endpoint: /v2/commerce/prices/<item_ids>
    """
    endpoint = "/v2/commerce/prices"

    def __init__(self, item_ids: list[int] = None) -> None:
        super().__init__(self.endpoint, item_ids)
