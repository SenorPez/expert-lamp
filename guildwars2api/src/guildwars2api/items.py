"""Provides a class for using the items endpoint for the Guild Wars 2 API

"""
from typing import TypeVar

from guildwars2api.endpoint import Endpoint

T = TypeVar('T')


class Items(Endpoint):
    """Represents items discovered in the game.
    API Endpoint: /v2/items?id=<item_ids>
    """
    endpoint = "/v2/items"

    def __init__(self, item_ids: list[int] = None) -> None:
        super().__init__(self.endpoint, item_ids)
