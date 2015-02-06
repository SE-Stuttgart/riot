describe('AuthenticationInterceptor', function() {

  var urlPrefix = '/api/v1';

  beforeEach(module('riot'));

  beforeEach(module(function ($urlRouterProvider) {
    //disable router to avoid HTTP requests for templates
    $urlRouterProvider.otherwise(function() {
      return false;
    });
  }));

  beforeEach(inject(function($httpBackend, Auth) {
    //mock http call
    $httpBackend.when('GET', urlPrefix + '/users/self').respond(200, {
      username: 'mock_user'
    });

    Auth.reset();
  }));

  afterEach(inject(function($httpBackend) {
    $httpBackend.verifyNoOutstandingExpectation();
    $httpBackend.verifyNoOutstandingRequest();
  }));

  var login = inject(function($httpBackend, Auth) {
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
  });

  it('should successfully reauthenticate and resend', inject(function($httpBackend, Auth, User) {
    //==================
    // Step 1: login
    //==================
    login();

    //==================
    // Step 2: get user
    //==================
    var callback = {
      success: function() {},
      error: function() {}
    };

    //spy on callbacks
    spyOn(callback, 'success');
    spyOn(callback, 'error');

    //mock http calls
    $httpBackend.expect('GET', urlPrefix + '/users/self').respond(401);
    $httpBackend.expect('PUT', urlPrefix + '/auth/refresh').respond(200, {
      accessToken: 'mock_accessToken2',
      refreshToken: 'mock_refreshToken2'
    });
    $httpBackend.expect('GET', urlPrefix + '/users/self').respond(200, {
      username: 'mock_user'
    });

    //get user data
    User.self().then(callback.success, callback.error);

    //flush http responses for reauthentication
    $httpBackend.flush(2);

    //verify token
    expect(Auth.isAuthenticated()).toBe(true);
    expect(Auth.getAccessToken()).toBe('mock_accessToken2');
    expect(Auth.getRefreshToken()).toBe('mock_refreshToken2');
    expect(Auth.getUser().username).toBe('mock_user');

    //flush send original request again
    $httpBackend.flush(1);

    //verify
    expect(callback.success).toHaveBeenCalled();
  }));

  it('should successfully reauthenticate and fail resend with 401 error', inject(function($httpBackend, Auth, User) {
    //==================
    // Step 1: login
    //==================
    login();

    //==================
    // Step 2: get user
    //==================
    var callback = {
      success: function() {},
      error: function() {}
    };

    //spy on callbacks
    spyOn(callback, 'success');
    spyOn(callback, 'error');

    //mock http calls
    $httpBackend.expect('GET', urlPrefix + '/users/self').respond(401);
    $httpBackend.expect('PUT', urlPrefix + '/auth/refresh').respond(200, {
      accessToken: 'mock_accessToken2',
      refreshToken: 'mock_refreshToken2'
    });
    $httpBackend.expect('GET', urlPrefix + '/users/self').respond(401);

    //get user data
    User.self().then(callback.success, callback.error);

    //flush http responses for reauthentication
    $httpBackend.flush(2);

    //verify token
    expect(Auth.isAuthenticated()).toBe(true);
    expect(Auth.getAccessToken()).toBe('mock_accessToken2');
    expect(Auth.getRefreshToken()).toBe('mock_refreshToken2');
    expect(Auth.getUser().username).toBe('mock_user');

    //flush send original request again
    $httpBackend.flush(1);

    //verify
    expect(callback.error).toHaveBeenCalled();
    expect(Auth.isAuthenticated()).toBe(false);
    expect(Auth.getAccessToken()).toBeNull();
    expect(Auth.getRefreshToken()).toBeNull();
    expect(Auth.getUser()).toBeNull();
  }));

  it('should successfully reauthenticate and fail resend with other error', inject(function($httpBackend, Auth, User) {
    //==================
    // Step 1: login
    //==================
    login();

    //==================
    // Step 2: get user
    //==================
    var callback = {
      success: function() {},
      error: function() {}
    };

    //spy on callbacks
    spyOn(callback, 'success');
    spyOn(callback, 'error');

    //mock http calls
    $httpBackend.expect('GET', urlPrefix + '/users/self').respond(401);
    $httpBackend.expect('PUT', urlPrefix + '/auth/refresh').respond(200, {
      accessToken: 'mock_accessToken2',
      refreshToken: 'mock_refreshToken2'
    });
    $httpBackend.expect('GET', urlPrefix + '/users/self').respond(400);

    //get user data
    User.self().then(callback.success, callback.error);

    //flush http responses for reauthentication
    $httpBackend.flush(2);

    //verify token
    expect(Auth.isAuthenticated()).toBe(true);
    expect(Auth.getAccessToken()).toBe('mock_accessToken2');
    expect(Auth.getRefreshToken()).toBe('mock_refreshToken2');
    expect(Auth.getUser().username).toBe('mock_user');

    //flush send original request again
    $httpBackend.flush(1);

    //verify
    expect(callback.error).toHaveBeenCalled();
  }));

  it('should fail reauthenticate', inject(function($httpBackend, Auth, User) {
    //==================
    // Step 1: login
    //==================
    login();

    //==================
    // Step 2: get user
    //==================
    var callback = {
      success: function() {},
      error: function() {}
    };

    //spy on callbacks
    spyOn(callback, 'success');
    spyOn(callback, 'error');

    //mock http calls
    $httpBackend.expect('GET', urlPrefix + '/users/self').respond(401);
    $httpBackend.expect('PUT', urlPrefix + '/auth/refresh').respond(400);

    //get user data
    User.self().then(callback.success, callback.error);

    //flush http responses
    $httpBackend.flush(2);

    //verify
    expect(callback.error).toHaveBeenCalled();
    expect(Auth.isAuthenticated()).toBe(false);
    expect(Auth.getAccessToken()).toBeNull();
    expect(Auth.getRefreshToken()).toBeNull();
    expect(Auth.getUser()).toBeNull();
  }));
});
