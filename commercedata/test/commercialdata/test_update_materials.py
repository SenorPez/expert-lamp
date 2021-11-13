import unittest
from unittest import mock
from unittest.mock import MagicMock, call

from src.commercedata.update_materials import connect, update


class TestUpdateMaterials(unittest.TestCase):
    @mock.patch('src.commercedata.update_materials.MongoClient')
    def test_connect(self, mock_mongo_client):
        _ = connect('expert-lamp')
        mock_mongo_client.assert_called_with('mongodb://localhost:27017/expert-lamp')

    @mock.patch('src.commercedata.update_materials.Materials')
    def test_update(self, mock_materials):
        materials = [
            {"id": 1, "name": "Alpha"},
            {"id": 2, "name": "Beta"}]
        mock_materials.return_value.configure_mock(values=materials)

        mock_collection = MagicMock()
        mock_database = {'materials': mock_collection}
        update(mock_database)

        mock_collection.find_one_and_replace.assert_has_calls([
            call({"id": 1}, materials[0], upsert=True),
            call({"id": 2}, materials[1], upsert=True)
        ])


if __name__ == '__main__':
    unittest.main()
