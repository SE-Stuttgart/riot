angular.module('riot').factory('PermissionHelper', function() {
	
	function getPermissionArray(permissionString) {
		var PART_DIVIDER_TOKEN = ":";
		var SUBPART_DIVIDER_TOKEN = ",";
		permissionString = permissionString.trim().toLocaleLowerCase();
		var parts = permissionString.split(PART_DIVIDER_TOKEN);
		var partsLength = parts.length;
		for (var i = 0; i < partsLength; i++) {
			parts[i] = parts[i].split(SUBPART_DIVIDER_TOKEN);
		}
		return parts;
    }

    var service = {
        checkPermission: function(permission, requiredPermission) {
            var WILDCARD_TOKEN = "*";
            var permissionArray = getPermissionArray(permission);
            var requiredArray = getPermissionArray(requiredPermission);
            var requiredArrayLength = requiredArray.length;
            if (requiredArrayLength !== permissionArray.length) {
                return false;
            }
            for (var i = 0; i < requiredArrayLength; i++) {
                if (permissionArray[i].indexOf(WILDCARD_TOKEN) === -1) {
                    var partsLength = requiredArray[i].length;
                    for (var z = 0; z < partsLength; z++) {
                        if (permissionArray[i].indexOf(requiredArray[i][z]) === -1) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
    };

    return service;

});