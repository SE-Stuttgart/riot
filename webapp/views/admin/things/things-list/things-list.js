angular.module('riot').config(function($stateProvider) {
  $stateProvider.state('admin.things.list', {
    url: '/list',
    templateUrl: 'views/admin/things/things-list/things-list.html'
  });
});

angular.module('riot').controller('ThingsListCtrl',function($scope, Thing, locale){
  var init = function() {
    $scope.pagination = {
      current: 1,
      limit: 10,
      total: 0
    };
    $scope.filter = {
      filters: [],
      properties: [
        {
          key: 'type',
          name: locale.getString('thing.type')
        }
      ]
    };

    $scope.getThings();
  };

  $scope.getThings = function() {
    //pagination
    var parameters = {
      limit: $scope.pagination.limit,
      offset: ($scope.pagination.current - 1) * $scope.pagination.limit
    };

    //filters
    for (var i = 0; i < $scope.filter.filters.length; i++) {
      var filter = $scope.filter.filters[i];
      parameters[filter.property + '_' + filter.operator] = filter.value;
    }

    Thing.getList(parameters).then(function(things) {
      $scope.pagination.limit = things.pagination.limit;
      $scope.pagination.total = things.pagination.total;
      $scope.things = things;
      $scope.getThingsDescription();
      $scope.getThingsState();
      $scope.getThingslastConnection();
    });
  };

  $scope.getThingsDescription = function() {
    for (var i = 0; i < $scope.things.length; i++) {
      $scope.things[i].description = $scope.things[i].getDescription().$object;
    }
  };

  $scope.getThingsState = function() {
    for (var i = 0; i < $scope.things.length; i++) {
      $scope.things[i].state = $scope.things[i].getState().$object;
    }
  };

  $scope.getThingslastConnection = function() {
    for (var i = 0; i < $scope.things.length; i++) {
      $scope.things[i].lastConnection = $scope.things[i].getLastConnection().$object;
    }
  };

  $scope.convertThingPropertyValue = function(value) {
    if (angular.isArray(value)) {
      return value[1];
    }
    else {
      return value;
    }
  };

  locale.ready('thing').then(init);
});
