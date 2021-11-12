"""Provides a class for using the materials endpoint from the Guild Wars 2 API

"""

from guildwars2api.endpoint import Endpoint


class Materials(Endpoint):
    """Represents information about the categories in material storage.
    API Endpoint: /v2/materials?id=<item_ids>
    """
    endpoint = "/v2/materials"

    def __init__(self, item_ids: list[int] = None) -> None:
        super().__init__(self.endpoint, item_ids)
