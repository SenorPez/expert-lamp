from guildwars2api.materials import Materials
from src.commercedata.database import Database


def update():
    database = Database()
    database.connect('expert-lamp')

    materials = Materials()
    matches = [{"id": x['id']} for x in materials.values]
    database.update('materials', matches, materials.values)


if __name__ == "__main__":
    update()
