angular.module('riot').config(function($stateProvider) {
  $stateProvider.state('app.things.list', {
    url: '/list',
    templateUrl: 'views/app/things/things-list/things-list.html'
  });
});

angular.module('riot').controller('ThingsUserListCtrl',function($scope, Thing){
  var init = function() {
    $scope.current = 1;
    $scope.limit = 10;
    $scope.total = 10;
    $scope.filter = null;

    $scope.getThings();
  };

  $scope.getThings = function() {
    Thing.getList({limit: $scope.limit, offset: ($scope.current - 1) * $scope.limit}).then(function(things) {
      $scope.things = things;
    });
  };

  $scope.$watch('limit', function() {
    $scope.getThings();
  });

  init();
});
