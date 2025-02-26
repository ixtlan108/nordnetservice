#!/usr/bin/python3

import redis
import argparse

REDIS_PROD = 0
REDIS_DEMO = 4
REDIS_TEST = 5
REDIS_INTEGRATION = 6

TICKERS = {
    # "AKERBP": 1,
    "AKSO": 1,
    "BAKKA": 1,
    "BWLPG": 1,
    "DNB": 2,
    "DNO": 1,
    "EQNR": 2,
    "GJF": 1,
    "GOGL": 1,
    # "MHG",
    "NAS": 2,
    "NHY": 2,
    "OBX": 1,
    "ORK": 1,
    "PGS": 2,
    # "REC": 1,
    "SDRL": 1,
    "STB": 1,
    "SUBC": 2,
    "TEL": 1,
    "TGS": 1,
    "TOM": 1,
    "YAR": 1,
}


def expiry(db):
    if db == REDIS_PROD:
        result = [
            #1702594800000,
            #1705618800000,
            #1710457200000,
            #1718920800000,
            #1726783200000,
            1742511600000,
            1744668000000,
            1747346400000,
            1750370400000,
            1758232800000,
            1766098800000
        ]
    elif db == REDIS_DEMO:
        result = [
            1674169200000,
            1676588400000,
            1679007600000,
            1686866400000,
            1694728800000,
        ]
    elif db == REDIS_TEST:
        result = [
            # 1674169200000,
            # 1676588400000,
            # 1679007600000,
            # 1686866400000,
            # 1694728800000,
            1766098800000
        ]
    elif db == REDIS_INTEGRATION:
        result = [
            1766098800000
        ]
    else:
        raise Exception("No such db: %d" % db)
    return result


def init_redis(db):
    return redis.Redis(host='172.20.1.2', port=6379, db=db)


def populate_tickers(r):
    for k, v in TICKERS.items():
        print(k)
        r.hset("expiry", k, v)


def populate_expiry_x(ex, index, r):
    redis_key = "expiry-%d" % index
    for k, v in ex.items():
        r.hset(redis_key, k, v)


def populate_expiry(db, r):
    """
    populate_expiry_x(expiry_1(is_test), 1, r)
    populate_expiry_x(expiry_2(is_test), 2, r)

    print(r.hgetall("expiry"))
    print(r.hgetall("expiry-1"))
    print(r.hgetall("expiry-2"))
    """
    redis_key = "expiry"
    vals = expiry(db)
    for v in vals:
        r.sadd(redis_key, v)
    print(r.smembers(redis_key))


def populate_opening_prices(r):
    redis_key = "openingprices"
    r.hset(redis_key, "2", "180.00")    # EQNR
    r.hset(redis_key, "1", "66.74")     # NHY
    r.hset(redis_key, "3", "403.00")    # YAR

    # 127.0.0.1:6379[5]> hgetall openingprices
    # "EQNR" "180.00"
    # "NHY" "66.74"
    # "YAR" "482.0"


# def populate(is_test, flush_all, add_tickers):

def populate_splu(r):
    redis_key = "splu"
    r.hset(redis_key, "EQNR", "1620594773")

def populate_test_url(db, r):
    if db == REDIS_DEMO:
        redis_key = "demo-url"
        redis_val =  "file:////home/rcs/opt/java/nordnetservice/test/resources/html/yar2.html"
    elif db == REDIS_TEST:
        redis_key = "test-url"
        redis_val =  "file:////home/rcs/opt/java/nordnetservice/test/resources/html/yar.html"
    r.set(redis_key, redis_val)

def populate(args):
    r = init_redis(args.db)
    if args.flush_all == True:
        r.flushall()
    print(args)
    if args.add_expiry == True:
        populate_expiry(args.db, r)
    if args.add_opening_prices == True:
        populate_opening_prices(r)
    if args.add_splu == True:
        populate_splu(r)
    if args.add_test_url == True:
        populate_test_url(args.db, r)


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Populate Redis cache.')

    #parser.add_argument('integers', metavar='N', type=int, nargs='+',help='an integer for the accumulator')
    
    parser.add_argument('--db', dest='db', metavar='DB', type=int,
                        default=4, help='Redis db: prod = 0, demo = 4, test = 5, integration test = 6. Default: 4 (demo)')

    parser.add_argument('--flushall', dest='flush_all', action='store_true',
                        default=False, help='Flush Redis cache before populate. default: false')

    parser.add_argument('--tickers', dest='add_tickers', action='store_true',
                        default=False, help='Populate tickers. default: false')

    parser.add_argument('--expiry', dest='add_expiry', action='store_true',
                        default=False, help='Populate all expiry dates. default: false')

    parser.add_argument('--opening', dest='add_opening_prices', action='store_true',
                        default=False, help='Populate opening prices. default: false')

    parser.add_argument('--splu', dest='add_splu', action='store_true',
                        default=False, help='Populate splu (stockprices last updated). default: false')

    parser.add_argument('--test-url', dest='add_test_url', action='store_true',
                        default=False, help='Add url to test-url (--db 5)/demo-url (db 4). default: false')

    args = parser.parse_args()
    populate(args)
