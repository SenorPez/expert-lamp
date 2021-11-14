def mocked_requests_get(*args, **kwargs):
    class MockResponse:
        def __init__(self, *, json_data):
            self.json_data = json_data

        def json(self):
            """Returns JSON data of response"""
            return self.json_data

        @staticmethod
        def raise_for_status():
            """Return raise for status of response"""
            return None

    return MockResponse(*args, **kwargs)
