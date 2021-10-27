Feature: Generate JWT token

  Scenario: Generate JWT token for authorization
    * def jwtToken =
       """
       function() {
         var AuthJwtTokenGenerator = Java.type('io.skai.api.test.AuthJwtTokenGenerator');
         var authJwtTokenGenerator = new AuthJwtTokenGenerator();
         return authJwtTokenGenerator.getToken();
       }
       """