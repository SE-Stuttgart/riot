angular.module('riot').config(function($stateProvider, $urlRouterProvider) {
  $stateProvider.state('app.notifications', {
    url: '/notifications',
    templateUrl: 'views/app/notifications/notifications.html'
  });
});

angular.module('riot').controller('NotificationsCtrl',function($scope, Thing, Notification, Socket, Restangular){
    
  var thingIdsToFetch = []; //hold the IDs of the things, which are already getting fetched. Avoids unnecessary fetching of things
  var pagination = {};
  var socketNotifications = [];
  
  pagination.limit = 20;
  pagination.offset = 0;
  
  $scope.things = {};
  $scope.notifications = [];
  
  $scope.$on('Socket:notification', function(event, undismissedNotifications) {
    angular.forEach(undismissedNotifications, function(notification) {
      if(!_.contains(socketNotifications, notification.id)) {
        $scope.notifications.unshift(notification);
        socketNotifications.push(notification.id);
      }
    });    
  });
  
  $scope.fetchNotificationsAndThings = function() {
   $scope.loadMore = true;
    Notification.one('all').get({limit: pagination.limit, offset: pagination.offset}).then(function(notifications) {
      $scope.notifications.push.apply($scope.notifications, notifications);
      fetchThings();
      pagination.offset = pagination.offset + pagination.limit;
      $scope.loadMore = false;
    }, function() {
      $scope.loadMore = false;
    });
  };
  
  $scope.dismissNotifications = function() {
    angular.forEach($scope.notifications, function(notification) {
      $scope.dismissNotification(notification);
    });
  };
  
  $scope.dismissNotification = function(notification) {
    if(!notification.dismissed) {
      notification.dismissed = true;
      Restangular.restangularizeElement(null, notification, "notifications");
      notification.put();
    }
  };
  
  function fetchThings() {
    angular.forEach($scope.notifications, function(notification) {
      if(thingIdsToFetch.indexOf(notification.thingID) === -1) {
        thingIdsToFetch.push(notification.thingID);
        Thing.one(notification.thingID).get().then(function(thing) {
          $scope.things[notification.thingID] = thing;
        });
      }
    });
  }
});