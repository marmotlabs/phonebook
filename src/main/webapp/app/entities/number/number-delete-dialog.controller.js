(function() {
    'use strict';

    angular
        .module('phonebookApp')
        .controller('NumberDeleteController',NumberDeleteController);

    NumberDeleteController.$inject = ['$uibModalInstance', 'entity', 'Number'];

    function NumberDeleteController($uibModalInstance, entity, Number) {
        var vm = this;

        vm.number = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Number.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
