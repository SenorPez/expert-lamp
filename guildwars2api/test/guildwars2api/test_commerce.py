import unittest
from unittest import mock
from unittest.mock import sentinel

from src.guildwars2api.commerce import Prices


def mocked_requests_get(*args, **kwargs):
    class MockResponse:
        def __init__(self, *, json_data):
            self.json_data = json_data

        def json(self):
            """Returns JSON data of response"""
            return self.json_data

        @staticmethod
        def raise_for_status():
            """Return raise for status of response"""
            return None

    return MockResponse(*args, **kwargs)


def mock_commerce_prices_item(item_id, whitelisted=True, buys_quantity=None, buys_unit_price=None, sells_quantity=None,
                              sells_unit_price=None):
    return {"id": item_id,
            "whitelisted": whitelisted,
            "buys": {
                "quantity": buys_quantity,
                "unit_price": buys_unit_price
            },
            "sells": {
                "quantity": sells_quantity,
                "unit_price": sells_unit_price
            }}


class TestPrices(unittest.TestCase):
    """Unit tests against the Prices class"""
    @mock.patch('src.guildwars2api.commerce.requests.get')
    def test_init(self, mock_get):
        json_data = mock_commerce_prices_item(id(sentinel.item_id), False, id(sentinel.buys_quantity),
                                              id(sentinel.buys_unit_price), id(sentinel.sells_quantity),
                                              id(sentinel.sells_unit_price))
        mock_get.return_value = mocked_requests_get(json_data=[json_data])
        url = "https://api.guildwars2.com/v2/commerce/prices"
        params = {"ids": ",".join(map(str, [id(sentinel.item_id)]))}

        instance = Prices([id(sentinel.item_id)])
        expected_value = Prices
        self.assertIsInstance(instance, expected_value)
        mock_get.assert_called_with(url, params=params)

    @mock.patch('src.guildwars2api.commerce.requests.get')
    def test_values(self, mock_get):
        json_data = mock_commerce_prices_item(id(sentinel.item_id), False, id(sentinel.buys_quantity),
                                              id(sentinel.buys_unit_price), id(sentinel.sells_quantity),
                                              id(sentinel.sells_unit_price))
        mock_get.return_value = mocked_requests_get(json_data=[json_data])
        url = "https://api.guildwars2.com/v2/commerce/prices"
        params = {"ids": ",".join(map(str, [id(sentinel.item_id)]))}

        instance = Prices([id(sentinel.item_id)])
        expected_id = [id(sentinel.item_id)]
        expected_whitelist = [False]
        expected_buys_quantity = [id(sentinel.buys_quantity)]
        expected_buys_unit_price = [id(sentinel.buys_unit_price)]
        expected_sells_quantity = [id(sentinel.sells_quantity)]
        expected_sells_unit_price = [id(sentinel.sells_unit_price)]
        self.assertEqual([x['id'] for x in instance.values], expected_id)
        self.assertEqual([x['whitelisted'] for x in instance.values], expected_whitelist)
        self.assertEqual([x['buys']['quantity'] for x in instance.values], expected_buys_quantity)
        self.assertEqual([x['buys']['unit_price'] for x in instance.values], expected_buys_unit_price)
        self.assertEqual([x['sells']['quantity'] for x in instance.values], expected_sells_quantity)
        self.assertEqual([x['sells']['unit_price'] for x in instance.values], expected_sells_unit_price)
        mock_get.assert_called_with(url, params=params)


if __name__ == '__main__':
    unittest.main()
