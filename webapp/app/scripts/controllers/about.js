'use strict';

/**
 * @ngdoc function
 * @name riotApp.controller:AboutCtrl
 * @description
 * # AboutCtrl
 * Controller of the riotApp
 */
angular.module('riotApp')
  .controller('AboutCtrl', function ($scope) {
    $scope.awesomeThings = [
      'HTML5 Boilerplate',
      'AngularJS',
      'Karma'
    ];
  });
