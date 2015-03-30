angular.module('riot').config(function($stateProvider) {
  $stateProvider.state('app.dashboard', {
    url: '/dashboard',
    templateUrl: 'views/app/dashboard/dashboard.html'
  });
});

angular.module('riot').controller('DashboardCtrl', function($scope, Restangular, Thing, Calendar, Notification) {
  var fetchedThingIds = [];
  var socketNotifications = [];
  
  $scope.items = {};
  $scope.items.things = [];
  $scope.items.events = [];

  $scope.log = {};
  $scope.things = [];
  
  Thing.getList({limit: 5, offset: 0}).then(function(things) {
    $scope.things = things;
    angular.forEach(things, function(thing) {
      var itemThing = {};
      itemThing.name = thing.metainfo.name;
      itemThing.state = 'app.things.detail';
      itemThing.stateParams = {thingid: thing.id};
      itemThing.type = null;
      $scope.items.things.push(itemThing);
      
      fetchedThingIds.push(thing.id);
    });
  });
  
  Calendar.getList({limit: 5, offset: 0}).then(function(events) {
    angular.forEach(events, function(event) {
      var itemEvent = {};
      itemEvent.name = event.title;
      itemEvent.state = 'app.calendar.detail';
      itemEvent.stateParams = {calenderEntryId: event.id};
      itemEvent.type = null;
      $scope.items.events.push(itemEvent);
    });
  });
  
  Notification.one('all').get({limit: 10, offset: 0}).then(function(notifications) {
    $scope.log.notifications = notifications;
    
    angular.forEach(notifications, function(notification) {
      if(!_.contains(fetchedThingIds, notification.thingID)) {
        fetchedThingIds.push(notification.thingID);
        Thing.one(notification.thingID).get().then(function(thing) {
          $scope.things.push(thing);
        });
      }
    });
  });
  
  $scope.$on('Socket:notification', function(event, undismissedNotifications) {
    angular.forEach(undismissedNotifications, function(notification) {
      if(!_.contains(socketNotifications, notification.id)) {
        $scope.log.notifications.unshift(notification);
        socketNotifications.push(notification.id);
        
        if($scope.log.notifications.length > 10) {
          $scope.log.notifications.pop();
        }
      }
    });    
  });
  
  $scope.getThingName = function(thingId) {
    var thingName = "";
    angular.forEach($scope.things, function(thing) {
      if(thing.id === thingId) {
        thingName = thing.metainfo.name;
      }
    });
    return thingName;
  };
  
  $scope.dismissNotification = function(notification) {
    if(!notification.dismissed) {
      notification.dismissed = true;
      Restangular.restangularizeElement(null, notification, "notifications");
      notification.put();
    }
  };
});
