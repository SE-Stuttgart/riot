angular.module('riot').config(function($stateProvider) {
  $stateProvider.state('app.calendar.detail', {
    url: '/detail/:calenderEntryId',
    templateUrl: 'views/app/calendar/calendar-detail/calendar-detail.html'
  });
});

angular.module('riot').controller('CalendarDetailCtrl', function($scope, $stateParams, Calendar) {
  var init = function() {
    $scope.getCalendarEntry();
  };

  $scope.getCalendarEntry = function() {
    Calendar.one($stateParams.calenderEntryId).get().then(function(calendarEntry) {
      $scope.calendarEntryDetail = calendarEntry;
    });
  };

  $scope.save = function() {
    $scope.calendarEntryDetail.put().then(function() {
      $scope.getCalendarEntry();
    });
  };

  init();
});
