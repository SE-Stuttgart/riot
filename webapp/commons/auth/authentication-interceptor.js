angular.module('riot').factory('AuthenticationInterceptor', function($q, $injector) {
  var errorCount = 0;
  var interceptor = {
    request: function(config) {
      var Auth = $injector.get('Auth');
      if (Auth.hasToken()) {
        config.headers['Access-Token'] = Auth.getAccessToken();
      }

      return config;
    },
    response: function(response) {
      errorCount = 0;
      return response;
    },
    responseError: function(response) {
      if (response.status === 401) {
        var Auth = $injector.get('Auth');
        var $http = $injector.get('$http');
        var deferred = $q.defer();

        //refresh tokens
        if (errorCount++ === 0) {
          Auth.refresh(Auth.refreshToken).then(deferred.resolve, deferred.reject);

          return deferred.promise.then(function(data) {
            //send the previous request again
            return $http(response.config);
          });
        }
        else {
          errorCount = 0;
          Auth.reset();
        }
      }

      return $q.reject(response);
    }
  };

  return interceptor;
});

angular.module('riot').config(function($httpProvider) {
  $httpProvider.interceptors.push('AuthenticationInterceptor');
});
