angular.module('riot').config(function($stateProvider) {
  $stateProvider.state('admin.things.detail', {
    url: '/detail/:thingid',
    templateUrl: 'views/admin/things/things-detail/things-detail.html'
  });
});

angular.module('riot').controller('ThingsDetailCtrl', function($scope, $rootScope, $state, $stateParams, $modal, Thing, User, locale){
  var alertId = null;

  var init = function() {
    locale.ready('thing').then(function () {
      $scope.users.selection = locale.getString('thing.chooseShareUser');
      
      //selectedRight must be set after the locale service has initialized, in order to 
      //force the select box with the permissions to update
      $scope.selectedRight = null; 
    });

    $scope.$watch('thingChildrenTree.currentNode', function(newObj, oldObj) {
      if($scope.thingChildrenTree && angular.isObject($scope.thingChildrenTree.currentNode) ) {
        var id = $scope.thingChildrenTree.currentNode.id;
        $state.go('app.things.detail', {thingid: id});
      }
    }, false);

    $scope.things = {
      data: [],
      selection: null,
      pagination: {
        current: 1,
        limit: 10,
        total: 0
      },
      filter: {
        property: 'name',
        operator: 'CT',
        value: null
      },
      disabled: [],
      update: $scope.getThings,
      toString: function(dataEntry) {
        return dataEntry['metainfo']['name'];
      }
    };
    
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

  $scope.getThings = function() {
    //pagination
    var parameters = { 
      limit: $scope.things.pagination.limit,
      offset: ($scope.things.pagination.current - 1) * $scope.things.pagination.limit
    };

    //filter
    if ($scope.things.filter.value) {
      parameters[$scope.things.filter.property + '_' + $scope.things.filter.operator] = $scope.things.filter.value;
    }

    Thing.getList(parameters).then(function(things) {
      $scope.things.pagination.limit = things.pagination.limit;
      $scope.things.pagination.total = things.pagination.total;
      $scope.things.data = things;
    });
  };

  $scope.getThing = function() {
    Thing.one($stateParams.thingid).get({return: ['ALLCHILDREN', 'METAINFO']}).then(function(thing) {
      //request parent thing
      $scope.things.disabled = [thing];
      Thing.one(thing.metainfo.parentId).get().then(function(thing) {
        $scope.things.selection = thing;
      });

      $scope.thingDetail = thing;
      $scope.thingDetail.getDescription().then(function(description) {
        $scope.thingDescription = description;
      
        $scope.thingState = {};
        $scope.thingDetail.getState().then(function(state) {
          angular.forEach(state.propertyValues, function (propertyValueValue, propertyValueKey) {
            if(angular.isArray(propertyValueValue)) {
              this[propertyValueKey] = propertyValueValue[1];
            } else {
              this[propertyValueKey] = propertyValueValue;
            }
          }, $scope.thingState);
        });
      });
    });
  };

  $scope.updateThing = function() {
    if ($scope.things.selection) {
      $scope.thingDetail.metainfo.parentId = $scope.things.selection.id;
    }
    else {
      $scope.thingDetail.metainfo.parentId = null;
    }

    return $scope.thingDetail.updateMetainfo().then(function() {
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
    Thing.one($stateParams.thingid).getList('sharesWithUsers').then(function(userPermissions) {
      $scope.sharedUserPermissions = [];
      
      angular.forEach(userPermissions, function(userPermission) {
        angular.forEach(userPermission.permissions, function(permission) {
          if(userPermission.user.username !== $rootScope.user().username) {
            $scope.sharedUserPermissions.push({
              user: userPermission.user,
              permission: permission,
              allPermissions: userPermission.permissions
            });
          }
        });
      });
    });
  };
  
  $scope.getUsers = function() {
    User.getList({limit: $scope.users.pagination.limit, offset: ($scope.users.pagination.current - 1) * $scope.users.pagination.limit}).then(function(users) {
      $scope.users.pagination.limit = users.pagination.limit;
      $scope.users.pagination.total = users.pagination.total;
      
      //remove current user from array, so he can not be used for sharing
      for (var i = 0; i < users.length; i++) {
        if(users[i].username === $rootScope.user().username) {
          users.splice(i, 1);
          break;
        }
      }
      
      $scope.users.data = users;
    });
  };
  
  $scope.share = function(userId, permission) {
    if(!userId || !permission) {
      return;
    }
    
    Thing.one($stateParams.thingid).one('shares').get().then(function(thingShares) {
      
      var permissions = [];
      angular.forEach(thingShares, function(thingShare) {
        if(thingShare.userId === userId) {
          permissions = thingShare.permissions;
        }
      });
      
      if(permission === locale.getString('thing.read')) {
        pushIfNotExistsInArray(permissions, 'READ');
      } else if (permission === locale.getString('thing.control')) {
        pushIfNotExistsInArray(permissions, 'CONTROL');
      } else if (permission === locale.getString('thing.execute')) {
        pushIfNotExistsInArray(permissions, 'EXECUTE');
      } else if (permission === locale.getString('thing.delete')) {
        pushIfNotExistsInArray(permissions, 'DELETE');
      } else if (permission === locale.getString('thing.share')) {
        pushIfNotExistsInArray(permissions, 'SHARE');
      } else if (permission === locale.getString('thing.full')) {
        pushIfNotExistsInArray(permissions, 'READ');
        pushIfNotExistsInArray(permissions, 'CONTROL');
        pushIfNotExistsInArray(permissions, 'EXECUTE');
        pushIfNotExistsInArray(permissions, 'DELETE');
        pushIfNotExistsInArray(permissions, 'SHARE');
      }

      
      Thing.one($stateParams.thingid).post('shares', {
        userId: userId,
        permissions: permissions
      }).then(function() {
        $scope.getSharedUsers();
      });
    });
  };
  
  $scope.unshare = function(userPermissions, permission, userId) {
    permission = permission.toUpperCase();
  
    if(userPermissions.indexOf(permission) !== -1) {
      userPermissions.splice(userPermissions.indexOf(permission), 1);
      Thing.one($stateParams.thingid).post('shares', {
        userId: userId,
        permissions: userPermissions
      }).then(function() {
        $scope.getSharedUsers();
      });
    }
  };  

  $scope.$on('Socket:propertyChange:' + $stateParams.thingid, function(ev, propertyChange) {
    var propertyName = propertyChange.name.substr(0, propertyChange.name.length - 7);
    if($scope.thingState && $scope.thingState[propertyName] !== undefined) {
      if(angular.isArray(propertyChange.newValue)) {
        $scope.thingState[propertyName] = propertyChange.newValue[1];
      } else {
        $scope.thingState[propertyName] = propertyChange.newValue;
      }
    } 
    console.log("websocket: " + propertyName + ": " + $scope.thingState[propertyName]);
  });
  
  var pushIfNotExistsInArray = function(array, value) {
    if(array.indexOf(value) === -1) {
      array.push(value);
    }
  };

  $scope.save = function() {
    $scope.updateThing();
  };

  init();
});

angular.module('riot').controller('PropertyCtrl', function($scope, Thing){
  if($scope.property.writable) {
    console.log($scope.property.name + " is writable: " + 'thingState["' + $scope.property.name + '"]');
    $scope.$watch('thingState["' + $scope.property.name + '"]', function(newValue) {
      console.log("normal watch: " + newValue);
      Thing.one($scope.thingDetail.id).post('action', { 
        type: 'de.uni_stuttgart.riot.thing.PropertySetAction$Instance', 
        thingId: $scope.thingDetail.id, 
        name: $scope.property.name + '_set', 
        time: Date.now(), 
        newValue: newValue 
      });
    });
  }
});
