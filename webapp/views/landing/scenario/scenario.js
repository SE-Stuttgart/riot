angular.module('riot').config(function($stateProvider) {
  $stateProvider.state('landing.scenario', {
    url: '/scenario',
    templateUrl: 'views/landing/scenario/scenario.html'
  });
});

angular.module('riot').controller('ScenarioCtrl', function($scope) {


});
