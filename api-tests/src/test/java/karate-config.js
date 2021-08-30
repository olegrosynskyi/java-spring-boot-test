function fn() {
    karate.configure('ssl', true);
    karate.configure('connectTimeout', 60000);
    karate.configure('readTimeout', 60000);
    return {
        APPLICATION_URL : karate.properties['url'] || 'http://localhost:8080'
    }
}