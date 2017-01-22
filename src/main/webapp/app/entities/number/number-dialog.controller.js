(function() {
    'use strict';

    angular
        .module('phonebookApp')
        .controller('NumberDialogController', NumberDialogController);

    NumberDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Number', 'Person'];

    function NumberDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Number, Person) {
        var vm = this;

        vm.number = entity;
        vm.clear = clear;
        vm.save = save;
        vm.people = Person.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.number.id !== null) {
                Number.update(vm.number, onSaveSuccess, onSaveError);
            } else {
                Number.save(vm.number, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('phonebookApp:numberUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
