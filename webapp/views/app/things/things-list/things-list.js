angular.module('riot').config(function($stateProvider) {
  $stateProvider.state('app.things.list', {
    url: '/list',
    templateUrl: 'views/app/things/things-list/things-list.html'
  });
});
