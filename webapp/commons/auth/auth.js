angular.module('riot').factory('Auth', function($q, $rootScope, $http, localStorageService) {
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
    hasAuthentication: function() {
      return accessToken !== null;
    },
    isAuthenticated: function() {
      return !$.isEmptyObject(user);
    },
    hasRole: function(role) {
      console.error("TODO check role");
      return false;
    },
    hasPermission: function(permission) {
      console.error("TODO check permission");
      return false;
    },
    reset: function() {
      user = null;
      accessToken = null;
      refreshToken = null;

      storage.clear();
    },
    getSelf: function() { //TODO move in user service
      var deferred = $q.defer();

      $http.get('http://localhost:8080/riot/api/v1/users/self')
        .success(function(data, status) {
          user = data;

          deferred.resolve();
        })
        .error(function(data, status) {
          data = data || {};

          var errorMessage = data.errorMessage || 'Unknown error';
          var errorCode = data.errorCode || -1;

          deferred.reject(errorMessage + ' (Error Code: ' + errorCode + ')');
        });

      return deferred.promise;
    },
    login: function(username, password) {
      var deferred = $q.defer();

      $http.put('http://localhost:8080/riot/api/v1/auth/login', {
          username: username,
          password: password
        })
        .success(function(data, status) {
          data = data || {};

          accessToken = data.accessToken;
          refreshToken = data.refreshToken;
          user = data.user;

          storage.save();

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

      $http.put('http://localhost:8080/riot/api/v1/auth/logout', {})
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

      $http.put('http://localhost:8080/riot/api/v1/auth/refresh', {
          refreshToken: refreshToken
        })
        .success(function(data, status) {
          data = data || {};

          accessToken = data.accessToken;
          refreshToken = data.refreshToken;

          storage.save();

          deferred.resolve();
        })
        .error(function(data, status) {
          data = data || {};

          var errorMessage = data.errorMessage || 'Unknown error';
          var errorCode = data.errorCode || -1;

          deferred.reject(errorMessage + ' (Error Code: ' + errorCode + ')');
        });

      return deferred.promise;
    }
  };

  //make auth functions available to all scopes
  $rootScope.user = service.getUser;
  $rootScope.accessToken = service.getAccessToken;
  $rootScope.refreshToken = service.getRefreshToken;
  $rootScope.hasAuthentication = service.hasAuthentication;
  $rootScope.isAuthenticated = service.isAuthenticated;
  $rootScope.hasRole = service.hasRole;
  $rootScope.hasPermission = service.hasPermission;
  $rootScope.login = service.login;
  $rootScope.logout = service.logout;
  $rootScope.refresh = service.refresh;

  //init
  storage.load();
  if (service.hasAuthentication()) {
    service.getSelf();
  }

  return service;
});
