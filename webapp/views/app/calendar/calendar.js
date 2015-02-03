angular.module('riot').config(function($stateProvider) {
    $stateProvider.state('app.calendar', {
        url: '/calendar',
        templateUrl: 'views/app/calendar/calendar.html'
    });
});

angular.module('riot').controller('CalendarCtrl', function($scope, Calendar) {
    $scope.events = [];
    $scope.eventSources = [$scope.events];
	Calendar.getList().then(function(events) {
		$scope.restData = events;
		var restDataLength = $scope.restData.length;
		for (var i = 0; i < restDataLength; i++) {
			var rd = $scope.restData[i];
			$scope.events.push({
				id: rd.id,
				title: rd.title,
				allDay: rd.allDayEvent,
				location: rd.location,
				//description: rd.description,
				start: new Date(rd.startTime),
				end: new Date(rd.endTime)
			});
		}
	}, function(reason) {
		$scope.alertMessage = 'Error ' + reason;
	});
    $scope.alertOnEventClick = function(date, jsEvent, view) {
        $("#startTime").html(moment(date.start).format('DD.MM.YYYY hh:mm'));
        $("#endTime").html(moment(date.end).format('DD.MM.YYYY hh:mm'));
		$("#location").html(date.location);
        $("#eventInfo").html(date.description);
        $("#eventContent").dialog({ modal: true, title: date.title, width:350});
        //$scope.alertMessage = date.title + ' ' + date.start + ' ' + date.end + ' was clicked ';
    };
	$scope.alertOnDayClick = function(date, jsEvent, view) {
        //$scope.alertMessage = date + ' was clicked ';
    };
    $scope.uiConfig = {
        calendar: {
            editable: false,
            header: {
				left: 'title',
				center: '',
				right: 'today prev,next'
            },
			timeFormat: 'HH:mm',
            eventClick: $scope.alertOnEventClick,
			dayClick: $scope.alertOnDayClick
        }
    };
});