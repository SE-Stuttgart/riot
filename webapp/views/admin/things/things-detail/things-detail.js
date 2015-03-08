angular.module('riot').config(function($stateProvider) {
  $stateProvider.state('admin.things.detail', {
    url: '/detail/:thingid',
    templateUrl: 'views/admin/things/things-detail/things-detail.html'
  });
});

angular.module('riot').controller('ThingsAdminDetailCtrl', function($scope, $state, $stateParams, $modal, Thing, User, locale){
  var alertId = null;

  var init = function() {
    
    locale.ready('thing').then(function () {
      $scope.users.selection = locale.getString('thing.chooseShareUser');
      
      //selectedRight must be set after the locale service has initialized, in order to 
      //force the select box with the permissions to update
      $scope.selectedRight = null; 
    });
    
    $scope.users = {
      data: [],
      selection: null,
      pagination: {
        current: 1,
        limit: 10,
        total: 0
      },
      filter: null,
      update: $scope.getUsers,
      toString: function(data) {
        if(data['username']) {
          return data['username'];
        }
        return data;
      }
    };
    
    $scope.getThing();
    $scope.getSharedUsers();
  };

  $scope.getThing = function() {
    Thing.one($stateParams.thingid).get().then(function(thing) {
      $scope.thingDetail = thing;
      $scope.thingDescription = $scope.thingDetail.getDescription().$object;
    });
  };

  $scope.updateThing = function() {
    return $scope.thingDetail.put().then(function() {
        $scope.alerts.close(alertId);
        alertId = $scope.alerts.showSuccess('Successfully updated thing');
        $scope.getThing();
      }, function(reason) {
        $scope.alerts.close(alertId);
        alertId = $scope.alerts.showError('Couldn\'t update thing: ' + reason);
        $scope.getThing();
      });
  };

  $scope.removeThing = function() {
    return $scope.thingDetail.remove().then(function() {
        $state.go('admin.things.list');
      }, function(reason) {
        $scope.alerts.close(alertId);
        alertId = $scope.alerts.showError('Couldn\'t remove thing: ' + reason);
      });
  };
  
  $scope.getSharedUsers = function() {
    Thing.one($stateParams.thingid).getList('sharedWithUsers').then(function(userPermissions) {
      $scope.sharedUserPermissions = [];
      
      angular.forEach(userPermissions, function(value) {
        angular.forEach(value.value, function(permission) {
          $scope.sharedUserPermissions.push({
            user: value.key,
            permission: permission
          });
        });
      });
    });
  };
  
  $scope.getUsers = function() {
    User.getList({limit: $scope.users.pagination.limit, offset: ($scope.users.pagination.current - 1) * $scope.users.pagination.limit}).then(function(users) {
      $scope.users.pagination.limit = users.pagination.limit;
      $scope.users.pagination.total = users.pagination.total;
      $scope.users.data = users;
    });
  };
  
  $scope.share = function(userId, right) {
    if(!userId || !right) {
      return;
    }
    
    if(right === locale.getString('thing.read')) {
      right = 'READ';
    } else if (right === locale.getString('thing.control')) {
      right = 'CONTROL';
    } else if (right === locale.getString('thing.execute')) {
      right = 'EXECUTE';
    } else if (right === locale.getString('thing.delete')) {
      right = 'DELETE';
    } else if (right === locale.getString('thing.share')) {
      right = 'SHARE';
    } else if (right === locale.getString('thing.full')) {
      right = 'FULL';
    }
    
    Thing.one($stateParams.thingid).post('share', {
      userId: userId,
      permission: right
    }).then(function() {
      $scope.getSharedUsers();
    });
    
  };
  
  $scope.unshare = function(userId, right) {
    Thing.one($stateParams.thingid).post('unshare', {
      userId: userId,
      permission: right.toUpperCase()
    }).then(function() {
      $scope.getSharedUsers();
    });
  };

  init();
});
