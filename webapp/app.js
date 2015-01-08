angular.module('riot', ['ui.bootstrap', 'ui.utils', 'ui.router', 'ngAnimate', 'restangular', 'LocalStorageModule']);

angular.module('riot').run(function($rootScope, $state, Auth) {
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
