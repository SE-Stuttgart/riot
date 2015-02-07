angular.module('riot').config(function($stateProvider) {
  $stateProvider.state('app.dashboard', {
    url: '/dashboard',
    templateUrl: 'views/app/dashboard/dashboard.html'
  });
});

angular.module('riot').controller('DashboardCtrl', function($scope) {
  $scope.items = {};

  $scope.items.devices = [{
    name: "Device 1",
    link: "http://www.google.de",
    type: "ok"
  }, {
    name: "Device 2Device 2Device 2Device 2Device 2Device 2Device 2Device 2Device 2Device 2Device 2Device 2Device 2Device 2Device 2Device 2Device 2",
    link: "http://www.google.de",
    type: "ok"
  }, {
    name: "Device 3",
    link: "http://www.google.de",
    type: "error"
  }, {
    name: "Device 4",
    link: "http://www.google.de",
    type: "error"
  }, {
    name: "Device 5",
    link: "http://www.google.de",
    type: "warning"
  }, {
    name: "Device 6",
    link: "http://www.google.de",
    type: "warning"
  }];

  $scope.items.errWarn = [{
    name: "Error 1",
    link: "http://www.google.de",
    type: "error"
  }, {
    name: "Warning 1",
    link: "http://www.google.de",
    type: "warning"
  }, {
    name: "Warning 2",
    link: "http://www.google.de",
    type: "warning"
  }, {
    name: "Error 2",
    link: "http://www.google.de",
    type: "error"
  }, {
    name: "Error 3",
    link: "http://www.google.de",
    type: "error"
  }, {
    name: "Warning 3",
    link: "http://www.google.de",
    type: "warning"
  }];

  $scope.items.events = [{
    name: "Event 1",
    link: "http://www.google.de"
  }, {
    name: "Event 2",
    link: "http://www.google.de"
  }, {
    name: "Event 3",
    link: "http://www.google.de"
  }, {
    name: "Event 4",
    link: "http://www.google.de"
  }, {
    name: "Event 5",
    link: "http://www.google.de"
  }];

  $scope.log = {};
  $scope.log.items = [{
    text: "This is a test text 1",
    important: "Error",
    time: new Date(),
    icon: "fa-exclamation-circle"
  }, {
    text: "This is a test text 2",
    important: "Warning",
    time: new Date(),
    icon: "fa-info-circle"
  }, {
    text: "This is a test text 3",
    time: new Date()
  }];
});
