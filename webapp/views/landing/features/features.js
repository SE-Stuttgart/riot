angular.module('riot').config(function($stateProvider) {
  $stateProvider.state('landing.features', {
    url: '/features',
    templateUrl: 'views/landing/features/features.html'
  });
});

angular.module('riot').controller('FeaturesCtrl', function($scope) {

});
