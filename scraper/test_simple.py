import unittest

class TestSimple(unittest.TestCase):
  def test_pass(self):
    self.assertEqual(42, 42)
    
  def test_fail(self):
    self.assertEqual(11, 42)
