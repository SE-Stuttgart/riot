angular.module('riot').filter('duplicateFilter', function() {
  return function(input, resource) {    
    var output = [];

    if (!input || !resource) {
      return output;
    }

    for (var i = 0; i < input.length; i++) {
      var exists = false;
      for (var j = 0; j < resource.length; j++) {
        if (input[i].id == resource[j].id) {
          exists = true;
          break;
        }
      }
      if (!exists) {
        output.push(input[i]);
      }
    }

    return output;
  };
});
