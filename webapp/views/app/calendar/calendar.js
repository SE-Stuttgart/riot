angular.module('riot').config(function($stateProvider) {
    $stateProvider.state('app.calendar', {
        url: '/calendar',
        templateUrl: 'views/app/calendar/calendar.html'
    });
});

angular.module('riot').controller('CalendarCtrl', function($scope, $http) {
    $scope.events = [];
    $scope.eventSources = [$scope.events];
	/*
    $http.get(url).
    success(function(data, status, headers, config) {
        $scope.restData = 'OK: STATUS CODE ' + status + " " + data;
    }).
    error(function(data, status, headers, config) {
        $scope.restData = 'STATUS CODE ' + status + " " + data;
    });
	*/
	$http.get('https://localhost:8181/riot/api/v1/calendar').then(function(response) {
	//Restangular.all('calendar').getList().then(function(events) {
		$scope.restData = response.data;
		//$scope.restData = events;
		var restDataLength = $scope.restData.length;
		for (var i = 0; i < restDataLength; i++) {
			var rd = $scope.restData[i];
			$scope.events.push({
				id: rd.id,
				title: rd.title,
				allDay: rd.allDayEvent,
				start: new Date(rd.startTime),
				end: new Date(rd.endTime)
			});
		}
	}, function(reason) {
		$scope.alertMessage = 'Error ' + reason;
	});
    $scope.alertOnEventClick = function(date, jsEvent, view) {
        $scope.alertMessage = date.title + ' ' + date.start + ' ' + date.end + ' was clicked ';
    };
	$scope.alertOnDayClick = function(date, jsEvent, view) {
        $scope.alertMessage = date + ' was clicked ';
    };
    $scope.uiConfig = {
        calendar: {
            editable: true,
            header: {
                left: 'title',
                center: '',
                right: 'today prev,next'
            },
            eventClick: $scope.alertOnEventClick,
			dayClick: $scope.alertOnDayClick
        }
    };
    $scope.uiConfig.calendar.dayNames = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
    $scope.uiConfig.calendar.dayNamesShort = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];
});