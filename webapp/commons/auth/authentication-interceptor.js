angular.module('riot').factory('AuthenticationInterceptor', function($q, $injector) {
  var processingRefreshRequest = false;
  var interceptor = {
    request: function(config) {
      var Auth = $injector.get('Auth');
      config.headers['Access-Token'] = Auth.getAccessToken() || '';

      return config;
    },
    responseError: function(response) {
      if (response.status === 401) {
        var Auth = $injector.get('Auth');
        var $http = $injector.get('$http');
        var deferred = $q.defer();

        if (!processingRefreshRequest) {
          processingRefreshRequest = true;

          //reauthenticate using the refresh token
          Auth.refresh(Auth.refreshToken).then(
            deferred.resolve,
            deferred.reject
          );

          return deferred.promise.then(
            function(data) {
              //send the original request again
              return $http(response.config)
                .success(function() {
                  processingRefreshRequest = false;
                })
                .error(function(data, status) {
                  processingRefreshRequest = false;
                  
                  if (status === 401) {
                    Auth.reset();
                  }
                });
            },
            function() {
              Auth.reset();
              return $q.reject(response);
            });
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
