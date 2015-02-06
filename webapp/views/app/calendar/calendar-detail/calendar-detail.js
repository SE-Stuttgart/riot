angular.module('riot').config(function($stateProvider) {
  $stateProvider.state('app.calendar.detail', {
    url: '/detail/:calenderEntryId',
    templateUrl: 'views/app/calendar/calendar-detail/calendar-detail.html'
  });
});

angular.module('riot').controller('CalendarDetailCtrl', function($scope, $stateParams, Calendar) {
  var alertId = null;

  var init = function() {
    $scope.getCalendarEntry();
    $scope.datepickerOptions = {
      format: 'dd.MM.yyyy',
      formatYear: 'yy',
      startingDay: 1,
      currentText: 'Today',
      clearText: 'Clear',
      closeText: 'Close'
    };
    $scope.timepickerOptions = {
      meridian: false,
      minuteStep: 5,
      hourStep: 1
    };
    $scope.a = new Date();
  };

  $scope.getCalendarEntry = function() {
    Calendar.one($stateParams.calenderEntryId).get().then(function(calendarEntry) {
      $scope.calendarEntryDetail = calendarEntry;
    });
  };

  $scope.save = function() {
    return $scope.calendarEntryDetail.put().then(function() {
        $scope.alerts.close(alertId);
        alertId = $scope.alerts.showSuccess('Successfully updated calendar entry');
        $scope.getCalendarEntry();
      }, function(reason) {
        $scope.alerts.close(alertId);
        alertId = $scope.alerts.showError('Couldn\'t update calendar entry: ' + reason);
        $scope.getCalendarEntry();
      });
  };

  init();
});
