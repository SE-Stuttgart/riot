angular.module('riot').config(function($stateProvider) {
  $stateProvider.state('app.calendar.list', {
    url: '/list',
    templateUrl: 'views/app/calendar/calendar-list/calendar-list.html'
  });
});

angular.module('riot').controller('CalendarListCtrl', function($scope, $state, Calendar) {
  var init = function() {
    $scope.events = [];
    $scope.eventSources = [$scope.events];
    $scope.calendarConfig = {
      editable: false,
      header: {
        left: 'title',
        center: '',
        right: 'today prev,next'
      },
      timeFormat: 'HH:mm',
      eventClick: $scope.alertOnEventClick,
      dayClick: $scope.alertOnDayClick
    };

    $scope.getCalendarEntries();
  };

  $scope.getCalendarEntries = function() {
    $scope.events.length = 0;
    Calendar.getList().then(function(calendarEntries) {
      for (var i = 0; i < calendarEntries.length; i++) {
        $scope.events.push({
          id: calendarEntries[i].id,
          title: calendarEntries[i].title,
          allDay: calendarEntries[i].allDayEvent,
          location: calendarEntries[i].location,
          start: new Date(calendarEntries[i].startTime),
          end: new Date(calendarEntries[i].endTime)
        });
      }
    }, function(reason) {
        $scope.alertMessage = 'Error ' + reason;
    });
  };

  $scope.alertOnEventClick = function(date, jsEvent, view) {
    $state.go('app.calendar.detail', {calenderEntryId: date.id});
  };

  $scope.alertOnDayClick = function(date, jsEvent, view) {

  };

  init();
});