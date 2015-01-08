describe('Auth', function() {

  var urlPrefix = 'https://localhost:8181/riot/api/v1';

  beforeEach(module('riot'));

  beforeEach(module(function ($urlRouterProvider) {
    //disable router to avoid HTTP requests for templates
    $urlRouterProvider.otherwise(function() {
      return false;
    });
  }));

  beforeEach(inject(function($httpBackend) {
    //mock http call
    $httpBackend.when('GET', urlPrefix + '/users/self').respond(200, {
      username: 'mock_user'
    });
  }));

  afterEach(inject(function($httpBackend) {
    $httpBackend.verifyNoOutstandingExpectation();
    $httpBackend.verifyNoOutstandingRequest();
  }));

  it('should successfully login', inject(function($httpBackend, Auth) {
    var callback = {
      success: function() {},
      error: function() {}
    };

    //spy on callbacks
    spyOn(callback, 'success');
    spyOn(callback, 'error');

    //mock http login call
    $httpBackend.expect('PUT', urlPrefix + '/auth/login').respond(200, {
      accessToken: 'mock_accessToken',
      refreshToken: 'mock_refreshToken',
      user: {
        username: 'mock_user'
      }
    });

    //login
    Auth.login('Yoda', 'YodaPW').then(callback.success, callback.error);

    //flush http responses
    $httpBackend.flush();

    //verify
    expect(callback.success).toHaveBeenCalled();
    expect(Auth.isAuthenticated()).toBe(true);
    expect(Auth.getAccessToken()).toBe('mock_accessToken');
    expect(Auth.getRefreshToken()).toBe('mock_refreshToken');
    expect(Auth.getUser().username).toBe('mock_user');
  }));

  it('should fail login', inject(function($httpBackend, Auth) {
    var callback = {
      success: function() {},
      error: function() {}
    };

    //spy on callbacks
    spyOn(callback, 'success');
    spyOn(callback, 'error');

    //mock http login call
    $httpBackend.expectPUT(urlPrefix + '/auth/login').respond(400);

    //login
    Auth.login('Yoda', 'YodaPW').then(callback.success, callback.error);

    //flush http responses
    $httpBackend.flush();

    //verify
    expect(callback.error).toHaveBeenCalled();
    expect(Auth.isAuthenticated()).toBe(false);
    expect(Auth.getAccessToken()).toBeNull();
    expect(Auth.getRefreshToken()).toBeNull();
    expect(Auth.getUser()).toBeNull();
  }));

  it('should successfully refresh', inject(function($httpBackend, Auth) {
    //==================
    // Step 1: login
    //==================
    //mock http login call
    $httpBackend.expect('PUT', urlPrefix + '/auth/login').respond(200, {
      accessToken: 'mock_accessToken',
      refreshToken: 'mock_refreshToken',
      user: {
        username: 'mock_user'
      }
    });

    //login
    Auth.login('Yoda', 'YodaPW');

    //flush http responses
    $httpBackend.flush();

    //verify
    expect(Auth.isAuthenticated()).toBe(true);
    expect(Auth.getAccessToken()).toBe('mock_accessToken');
    expect(Auth.getRefreshToken()).toBe('mock_refreshToken');
    expect(Auth.getUser().username).toBe('mock_user');

    //==================
    // Step 2: refresh
    //==================
    var callback = {
      success: function() {},
      error: function() {}
    };

    //spy on callbacks
    spyOn(callback, 'success');
    spyOn(callback, 'error');

    //mock http refresh call
    $httpBackend.expect('PUT', urlPrefix + '/auth/refresh').respond(200, {
      accessToken: 'mock_accessToken2',
      refreshToken: 'mock_refreshToken2'
    });

    //login
    Auth.refresh().then(callback.success, callback.error);

    //flush http responses
    $httpBackend.flush(1);

    //verify
    expect(callback.success).toHaveBeenCalled();
    expect(Auth.isAuthenticated()).toBe(true);
    expect(Auth.getAccessToken()).toBe('mock_accessToken2');
    expect(Auth.getRefreshToken()).toBe('mock_refreshToken2');
    expect(Auth.getUser().username).toBe('mock_user');
  }));

  it('should fail refresh', inject(function($httpBackend, Auth) {
    //==================
    // Step 1: login
    //==================
    //mock http login call
    $httpBackend.expect('PUT', urlPrefix + '/auth/login').respond(200, {
      accessToken: 'mock_accessToken',
      refreshToken: 'mock_refreshToken',
      user: {
        username: 'mock_user'
      }
    });

    //login
    Auth.login('Yoda', 'YodaPW');

    //flush http responses
    $httpBackend.flush();

    //verify
    expect(Auth.isAuthenticated()).toBe(true);
    expect(Auth.getAccessToken()).toBe('mock_accessToken');
    expect(Auth.getRefreshToken()).toBe('mock_refreshToken');
    expect(Auth.getUser().username).toBe('mock_user');

    //==================
    // Step 2: refresh
    //==================
    var callback = {
      success: function() {},
      error: function() {}
    };

    //spy on callbacks
    spyOn(callback, 'success');
    spyOn(callback, 'error');

    //mock http refresh call
    $httpBackend.expect('PUT', urlPrefix + '/auth/refresh').respond(400);

    //login
    Auth.refresh().then(callback.success, callback.error);

    //flush http responses
    $httpBackend.flush(1);

    //verify
    expect(callback.error).toHaveBeenCalled();
    expect(Auth.isAuthenticated()).toBe(false);
    expect(Auth.getAccessToken()).toBeNull();
    expect(Auth.getRefreshToken()).toBeNull();
    expect(Auth.getUser()).toBeNull();
  }));

  it('should successfully logout', inject(function($httpBackend, Auth) {
    //==================
    // Step 1: login
    //==================
    //mock http login call
    $httpBackend.expect('PUT', urlPrefix + '/auth/login').respond(200, {
      accessToken: 'mock_accessToken',
      refreshToken: 'mock_refreshToken',
      user: {
        username: 'mock_user'
      }
    });

    //login
    Auth.login('Yoda', 'YodaPW');

    //flush http responses
    $httpBackend.flush();

    //verify
    expect(Auth.isAuthenticated()).toBe(true);
    expect(Auth.getAccessToken()).toBe('mock_accessToken');
    expect(Auth.getRefreshToken()).toBe('mock_refreshToken');
    expect(Auth.getUser().username).toBe('mock_user');

    //==================
    // Step 2: logout
    //==================
    var callback = {
      success: function() {},
      error: function() {}
    };

    //spy on callbacks
    spyOn(callback, 'success');
    spyOn(callback, 'error');

    //mock http logout call
    $httpBackend.expect('PUT', urlPrefix + '/auth/logout').respond(200);

    //login
    Auth.logout().then(callback.success, callback.error);

    //flush http responses
    $httpBackend.flush(1);

    //verify
    expect(callback.success).toHaveBeenCalled();
    expect(Auth.isAuthenticated()).toBe(false);
    expect(Auth.getAccessToken()).toBeNull();
    expect(Auth.getRefreshToken()).toBeNull();
    expect(Auth.getUser()).toBeNull();
  }));

  it('should fail logout', inject(function($httpBackend, Auth) {
    //==================
    // Step 1: login
    //==================
    //mock http login call
    $httpBackend.expect('PUT', urlPrefix + '/auth/login').respond(200, {
      accessToken: 'mock_accessToken',
      refreshToken: 'mock_refreshToken',
      user: {
        username: 'mock_user'
      }
    });

    //login
    Auth.login('Yoda', 'YodaPW');

    //flush http responses
    $httpBackend.flush();

    //verify
    expect(Auth.isAuthenticated()).toBe(true);
    expect(Auth.getAccessToken()).toBe('mock_accessToken');
    expect(Auth.getRefreshToken()).toBe('mock_refreshToken');
    expect(Auth.getUser().username).toBe('mock_user');

    //==================
    // Step 2: logout
    //==================
    var callback = {
      success: function() {},
      error: function() {}
    };

    //spy on callbacks
    spyOn(callback, 'success');
    spyOn(callback, 'error');

    //mock http logout call
    $httpBackend.expect('PUT', urlPrefix + '/auth/logout').respond(400);

    //login
    Auth.logout().then(callback.success, callback.error);

    //flush http responses
    $httpBackend.flush(1);

    //verify
    expect(callback.error).toHaveBeenCalled();
    expect(Auth.isAuthenticated()).toBe(false);
    expect(Auth.getAccessToken()).toBeNull();
    expect(Auth.getRefreshToken()).toBeNull();
    expect(Auth.getUser()).toBeNull();
  }));
});
