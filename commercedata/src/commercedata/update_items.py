from guildwars2api.items import Items
from src.commercedata.database import Database


def update():
    database = Database()
    database.connect('expert-lamp')

    items = Items()
    matches = [{"id": x['id']} for x in items.values]
    database.update('items', matches, items.values)


if __name__ == "__main__":
    update()
