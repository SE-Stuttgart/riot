angular.module('riot').config(function($stateProvider) {
  $stateProvider.state('app.devices', {
    url: '/devices',
    templateUrl: 'views/app/devices/devices.html'
  });
});

angular.module('riot').controller('DevicesCtrl', function($scope) {

});
