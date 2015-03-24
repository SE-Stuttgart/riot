angular.module('riot').config(function($stateProvider) {
  $stateProvider.state('app.rules.detail', {
    url: '/detail/:ruleId',
    templateUrl: 'views/app/rules/rule-detail/rule-detail.html'
  });
});

angular.module('riot').controller('RuleDetailCtrl',function($scope, $stateParams, $state, $q, locale, Rule, Restangular){

  var alertId = null;
  $scope.rule = {};
  $scope.ruleDescription = {};
  
  function init() {
    if($stateParams.ruleId === 'new') {
      $scope.rule = Restangular.restangularizeElement(null, $scope.rule, 'rules');
      $scope.ruleExists = false;
      $scope.rule.status = 'ACTIVE';
      fetchRuleTypes();
      watchRuleDescription();
    } else {
      $scope.ruleExists = true;
      fetchRule();
    }
  }
  
  //remove rule
  $scope.removeRule = function() {
    $scope.rule.remove().then(function() {
      var resourceName = locale.getString('common.resource_rules');
      $scope.alerts.close(alertId);
      alertId = $scope.alerts.showSuccess(locale.getString('common.resource_updateSuccess', {
        resource: resourceName
      }));
      $state.go('app.rules.list');
    }, function() {
      var resourceName = locale.getString('common.resource_rules');
      $scope.alerts.close(alertId);
      alertId = $scope.alerts.showError(locale.getString('common.resource_updateError', {
        resource: resourceName
      }));
    });
  };
  
  //update or create new rule
  $scope.save = function() {
    var rule = createThingParameterWithId($scope.rule, $scope.ruleDescription.description);
    if($stateParams.ruleId === 'new') {
      return createNewRule(rule);
    } else {
      return updateRule(rule);
    }
  };
  
  //check if the status is either ACTIVE or DEACTIVATED.
  $scope.showStatusDropDown = function() {
    if($scope.rule.status === 'ACTIVE' || $scope.rule.status === 'DEACTIVATED') {
      return true;
    }
    return false;
  };
  
  //check if all data is entered correctly
  $scope.isDataInvalid = function() {
    
    var invalid = false;
    
    if($scope.formRuleDetail.$invalid) {
      invalid = true;
    }

    angular.forEach($scope.rule.parameterValues, function(param, key) {
      if((typeof param === 'number') || (typeof param === 'boolean')) {
        return;
      }

      if(!param) {
        invalid = true;
      } 
    });
    return invalid;
  };
  
  //fetch an existing rule
  function fetchRule() {
    Rule.one($stateParams.ruleId).get().then(function(rule) {
      $scope.rule = rule;
      
      Rule.one('description').one(rule.type).get().then(function(description) {
        $scope.ruleDescription.description = description;
      });
    });
  }
  
  //fetch all rule types
  function fetchRuleTypes() {
    Rule.one('descriptions').get().then(function(descriptions) {
      $scope.descriptions = descriptions;
      $scope.ruleDescription.description = "";
    });
  }
  
  //create a new rule
  function createNewRule(rule) {
    return showAlert(rule.post());
  }
  
  //update a existing rule
  function updateRule(rule) {
    console.log(rule);
    return showAlert(rule.put());
  }
  
  //show an alert, if an operation was successful or not
  function showAlert(promisse) {
    return $q.all(promisse).then(function() {
      var resourceName = locale.getString('common.resource_rules');
      $scope.alerts.close(alertId);
      alertId = $scope.alerts.showSuccess(locale.getString('common.resource_updateSuccess', {
        resource: resourceName
      }));
    }, function() {
      var resourceName = locale.getString('common.resource_rules');
      $scope.alerts.close(alertId);
      alertId = $scope.alerts.showError(locale.getString('common.resource_updateError', {
        resource: resourceName
      }));
    });
  }
  
  //watch the rule description and add empty parameters to the rule in order to avoid problems with child scopes
  function watchRuleDescription() {
    $scope.$watch('ruleDescription.description', function(newValue) {
      if(newValue) {
        var parameterValues = {};
        angular.forEach(newValue.parameters, function(parameter) {
          this[parameter.name] = "";
        }, parameterValues);
        
        $scope.rule.parameterValues = parameterValues;
        $scope.rule.type = newValue.type;
      }
    });
  }
  
  //copy the rule and replace the thing with its id
  function createThingParameterWithId(rule, description) {
    var ruleCopy = Restangular.copy(rule);
    angular.forEach(description.parameters, function(parameter) {
      if(parameter.uiHint.type === 'ThingReferenceDropDown') {
        this.parameterValues[parameter.name] = this.parameterValues[parameter.name].id;
      }
    }, ruleCopy);
    return ruleCopy;
  }
  
  init();
});