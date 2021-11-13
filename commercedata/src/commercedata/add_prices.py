from guildwars2api.commerce import Prices
from src.commercedata.database import Database


def add():
    database = Database()
    database.connect('expert-lamp')

    prices = Prices()
    database.add('prices', prices.values)


if __name__ == "__main__":
    add()
