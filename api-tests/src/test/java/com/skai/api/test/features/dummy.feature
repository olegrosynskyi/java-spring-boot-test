@SmokeTest
Feature: A simple Karate test that always passes

  Background:
    * def generateAuthJwtToken =
 """
 function() {
   var AuthJwtTokenGenerator = Java.type('com.skai.api.test.AuthJwtTokenGenerator');
   var authJwtTokenGenerator = new AuthJwtTokenGenerator();
   return authJwtTokenGenerator.getToken();
 }
 """

  Scenario: Dummy Test
    And header Authorization = "Bearer " + generateAuthJwtToken()
    * def foo = 1234
    * def bar = 1234
    * match foo == bar
