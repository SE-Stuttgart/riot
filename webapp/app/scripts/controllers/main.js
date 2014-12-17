'use strict';

/**
 * @ngdoc function
 * @name riotApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the riotApp
 */
angular.module('riotApp')
  .controller('MainCtrl', function ($scope) {
    $scope.awesomeThings = [
      'HTML5 Boilerplate',
      'AngularJS',
      'Karma'
    ];
  });
