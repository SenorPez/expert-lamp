import unittest
from unittest import mock
from unittest.mock import MagicMock

from src.commercedata.update_items import update


class TestUpdateItems(unittest.TestCase):
    @mock.patch('src.commercedata.update_items.Items')
    @mock.patch('src.commercedata.update_items.Database')
    def test_update(self, mock_database, mock_items):
        mock_init_database = MagicMock()
        mock_database.return_value = mock_init_database

        items = [
            {"id": 1, "name": "Alpha"},
            {"id": 2, "name": "Beta"}]
        mock_items.return_value.configure_mock(values=items)

        update()

        mock_database.assert_called_with()
        mock_init_database.connect.assert_called_with('expert-lamp')
        mock_init_database.update.assert_called_with(
            'materials',
            [{"id": 1}, {"id": 2}],
            items
        )


if __name__ == '__main__':
    unittest.main()
