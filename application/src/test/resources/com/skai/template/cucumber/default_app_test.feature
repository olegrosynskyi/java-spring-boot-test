Feature: Testing application

  Scenario: Wiremock stub
    When I go to service with path /wiremocktest response should be 200 with wiremock stub

  Scenario: Bad url
    When I go to service path /bad response should be 404

  Scenario: Good url
    When I go to service path /api/v1/test response should be 200