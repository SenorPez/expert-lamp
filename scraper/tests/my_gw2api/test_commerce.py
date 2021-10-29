import unittest
from unittest import mock
from unittest.mock import sentinel

from src.my_gw2api.commerce import Price


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


def mock_commerce_prices(item_id, whitelisted=True, buys_quantity=None, buys_unit_price=None,
                         sells_quantity=None, sells_unit_price=None):
    return {"id": item_id,
            "whitelisted": whitelisted,
            "buys": {
                "quantity": buys_quantity,
                "unit_price": buys_unit_price},
            "sells": {
                "quantity": sells_quantity,
                "unit_price": sells_unit_price}
            }


class TestPrice(unittest.TestCase):
    """Unit tests against the Price class"""
    @mock.patch('src.my_gw2api.commerce.requests.get')
    def test_init(self, mock_get):
        json_data = mock_commerce_prices(id(sentinel.item_id), False, id(sentinel.buys_quantity),
                                         id(sentinel.buys_unit_price), id(sentinel.sells_quantity),
                                         id(sentinel.sells_unit_price))
        mock_get.return_value = mocked_requests_get(json_data=json_data)
        instance = Price(id(sentinel.item_id))
        expected_value = Price
        self.assertIsInstance(instance, expected_value)

    @mock.patch('src.my_gw2api.commerce.requests.get')
    def test_property_id(self, mock_get):
        json_data = mock_commerce_prices(id(sentinel.item_id))
        mock_get.return_value = mocked_requests_get(json_data=json_data)
        instance = Price(id(sentinel.item_id))
        expected_value = id(sentinel.item_id)
        self.assertEqual(instance.id, expected_value)

    @mock.patch('src.my_gw2api.commerce.requests.get')
    def test_property_whitelisted(self, mock_get):
        json_data = mock_commerce_prices(id(sentinel.item_id), False)
        mock_get.return_value = mocked_requests_get(json_data=json_data)
        instance = Price(id(sentinel.item_id))
        expected_value = False
        self.assertEqual(instance.whitelisted, expected_value)

    @mock.patch('src.my_gw2api.commerce.requests.get')
    def test_property_buys_quantity(self, mock_get):
        json_data = mock_commerce_prices(id(sentinel.item_id), buys_quantity=id(sentinel.buys_quantity))
        mock_get.return_value = mocked_requests_get(json_data=json_data)
        instance = Price(id(sentinel.item_id))
        expected_value = id(sentinel.buys_quantity)
        self.assertEqual(instance.buys_quantity, expected_value)

    @mock.patch('src.my_gw2api.commerce.requests.get')
    def test_property_buys_unit_price(self, mock_get):
        json_data = mock_commerce_prices(id(sentinel.item_id), buys_unit_price=id(sentinel.buys_unit_price))
        mock_get.return_value = mocked_requests_get(json_data=json_data)
        instance = Price(id(sentinel.item_id))
        expected_value = id(sentinel.buys_unit_price)
        self.assertEqual(instance.buys_unit_price, expected_value)

    @mock.patch('src.my_gw2api.commerce.requests.get')
    def test_property_sells_quantity(self, mock_get):
        json_data = mock_commerce_prices(id(sentinel.item_id), sells_quantity=id(sentinel.sells_quantity))
        mock_get.return_value = mocked_requests_get(json_data=json_data)
        instance = Price(id(sentinel.item_id))
        expected_value = id(sentinel.sells_quantity)
        self.assertEqual(instance.sells_quantity, expected_value)

    @mock.patch('src.my_gw2api.commerce.requests.get')
    def test_property_sells_quantity_price(self, mock_get):
        json_data = mock_commerce_prices(id(sentinel.item_id), sells_unit_price=id(sentinel.sells_unit_price))
        mock_get.return_value = mocked_requests_get(json_data=json_data)
        instance = Price(id(sentinel.item_id))
        expected_value = id(sentinel.sells_unit_price)
        self.assertEqual(instance.sells_unit_price, expected_value)


if __name__ == '__main__':
    unittest.main()
