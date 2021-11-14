import unittest
from unittest import mock
from unittest.mock import sentinel, call

from guildwars2api.commerce import Prices
from mocked_requests import mocked_requests_get


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
    @mock.patch('guildwars2api.endpoint.requests.get')
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

    @mock.patch('guildwars2api.endpoint.requests.get')
    def test_init_all(self, mock_get):
        json_ids = [id(sentinel.item_id_1), id(sentinel.item_id_2)]
        json_data_1 = mock_commerce_prices_item(id(sentinel.item_id_1), False, id(sentinel.buys_quantity),
                                              id(sentinel.buys_unit_price), id(sentinel.sells_quantity),
                                              id(sentinel.sells_unit_price))
        json_data_2 = mock_commerce_prices_item(id(sentinel.item_id_2), False, id(sentinel.buys_quantity),
                                              id(sentinel.buys_unit_price), id(sentinel.sells_quantity),
                                              id(sentinel.sells_unit_price))
        mock_get.side_effect = [mocked_requests_get(json_data=json_ids), mocked_requests_get(json_data=[json_data_1, json_data_2])]
        url = "https://api.guildwars2.com/v2/commerce/prices"
        params = {"ids": ",".join(map(str, [id(sentinel.item_id_1), id(sentinel.item_id_2)]))}

        instance = Prices()
        expected_value = Prices
        self.assertIsInstance(instance, expected_value)
        mock_get.assert_has_calls([call(url), call(url, params=params)])

    @mock.patch('guildwars2api.endpoint.requests.get')
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

    @mock.patch('guildwars2api.endpoint.requests.get')
    def test_values_all(self, mock_get):
        json_ids = [id(sentinel.item_id_1), id(sentinel.item_id_2)]
        json_data_1 = mock_commerce_prices_item(id(sentinel.item_id_1), False, id(sentinel.buys_quantity),
                                                id(sentinel.buys_unit_price), id(sentinel.sells_quantity),
                                                id(sentinel.sells_unit_price))
        json_data_2 = mock_commerce_prices_item(id(sentinel.item_id_2), False, id(sentinel.buys_quantity),
                                                id(sentinel.buys_unit_price), id(sentinel.sells_quantity),
                                                id(sentinel.sells_unit_price))
        mock_get.side_effect = [mocked_requests_get(json_data=json_ids), mocked_requests_get(json_data=[json_data_1, json_data_2])]
        url = "https://api.guildwars2.com/v2/commerce/prices"
        params = {"ids": ",".join(map(str, [id(sentinel.item_id_1), id(sentinel.item_id_2)]))}

        instance = Prices()
        expected_ids = [id(sentinel.item_id_1), id(sentinel.item_id_2)]
        expected_whitelists = [False, False]
        expected_buys_quantitys = [id(sentinel.buys_quantity), id(sentinel.buys_quantity)]
        expected_buys_unit_prices = [id(sentinel.buys_unit_price), id(sentinel.buys_unit_price)]
        expected_sells_quantitys = [id(sentinel.sells_quantity), id(sentinel.sells_quantity)]
        expected_sells_unit_prices = [id(sentinel.sells_unit_price), id(sentinel.sells_unit_price)]
        self.assertEqual([x['id'] for x in instance.values], expected_ids)
        self.assertEqual([x['whitelisted'] for x in instance.values], expected_whitelists)
        self.assertEqual([x['buys']['quantity'] for x in instance.values], expected_buys_quantitys)
        self.assertEqual([x['buys']['unit_price'] for x in instance.values], expected_buys_unit_prices)
        self.assertEqual([x['sells']['quantity'] for x in instance.values], expected_sells_quantitys)
        self.assertEqual([x['sells']['unit_price'] for x in instance.values], expected_sells_unit_prices)
        mock_get.assert_has_calls([call(url), call(url, params=params)])


if __name__ == '__main__':
    unittest.main()
