angular.module('riot').config(function($stateProvider) {
  $stateProvider.state('admin.things.detail', {
    url: '/detail/:thingid',
    templateUrl: 'views/admin/things/things-detail/things-detail.html'
  });
});

angular.module('riot').controller('ThingsAdminDetailCtrl', function($scope, $state, $stateParams, Thing){
  var alertId = null;

  var init = function() {
    $scope.getThing();
  };

  $scope.getThing = function() {
    Thing.one($stateParams.thingid).get().then(function(thing) {
      $scope.thingDetail = thing;
      $scope.thingDescription = $scope.thingDetail.getDescription().$object;
    });
  };

  $scope.updateThing = function() {
    return $scope.thingDetail.put().then(function() {
        $scope.alerts.close(alertId);
        alertId = $scope.alerts.showSuccess('Successfully updated thing');
        $scope.getThing();
      }, function(reason) {
        $scope.alerts.close(alertId);
        alertId = $scope.alerts.showError('Couldn\'t update thing: ' + reason);
        $scope.getThing();
      });
  };

  $scope.removeThing = function() {
    return $scope.thingDetail.remove().then(function() {
        $state.go('admin.things.list');
      }, function(reason) {
        $scope.alerts.close(alertId);
        alertId = $scope.alerts.showError('Couldn\'t remove thing: ' + reason);
      });
  };

  init();
});
