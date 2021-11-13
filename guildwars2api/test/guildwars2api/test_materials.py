import unittest
from unittest import mock
from unittest.mock import sentinel, call

from guildwars2api.materials import Materials
from mocked_requests import mocked_requests_get


def materials_resource(item_id,
                       name=None,
                       items=None,
                       order=None):
    return {"id": item_id,
            "name": name,
            "items": items,
            "order": order}


class TestMaterials(unittest.TestCase):
    """Unit tests against the Materials class"""

    @mock.patch('src.guildwars2api.endpoint.requests.get')
    def test_init(self, mock_get):
        json_data = materials_resource(id(sentinel.item_id),
                                       id(sentinel.name),
                                       id(sentinel.items),
                                       id(sentinel.order))
        mock_get.return_value = mocked_requests_get(json_data=[json_data])
        url = "https://api.guildwars2.com/v2/materials"
        params = {"ids": ",".join(map(str, [id(sentinel.item_id)]))}

        instance = Materials([id(sentinel.item_id)])
        expected_value = Materials
        self.assertIsInstance(instance, expected_value)
        mock_get.assert_called_with(url, params=params)

    @mock.patch('src.guildwars2api.endpoint.requests.get')
    def test_init_all(self, mock_get):
        json_ids = [id(sentinel.item_id_1), id(sentinel.item_id_2)]
        json_data_1 = materials_resource(id(sentinel.item_id_1),
                                         id(sentinel.name),
                                         id(sentinel.items),
                                         id(sentinel.order))
        json_data_2 = materials_resource(id(sentinel.item_id_2),
                                         id(sentinel.name),
                                         id(sentinel.items),
                                         id(sentinel.order))
        mock_get.side_effect = [mocked_requests_get(json_data=json_ids),
                                mocked_requests_get(json_data=[json_data_1, json_data_2])]
        url = "https://api.guildwars2.com/v2/materials"
        params = {"ids": ",".join(map(str, [id(sentinel.item_id_1), id(sentinel.item_id_2)]))}

        instance = Materials()
        expected_value = Materials
        self.assertIsInstance(instance, expected_value)
        mock_get.assert_has_calls([call(url), call(url, params=params)])

    # TODO: Test data types for fields.


if __name__ == '__main__':
    unittest.main()
