angular.module('riot').config(function($stateProvider, $urlRouterProvider) {
  $stateProvider.state('app', {
    abstract: true,
    url: '/app',
    templateUrl: 'views/app/app.html',
    data: {
      auth: {
        authentication: true
      }
    }
  });
  $urlRouterProvider.when('/app', '/app/dashboard');
});

angular.module('riot').controller('AppCtrl', function($scope, $rootScope, $state, $location, $anchorScroll) {
  $rootScope.$on('auth-logout', function(e, args) {
    $state.go('landing.home');
  });

  //alert stuff
  $scope.alert = {};
  $scope.alert.hide = true;

  $scope.alert.close = function() {
    $scope.alert.hide = true;
  };

  $scope.alert.showSuccess = function(msg) {
    showAlert(msg, 'success');
  }

  $scope.alert.showInfo = function(msg) {
    showAlert(msg, 'info');
  }

  $scope.alert.showWarning = function(msg) {
    showAlert(msg, 'warning');
  }

  $scope.alert.showError = function(msg) {
    showAlert(msg, 'danger');
  }

  var showAlert = function(msg, type) {
    $scope.alert.msg = msg;
    $scope.alert.type = type;
    $scope.alert.hide = false;

    $location.hash('alert');
    $anchorScroll();
  }

  $rootScope.$on('$stateChangeStart', function(e, state) {
    //close alert on view change
    $scope.alert.hide = true;
  });
});
