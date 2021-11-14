import unittest
from unittest import mock
from unittest.mock import MagicMock, call

from commercedata.database import Database


class TestDatabase(unittest.TestCase):
    @mock.patch('commercedata.database.MongoClient')
    def test_connect(self, mock_mongo_client):
        instance = Database()
        instance.connect('expert-lamp')
        expected_value = Database

        self.assertIsInstance(instance, expected_value)
        mock_mongo_client.assert_called_with('mongodb://localhost:27017/expert-lamp')

    def test_update(self):
        mock_collection = MagicMock()
        instance = Database()
        instance.database = {'collection': mock_collection}

        matches = [
            {"id": 1},
            {"id": 2}
        ]
        values = [
            {"id": 1, "name": "Alpha"},
            {"id": 2, "name": "Beta"}
        ]
        instance.update('collection', matches, values)

        mock_collection.find_one_and_replace.assert_has_calls([
            call({"id": 1}, {"id": 1, "name": "Alpha"}, upsert=True),
            call({"id": 2}, {"id": 2, "name": "Beta"}, upsert=True)
        ])


if __name__ == '__main__':
    unittest.main()
