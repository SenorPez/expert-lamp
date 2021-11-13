"""Provides a class for database access.

"""
from pymongo import MongoClient


class Database:
    """Represents a database connection"""
    connection_string = "mongodb://localhost:27017/expert-lamp"
    client = None
    database = None

    def connect(self, dbname):
        self.client = MongoClient(self.connection_string)
        self.database = self.client[dbname]

    def update(self, collection, matchers, values):
        collection = self.database[collection]
        for (match, value) in zip(matchers, values):
            collection.find_one_and_replace(
                match,
                value,
                upsert=True)
            pass
