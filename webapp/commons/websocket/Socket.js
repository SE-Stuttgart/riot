angular.module('riot').factory('Socket',function($rootScope, $websocket, $log, $location, Auth) {
  //the socket connection
  var dataStream;

  //contains only notifications, which are not dismissed yet
  var notifications = [];
  
  //hack, so it works on the server and local
  //TODO remove in final version or find working proxy for websockets
  if($location.port() === 9001) {
    dataStream = $websocket('wss://' + $location.host() + ':8181/riot/connect/' + Auth.getAccessToken());
  } else {
    dataStream = $websocket('wss://' + $location.host() + ':' + $location.port() + '/connect/' + Auth.getAccessToken());
  }

  dataStream.onMessage(function(message) {
    var data = JSON.parse(message.data);
    if(data.type === 'NotificationMessage') {
      handleNotification(data.notification);
    } else {
      $log.log('Received unkonwn message type: ' + data.type);
    }    
  });
  
  dataStream.onError(function(event) {
    $log.error(event);
  });
  
  dataStream.onClose(function(event) {
    $log.log(event);
  });
  
  function handleNotification(notification) {
    if(!notification.dismissed) {
      //add notification to the array, because it is not dismissed yet
      notifications.unshift(notification);
    } else {
      //remove the notification from the array, because it is already dismissed
      notifications = notifications.splice(getNotificationIndexInArray(notifications, notification), 1);
      $rootScope.$broadcast('Socket:notificationDismissed', notification);
    }  
    $rootScope.$broadcast('Socket:notification', notifications);
  }

  function getNotificationIndexInArray(notifications, notification) {
    var foundIndex = -1;
    angular.forEach(notifications, function(notif, index) {
      if(notif.id === notification.id) {
        foundIndex = index;
      }
    });
    return foundIndex;
  }
  
  var methods = {
      notifications: notifications
  };

  return methods;
});