angular.module('riot').factory('Auth', function($q, $rootScope, $http, localStorageService, User) {
  var user = null;
  var accessToken = null;
  var refreshToken = null;

  var storage = {
    load: function() {
      accessToken = localStorageService.get('accessToken');
      refreshToken = localStorageService.get('refreshToken');
    },
    save: function() {
      localStorageService.set('accessToken', accessToken);
      localStorageService.set('refreshToken', refreshToken);
    },
    clear: function() {
      localStorageService.remove('accessToken');
      localStorageService.remove('refreshToken');
    }
  };

  var service = {
    getUser: function() {
      return user;
    },
    getAccessToken: function() {
      return accessToken;
    },
    getRefreshToken: function() {
      return refreshToken;
    },
    isAuthenticated: function() {
      return accessToken != null && refreshToken != null;
    },
    hasRole: function(role) {
      console.error("TODO check role");
      return true;
    },
    hasPermission: function(permission) {
      console.error("TODO check permission");
      return true;
    },
    reset: function() {
      user = null;
      accessToken = null;
      refreshToken = null;

      storage.clear();

      $rootScope.$broadcast('auth-logout');
    },
    login: function(username, password) {
      var deferred = $q.defer();

      $http.put('/api/v1/auth/login', {
          username: username,
          password: password
        })
        .success(function(data, status) {
          data = data || {};

          accessToken = data.accessToken;
          refreshToken = data.refreshToken;
          user = data.user;

          storage.save();

          $rootScope.$broadcast('auth-login');

          deferred.resolve();
        })
        .error(function(data, status) {
          data = data || {};

          var errorMessage = data.errorMessage || 'Unknown error';
          var errorCode = data.errorCode || -1;

          service.reset();

          deferred.reject(errorMessage + ' (Error Code: ' + errorCode + ')');
        });

      return deferred.promise;
    },
    logout: function() {
      var deferred = $q.defer();

      $http.put('/api/v1/auth/logout', {})
        .success(function(data, status) {
          service.reset();

          deferred.resolve();
        })
        .error(function(data, status) {
          data = data || {};

          var errorMessage = data.errorMessage || 'Unknown error';
          var errorCode = data.errorCode || -1;

          service.reset();

          deferred.reject(errorMessage + ' (Error Code: ' + errorCode + ')');
        });

      return deferred.promise;
    },
    refresh: function() {
      var deferred = $q.defer();

      $http.put('/api/v1/auth/refresh', {
          refreshToken: refreshToken
        })
        .success(function(data, status) {
          data = data || {};

          accessToken = data.accessToken;
          refreshToken = data.refreshToken;

          storage.save();

          $rootScope.$broadcast('auth-refresh');

          deferred.resolve();
        })
        .error(function(data, status) {
          data = data || {};

          var errorMessage = data.errorMessage || 'Unknown error';
          var errorCode = data.errorCode || -1;

          service.reset();

          deferred.reject(errorMessage + ' (Error Code: ' + errorCode + ')');
        });

      return deferred.promise;
    }
  };

  //make auth functions available to all scopes
  $rootScope.user = service.getUser;
  $rootScope.accessToken = service.getAccessToken;
  $rootScope.refreshToken = service.getRefreshToken;
  $rootScope.isAuthenticated = service.isAuthenticated;
  $rootScope.hasRole = service.hasRole;
  $rootScope.hasPermission = service.hasPermission;
  $rootScope.login = service.login;
  $rootScope.logout = service.logout;
  $rootScope.refresh = service.refresh;

  //init
  storage.load();
  User.self().then(
    function(u) {
      user = u;
    },
    function() {
      service.reset();
    });

  return service;
});

angular.module('riot').run(function($rootScope, Auth) {
  //check authorization on state change
  $rootScope.$on('$stateChangeStart', function(e, state) {
    state.data = state.data || {};
    var auth = state.data.auth;
    if (auth) {
      var roles = auth.roles;
      var premissions = auth.premissions;
      var authenticated = (auth.authentication) ? true : false;
      var grant = false;

      //check roles
      if (roles) {
        for (var i = 0; i < roles.length; i++) {
          grant = grant || $rootScope.hasRole(roles[i]);
        }
      }
      //check permissions
      else if (premissions) {
        for (var j = 0; j < roles.length; j++) {
          grant = grant || $rootScope.hasPermission(roles[j]);
        }
      }
      //check authentication
      else if (authenticated) {
        grant = $rootScope.isAuthenticated();
      }

      if (!grant) {
        e.preventDefault();
        $state.go('landing.home');
      }
    }
  });
});
