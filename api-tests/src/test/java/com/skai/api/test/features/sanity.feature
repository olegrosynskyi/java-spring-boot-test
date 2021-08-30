@ApiTest
Feature: Sanity Test

  Background:
    * def jwtUtils = call read('utils/jwt_token.feature')
    * def jwtToken = jwtUtils.jwtToken()

  Scenario: app default page is responsive
    Given url APPLICATION_URL
    When method get
    Then status 200

  @SmokeTest
  Scenario: app default page returns 200 OK response with X-kenshoo-trace-id header
    Given url APPLICATION_URL
    When method get
    Then status 200
    And match responseHeaders['X-kenshoo-trace-id'][0] == '#notnull'

  @SmokeTest
  Scenario: app default page returns 200 OK populates response with new X-kenshoo-trace-id header when header is empty string
    Given url APPLICATION_URL
    And header X-kenshoo-trace-id = ''
    When method get
    Then status 200
    And match responseHeaders['X-kenshoo-trace-id'][0] == '#notnull'
    And match responseHeaders['X-kenshoo-trace-id'][0] != ''

  @SmokeTest
  Scenario: app default page returns 200 OK response with X-kenshoo-trace-id header same as the one that came with the request
    Given url APPLICATION_URL
    And header X-kenshoo-trace-id = '12345'
    When method get
    Then status 200
    And match responseHeaders['X-kenshoo-trace-id'][0] == '12345'

  Scenario: secured route returns unauthorized when no Authorization header is provided
    Given url APPLICATION_URL + "/api/v1/test"
    When request {}
    And method post
    Then status 401
    And match response.message == 'Access Denied. Credentials are required to access this resource.'

  Scenario: secured route returns unauthorized when Authorization header has wrong auth jwt token
    Given url APPLICATION_URL + "/api/v1/test"
    And header Authorization = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJleWFsLmJyYXZlQGtlbnNob28uY29tIiwiZXhwIjoxNTY4MDU3MzA2LCJpc3MiOiJodHRwOi8va2Vuc2hvby5jb20iLCJ1c2VyaWQiOjAsImFnZW5jeUlkIjo1LCJuYW1lIjoiRXlhbCBUZXN0Iiwicm9sZXMiOlsiS2Vuc2hvbyBBZG1pbiJdLCJiaWxsaW5nSWQiOjAsImFwaWMiOiI5MjAzMSJ9.tLmY4_kjXhXixLrXSz6Ki0osrpn0ktWZ48wVFp5ocsM"
    When request {}
    And method post
    Then status 401
    And match response.message == 'Access Denied. Credentials are required to access this resource.'

  Scenario: secured route returns 200 OK response when Authorization header has a verified auth jwt token
    Given url APPLICATION_URL + "/api/v1/test"
    And header Authorization = "Bearer " + jwtToken
    When request {}
    And method post
    Then status 200

  @SmokeTest
  Scenario: app is up and health check return appropriate status
    Given url APPLICATION_URL + "/actuator/health"
    When method get
    Then status 200
    And match response.status == 'UP'