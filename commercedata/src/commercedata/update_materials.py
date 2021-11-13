from pymongo import MongoClient

from guildwars2api.materials import Materials


def connect(dbname):
    connection_string = "mongodb://localhost:27017/expert-lamp"
    client = MongoClient(connection_string)
    return client[dbname]


def update(database):
    collection = database['materials']
    materials = Materials()

    for material in materials.values:
        collection.find_one_and_replace(
            {"id": material['id']},
            material,
            upsert=True)
        pass


if __name__ == "__main__":
    database = connect('expert-lamp')
    update(database)
