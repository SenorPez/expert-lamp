import unittest
from unittest import mock
from unittest.mock import sentinel, call

from guildwars2api.items import Items
from mocked_requests import mocked_requests_get


def mock_item(item_id, chat_link=None, name=None, icon=None, description=None, item_type=None, rarity=None, level=None,
              vendor_value=None, default_skin=None, flags=None, game_types=None, restrictions=None, upgrades_into=None,
              upgrades_from=None, details=None):
    return {"id": item_id,
            "chat_link": chat_link,
            "name": name,
            "icon": icon,
            "descriptions": description,
            "type": item_type,
            "rarity": rarity,
            "level": level,
            "vendor_value": vendor_value,
            "default_skin": default_skin,
            "flags": flags,
            "game_types": game_types,
            "restrictions": restrictions,
            "upgrades_into": upgrades_into,
            "upgrades_from": upgrades_from,
            "details": details}


class TestItems(unittest.TestCase):
    """Unit tests against the Items class"""

    @mock.patch('src.guildwars2api.endpoint.requests.get')
    def test_init(self, mock_get):
        json_data = mock_item(id(sentinel.item_id),
                              id(sentinel.chat_link),
                              id(sentinel.name),
                              id(sentinel.icon),
                              id(sentinel.description),
                              id(sentinel.item_type),
                              id(sentinel.rarity),
                              id(sentinel.level),
                              id(sentinel.vendor_value),
                              id(sentinel.default_skin),
                              id(sentinel.flags),
                              id(sentinel.game_types),
                              id(sentinel.restrictions),
                              id(sentinel.upgrades_into),
                              id(sentinel.upgrades_from),
                              id(sentinel.details))
        mock_get.return_value = mocked_requests_get(json_data=[json_data])
        url = "https://api.guildwars2.com/v2/items"
        params = {"ids": ",".join(map(str, [id(sentinel.item_id)]))}

        instance = Items([id(sentinel.item_id)])
        expected_value = Items
        self.assertIsInstance(instance, expected_value)
        mock_get.assert_called_with(url, params=params)

    @mock.patch('src.guildwars2api.endpoint.requests.get')
    def test_init_all(self, mock_get):
        json_ids = [id(sentinel.item_id_1), id(sentinel.item_id_2)]
        json_data_1 = mock_item(id(sentinel.item_id_1),
                                id(sentinel.chat_link),
                                id(sentinel.name),
                                id(sentinel.icon),
                                id(sentinel.description),
                                id(sentinel.item_type),
                                id(sentinel.rarity),
                                id(sentinel.level),
                                id(sentinel.vendor_value),
                                id(sentinel.default_skin),
                                id(sentinel.flags),
                                id(sentinel.game_types),
                                id(sentinel.restrictions),
                                id(sentinel.upgrades_into),
                                id(sentinel.upgrades_from),
                                id(sentinel.details))
        json_data_2 = mock_item(id(sentinel.item_id_1),
                                id(sentinel.chat_link),
                                id(sentinel.name),
                                id(sentinel.icon),
                                id(sentinel.description),
                                id(sentinel.item_type),
                                id(sentinel.rarity),
                                id(sentinel.level),
                                id(sentinel.vendor_value),
                                id(sentinel.default_skin),
                                id(sentinel.flags),
                                id(sentinel.game_types),
                                id(sentinel.restrictions),
                                id(sentinel.upgrades_into),
                                id(sentinel.upgrades_from),
                                id(sentinel.details))
        mock_get.side_effect = [mocked_requests_get(json_data=json_ids), mocked_requests_get(json_data=[json_data_1, json_data_2])]
        url = "https://api.guildwars2.com/v2/items"
        params = {"ids": ",".join(map(str, [id(sentinel.item_id_1), id(sentinel.item_id_2)]))}

        instance = Items()
        expected_value = Items
        self.assertIsInstance(instance, expected_value)
        mock_get.assert_has_calls([call(url), call(url, params=params)])

    #TODO: Test various item type structures
