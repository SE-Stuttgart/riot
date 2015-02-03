angular.module('riot', ['ui.bootstrap', 'ui.utils', 'ui.router', 'ngAnimate', 'restangular', 'LocalStorageModule']);

angular.module('riot').run(function($rootScope, $state, $sce, Auth) {
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

  //dialog
  $rootScope.dialog = {
    header: '',
    body: '',
    footer: '<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>',
    show: function() {
      $('#dialog').modal('show');
    },
    hide: function() {
      $('#dialog').modal('hide');
    }
  };

  $rootScope.$watch('dialog.header', function(newValue, oldValue) {
    if (typeof newValue === 'string') {
      $rootScope.dialog.header = $sce.trustAsHtml(newValue || '');
    }
  });

  $rootScope.$watch('dialog.body', function(newValue, oldValue) {
    if (typeof newValue === 'string') {
      $rootScope.dialog.body = $sce.trustAsHtml(newValue || '');
    }
  });

  $rootScope.$watch('dialog.footer', function(newValue, oldValue) {
    if (typeof newValue === 'string') {
      $rootScope.dialog.footer = $sce.trustAsHtml(newValue || '');
    }
  });
});
