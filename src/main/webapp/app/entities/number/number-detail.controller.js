(function() {
    'use strict';

    angular
        .module('phonebookApp')
        .controller('NumberDetailController', NumberDetailController);

    NumberDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Number', 'Person'];

    function NumberDetailController($scope, $rootScope, $stateParams, previousState, entity, Number, Person) {
        var vm = this;

        vm.number = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('phonebookApp:numberUpdate', function(event, result) {
            vm.number = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
