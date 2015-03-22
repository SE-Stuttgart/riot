angular.module('riot').config(function($stateProvider) {
  $stateProvider.state('app.things.detail', {
    url: '/detail/:thingid',
    templateUrl: 'views/admin/things/things-detail/things-detail.html'
  });
});

