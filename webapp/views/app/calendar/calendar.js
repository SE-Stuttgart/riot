angular.module('riot').config(function($stateProvider, $urlRouterProvider) {
  $stateProvider.state('app.calendar', {
    abstract: true,
    url: '/calendar',
    templateUrl: 'views/app/calendar/calendar.html'
  });
  $urlRouterProvider.when('/app/calendar', '/app/calendar/list');
});

angular.module('riot').controller('CalendarCtrl', function($scope) {

});