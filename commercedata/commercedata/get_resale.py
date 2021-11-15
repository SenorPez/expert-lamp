from commercedata.database import Database


def get():
    database = Database()
    database.connect('expert-lamp')

    collection = database.get_collection('view_basiccraftingmaterials')
    items = collection.find()
    item_data = []

    for item in items:
        try:
            item['prices'].sort(reverse=True, key=lambda x: x['timestamp'])
            buy = item['buy']['unit_price']
            sell = item['sell']['unit_price']
            profit = sell - max(1, round(sell * 0.05)) - max(1, round(sell * 0.1)) - buy
            roi = ((buy + profit) / buy - 1) * 100
            volume = item['buy']['quantity'] + item['sell']['quantity']
            item_data.append({
                'name': item['name'],
                'buy': buy,
                'sell': sell,
                'profit': profit,
                'roi': roi,
                'volume': volume})
        except KeyError:
            pass
        except ZeroDivisionError:
            pass

    name_length = max([len(x['name']) for x in item_data])
    item_data.sort(reverse=True, key=lambda x: (x['roi'], x['profit']))

    for item in filter(lambda x: x['profit'] >= 100 and x['roi'] >= 0, item_data):
        print('{:{name_length}} {: 7d} {: 7d} {: 7d} {: 7.2f} {: 9d}'.format(item['name'], item['buy'], item['sell'], item['profit'], item['roi'], item['volume'], name_length=name_length))


if __name__ == "__main__":
    get()
