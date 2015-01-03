angular.module('riot').config(function($stateProvider) {
  $stateProvider.state('app.calendar', {
    url: '/calendar',
    templateUrl: 'views/app/calendar/calendar.html'
  });
});

angular.module('riot').controller('CalendarCtrl',function($scope){


});
