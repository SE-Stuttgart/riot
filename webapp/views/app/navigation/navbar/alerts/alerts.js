angular.module('riot').directive('alerts', function() {
  return {
    restrict: 'A',
    replace: true,
    templateUrl: 'views/app/navigation/navbar/alerts/alerts.html',
    controller: function($scope, Notification) {
      var socketNotifications = [];
      
      Notification.getList().then(function(notifications) {
        $scope.notifications = notifications;
      });
      
      $scope.$on('Socket:notificationDismissed', function(event, dismissedNotification) {
        var idToRemove = null;
        angular.forEach($scope.notifications, function(notification, key) {
          if(notification.id === dismissedNotification.id) {
            idToRemove = key;
          }
        });
        $scope.notifications.splice(idToRemove, 1);
      });
      
      $scope.$on('Socket:notification', function(event, undismissedNotifications) {
        angular.forEach(undismissedNotifications, function(notification) {
          if(!_.contains(socketNotifications, notification.id)) {
            $scope.notifications.unshift(notification);
            socketNotifications.push(notification.id);
          }
        });    
      });
      
    }
  };
});
