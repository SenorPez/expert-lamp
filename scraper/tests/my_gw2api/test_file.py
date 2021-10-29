import unittest
from src.my_gw2api.file import add_one


class Test(unittest.TestCase):
    def test_add_one(self):
        test_value = 1
        expected_value = 2
        self.assertEqual(add_one(test_value), expected_value, "Failed")


if __name__ == '__main__':
    unittest.main()
