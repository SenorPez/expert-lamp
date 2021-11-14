import unittest
from unittest import mock
from unittest.mock import MagicMock

from commercedata.add_prices import add


class TestAddPrices(unittest.TestCase):
    @mock.patch('commercedata.add_prices.Prices')
    @mock.patch('commercedata.add_prices.Database')
    def test_update(self, mock_database, mock_prices):
        mock_init_database = MagicMock()
        mock_database.return_value = mock_init_database

        prices = [
            {"id": 1, "name": "Alpha"},
            {"id": 2, "name": "Beta"}]
        mock_prices.return_value.configure_mock(values=prices)

        add()

        mock_database.assert_called_with()
        mock_init_database.connect.assert_called_with('expert-lamp')
        mock_init_database.add.assert_called_with(
            'prices',
            prices
        )


if __name__ == '__main__':
    unittest.main()
