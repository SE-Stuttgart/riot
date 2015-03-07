angular.module('riot').config(function($stateProvider) {
  $stateProvider.state('admin.things.detail', {
    url: '/detail/:thingid',
    templateUrl: 'views/admin/things/things-detail/things-detail.html'
  });
});

angular.module('riot').controller('ThingsAdminDetailCtrl', function($scope, $state, $stateParams, $modal, Thing, User, locale){
  var alertId = null;

  var init = function() {
    $scope.users = [];
    $scope.limit = 10;
    $scope.current = 1;
    
    locale.ready('thing').then(function () {
      $scope.selectedUser = {};
      $scope.selectedUser.username = locale.getString('thing.chooseShareUser');
      
      //selectedRight must be set after the locale service has initialized, in order to 
      //force the select box with the permissions to update
      $scope.selectedRight = {}; 
    });
    
    $scope.getThing();
    $scope.getUsers();
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
    //TODO would it be feasible to get all users?
//    User.getList({limit: 1000, offset: 0}).then(function(users, u) {
//      $scope.users = users;
//      $scope.selectedUser = {}; //selectedUser must be set in order to update the select box, when the users array is modified
//      $scope.selectedRight = {};
//    });
  };
  
  $scope.share = function(userId, right) {
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
  
  $scope.openSelectUserModal = function() {
    var modalInstance = $modal.open({
      templateUrl: 'views/admin/things/things-detail/select-user-modal/select-user-modal.html',
      controller: 'SelectUserModalCtrl',
      size: 'lg'
    });

    modalInstance.result.then(function (selectedItem) {
      $scope.selectedUser = selectedItem;
    });
  };

  init();
});
